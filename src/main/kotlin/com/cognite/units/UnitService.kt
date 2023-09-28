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
import java.net.URL
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToLong

class UnitService(unitsPath: URL, systemPath: URL) {
    companion object {
        val service: UnitService by lazy {
            UnitService(
                UnitService::class.java.getResource("/units.json")!!,
                UnitService::class.java.getResource("/unitSystems.json")!!,
            )
        }
    }

    private val unitsByExternalId = mutableMapOf<String, TypedUnit>()
    private val unitsByQuantity = mutableMapOf<String, ArrayList<TypedUnit>>()
    private val unitsByQuantityAndAlias = mutableMapOf<String, LinkedHashMap<String, TypedUnit>>()
    private val defaultUnitByQuantityAndSystem = mutableMapOf<String, MutableMap<String, TypedUnit>>()

    init {
        loadUnits(unitsPath)
        loadSystem(systemPath)
    }

    private fun loadUnits(unitsPath: URL) {
        val units = unitsPath.readText()
        val mapper: ObjectMapper = jacksonObjectMapper()

        // 1. Syntax Check: Every unit item in `units.json` must have the specified keys
        val listOfUnits: List<TypedUnit> = mapper.readValue<List<TypedUnit>>(units)

        listOfUnits.forEach {
            // 2. Unique IDs: All unit `externalIds` in `units.json` must be unique
            assert(unitsByExternalId[it.externalId] == null) { "Duplicate externalId ${it.externalId}" }
            unitsByExternalId[it.externalId] = it

            unitsByQuantity.computeIfAbsent(it.quantity) { ArrayList() }.add(it)
            unitsByQuantityAndAlias.computeIfAbsent(it.quantity) { LinkedHashMap() }
            // convert to set first, to remove duplicate aliases due to encoding (e.g. "\u00b0C" vs "°C")
            it.aliasNames.toSet().forEach { alias ->
                // 6. Unique aliases: All pairs of (alias and quantity) must be unique, for all aliases in `aliasNames`
                assert(unitsByQuantityAndAlias[it.quantity]!![alias] == null) {
                    "Duplicate alias $alias for quantity ${it.quantity}"
                }
                unitsByQuantityAndAlias[it.quantity]!![alias] = it
            }
        }
    }

    private fun loadSystem(systemPath: URL) {
        val systems = systemPath.readText()
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
        // check if a default system is defined
        assert(defaultUnitByQuantityAndSystem.containsKey("default")) { "Missing default system" }
        // 4. Default Quantities: All quantities must be present in the `unitSystems.json` for the default quantity
        assert(defaultUnitByQuantityAndSystem["default"]!!.size == unitsByQuantity.size) {
            "Missing units in default system"
        }
    }

    fun getUnitByExternalId(externalId: String): TypedUnit {
        return unitsByExternalId[externalId] ?: throw IllegalArgumentException("Unknown unit '$externalId'")
    }

    fun getUnitsByQuantity(quantity: String): List<TypedUnit> {
        return unitsByQuantity[quantity] ?: throw IllegalArgumentException("Unknown unit quantity '$quantity'")
    }

    fun getUnitsByQuantityAndAlias(quantity: String, alias: String): TypedUnit {
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
            ?: defaultUnitByQuantityAndSystem["default"]!![sourceUnit.quantity] ?: throw IllegalArgumentException(
            "Cannot convert from ${sourceUnit.quantity}",
        )
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

    /*
     * Conversion factors can't always be represented exactly in floating point. Also, some arithmetics may result
     * in numbers like 0.9999999999999999 which should be rounded to 1.0.
     * This function rounds to the specified number of significant digits.
     */
    private fun roundToSignificantDigits(value: Double, significantDigits: Int): Double {
        if (value == 0.0 || !value.isFinite()) {
            return value
        }
        val digits = ceil(log10(abs(value)))
        val power = significantDigits - digits
        val magnitude = 10.0.pow(power)
        val shifted = (value * magnitude).roundToLong()
        return shifted / magnitude
    }

    fun isValidUnit(unitExternalId: String): Boolean {
        return unitsByExternalId.containsKey(unitExternalId)
    }
}
