import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val EU_STORE_URL = "https://search.nintendo-europe.com/en/select"

class StoreApiEU {
    companion object {
        suspend fun fetchGames(): List<EuGameDto> {
            println("EU store fetch games started")

            val gamesResult = arrayListOf<EuGameDto>()

            val euStoreClient = HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                            coerceInputValues = true
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

                gamesResult.addAll(newItems)

                println("New items eu games size = ${newItems.size}")
                println("Result eu games size: ${gamesResult.size}")

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
    var title: String = "",
    val url: String = "",
    @SerialName("image_url") val imageUrl: String = "",
//    val regionCode: RegionCode = RegionCode.EU,
    val nsuid: String = ""
)