import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GameNA(
    val title: String?,
    val description: String?,
    val url: String?, // not used, redirect to storeUrl
    val productCode: String?, // need get with extra data
    val nsuid: String?,
    val slug: String?,
    @SerialName("numOfPlayers") val players: String?, // "up to 8 players"
    val languages: List<String>?, // need get with extra data
    @SerialName("genres") val categories: List<String>?, // ["Platformer", "Action"]
    @SerialName("boxart") val image: String?, // image but not square like for EU store
    @SerialName("horizontalHeaderImage") val horizontalHeaderImage: String?,
) {
    companion object {
        const val NA_GAME_STORE_URL = "https://www.nintendo.com/store/products/"
    }

    val storeUrl: String
        get() = "$NA_GAME_STORE_URL$slug/"

}


@Serializable
data class PropsDto(val props: Props)

@Serializable
data class Props(val pageProps: PageProps)

@Serializable
data class PageProps(val product: Product)

@Serializable
data class Product(
    val productCode: String,
    val supportedLanguages: List<String> = listOf("English")
)

data class GameExtras(val languages: List<String>, val productCode: String)