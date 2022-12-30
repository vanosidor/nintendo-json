package api

import EuStoreResponseRootDto
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
import kotlinx.serialization.json.Json


class StoreApiEU {
    enum class EuStore(val url: String) {
        Ru(EU_RU_GAME_SEARCH_URL),
        En(EU_EN_GAME_SEARCH_URL)
    }

    companion object {
        private const val EU_RU_GAME_SEARCH_URL = "https://search.nintendo-europe.com/ru/select"
        private const val EU_EN_GAME_SEARCH_URL = "https://search.nintendo-europe.com/en/select"

        private const val EU_RU_GAME_LINK_URL = "https://www.nintendo.ru"
        private const val EU_EN_GAME_LINK_URL = "https://www.nintendo.co.uk"

        suspend fun fetchGames(): List<Game> {
            val gamesRu = fetchEuStoreGames(EuStore.Ru)
            val gamesEn = fetchEuStoreGames(EuStore.En)

            // Important: valid game url and some other fields make after merge operation
            return mergeEnRuGames(gamesEn, gamesRu)
        }

        private fun mergeEnRuGames(enGames: List<Game>, ruGames: List<Game>): List<Game> {
            val result = arrayListOf<Game>()
            val ruGamesCopy = ruGames.toMutableList()

            for (enGame in enGames) {
                val ruGame = ruGamesCopy.firstOrNull { it.nsuid == enGame.nsuid }

                if (ruGame != null) {
                    val storeUrl = getStoreUrl(ruGame.storeUrl, EuStore.Ru)
                    val gameModified =
                        enGame.copy(title = ruGame.title, storeUrl = storeUrl, description = ruGame.description)
                    result.add(gameModified)

                    ruGamesCopy.remove(ruGame)
                } else {
                    val storeUrl = getStoreUrl(enGame.storeUrl, EuStore.En)
                    val gameModified = enGame.copy(storeUrl = storeUrl)
                    result.add(gameModified)
                }
            }

            result.addAll(ruGamesCopy)

            return result
        }

        private fun getStoreUrl(slug: String, store: EuStore): String {
            return when (store) {
                EuStore.Ru -> "$EU_RU_GAME_LINK_URL$slug"
                EuStore.En -> "$EU_EN_GAME_LINK_URL$slug"
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
        private suspend fun fetchEuStoreGames(store: EuStore): List<Game> {
            println("EU store fetch games $store started")

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
            val rows = 99999

            while (true) {
                val httpResponse: HttpResponse = httpClient.get(store.url) {
                    url {
                        parameters.apply {
                            append("q", "*")
                            append("sort", "popularity asc")
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

                val newItemsFiltered = newItems.filter { it.type == "GAME" || it.type == "DLC" }

                gamesResult.addAll(newItemsFiltered.mapIndexed { index, game ->
                    Game.fromEuDto(
                        game,
                        popularity = newItemsFiltered.size - index
                    )
                })

                println("New items eu games size = ${newItemsFiltered.size}")
                println("Result eu games size: ${gamesResult.size}")

                start += rows
            }

            httpClient.close()

            println("EU $store store games fetched\n")

            return gamesResult
        }
    }
}


