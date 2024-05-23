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
import org.junit.jupiter.api.Test

class DuplicatedUnitsTest {
    @Test
    fun getDuplicateConversions() {
        val unitService = UnitService.service
        val duplicates = unitService.getDuplicateConversions(unitService.getUnits())
        if (duplicates.isNotEmpty()) {
            println("## Equivalent units found in the catalog")
            println(
                "This check scans the catalog looking for equivalent " +
                    "(or duplicate) unit entries for each quantity.",
            )
            println("Equivalent units are allowed, but duplicate units are not allowed.")
            println(
                "The reviewer needs to go through the list and confirm no " +
                    "duplicate units were introduced in the current PR.",
            )
            println()
            duplicates.forEach { (quantity, duplicatesByConversion) ->
                println()
                println("### Quantity: *$quantity*")
                duplicatesByConversion.forEach { (conversion, duplicatesList) ->
                    println("  * Multiplier: `${conversion.multiplier}` Offset: `${conversion.offset}`")
                    duplicatesList.forEach { duplicate ->
                        println("    - `${duplicate.externalId}`")
                    }
                }
            }
        } else {
            println("No equivalent units exist in the catalog.")
        }
    }
}
