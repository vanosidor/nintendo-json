package api

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

const val HK_STORE_URL = "https://www.nintendo.com.hk/data/json/switch_software.json"

class StoreApiHK {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        suspend fun fetchGames(): List<HkGame> {
            println("HK store fetch games started")

            val gamesResult = arrayListOf<HkGame>()

            val storeHttpClient = HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                            explicitNulls = false
                        },
                    )
                }
            }

            val response: HttpResponse = storeHttpClient.get(HK_STORE_URL)

            val responseStatusCode = response.status.value

            if (responseStatusCode != 200) {
                println("HK statusCode: $responseStatusCode")
            } else {
                println("HK store fetch games status = $responseStatusCode")
                val games: List<HkGame> = response.body()
                println("HK store games size total: ${games.size}")
                gamesResult.addAll(games)
            }

            storeHttpClient.close()

            println("HK store games fetched\n")

            return gamesResult
        }
    }
}

@Serializable
data class HkGame(
    val title: String,
    @SerialName("lang") val language: String, // "CN" or empty string
    val link: String, // link to store "https://store.nintendo.com.hk/70010000023476"
    @SerialName("product_code") val productCode: String, // "HACPATJFA"
    val price: String,  // 399
    val category: String,  // "模擬RPG" or "RPG"
)
