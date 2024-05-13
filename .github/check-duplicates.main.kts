import com.cognite.units.UnitService

fun main() {
    val service = UnitService.service
    val result = service.checkForDuplicateConversions()  // Assume someMethod is a method you want to invoke
    println("duplicate conversion: $result")
}

main()
