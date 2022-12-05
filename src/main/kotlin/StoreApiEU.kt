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

const val EU_STORE_URL = "https://search.nintendo-europe.com/ru/select"

class StoreApiEU {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        suspend fun fetchGames(): List<Game> {
            println("EU store fetch games started")

            val gamesResult = arrayListOf<Game>()

            val euStoreClient = HttpClient(OkHttp) {
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
                val euResponse: HttpResponse = euStoreClient.get(EU_STORE_URL) {
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

                val statusCode = euResponse.status.value

                println("EU statusCode: $statusCode")

                if (statusCode != 200) {
                    break
                }

                val responseDto: EuStoreResponseRootDto = euResponse.body()

                val newItems = responseDto.response.docs

                if (newItems.isEmpty()) break

                gamesResult.addAll(newItems.map { Game.fromEuDto(it) })

                println("New items eu games size = ${newItems.size}")
                println("Result eu games size: ${gamesResult.size}")

//                todo revert
                if (start >= 200) break

                start += rows
            }

            euStoreClient.close()

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
    @SerialName("image_url_h2x1_s") val imageUrl2x1: String?

//    TODO make prices
)