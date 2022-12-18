package entities

import PriceDto
import java.time.Instant
import java.util.Date

data class Price(
    val nsuid: String,
    val country: StoreCountry,
    val value: Double?,
    val currency: String?,
    val saleValue: Double?,
    val saleStartDate: Date?,
    val saleEndDate: Date?,
) {
    companion object {
        fun fromDto(priceDto: PriceDto, country: StoreCountry): Price {
            val nsuid = priceDto.titleId
            val currency = priceDto.regularPrice?.currency
            val value = priceDto.regularPrice?.rawValue
            val saleValue = priceDto.discountPrice?.rawValue
            val startDate = parseDate(priceDto.discountPrice?.startDate)
            val endDate = parseDate(priceDto.discountPrice?.endDate)

            return Price(
                nsuid = nsuid,
                currency = currency,
                value = value,
                saleValue = saleValue,
                saleStartDate = startDate,
                saleEndDate = endDate,
                country = country
            )
        }

        private fun parseDate(dateStr: String?): Date? { // dateStr: "2022-11-17T14:00:00Z"
            if (dateStr == null) return null

            return try {
                val instant = Instant.parse(dateStr)
                Date.from(instant)
            } catch (e: Exception) {
                println("Error parsing $dateStr")
                null
            }
        }
    }
}