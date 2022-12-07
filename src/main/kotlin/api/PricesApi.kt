package api

import entities.Price
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

// TODO parse date if need
class PricesApi {
    companion object {
        private const val PRICE_API_URL = "https://api.ec.nintendo.com/v1/price"

        @OptIn(ExperimentalSerializationApi::class)
        suspend fun fetchPricesForCountry(country: String, nsuids: List<String>): List<Price> {

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
            }

            val rowCount = 50
            var start = 0
            val result = arrayListOf<Price>()

            while (start < nsuids.size) {
                val lastIndex = if (start + rowCount >= nsuids.size) nsuids.size else start + rowCount
                val chunk = nsuids.subList(start, lastIndex)

                val ids = chunk.joinToString(",")
                println("nsuids joined params:")
                println(ids)

                val httpResponse: HttpResponse = httpClient.get(PRICE_API_URL) {
                    url {
                        parameters.apply {
                            append("country", country)
                            append("lang", "en")
                            append("ids", ids)
                        }
                    }
                }

                val statusCode = httpResponse.status.value

                println("Get prices for $country: $statusCode")

                start += rowCount

                if (statusCode != 200) {
                    break
                }

                val pricesResponseDto: PricesResponseDto = httpResponse.body()

                val prices = pricesResponseDto.prices.map {
//                    TODO move to from dto
//                     TODO fix serialization
                    Price(
                        value = it.regularPrice.rawValue,
                        currency = it.regularPrice.currency,
                        saleValue = null
                    )
                }

                result.addAll(prices)
            }

            httpClient.close()

            return result
        }
    }
}

@Serializable
data class PricesResponseDto(val prices: List<PriceDto>)

@Serializable
data class PriceDto(
    @SerialName("title_id") val titleId: String,
    @SerialName("sales_status") val salesStatus: String,
    @SerialName("regular_price") val regularPrice: RegularPrice
)

@Serializable
data class RegularPrice(
    val amount: String, val currency: String, @SerialName("raw_value") val rawValue: Double
)
