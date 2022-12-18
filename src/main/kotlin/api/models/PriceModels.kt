import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PricesResponseDto(val prices: List<PriceDto>)

@Serializable
data class PriceDto(
    @SerialName("title_id") val titleId: String,
    @SerialName("sales_status") val salesStatus: String,
    @SerialName("regular_price") val regularPrice: RegularPrice?,
    @SerialName("discount_price") val discountPrice: DiscountPrice?
)

@Serializable
data class RegularPrice(
    val amount: String, val currency: String, @SerialName("raw_value") val rawValue: Double?
)

@Serializable
data class DiscountPrice(
    val amount: String, val currency: String, @SerialName("raw_value") val rawValue: Double,
    @SerialName("start_datetime") val startDate: String,
    @SerialName("end_datetime") val endDate: String
)