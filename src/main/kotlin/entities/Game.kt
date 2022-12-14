package entities

import GameEU
import GameExtras
import GameHK
import GameJP
import GameNA

data class Game(
    val title: String,
    val description: String = "",
    val nsuid: String = "",
    val players: Int,
    val productCode: String = "", // unique id to join across the regions
    val regionCode: RegionCode,
    val storeUrl: String,
    val imageUrl: String,
    val imageUrl2x1: String,
    val languages: List<String>,
    val categories: List<String>,
    val prices: List<Price>,
    val popularity: Int = 0,
    val type: GameType? = null
) {
    companion object Factory {
        fun fromEuDto(euGame: GameEU, popularity: Int = 0): Game {
            val languages: List<String> = euGame.languages?.firstOrNull()?.split(",") ?: emptyList()

            val type = when (euGame.type) {
                "GAME" -> GameType.GAME
                "DLC" -> GameType.DLC
                else -> null
            }

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
                prices = emptyList(),
                popularity = popularity,
                type = type
            )
        }

        fun fromJpDto(jpGame: GameJP): Game {
            var productCode = ""

            if (jpGame.hard != null && jpGame.icode != null) {
                productCode = "${jpGame.hard.subSequence(2, jpGame.hard.length)}${jpGame.icode}"
            }

            val playersString: String = jpGame.player?.firstOrNull() ?: "0"

            val players = playersString.split("~").max().toInt()

//            TODO fix 70010000012085sora
//            TODO fix 70010000012085K
            val nsuid = jpGame.nsuid?.replace("_", "") ?: "" // contains invalid nsuid 70010000039949_2

            val type = when (jpGame.sform) {
                "DLC", "DL_DLC", "dl_only" -> GameType.DLC
                "HAC_DOWNLOADABLE", "HAC_DL" -> GameType.GAME
                else -> null
            }

            return Game(
                title = jpGame.title ?: "",
                description = jpGame.description ?: "",
                nsuid = nsuid,
                productCode = productCode,
                players = players,
                storeUrl = jpGame.storeUrl ?: "",
                regionCode = RegionCode.JP,
                imageUrl = "",
                imageUrl2x1 = "",
                languages = jpGame.languages ?: emptyList(),
                categories = jpGame.categories ?: emptyList(),
                prices = emptyList(),
                type = type
            )
        }

        fun fromNaDto(naGame: GameNA, gameExtras: GameExtras? = null): Game {
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

        fun fromHkDto(hkGame: GameHK): Game {
            val matcher = "\\d{1,14}".toRegex()
            val nsuid = matcher.find(hkGame.link)?.value ?: ""

            return Game(
                title = hkGame.title,
                nsuid = nsuid,
                productCode = hkGame.productCode,
                players = 0,
                storeUrl = hkGame.link,
                regionCode = RegionCode.HK,
                imageUrl = "",
                imageUrl2x1 = "",
                languages = listOf(hkGame.language),
                categories = emptyList(),
                prices = emptyList(),
            )
        }
    }

    // Not unique, has some items with same uniqueId
    val uniqueId: String?
        get() {
            if (productCode.isEmpty() || productCode == "WUPWUP") return null

            return try {
                when (regionCode) {
                    RegionCode.JP -> productCode.substring(3..6)
                    RegionCode.EU, RegionCode.NA -> productCode.substring(4..7)
                    RegionCode.HK -> null
                }
            } catch (e: StringIndexOutOfBoundsException) {
                println("Get uniqueId invalid product code string length: $productCode")
                null
            }
        }
}

enum class GameType {
    GAME, DLC
}