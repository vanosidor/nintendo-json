package api

import entities.Game
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json


// TODO prices
// TODO merge En and Ru result

class StoreApiEU {
    companion object {
        private const val EU_RU_STORE_URL = "https://search.nintendo-europe.com/ru/select"
        const val EU_EN_STORE_URL = "https://search.nintendo-europe.com/en/select"

        @OptIn(ExperimentalSerializationApi::class)
        suspend fun fetchGames(): List<Game> {
            println("EU store fetch games started")

            val gamesResult = arrayListOf<Game>()

            val httpClient = HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            ignoreUnknownKeys = true
                            isLenient = true
                            coerceInputValues = true
                            explicitNulls = false
                        },
                        contentType = ContentType.Application.JavaScript,
                    )
                }
            }

            var start = 0
            val rows = 200

            while (true) {
                val httpResponse: HttpResponse = httpClient.get(EU_RU_STORE_URL) {
                    url {
                        parameters.apply {
                            append("q", "*")
                            append("sort", "sorting_title asc")
                            append("wt", "json")
                            append("rows", rows.toString())
                            append("start", start.toString())
                        }
                    }
                }

                val statusCode = httpResponse.status.value

                println("EU statusCode: $statusCode")

                if (statusCode != 200) {
                    break
                }

                val responseDto: EuStoreResponseRootDto = httpResponse.body()

                val newItems = responseDto.response.docs

                if (newItems.isEmpty()) break

                val newItemsFiltered = newItems.filter { it.type == "GAME" }

                gamesResult.addAll(newItemsFiltered.map { Game.fromEuDto(it) })

                println("New items eu games size = ${newItemsFiltered.size}")
                println("Result eu games size: ${gamesResult.size}")

//                todo revert
                if (start >= 200) break

                start += rows
            }

            httpClient.close()

            println("EU store games fetched\n")

            return gamesResult
        }
    }
}

@Serializable
data class EuStoreResponseRootDto(val response: EuStoreResponseDto)

@Serializable
data class EuStoreResponseDto(val numFound: Int, val docs: List<EuGameDto>)

@Serializable
data class EuGameDto(
    val title: String?,
    @SerialName("excerpt") val description: String?,
    @SerialName("nsuid_txt") val nsuid: List<String>?,
    @SerialName("product_code_txt") val productCode: List<String>?,
    val url: String?, // /DLC/-InfiniPrison-1800893.html
    @SerialName("language_availability") val languages: List<String>?, // ["english,french,german,italian,portuguese,spanish"]
    @SerialName("players_to") val players: Int = 0,
    @SerialName("game_categories_txt") val categories: List<String>?, //  ["Action", "Arcade", "Music", "Shooter"]
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("image_url_h2x1_s") val imageUrl2x1: String?,
    @SerialName("type") val type: String // "DLC", "GAME"

//    TODO make store link
//  "https://search.nintendo-europe.com/en/select"
//   https://www.nintendo.co.uk/Games/Nintendo-Switch-download-software/-cat-1952107.html

//    "https://search.nintendo-europe.com/ru/select"
//     https://www.nintendo.ru + /-/-Nintendo-Switch/Mario-Rabbids--1986931.html

)