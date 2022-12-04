import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val JP_STORE_URL = "https://search.nintendo.jp/nintendo_soft/search.json"

class StoreApiJP {
    companion object {
        suspend fun fetchGames(): List<JpGameDto> {
            println("JP store fetch games started")

            val gamesResult = arrayListOf<JpGameDto>()

            val jpStoreClient = HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                            coerceInputValues = true
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

                gamesResult.addAll(newItems)

                println("New items JP games size = ${newItems.size}")
                println("Result JP games size: ${gamesResult.size}")

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
    val id: String,
    val title: String = "",
    val url: String = "",
    val nsuid: String = "",
)