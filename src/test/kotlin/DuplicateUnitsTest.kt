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
import kotlin.test.DefaultAsserter.fail

class DuplicatedUnitsTest {
    @Test
    fun getDuplicateConversions() {
        getDuplicateConversions(false)
    }

    @Test
    fun getDuplicateConversionsFail() {
        getDuplicateConversions(true)
    }

    private fun getDuplicateConversions(failOnError: Boolean) {
        val unitService = UnitService.service
        val duplicates = unitService.getDuplicateConversions(unitService.getUnits())
        // We want to filter out all units that are marked as equivalent
        val newDuplicates = duplicates.mapValues {
                (_, duplicatesByConversion) ->
            duplicatesByConversion.filterNot {
                EquivalentUnits.equivalentUnits.containsAll(it.value.map { typedUnit -> typedUnit.externalId })
            }
        }.filter { it.value.isNotEmpty() }

        if (newDuplicates.isNotEmpty()) {
            println("## Equivalent units found in the catalog")
            println(
                "This check scans the catalog looking for equivalent " +
                    "(or duplicate) unit entries for each quantity.",
            )
            println("Equivalent units are allowed, but duplicate units are not allowed.")
            println("Duplicate units should be removed from the catalog. ")
            println("Equivalent units should be marked as such in EquivalentUnits.kt.")
            println()
            newDuplicates.forEach { (quantity, duplicatesByConversion) ->
                println()
                println("### Quantity: *$quantity*")
                duplicatesByConversion.forEach { (conversion, duplicatesList) ->
                    println("  * Multiplier: `${conversion.multiplier}` Offset: `${conversion.offset}`")
                    duplicatesList.forEach { duplicate ->
                        println("    - `${duplicate.externalId}`")
                    }
                }
            }
            val duplicateList =
                duplicates.flatMap {
                    it.value.values.flatten().map { typedUnit -> "\"${typedUnit.externalId}\"" }
                }
                    .joinToString(",\n", postfix = ",")
            if (failOnError) {
                fail("Duplicate units found in the catalog. Update list in EquivalentUnits.kt:\n$duplicateList")
            }
        } else {
            println("No equivalent units were introduced in this Pull Request.")
        }
    }
}
