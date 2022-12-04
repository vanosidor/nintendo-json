data class Game(
    val title: String,
    val description: String,
    val nsuid: String,
    val productCode: String,
    val regionCode: RegionCode
) {

}

enum class RegionCode {
    US, EU, JP, HK
}