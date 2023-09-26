package com.cognite.timeseries

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URL

class UnitTest {

    private fun getTestResource(filename: String): URL {
        return UnitTest::class.java.getResource("/$filename")!!
    }

    @Test
    fun loadProductionUnitService() {
        UnitService.service
    }

    @Test
    fun convertBetweenUnits() {
        val unitService = UnitService.service
        val unitCelcius = unitService.getUnitByExternalId("temperature:deg_c")
        val unitFahrenheit = unitService.getUnitByExternalId("temperature:deg_f")

        assertEquals(50.0, unitService.convertBetweenUnits(unitCelcius, unitFahrenheit, 10.0))
        assertEquals(50.0, unitService.convertBetweenUnits(unitFahrenheit, unitFahrenheit, 50.0))
        assertEquals(33.8, unitService.convertBetweenUnits(unitCelcius, unitFahrenheit, 1.0))
        assertEquals(0.555555555556, unitService.convertBetweenUnits(unitFahrenheit, unitCelcius, 33.0))
    }

    @Test
    fun convertToSystem() {
        val unitService = UnitService.service
        val unitCelcius = unitService.getUnitByExternalId("temperature:deg_c")
        val unitFahrenheit = unitService.getUnitByExternalId("temperature:deg_f")
        assertEquals(unitCelcius, unitService.getUnitBySystem(unitCelcius, "SI (Engineering)"))
        assertEquals(unitCelcius, unitService.getUnitBySystem(unitFahrenheit, "SI (Engineering)"))
        assertEquals(unitFahrenheit, unitService.getUnitBySystem(unitCelcius, "Imperial"))
        // fallback to default
        val unitPercent = unitService.getUnitByExternalId("fraction:percent")
        val unitFraction = unitService.getUnitByExternalId("fraction:fraction")
        assertEquals(unitPercent, unitService.getUnitBySystem(unitFraction, "Imperial"))
    }

    @Test
    fun convertVarianceBetweenUnits() {
        val unitService = UnitService.service
        val unitCelcius = unitService.getUnitByExternalId("temperature:deg_c")
        val unitFahrenheit = unitService.getUnitByExternalId("temperature:deg_f")
        assertEquals(
            81.0 / 25,
            unitService.convertBetweenUnitsSquareMultiplier(unitCelcius, unitFahrenheit, 1.0),
            1e-12,
        )
        assertEquals(3.15, unitService.convertBetweenUnitsSquareMultiplier(unitCelcius, unitCelcius, 3.15))
        assertEquals(0.0, unitService.convertBetweenUnitsSquareMultiplier(unitFahrenheit, unitCelcius, 0.0))
        assertEquals(
            25.0 / 81,
            unitService.convertBetweenUnitsSquareMultiplier(unitFahrenheit, unitCelcius, 1.0),
            1e-12,
        )
    }

    @Test
    fun jsonWithDuplicateExternalId() {
        try {
            UnitService(getTestResource("duplicateExternalId.json"), getTestResource("unitSystems.json"))
            fail("Expected AssertionError")
        } catch (e: AssertionError) {
            assertEquals("Duplicate externalId temperature:deg", e.message)
        }
    }

    @Test
    fun jsonWithDuplicateAlias() {
        try {
            UnitService(getTestResource("duplicateAlias.json"), getTestResource("unitSystems.json"))
            fail("Expected AssertionError")
        } catch (e: AssertionError) {
            assertEquals("Duplicate alias degrees for quantity Temperature", e.message)
        }
    }

    @Test
    fun lookupUnits() {
        val unitService = UnitService.service
        assertEquals(
            unitService.getUnitByExternalId("temperature:deg_c"),
            unitService.getUnitsByQuantityAndAlias("Temperature", "degC"),
        )
        assertEquals(
            unitService.getUnitByExternalId("temperature_gradient:k-per-m"),
            unitService.getUnitsByQuantity("Temperature Gradient").first(),
        )
    }

    @Test
    fun lookupIllegalUnits() {
        val unitService = UnitService.service
        assertThrows<IllegalArgumentException> {
            unitService.getUnitsByQuantity("unknown")
        }
        assertThrows<IllegalArgumentException> {
            unitService.getUnitByExternalId("unknown")
        }
        assertThrows<IllegalArgumentException> {
            unitService.getUnitsByQuantityAndAlias("unknown", "unknown")
        }
        assertThrows<IllegalArgumentException> {
            unitService.getUnitsByQuantityAndAlias("Temperature", "unknown")
        }
    }
}
