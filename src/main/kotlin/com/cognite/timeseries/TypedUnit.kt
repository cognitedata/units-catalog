package com.cognite.timeseries

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
