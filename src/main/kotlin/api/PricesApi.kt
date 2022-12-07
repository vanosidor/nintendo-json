package api

import entities.Game
import entities.Price
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class PricesApi {
    companion object {
        //        TODO parse date if need

        @OptIn(ExperimentalSerializationApi::class)
        private fun fetchPricesForCountry(country: String, nsuids: List<String>) {
            val pricesHttpClient = HttpClient(OkHttp) {
                install(ContentNegotiation) {
                    json(
                        Json {
                            prettyPrint = true
                            ignoreUnknownKeys = true
                            isLenient = true
                            explicitNulls = false
                        },
                    )
                }
            }

//            TODO make logic
            val rowCount: Int = 50
            var start: Int = 0
            var result = arrayListOf<Price>();

            var chunk = nsuids.subList(start, rowCount)

            while (chunk.isNotEmpty()) {
                chunk.joinToString(",")

                start += rowCount

            }

            pricesHttpClient.close()
        }

    }
}
