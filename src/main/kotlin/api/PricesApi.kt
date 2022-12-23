package api

import PricesResponseDto
import entities.Price
import entities.StoreCountry
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

class PricesApi {
    companion object {
        private const val PRICE_API_URL = "https://api.ec.nintendo.com/v1/price"

        @OptIn(ExperimentalSerializationApi::class)
        suspend fun fetchPricesForCountry(country: StoreCountry, _nsuids: List<String>): List<Price> {
            val nsuids = _nsuids.filter { it.isNotEmpty() }
            println("Get prices for $country started")

            if (nsuids.isEmpty()) return emptyList()

            val httpClient = HttpClient(OkHttp) {
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
                install(HttpTimeout) {
                    connectTimeoutMillis = 10000
                }

                install(HttpRequestRetry) // 3 retries by default and exponential

//                install(Logging) {
//                    level = LogLevel.BODY
//                }
            }

            val rowCount = 50
            var start = 0
            val result = arrayListOf<Price>()

            while (start < nsuids.size) {
                val lastIndex = if (start + rowCount >= nsuids.size) nsuids.size else start + rowCount
                val chunk = nsuids
                    .subList(start, lastIndex)

                val ids = chunk.joinToString(",")
                println("nsuids joined params:")
                println(ids)

                // may cause ConnectTimeoutException
                val httpResponse: HttpResponse = httpClient.get(PRICE_API_URL) {
                    url {
                        parameters.apply {
                            append("country", country.value)
                            append("lang", "en")
                            append("ids", ids)
                        }
                    }
                }

                val statusCode = httpResponse.status.value

                println("Get prices $country: $statusCode")

                start += rowCount

                if (statusCode == 200) {
                    val pricesResponseDto: PricesResponseDto = httpResponse.body()

                    val prices = pricesResponseDto.prices.map {
                        Price.fromDto(it, country)
                    }

                    result.addAll(prices)
                }
            }

            httpClient.close()

            println("Get prices for $country completed\n")
            return result
        }
    }
}
