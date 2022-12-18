import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EuStoreResponseRootDto(val response: EuStoreResponseDto)

@Serializable
data class EuStoreResponseDto(val numFound: Int, val docs: List<GameEU>)

@Serializable
data class GameEU(
    val title: String?,
    @SerialName("excerpt") val description: String?,
    @SerialName("nsuid_txt") val nsuid: List<String>?,
    @SerialName("product_code_txt") val productCode: List<String>?,
    val url: String?, // /DLC/-InfiniPrison-1800893.html
    @SerialName("language_availability") val languages: List<String>?, // ["english,french,german,italian,portuguese,spanish"]
    @SerialName("players_to") val players: Int = 0,
    @SerialName("game_categories_txt") val categories: List<String>?, //  ["Action", "Arcade", "Music", "Shooter"]
    @SerialName("image_url") val imageUrl: String?,
    @SerialName("image_url_h2x1_s") val imageUrl2x1: String?,
    @SerialName("type") val type: String // "DLC", "GAME"
)