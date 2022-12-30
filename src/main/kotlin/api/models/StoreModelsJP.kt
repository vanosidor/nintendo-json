import api.StoreApiJP.Companion.JP_PRODUCT_URL
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JpStoreResponseDto(val result: JpStoreResultDto)

@Serializable
data class JpStoreResultDto(val items: List<GameJP>)

@Serializable
data class GameJP(
    val title: String?,
    @SerialName("text") val description: String?,
    @SerialName("nsuid") val nsuid: String?,
    val hard: String?,
    val icode: String?,
    val player: List<String>?,  // ["1~2"]
    val sform: String?,
    @SerialName("genre") val categories: List<String>?,  // ["アーケード","アクション","アドベンチャー","その他"]
    @SerialName("lang") val languages: List<String>?,   //  "lang": [ "ja", "pt_US", "en_US" ],
) {
    //    https://store-jp.nintendo.com/list/software/70010000057801.html
    val storeUrl: String?
        get() {
            return if (nsuid == null) null
            else "$JP_PRODUCT_URL${nsuid}.html"
        }
}