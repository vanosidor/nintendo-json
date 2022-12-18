package api

import GameHK
import entities.Game
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class StoreApiHK {
    companion object {
        private const val HK_STORE_URL = "https://www.nintendo.com.hk/data/json/switch_software.json"

        @OptIn(ExperimentalSerializationApi::class)
        suspend fun fetchGames(): List<Game> {
            println("HK store fetch games started")

            val games = arrayListOf<Game>()

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
                val hkGames: List<GameHK> = response.body()

                games.addAll(hkGames.map { Game.fromHkDto(it) })
            }

            storeHttpClient.close()

            println("HK store games fetched\n")

            val result = games.filter { it.nsuid.isNotEmpty() }

            println("HK store games size total: ${result.size}")

            return result
        }
    }
}
