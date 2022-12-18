package entities


// TODO add popularity
data class GameMerged(
    val euGame: Game? = null,
    val naGame: Game? = null,
    val jpGame: Game? = null,
    val hkGame: Game? = null
) {
    val id: String?
        get() {
            if (euGame?.uniqueId != null) return euGame.uniqueId
            else if (naGame?.uniqueId != null) return naGame.uniqueId
            else if (jpGame?.uniqueId != null) return jpGame.uniqueId

            return null
        }

    val title: String?
        get() {
            if (hasEuGame) return euGame?.title
            else if (hasNaGame) return naGame?.title
            else if (hasJpGame) return jpGame?.title

            return null
        }
    private val hasNaGame: Boolean
        get() = naGame != null

    private val hasEuGame: Boolean
        get() = euGame != null

    private val hasJpGame: Boolean
        get() = jpGame != null


}
