import api.PricesApi
import api.StoreApiEU
import entities.Game
import entities.GameMerged
import entities.Price
import entities.StoreCountry
import java.io.File

// TODO move to readme
// https://github.com/fedecalendino/nintendeals
// https://github.com/fedecalendino/nintendeals/blob/main/nintendeals/commons/classes/eshops.py

// https://gist.github.com/GabrielMMelo/b88f3d4d68a8883f17f1c3300e58a23f

// too old api
// https://github.com/briansamuel/eshop-game

suspend fun main() {
    val startTime = System.currentTimeMillis()

    val games = getAllGames()

    File("merged_games_result.txt").printWriter().use { out ->
        games.forEachIndexed { index, it ->
            out.println("${index + 1} ${it.title} ${it.id}")
        }
    }

    println("All tasks completed: elapsed time = ${System.currentTimeMillis() - startTime}ms, games count = ${games.size}")
}

suspend fun getAllGames(): List<GameMerged> {
    val euGames = getEuGames()
    val jpGames = getJpGames()
    val naGames = getNaGames()
    val hkGames = getHkGames()

    return mergeGames(euGames, jpGames, naGames, hkGames)
}

private suspend fun getEuGames(): List<Game> {
    val gamesWithoutPrices = StoreApiEU.fetchGames()

    val nsuids = gamesWithoutPrices.map { it.nsuid }

    val pricesRussian = PricesApi.fetchPricesForCountry(StoreCountry.RussianFederation, nsuids)
    val pricesGreatBritain = PricesApi.fetchPricesForCountry(StoreCountry.UnitedKingdom, nsuids)

    return mergePricesIntoGames(gamesWithoutPrices, pricesRussian, pricesGreatBritain)
}

private suspend fun getJpGames(): List<Game> {
    val gamesWithoutPrices = api.StoreApiJP.fetchGames()

    val nsuids = gamesWithoutPrices.map { it.nsuid }

    val pricesJapan = PricesApi.fetchPricesForCountry(StoreCountry.Japan, nsuids)

    return mergePricesIntoGames(gamesWithoutPrices, pricesJapan)
}

private suspend fun getNaGames(): List<Game> {
    val gamesWithoutPrices = api.StoreApiNA.fetchGames()

    val nsuids = gamesWithoutPrices.map { it.nsuid }

    val pricesUnitedStates = PricesApi.fetchPricesForCountry(StoreCountry.UnitedState, nsuids)
    val pricesBrazil = PricesApi.fetchPricesForCountry(StoreCountry.Brazil, nsuids)

    return mergePricesIntoGames(gamesWithoutPrices, pricesUnitedStates, pricesBrazil)
}

private suspend fun getHkGames(): List<Game> {
    val gamesWithoutPrices = api.StoreApiHK.fetchGames()

    val nsuids = gamesWithoutPrices.map { it.nsuid }
    val pricesHongKong = PricesApi.fetchPricesForCountry(StoreCountry.HongKong, nsuids)
    return mergePricesIntoGames(gamesWithoutPrices, pricesHongKong)
}

private fun mergePricesIntoGames(games: List<Game>, vararg prices: List<Price>): List<Game> {
    return games.map { game ->
        val pricesResult = arrayListOf<Price>()
        for (priceList in prices) {
            val price = priceList.firstOrNull { it.nsuid == game.nsuid }

            if (price != null) pricesResult.add(price)
        }

        game.copy(prices = pricesResult)
    }
}

private fun mergeGames(
    euGames: List<Game>,
    jpGames: List<Game>,
    naGames: List<Game>,
    hkGames: List<Game>
): List<GameMerged> {
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
            result.add(GameMerged(euGame = euGame))
        }
    }

    for (naGame in naGamesCopy) {
        val naGameId = naGame.uniqueId

        if (naGameId != null) {
            val jpGame = jpGamesCopy.firstOrNull { it.uniqueId == naGameId }
            jpGamesCopy.remove(jpGame)

            result.add(GameMerged(naGame = naGame, jpGame = jpGame))
        } else {
            result.add(GameMerged(jpGame = null, naGame = naGame))
        }
    }

    for (jpGame in jpGamesCopy) {
        result.add(GameMerged(jpGame = jpGame))
    }

    for (hkGame in hkGames) {
        result.add(GameMerged(hkGame = hkGame))
    }

    println("Merge games completed: elapsedTime = ${System.currentTimeMillis() - startTime}ms")
    return result
}