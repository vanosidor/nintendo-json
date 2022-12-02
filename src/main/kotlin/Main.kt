import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import io.ktor.client.engine.okhttp.*

// https://github.com/fedecalendino/nintendeals
// https://github.com/fedecalendino/nintendeals/blob/main/nintendeals/commons/classes/eshops.py

// https://gist.github.com/GabrielMMelo/b88f3d4d68a8883f17f1c3300e58a23f

// too old api
// https://github.com/briansamuel/eshop-game


const val EU_STORE_URL = "https://search.nintendo-europe.com/en/select"
const val HK_STORE_URL = "https://www.nintendo.com.hk/data/json/switch_software.json"
const val JP_STORE_URL = "https://search.nintendo.jp/nintendo_soft/search.json"
const val JP_STORE_URL_XML = "https://www.nintendo.co.jp/data/software/xml/switch.xml"

@Serializable
data class EuStoreResponse(val response: EuStoreResponseBody)

@Serializable
data class EuStoreResponseBody(val numFound: Int, val docs: List<EuGame>)

@Serializable
data class EuGame(
    var title: String = "",
    val url: String = "",
    @SerialName("image_url") val imageUrl: String = "",
    val regionCode: String = "EU"
)

@Serializable
data class HkGame(val title: String)

@Serializable
data class JpStoreResponse(val result: JpStoreResult)

@Serializable
data class JpStoreResult(val items: List<JpGame>)

@Serializable
data class JpGame(val id: String, val title: String = "", val url: String = "", val nsuid: String = "")

suspend fun main() {
    val startTime = System.currentTimeMillis()
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

    println("EU store fetch games started")

    var start = 0
    val rows = 200
    val euGamesResult = arrayListOf<EuGame>()
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

        val euResponseModel: EuStoreResponse = euResponse.body()

        val newItems = euResponseModel.response.docs

        if (newItems.isEmpty()) break

        euGamesResult.addAll(newItems)

        println("New items eu games size = ${newItems.size}")
        println("Result eu games size: ${euGamesResult.size}")

        start += rows
    }

    println("HK store fetch games started")

    val hkStoreClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                },
            )
        }
    }

    val hkResponse: HttpResponse = hkStoreClient.get(HK_STORE_URL)

    val hkResponseStatusCode = hkResponse.status.value

    val hkGamesResult = arrayListOf<HkGame>()
    if (hkResponseStatusCode != 200) {
        println("HK statusCode: $hkResponseStatusCode")
    } else {
        println("HK store fetch games status = $hkResponseStatusCode")
        val hkGames: List<HkGame> = hkResponse.body()
        println("HK store games size total: ${hkGames.size}")
        hkGamesResult.addAll(hkGames)
    }

    hkStoreClient.close()

    println("JP store fetch games started")

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
    val jpGamesResult = arrayListOf<JpGame>()
    val limit = 150
    while (true) {
        val jpResponse: HttpResponse = jpStoreClient.get(JP_STORE_URL) {
            url {
                parameters.apply {
                    append("limit", limit.toString())
                    append("page", page.toString())
                }
            }
        }

        val statusCode = hkResponse.status.value

        println("JP statusCode: $statusCode")

        if (statusCode != 200) {
            break
        }

        val jpStoreResponseDeserialized: JpStoreResponse = jpResponse.body()

        val newItems = jpStoreResponseDeserialized.result.items

        if (newItems.isEmpty()) break

        jpGamesResult.addAll(newItems)

        println("New items JP games size = ${newItems.size}")
        println("Result JP games size: ${jpGamesResult.size}")

        page += 1
    }

    jpStoreClient.close()

    println("Get all items completed successfully")
    println("Total time elapsed: ${System.currentTimeMillis() - startTime} ms")
}