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

import com.cognite.units.UnitService
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
        val unitCelsius = unitService.getUnitByExternalId("temperature:deg_c")
        val unitFahrenheit = unitService.getUnitByExternalId("temperature:deg_f")

        assertEquals(50.0, unitService.convertBetweenUnits(unitCelsius, unitFahrenheit, 10.0))
        assertEquals(50.0, unitService.convertBetweenUnits(unitFahrenheit, unitFahrenheit, 50.0))
        assertEquals(33.8, unitService.convertBetweenUnits(unitCelsius, unitFahrenheit, 1.0))
        assertEquals(0.555555555556, unitService.convertBetweenUnits(unitFahrenheit, unitCelsius, 33.0))
    }

    @Test
    fun convertToSystem() {
        val unitService = UnitService.service
        val unitCelsius = unitService.getUnitByExternalId("temperature:deg_c")
        val unitFahrenheit = unitService.getUnitByExternalId("temperature:deg_f")
        assertEquals(unitCelsius, unitService.getUnitBySystem(unitCelsius, "Default"))
        assertEquals(unitCelsius, unitService.getUnitBySystem(unitFahrenheit, "Default"))
        assertEquals(unitFahrenheit, unitService.getUnitBySystem(unitCelsius, "Imperial"))
        // fallback to Default
        val unitPercent = unitService.getUnitByExternalId("dimensionless_ratio:percent")
        val unitFraction = unitService.getUnitByExternalId("dimensionless_ratio:fraction")
        assertEquals(unitFraction, unitService.getUnitBySystem(unitPercent, "Imperial"))
    }

    @Test
    fun convertVarianceBetweenUnits() {
        val unitService = UnitService.service
        val unitCelsius = unitService.getUnitByExternalId("temperature:deg_c")
        val unitFahrenheit = unitService.getUnitByExternalId("temperature:deg_f")
        assertEquals(
            81.0 / 25,
            unitService.convertBetweenUnitsSquareMultiplier(unitCelsius, unitFahrenheit, 1.0),
            1e-12,
        )
        assertEquals(3.15, unitService.convertBetweenUnitsSquareMultiplier(unitCelsius, unitCelsius, 3.15))
        assertEquals(0.0, unitService.convertBetweenUnitsSquareMultiplier(unitFahrenheit, unitCelsius, 0.0))
        assertEquals(
            25.0 / 81,
            unitService.convertBetweenUnitsSquareMultiplier(unitFahrenheit, unitCelsius, 1.0),
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
    fun jsonWithInvalidExternalId() {
        try {
            UnitService(getTestResource("invalidExternalId.json"), getTestResource("unitSystems.json"))
            fail("Expected AssertionError")
        } catch (e: AssertionError) {
            assertEquals(
                "Invalid externalId temperaturegradient:k-per-m for unit K-PER-M (Temperature Gradient)",
                e.message,
            )
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
