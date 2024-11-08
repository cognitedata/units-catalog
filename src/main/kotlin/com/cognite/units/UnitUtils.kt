package com.cognite.units

import java.net.URI

object UnitUtils {
    private fun sanitizeIdentifier(identifier: String): String {
        // remove all special characters except - and _
        return identifier.lowercase().replace(Regex("[^a-z0-9_-]"), "_")
    }

    private fun generateExpectedExternalId(unit: TypedUnit): String {
        val sanitizedQuantity = sanitizeIdentifier(unit.quantity)
        val sanitizedName = sanitizeIdentifier(unit.name)
        return "$sanitizedQuantity:$sanitizedName"
    }

    fun generatedExpectedSourceReference(unit: TypedUnit): String? {
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

    fun validateUnit(unit: TypedUnit) {
        // ExternalId Format: All unit `externalIds` must follow the pattern `{quantity}:{unit}`, where both
        // `quantity` and `unit` are in snake_case.
        assert(unit.externalId == generateExpectedExternalId(unit)) {
            "Invalid externalId ${unit.externalId} for unit ${unit.name} (${unit.quantity})"
        }

        // if source is qudt.org, reference should be in the format https://qudt.org/vocab/unit/{unit.name}
        if (unit.source == "qudt.org") {
            assert(unit.sourceReference == generatedExpectedSourceReference(unit)) {
                "Invalid sourceReference ${unit.sourceReference} for unit ${unit.name} (${unit.quantity})"
            }
        }

        return
    }
}
