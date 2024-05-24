/*
List of equivalent units; units that are equivalent to other units, but are not considered duplicates.
In other words, these units should **not** generate errors in the DuplicateUnitsTest.
 */
class EquivalentUnits {
    companion object {
        val equivalentUnits = setOf(
            "amount_of_substance_per_unit_volume:kilomol-per-m3",
            "amount_of_substance_per_unit_volume:mol-per-decim3",
            "amount_of_substance_per_unit_volume:mol-per-l",
            "amount_of_substance_per_unit_volume:micromol-per-l",
            "amount_of_substance_per_unit_volume:millimol-per-m3",
            "amount_of_substance_per_unit_volume:millimol-per-l",
            "amount_of_substance_per_unit_volume:mol-per-m3",
            "dimensionless:num",
            "dimensionless:unitless",
            "dimensionless_ratio:fraction",
            "dimensionless_ratio:unitless",
            "energy:j",
            "energy:w-sec",
            "energy:kilov-a-hr",
            "energy:kilow-hr",
            "energy:megav-a-hr",
            "energy:megaw-hr",
            "energy:v-a-hr",
            "energy:w-hr",
            "force:kilolb_f",
            "force:megalb_f",
            "frequency:hz",
            "frequency:num-per-sec",
            "power:j-per-sec",
            "power:w",
            "power:megaj-per-sec",
            "power:megaw",
            "pressure:hectopa",
            "pressure:millibar",
            "pressure:kilogm-per-m-sec2",
            "pressure:n-per-m2",
            "pressure:pa",
            "pressure:lb_f-per-in2",
            "pressure:psi",
            "pressure:megapa",
            "pressure:n-per-millim2",
            "surface_tension:j-per-m2",
            "surface_tension:kilogm-per-sec2",
            "surface_tension:n-m-per-m2",
            "surface_tension:w-sec-per-m2",
            "volume:centim3",
            "volume:millil",
            "volume:decim3",
            "volume:l",
            "volume:kilol",
            "volume:m3",
            "volume:microl",
            "volume:millim3",
            "volume_flow_rate:centim3-per-sec",
            "volume_flow_rate:millil-per-sec",
            "volume_flow_rate:decim3-per-min",
            "volume_flow_rate:l-per-min",
            "volume_flow_rate:decim3-per-sec",
            "volume_flow_rate:l-per-sec",
            "volume_fraction:centim3-per-centim3",
            "volume_fraction:l-per-l",
            "volume_fraction:m3-per-m3",
            "volume_fraction:centim3-per-m3",
            "volume_fraction:microl-per-l",
            "volume_fraction:millil-per-m3"
        )
    }
}
