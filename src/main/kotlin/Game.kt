data class Game(
    val title: String,
    val description: String,
    val nsuid: String,
    val players: Int,
    val productCode: String, // unique id to join across the regions
    val regionCode: RegionCode,
    val storeUrl: String,
    val imageUrl: String,
    val imageUrl2x1: String,
    val languages: List<String>,
    val categories: List<String>

) {
    companion object Factory {

        fun fromEuDto(euGame: EuGameDto): Game {
            val languages: List<String> = euGame.languages?.firstOrNull()?.split(",") ?: emptyList()

            return Game(
                title = euGame.title ?: "",
                description = euGame.description ?: "",
                nsuid = euGame.nsuid?.firstOrNull() ?: "",
                productCode = euGame.productCode?.firstOrNull() ?: "",
                players = euGame.players,
//              TODO generate url to store
                storeUrl = euGame.url ?: "",
                regionCode = RegionCode.EU,
                imageUrl = euGame.imageUrl ?: "",
                imageUrl2x1 = euGame.imageUrl2x1 ?: "",
                languages = languages,
                categories = euGame.categories ?: emptyList()
            )
        }

        fun fromJpDto(jpGame: JpGameDto): Game {
            var productCode = ""

            if (jpGame.hard != null && jpGame.icode != null) {
                productCode = "${jpGame.hard.subSequence(2, jpGame.hard.length)}${jpGame.icode}"
            }

            val playersString: String = jpGame.player?.firstOrNull() ?: "0"

            val players = playersString.split("~").max().toInt()

            return Game(
                title = jpGame.title,
                description = jpGame.description ?: "",
                nsuid = jpGame.nsuid ?: "",
                productCode = productCode,
                players = players,
//              TODO generate store url
                storeUrl = jpGame.icode ?: "",
                regionCode = RegionCode.JP,
                imageUrl = "",
                imageUrl2x1 = "",
                languages = jpGame.languages ?: emptyList(),
                categories = jpGame.categories ?: emptyList(),
            )
        }

        fun fromNaDto(naGame: NaGameDto, gameExtras: GameExtras? = null): Game {
            val players = Regex("[^\\d]*").replace(naGame.players.toString(), "").toInt()
            val languages = gameExtras?.languages ?: emptyList()
            val productCode = gameExtras?.productCode ?: ""

            return Game(
                title = naGame.title ?: "",
                description = naGame.description ?: "",
                nsuid = naGame.nsuid ?: "",
                productCode = productCode,
                players = players,
                storeUrl = naGame.storeUrl,
                regionCode = RegionCode.NA,
                imageUrl = naGame.image ?: "",
                imageUrl2x1 = "",
                languages = languages,
                categories = naGame.categories ?: emptyList(),
            )
        }

//        TODO make factory methods for HK
    }
}

enum class RegionCode {
    NA, EU, JP, HK
}