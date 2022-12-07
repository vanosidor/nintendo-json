import api.PricesApi
import api.StoreApiEU

//TODO move to readme
// https://github.com/fedecalendino/nintendeals
// https://github.com/fedecalendino/nintendeals/blob/main/nintendeals/commons/classes/eshops.py

// https://gist.github.com/GabrielMMelo/b88f3d4d68a8883f17f1c3300e58a23f

// too old api
// https://github.com/briansamuel/eshop-game


suspend fun main() {

//    TODO merge all store results
    val startTime = System.currentTimeMillis()

    val euGames = StoreApiEU.fetchGames()

    val euNsuids = euGames.map { it.nsuid }

    val pricesRu = PricesApi.fetchPricesForCountry(country = "RU", nsuids = euNsuids)

//    val jpGames = api.StoreApiJP.fetchGames()
//    val hkGames = api.StoreApiHK.fetchGames()
//    val naGames = api.StoreApiNA.fetchGames()

    println("Get all items completed successfully")
    println("Total time elapsed: ${System.currentTimeMillis() - startTime} ms")
}