package entities

import api.EuGameDto
import api.GameExtras
import api.JpGameDto
import api.NaGameDto
import java.lang.NumberFormatException

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
    val categories: List<String>,
    val prices: List<Price>
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
                storeUrl = euGame.url ?: "",
                regionCode = RegionCode.EU,
                imageUrl = euGame.imageUrl ?: "",
                imageUrl2x1 = euGame.imageUrl2x1 ?: "",
                languages = languages,
                categories = euGame.categories ?: emptyList(),
                prices = emptyList()
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
                storeUrl = jpGame.storeUrl ?: "",
                regionCode = RegionCode.JP,
                imageUrl = "",
                imageUrl2x1 = "",
                languages = jpGame.languages ?: emptyList(),
                categories = jpGame.categories ?: emptyList(),
                prices = emptyList()
            )
        }

        fun fromNaDto(naGame: NaGameDto, gameExtras: GameExtras? = null): Game {
            val players = try {
                Regex("\\D*").replace(naGame.players.toString(), "").toInt()
            } catch (e: NumberFormatException) {
                println(e)
                0
            }

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
                prices = emptyList()
            )
        }

//       TODO make factory methods for HK
    }

    // Not unique, has some items with same uniqueId
    val uniqueId: String?
        get() {
            if (productCode.isEmpty()) return null

            return when (regionCode) {
                RegionCode.JP -> productCode.substring(3..6)
                RegionCode.EU, RegionCode.NA -> productCode.substring(4..7)
                RegionCode.HK -> null
            }
        }
}

