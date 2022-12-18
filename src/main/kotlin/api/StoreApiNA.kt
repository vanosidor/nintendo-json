package api

import GameExtras
import GameNA
import PropsDto
import com.algolia.search.client.ClientSearch
import com.algolia.search.dsl.requestOptions
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.Query
import entities.Game
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class StoreApiNA {

    companion object {

        // TODO create new APP_ID and api.API_KEY
        private const val APPLICATION_ID = "U3B6GR4UA3"
        private const val API_KEY = "c4da8be7fd29f0f5bfa42920b0a99dc7"
        private const val INDEX = "ncom_game_en_us"
        private const val PLATFORM_CODE = "7001"

        @OptIn(ExperimentalSerializationApi::class)
        val json: Json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            explicitNulls = false
        }

        suspend fun fetchGames(): List<Game> {
            println("NA store fetch games started")

            val gamesResult = arrayListOf<Game>()

            val client = ClientSearch(
                applicationID = ApplicationID(APPLICATION_ID),
                apiKey = APIKey(API_KEY)
            )

            val index = client.initIndex(indexName = IndexName(INDEX))

            val requestOptions = requestOptions {
                header("allowTyposOnNumericTokens", false)
                header("queryType", "prefixAll")
                header("restrictSearchableAttributes", listOf("nsuid"))
                header("facetFilters", listOf("platform:Nintendo Switch"))
                header("hitsPerPage", 500)
            }

            var current = 0
            var emptyPages = 0
            var successScraps = 0

            while (true) {
                println("Current page = $current")
                val currentFormatted: String = "%07d".format(current)
                val responseSearch =
                    index.search(Query("$PLATFORM_CODE$currentFormatted"), requestOptions = requestOptions)

                val games = responseSearch.hits.map {
                    json.decodeFromJsonElement<GameNA>(it.json)
                }

                if (games.isEmpty()) {
                    emptyPages++
                } else {
                    for (gameDto in games) {
                        val extras = extractGameExtras(gameDto.storeUrl)

                        if (extras != null) {
                            successScraps++
                        }

                        val game = Game.fromNaDto(gameDto, extras)
                        gamesResult.add(game)
                    }
                }

                println("New items NA games size = ${games.size}")
                println("Result NA games size: ${gamesResult.size}")

                if (emptyPages == 5) break

                current += 1
            }

            client.close()

            println("Successful scraps $successScraps, total items ${gamesResult.size}")
            println("NA store games fetched\n")


            return gamesResult
        }

        private fun extractGameExtras(storeUrl: String): GameExtras? {
            val doc: Document
            return try {
                doc = Jsoup.connect(storeUrl).get()
                val data = doc.select("#__NEXT_DATA__").first()?.data() ?: ""
                val dataDecoded = json.decodeFromString<PropsDto>(data)
                val product = dataDecoded.props.pageProps.product

                GameExtras(productCode = product.productCode, languages = product.supportedLanguages)

                // val h3 = doc.select("h3").find { it.text() == "Supported languages" }
            } catch (e: Exception) {
                println("Exception while scrapping NOA store game $storeUrl")
                println(e)
                null
            }
        }
    }
}

