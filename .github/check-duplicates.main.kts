import com.cognite.units.UnitService
import com.cognite.units.Conversion
import com.cognite.units.TypedUnit

fun main() {
    val service = UnitService.service
    val result: Map<String, Map<Conversion, List<TypedUnit>>> = service.getDuplicateConversions(service.getUnits())  // Assume someMethod is a method you want to invoke
    result.keys.forEach { outerKey ->
        println("Quantity with duplicates: $outerKey")
        result[outerKey]?.keys?.forEach { innerKey: Conversion ->
        }
    }
}

main()
