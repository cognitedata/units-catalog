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
import java.util.concurrent.CompletableFuture
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToLong

class UnitService(
    units: String? = null,
    systems: String? = null,
    private val getUnitSystemsCallback: ((String) -> CompletableFuture<String>)? = null,
    private val getUnitsCallback: ((String) -> CompletableFuture<String>)? = null,
) {
    constructor(unitsPath: URL, systemPath: URL) : this(units = unitsPath.readText(), systems = systemPath.readText())

    companion object {
        const val GLOBAL_PROJECT = "_global_"

        val service: UnitService by lazy {
            UnitService(
                UnitService::class.java.getResource("/units.json")!!,
                UnitService::class.java.getResource("/unitSystems.json")!!,
            )
        }
    }

    private val unitsByAlias = mutableMapOf<String, MutableMap<String, ArrayList<TypedUnit>>>()
    private val unitsByExternalId = mutableMapOf<String, MutableMap<String, TypedUnit>>()
    private val unitsByQuantity = mutableMapOf<String, MutableMap<String, ArrayList<TypedUnit>>>()
    private val unitsByQuantityAndAlias = mutableMapOf<String, MutableMap<String, LinkedHashMap<String, TypedUnit>>>()
    private val defaultUnitByQuantityAndSystem =
        mutableMapOf<String, MutableMap<String, MutableMap<String, TypedUnit>>>()

    init {
        if (units != null && systems != null) {
            loadUnits(units, GLOBAL_PROJECT)
            loadSystem(systems, GLOBAL_PROJECT)
        }
    }

    // For a given quantity, there should not be duplicate units
    // one way to check this is to verify that the conversion values are unique
    // Returns: A list of duplicate units in a map by conversion, by quantity
    fun getDuplicateConversions(units: List<TypedUnit>): Map<String, Map<Conversion, List<TypedUnit>>> {
        return units.groupBy { it.quantity }.mapValues { (_, units) ->
            units.groupBy { it.conversion }.filter { it.value.size > 1 }
        }.filter { it.value.isNotEmpty() }
    }

    private fun ensureProjectLoaded(projectName: String): CompletableFuture<Void> {
        if (unitsByExternalId.containsKey(projectName)) {
            return CompletableFuture.completedFuture(null)
        }
        return getUnitsCallback?.invoke(projectName)?.thenAccept { loadUnits(it, projectName) }
            ?: CompletableFuture.completedFuture(null)
    }

    private fun loadUnits(units: String, projectName: String = GLOBAL_PROJECT) {
        val mapper: ObjectMapper = jacksonObjectMapper()
        // 1. Syntax Check: Every unit item in `units.json` must have the specified keys
        val listOfUnits: List<TypedUnit> = mapper.readValue(units)

        listOfUnits.forEach {
            // 2. Unique IDs: All unit `externalIds` in `units.json` must be unique
            assert(unitsByExternalId[projectName]?.get(it.externalId) == null) {
                "Duplicate externalId ${it.externalId}"
            }
            unitsByExternalId.getOrPut(projectName) { mutableMapOf() }[it.externalId] = it

            UnitUtils.validateUnit(it)

            unitsByQuantity.getOrPut(projectName) { mutableMapOf() }
                .getOrPut(it.quantity) { ArrayList() }.add(it)
            unitsByQuantityAndAlias.getOrPut(projectName) { mutableMapOf() }
                .getOrPut(it.quantity) { LinkedHashMap() }
            // convert to set first, to remove duplicate aliases due to encoding (e.g. "\u00b0C" vs "°C")
            it.aliasNames.toSet().forEach { alias ->
                unitsByAlias.getOrPut(projectName) { mutableMapOf() }
                    .getOrPut(alias) { ArrayList() }.add(it)
                // 6. Unique aliases: All pairs of (alias and quantity) must be unique, for all aliases in `aliasNames`
                assert(unitsByQuantityAndAlias[projectName]!![it.quantity]!![alias] == null) {
                    "Duplicate alias $alias for quantity ${it.quantity}"
                }
                unitsByQuantityAndAlias[projectName]!![it.quantity]!![alias] = it
            }
        }
    }

    private fun loadSystem(systems: String, projectName: String = GLOBAL_PROJECT) {
        val mapper: ObjectMapper = jacksonObjectMapper()
        val listOfSystems: List<UnitSystem> = mapper.readValue(systems)

        listOfSystems.forEach {
            val system = it.name
            // check for duplicate systems
            assert(defaultUnitByQuantityAndSystem.getOrPut(projectName) { mutableMapOf() }[system] == null) {
                "Duplicate system $system"
            }
            defaultUnitByQuantityAndSystem[projectName]!![system] = it.quantities.associate { sq ->
                // 3. Reference Validation: There should be no references to non-existent unit `externalIds` in
                // `unitSystems.json`
                val unit = getUnitByExternalId(sq.unitExternalId, projectName).join()
                // 5. Consistent References: All quantity references in `unitSystems.json` must exist in `units.json`
                assert(unitsByQuantity[projectName]!!.containsKey(sq.name)) { "Unknown quantity ${sq.name}" }
                sq.name to unit
            }.toMutableMap()
        }
        // check if a Default system is defined
        assert(defaultUnitByQuantityAndSystem[projectName]!!.containsKey("Default")) { "Missing Default system" }
        // 4. Default Quantities: All quantities must be present in the `unitSystems.json` for the Default quantity
        assert(defaultUnitByQuantityAndSystem[projectName]!!["Default"]!!.size == unitsByQuantity[projectName]!!.size) {
            "Missing units in Default system"
        }
    }

    fun getUnits(projectName: String = GLOBAL_PROJECT): CompletableFuture<List<TypedUnit>> {
        return getUnitsCallback?.invoke(projectName)?.thenApply {
            loadUnits(it, projectName)
            unitsByExternalId[projectName]!!.values.toList()
        } ?: CompletableFuture.completedFuture(unitsByExternalId[GLOBAL_PROJECT]!!.values.toList())
    }

    fun getUnitSystems(projectName: String = GLOBAL_PROJECT): CompletableFuture<Set<String>> {
        return getUnitSystemsCallback?.invoke(projectName)?.thenApply {
            loadSystem(it, projectName)
            defaultUnitByQuantityAndSystem[projectName]!!.keys
        } ?: CompletableFuture.completedFuture(defaultUnitByQuantityAndSystem[GLOBAL_PROJECT]!!.keys)
    }

    fun getUnitByExternalId(externalId: String, projectName: String = GLOBAL_PROJECT): CompletableFuture<TypedUnit> {
        val defaultUnit = unitsByExternalId[GLOBAL_PROJECT]?.get(externalId)
        if (defaultUnit != null) {
            return CompletableFuture.completedFuture(defaultUnit)
        }
        return ensureProjectLoaded(projectName).thenApply {
            unitsByExternalId[projectName]?.get(externalId)
                ?: throw IllegalArgumentException("Unknown unit '$externalId'")
        }
    }

    fun getUnitsByQuantity(quantity: String, projectName: String = GLOBAL_PROJECT): CompletableFuture<List<TypedUnit>> {
        val defaultUnits = unitsByQuantity[GLOBAL_PROJECT]?.get(quantity)
        if (defaultUnits != null) {
            return CompletableFuture.completedFuture(defaultUnits)
        }
        return ensureProjectLoaded(projectName).thenApply {
            unitsByQuantity[projectName]?.get(quantity)
                ?: throw IllegalArgumentException("Unknown unit quantity '$quantity'")
        }
    }

    fun getUnitByQuantityAndAlias(
        quantity: String,
        alias: String,
        projectName: String = GLOBAL_PROJECT,
    ): CompletableFuture<TypedUnit> {
        val defaultQuantityTable = unitsByQuantityAndAlias[GLOBAL_PROJECT]?.get(quantity)
        if (defaultQuantityTable != null && defaultQuantityTable.containsKey(alias)) {
            return CompletableFuture.completedFuture(defaultQuantityTable[alias]!!)
        }
        return ensureProjectLoaded(projectName).thenApply {
            val quantityTable = unitsByQuantityAndAlias[projectName]?.get(quantity)
                ?: throw IllegalArgumentException("Unknown quantity '$quantity'")
            quantityTable[alias]
                ?: throw IllegalArgumentException("Unknown unit alias '$alias' for quantity '$quantity'")
        }
    }

    fun getUnitBySystem(
        sourceUnit: TypedUnit,
        targetSystem: String,
        projectName: String = GLOBAL_PROJECT,
    ): CompletableFuture<TypedUnit> {
        val defaultSystem = defaultUnitByQuantityAndSystem[GLOBAL_PROJECT]?.get(targetSystem)?.get(sourceUnit.quantity)
        if (defaultSystem != null) {
            return CompletableFuture.completedFuture(defaultSystem)
        }
        return ensureProjectLoaded(projectName).thenApply {
            defaultUnitByQuantityAndSystem[projectName]?.get(targetSystem)?.get(sourceUnit.quantity)
                ?: throw IllegalArgumentException("Cannot convert from ${sourceUnit.quantity}")
        }
    }

    fun getUnitsByAlias(alias: String, projectName: String = GLOBAL_PROJECT): CompletableFuture<ArrayList<TypedUnit>> {
        val defaultUnits = unitsByAlias[GLOBAL_PROJECT]?.get(alias)
        if (defaultUnits != null) {
            return CompletableFuture.completedFuture(defaultUnits)
        }
        return ensureProjectLoaded(projectName).thenApply {
            unitsByAlias[projectName]?.get(alias)
                ?: throw IllegalArgumentException("Unknown alias '$alias'")
        }
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

    fun isValidUnit(unitExternalId: String, projectName: String = GLOBAL_PROJECT): CompletableFuture<Boolean> {
        val defaultValid = unitsByExternalId[GLOBAL_PROJECT]?.containsKey(unitExternalId) ?: false
        if (defaultValid) {
            return CompletableFuture.completedFuture(true)
        }
        return ensureProjectLoaded(projectName).thenApply {
            unitsByExternalId[projectName]?.containsKey(unitExternalId) ?: false
        }
    }
}

// Kotlin shim to simplify interop with Scala
object UnitServiceFacade {
    fun getService(): UnitService = UnitService.service
}
