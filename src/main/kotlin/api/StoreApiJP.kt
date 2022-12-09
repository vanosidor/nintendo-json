package api

import entities.Game
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
const val JP_PRODUCT_URL = "https://store-jp.nintendo.com/list/software/"

class StoreApiJP {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        suspend fun fetchGames(maxPages: Int = Integer.MAX_VALUE): List<Game> {
            println("JP store fetch games started")

            val gamesResult = arrayListOf<Game>()

            val storeHttpClient = HttpClient(OkHttp) {
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
                val httpResponse: HttpResponse = storeHttpClient.get(JP_STORE_URL) {
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

                if (page >= maxPages) break

                page += 1
            }

            storeHttpClient.close()

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
    val player: List<String>?,  // ["1~2"]
    @SerialName("genre") val categories: List<String>?,  // ["アーケード","アクション","アドベンチャー","その他"]
    @SerialName("lang") val languages: List<String>?,   //  "lang": [ "ja", "pt_US", "en_US" ],
) {
    //    https://store-jp.nintendo.com/list/software/70010000057801.html
    val storeUrl: String?
        get() {
            return if (nsuid == null) null
            else "$JP_PRODUCT_URL${nsuid}.html"
        }


}