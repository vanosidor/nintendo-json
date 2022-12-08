import api.PricesApi
import api.StoreApiEU
import entities.Game
import entities.GameMerged

//TODO move to readme
// https://github.com/fedecalendino/nintendeals
// https://github.com/fedecalendino/nintendeals/blob/main/nintendeals/commons/classes/eshops.py

// https://gist.github.com/GabrielMMelo/b88f3d4d68a8883f17f1c3300e58a23f

// too old api
// https://github.com/briansamuel/eshop-game


suspend fun main() {
    val startTime = System.currentTimeMillis()

    //    TODO merge prices into model
    val euGames = StoreApiEU.fetchGames()
    val euNsuids = euGames.map { it.nsuid }
    val pricesRu = PricesApi.fetchPricesForCountry("RU", euNsuids)
    val pricesGB = PricesApi.fetchPricesForCountry("GB", euNsuids)


    //    TODO merge into jp games model
    val jpGames = api.StoreApiJP.fetchGames()
    val jpNsuids = jpGames.map { it.nsuid }
    val pricesJP = PricesApi.fetchPricesForCountry("JP", jpNsuids)

    // TODO merge prices
    val naGames = api.StoreApiNA.fetchGames()
    val naNsuids = naGames.map { it.nsuid }
    val pricesUS = PricesApi.fetchPricesForCountry("US", naNsuids)

    //    val hkGames = api.StoreApiHK.fetchGames()

//    TODO merge all games
    val mergedGames = mergeGames(euGames, jpGames, naGames)

//    TODO print to file
    println("All tasks completed: elapsed time = ${System.currentTimeMillis() - startTime}ms, games count = ${mergedGames.size}")
}

private fun mergeGames(euGames: List<Game>, jpGames: List<Game>, naGames: List<Game>): List<GameMerged> {
    val startTime = System.currentTimeMillis()

    val result = arrayListOf<GameMerged>()
    val naGamesCopy = naGames.toMutableList()
    val jpGamesCopy = jpGames.toMutableList()

    for (euGame in euGames) {
        val euGameId = euGame.uniqueId
        if (euGameId != null) {
            val jpGame = jpGamesCopy.firstOrNull { it.uniqueId == euGameId }
            val naGame = naGamesCopy.firstOrNull { it.uniqueId == euGameId }

            jpGamesCopy.remove(jpGame)
            naGamesCopy.remove(naGame)

            result.add(GameMerged(naGame, euGame, jpGame))
        } else {
            result.add(GameMerged(euGame = euGame, jpGame = null, naGame = null))
        }
    }

    for (naGame in naGamesCopy) {
        val naGameId = naGame.uniqueId

        if (naGameId != null) {
            val jpGame = jpGamesCopy.firstOrNull { it.uniqueId == naGameId }
            jpGamesCopy.remove(jpGame)

            result.add(GameMerged(naGame = naGame, euGame = null, jpGame = jpGame))
        } else {
            result.add(GameMerged(euGame = null, jpGame = null, naGame = naGame))
        }
    }

    for (jpGame in jpGamesCopy) {
        result.add(GameMerged(jpGame = jpGame, euGame = null, naGame = null))
    }

    println("Merge games completed: elapsedTime = ${System.currentTimeMillis() - startTime}ms")
    return result
}