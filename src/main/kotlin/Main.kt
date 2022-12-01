import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.ContentType.Application.JavaScript
import io.ktor.http.ContentType.Application.Json
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

// Links:
// https://gist.github.com/GabrielMMelo/b88f3d4d68a8883f17f1c3300e58a23f
// https://github.com/briansamuel/eshop-game

// https://github.com/fedecalendino/nintendeals
// https://github.com/fedecalendino/nintendeals/blob/main/nintendeals/commons/classes/eshops.py


@Serializable
data class StoreEuResponse(val numFound: Int, val docs: List<Game>)

//@Serializable
//data class StoreEuResponse(val url: String, val type: String, val title: String)

@Serializable
data class Game(val url: String, val type: String, val title: String)

const val EU_STORE_URL =
    "https://search.nintendo-europe.com/en/select?q=*&sort=sorting_title%20asc&wt=json&rows=999&start=0"

const val HK_STORE_URL = "https://www.nintendo.com.hk/data/json/switch_software.json"
const val JP_STORE_URL = "https://www.nintendo.co.jp/data/software/xml/switch.xml"

suspend fun main(args: Array<String>) {
    println("Starting...")

    val client = HttpClient(CIO) {

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    val response: HttpResponse =
        client.get(EU_STORE_URL) {
            headers {
                append(HttpHeaders.Accept, Json)
            }
        }

    val games: StoreEuResponse = response.body()
    println(response.status)
    client.close()
}