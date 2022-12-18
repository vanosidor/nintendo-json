import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameHK(
    val title: String,
    @SerialName("lang") val language: String, // "CN" or empty string
    val link: String, // link to store "https://store.nintendo.com.hk/70010000023476"
    @SerialName("product_code") val productCode: String, // "HACPATJFA"
    val price: String,  // 399
    val category: String,  // "模擬RPG" or "RPG"
)
