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

/* How to convert from a unit to the base unit, given a conversion factor:
 * baseUnitValue = (unitValue + shift) * multiplier
 */
data class Conversion(
    val multiplier: Double,
    val offset: Double,
)
data class TypedUnit(
    val externalId: String,
    val name: String,
    val longName: String,
    val aliasNames: List<String>,
    val quantity: String,
    val conversion: Conversion,
    val source: String?,
    val sourceReference: String?,
)

data class SystemQuantity(
    val name: String,
    val unitExternalId: String,
)
data class UnitSystem(
    val name: String,
    val quantities: List<SystemQuantity>,
)
