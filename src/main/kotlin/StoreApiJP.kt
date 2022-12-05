import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val JP_STORE_URL = "https://search.nintendo.jp/nintendo_soft/search.json"

class StoreApiJP {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        suspend fun fetchGames(): List<Game> {
            println("JP store fetch games started")

            val gamesResult = arrayListOf<Game>()

            val jpStoreClient = HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                            coerceInputValues = true
                            explicitNulls = false
                        },
                    )
                }
            }

            var page = 1
            val limit = 150

            while (true) {
                val httpResponse: HttpResponse = jpStoreClient.get(JP_STORE_URL) {
                    url {
                        parameters.apply {
                            append("limit", limit.toString())
                            append("page", page.toString())
                        }
                    }
                }

                val statusCode = httpResponse.status.value

                println("JP statusCode: $statusCode")

                if (statusCode != 200) {
                    break
                }

                val responseDto: JpStoreResponseDto = httpResponse.body()

                val newItems = responseDto.result.items

                if (newItems.isEmpty()) break

                gamesResult.addAll(newItems.map { Game.fromJpDto(it) })

                println("New items JP games size = ${newItems.size}")
                println("Result JP games size: ${gamesResult.size}")

//              TODO revert revert
                if (page >= 3) break

                page += 1
            }

            jpStoreClient.close()

            println("JP store games fetched\n")

            return gamesResult
        }
    }
}

@Serializable
data class JpStoreResponseDto(val result: JpStoreResultDto)

@Serializable
data class JpStoreResultDto(val items: List<JpGameDto>)

@Serializable
data class JpGameDto(
    val title: String,
    @SerialName("text") val description: String?,
    @SerialName("nsuid") val nsuid: String?,
    val hard: String?,
    val icode: String?,
    val url: String?,
    val player: List<String>?,  // ["1~2"]
    @SerialName("genre") val categories: List<String>?,  // ["アーケード","アクション","アドベンチャー","その他"]
    @SerialName("lang") val languages: List<String>?,   //  "lang": [ "ja", "pt_US", "en_US" ],

)