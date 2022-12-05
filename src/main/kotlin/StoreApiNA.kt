import com.algolia.search.client.ClientSearch
import com.algolia.search.dsl.requestOptions
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.algolia.search.model.search.Query
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

const val APPLICATION_ID = "U3B6GR4UA3"
const val API_KEY = "c4da8be7fd29f0f5bfa42920b0a99dc7"
const val INDEX = "ncom_game_en_us"
const val PLATFORM_CODE = "7001"

class StoreApiNA {
    companion object {
        @OptIn(ExperimentalSerializationApi::class)
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

            val json = Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                explicitNulls = false
            }

            var current = 0
            var emptyPages = 0

            while (true) {
                println("Current page = $current")
                val currentFormatted: String = "%07d".format(current)
                val responseSearch =
                    index.search(Query("$PLATFORM_CODE$currentFormatted"), requestOptions = requestOptions)

                val games = responseSearch.hits.map {
                    json.decodeFromJsonElement<NaGameDto>(it.json)
                }

                if (games.isEmpty()) {
                    emptyPages++
                } else {
                    gamesResult.addAll(games.map { Game.fromNaDto(it) })
                }

                println("New items NA games size = ${games.size}")
                println("Result NA games size: ${gamesResult.size}")

//                TODO revert
                if (gamesResult.size >= 50) break

                if (emptyPages == 5) break

                current += 1
            }

            client.close()

            println("NA store games fetched\n")

            return gamesResult
        }
    }
}

@Serializable
data class NaGameDto(
    val title: String?,
    val description: String?,
    val url: String?,
    val nsuid: String?,
    val slug: String?,
    @SerialName("numOfPlayers") val players: String?, // "up to 8 players"
    val languages: List<String>?, // not see it value is present
    @SerialName("genres") val categories: List<String>?, // ["Platformer", "Action"]
    @SerialName("boxart") val image: String?, // image but not square like for EU store
    @SerialName("horizontalHeaderImage") val horizontalHeaderImage: String?,
)