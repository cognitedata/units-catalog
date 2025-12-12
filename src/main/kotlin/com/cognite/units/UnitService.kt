/**
 * Copyright 2023 Cognite AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cognite.units

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URI
import java.net.URL
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.nextDown
import kotlin.math.pow
import kotlin.math.roundToLong

class UnitService(units: String, systems: String) {

    constructor(unitsPath: URL, systemPath: URL) : this(unitsPath.readText(), systemPath.readText())

    companion object {
        val service: UnitService by lazy {
            UnitService(
                UnitService::class.java.getResource("/units.json")!!,
                UnitService::class.java.getResource("/unitSystems.json")!!,
            )
        }
    }

    private val unitsByAlias = mutableMapOf<String, ArrayList<TypedUnit>>()
    private val unitsByExternalId = mutableMapOf<String, TypedUnit>()
    private val unitsByQuantity = mutableMapOf<String, ArrayList<TypedUnit>>()
    private val unitsByQuantityAndAlias = mutableMapOf<String, LinkedHashMap<String, TypedUnit>>()
    private val defaultUnitByQuantityAndSystem = mutableMapOf<String, MutableMap<String, TypedUnit>>()

    init {
        loadUnits(units)
        loadSystem(systems)
    }

    private fun sanitizeIdentifier(identifier: String): String {
        // remove all special characters except - and _
        return identifier.lowercase().replace(Regex("[^a-z0-9_-]"), "_")
    }

    private fun generateExpectedExternalId(unit: TypedUnit): String {
        val sanitizedQuantity = sanitizeIdentifier(unit.quantity)
        val sanitizedName = sanitizeIdentifier(unit.name)
        return "$sanitizedQuantity:$sanitizedName"
    }

    private fun generatedExpectedSourceReference(unit: TypedUnit): String? {
        if (unit.source == "qudt.org") {
            return "https://qudt.org/vocab/unit/${unit.name}"
        }

        val errorMessage = "Invalid sourceReference ${unit.sourceReference} for unit ${unit.name} (${unit.quantity})"

        // check reference is a valid http(s) url if present
        if (unit.sourceReference != null) {
            try {
                val url = URI.create(unit.sourceReference).toURL()
                if (url.protocol != "http" && url.protocol != "https") {
                    throw IllegalArgumentException(errorMessage)
                }
            } catch (e: Exception) {
                throw IllegalArgumentException(errorMessage, e)
            }
        }
        return unit.sourceReference
    }

    // For a given quantity, there should not be duplicate units
    // one way to check this is to verify that the conversion values are unique
    // Returns: A list of duplicate units in a map by conversion, by quantity
    fun getDuplicateConversions(units: List<TypedUnit>): Map<String, Map<Conversion, List<TypedUnit>>> {
        return units.groupBy { it.quantity }.mapValues { (_, units) ->
            units.groupBy { it.conversion }.filter { it.value.size > 1 }
        }.filter { it.value.isNotEmpty() }
    }

    private fun loadUnits(units: String) {
        val mapper: ObjectMapper = jacksonObjectMapper()

        // 1. Syntax Check: Every unit item in `units.json` must have the specified keys
        val listOfUnits: List<TypedUnit> = mapper.readValue<List<TypedUnit>>(units)

        listOfUnits.forEach {
            // 2. Unique IDs: All unit `externalIds` in `units.json` must be unique
            assert(unitsByExternalId[it.externalId] == null) { "Duplicate externalId ${it.externalId}" }
            unitsByExternalId[it.externalId] = it

            // 8. ExternalId Format: All unit `externalIds` must follow the pattern `{quantity}:{unit}`, where both
            // `quantity` and `unit` are in snake_case.
            assert(it.externalId == generateExpectedExternalId(it)) {
                "Invalid externalId ${it.externalId} for unit ${it.name} (${it.quantity})"
            }

            // if source is qudt.org, reference should be in the format https://qudt.org/vocab/unit/{unit.name}
            if (it.source == "qudt.org") {
                assert(it.sourceReference == generatedExpectedSourceReference(it)) {
                    "Invalid sourceReference ${it.sourceReference} for unit ${it.name} (${it.quantity})"
                }
            }

            unitsByQuantity.computeIfAbsent(it.quantity) { ArrayList() }.add(it)
            unitsByQuantityAndAlias.computeIfAbsent(it.quantity) { LinkedHashMap() }
            // convert to set first, to remove duplicate aliases due to encoding (e.g. "\u00b0C" vs "°C")
            it.aliasNames.toSet().forEach { alias ->
                unitsByAlias.computeIfAbsent(alias) { ArrayList() }.add(it)
                // 6. Unique Quantity-Alias Pairs: All pairs of (alias and quantity) must be unique, for all aliases in
                // `aliasNames`
                assert(unitsByQuantityAndAlias[it.quantity]!![alias] == null) {
                    "Duplicate alias $alias for quantity ${it.quantity}"
                }
                unitsByQuantityAndAlias[it.quantity]!![alias] = it
            }
        }
    }

    private fun loadSystem(systems: String) {
        val mapper: ObjectMapper = jacksonObjectMapper()
        val listOfSystems: List<UnitSystem> = mapper.readValue<List<UnitSystem>>(systems)

        listOfSystems.forEach {
            val system = it.name
            // check for duplicate systems
            assert(defaultUnitByQuantityAndSystem[system] == null) { "Duplicate system $system" }
            defaultUnitByQuantityAndSystem[system] = it.quantities.associate { sq ->
                // 3. Reference Validation: There should be no references to non-existent unit `externalIds` in
                // `unitSystems.json`
                val unit = getUnitByExternalId(sq.unitExternalId)
                // 5. Consistent References: All quantity references in `unitSystems.json` must exist in `units.json`
                assert(unitsByQuantity.containsKey(sq.name)) { "Unknown quantity ${sq.name}" }
                sq.name to unit
            }.toMutableMap()
        }
        // check if a Default system is defined
        assert(defaultUnitByQuantityAndSystem.containsKey("Default")) { "Missing Default system" }
        // 4. Default Quantities: All quantities must be present in the `unitSystems.json` for the Default quantity
        assert(defaultUnitByQuantityAndSystem["Default"]!!.size == unitsByQuantity.size) {
            "Missing units in Default system"
        }
    }

    fun getUnits(): List<TypedUnit> = unitsByExternalId.values.toList()

    fun getUnitSystems(): Set<String> = defaultUnitByQuantityAndSystem.keys

    fun getUnitByExternalId(externalId: String): TypedUnit {
        return unitsByExternalId[externalId] ?: throw IllegalArgumentException("Unknown unit '$externalId'")
    }

    fun getUnitsByQuantity(quantity: String): List<TypedUnit> {
        return unitsByQuantity[quantity] ?: throw IllegalArgumentException("Unknown unit quantity '$quantity'")
    }

    fun getUnitByQuantityAndAlias(quantity: String, alias: String): TypedUnit {
        val quantityTable = unitsByQuantityAndAlias[quantity] ?: throw IllegalArgumentException(
            "Unknown quantity '$quantity'",
        )
        return quantityTable[alias] ?: throw IllegalArgumentException(
            "Unknown unit alias '$alias' for quantity '$quantity'",
        )
    }

    fun getUnitBySystem(sourceUnit: TypedUnit, targetSystem: String): TypedUnit {
        if (!defaultUnitByQuantityAndSystem.containsKey(targetSystem)) {
            throw IllegalArgumentException("Unknown system $targetSystem")
        }
        return defaultUnitByQuantityAndSystem[targetSystem]!![sourceUnit.quantity]
            ?: defaultUnitByQuantityAndSystem["Default"]!![sourceUnit.quantity] ?: throw IllegalArgumentException(
            "Cannot convert from ${sourceUnit.quantity}",
        )
    }

    fun getUnitsByAlias(alias: String): ArrayList<TypedUnit> {
        return unitsByAlias[alias] ?: throw IllegalArgumentException("Unknown alias '$alias'")
    }

    fun verifyIsConvertible(unitFrom: TypedUnit, unitTo: TypedUnit) {
        if (unitFrom.quantity != unitTo.quantity) {
            throw IllegalArgumentException(
                "Cannot convert between units of different quantities " +
                    "(from '${unitFrom.quantity}' to '${unitTo.quantity}')",
            )
        }
    }

    fun convertBetweenUnits(unitFrom: TypedUnit, unitTo: TypedUnit, value: Double): Double {
        if (unitFrom == unitTo) {
            return value // avoid rounding errors
        }
        verifyIsConvertible(unitFrom, unitTo)
        val baseUnitValue = (value + unitFrom.conversion.offset) * unitFrom.conversion.multiplier
        val targetUnitValue = (baseUnitValue / unitTo.conversion.multiplier) - unitTo.conversion.offset
        return roundToSignificantDigits(targetUnitValue, 12)
    }

    // For total variation
    fun convertBetweenUnitsMultiplier(unitFrom: TypedUnit, unitTo: TypedUnit, value: Double): Double {
        if (unitFrom == unitTo) {
            return value // avoid rounding errors
        }
        verifyIsConvertible(unitFrom, unitTo)
        val baseUnitValue = value * unitFrom.conversion.multiplier
        val targetUnitValue = baseUnitValue / unitTo.conversion.multiplier
        return roundToSignificantDigits(targetUnitValue, 12)
    }

    // for variance
    fun convertBetweenUnitsSquareMultiplier(unitFrom: TypedUnit, unitTo: TypedUnit, value: Double): Double {
        if (unitFrom == unitTo) {
            return value // avoid rounding errors
        }
        verifyIsConvertible(unitFrom, unitTo)
        val baseUnitValue = value * unitFrom.conversion.multiplier * unitFrom.conversion.multiplier
        val targetUnitValue = baseUnitValue / unitTo.conversion.multiplier / unitTo.conversion.multiplier
        return roundToSignificantDigits(targetUnitValue, 12)
    }

    // Find the range of numbers that would convert to the given valueTo in the target unit
    fun convertBetweenUnitsInverseRange(unitFrom: TypedUnit, unitTo: TypedUnit, valueTo: Double): Pair<Double, Double> {
        if (unitFrom == unitTo) {
            return valueTo to valueTo // avoid rounding errors
        }
        verifyIsConvertible(unitFrom, unitTo)
        val roundingValues = roundingNeighbors(valueTo, 12)
        // roundedTarget is the aim: we want to find everything that would convert and round to this value
        val roundedTarget = roundingValues.rounded
        // Find the bounds for the target value. Anything between these bounds would round to roundedTarget
        val lowerBoundTo = (roundingValues.nextLower + roundedTarget) / 2
        val upperBoundTo = (roundedTarget + roundingValues.nextUpper) / 2
        // Do the reverse conversion to find the corresponding bounds in the source unit
        val lowerBaseUnit = convertBetweenUnits(unitTo, unitFrom, lowerBoundTo)
        val upperBaseUnit = convertBetweenUnits(unitTo, unitFrom, upperBoundTo)
        // Because the bounds can not be represented exactly, we do one more check to see if we are correct.
        // If they do not convert to the exact roundedTarget, we must use the neighbor number instead.
        val adjustedLower = if (convertBetweenUnits(unitFrom, unitTo, lowerBaseUnit) == roundedTarget) {
            lowerBaseUnit
        } else {
            roundingNeighbors(lowerBaseUnit, 12).nextUpper
        }
        val adjustedUpper = if (convertBetweenUnits(unitFrom, unitTo, upperBaseUnit) == roundedTarget) {
            upperBaseUnit
        } else {
            roundingNeighbors(upperBaseUnit, 12).nextLower
        }
        return adjustedLower to adjustedUpper
    }

    /*
     * Conversion factors can't always be represented exactly in floating point. Also, some arithmetics may result
     * in numbers like 0.9999999999999999 which should be rounded to 1.0.
     * This function rounds to the specified number of significant digits.
     */
    fun roundToSignificantDigits(value: Double, significantDigits: Int): Double {
        if (value == 0.0 || !value.isFinite()) {
            return value
        }
        val digits = ceil(log10(abs(value)))
        val power = significantDigits - digits
        val magnitude = 10.0.pow(power)
        val shifted = (value * magnitude).roundToLong()
        return shifted / magnitude
    }

    data class RoundingValues(
        val nextLower: Double,
        val rounded: Double,
        val nextUpper: Double,
    )

    /* Find the rounding neighbors. Eg. with 2 significant digits,
     * the neighbors of 1.234 are (1.22, 1.24). (First round to 1.23, then find neighbors.)
     */
    fun roundingNeighbors(value: Double, significantDigits: Int) : RoundingValues{
        if (value == 0.0 || !value.isFinite()) {
            return RoundingValues(value, value, value)
        }
        val digits = ceil(log10(abs(value)))
        val power = significantDigits - digits
        val magnitude = 10.0.pow(power)
        val shifted = (value * magnitude).roundToLong()
        return RoundingValues(
            (shifted - 1) / magnitude,
            shifted / magnitude,
            (shifted + 1) / magnitude,
            )
    }

    fun isValidUnit(unitExternalId: String): Boolean {
        return unitsByExternalId.containsKey(unitExternalId)
    }
}

// Kotlin shim to simplify interop with Scala
object UnitServiceFacade {
    fun getService(): UnitService = UnitService.service
}
