import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

const val HK_STORE_URL = "https://www.nintendo.com.hk/data/json/switch_software.json"

class StoreApiHK {
    companion object {
        suspend fun fetchGames(): List<HkGame> {
            println("HK store fetch games started")

            val gamesResult = arrayListOf<HkGame>()

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

            val response: HttpResponse = hkStoreClient.get(HK_STORE_URL)

            val responseStatusCode = response.status.value

            if (responseStatusCode != 200) {
                println("HK statusCode: $responseStatusCode")
            } else {
                println("HK store fetch games status = $responseStatusCode")
                val games: List<HkGame> = response.body()
                println("HK store games size total: ${games.size}")
                gamesResult.addAll(games)
            }

            hkStoreClient.close()

            println("HK store games fetched\n")

            return gamesResult
        }
    }
}

@Serializable
data class HkGame(val title: String)
