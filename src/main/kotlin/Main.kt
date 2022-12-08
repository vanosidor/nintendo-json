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

    // EU get data
    //    TODO merge prices into model
    val euGames = StoreApiEU.fetchGames()
    val euNsuids = euGames.map { it.nsuid }
//    val pricesRu = PricesApi.fetchPricesForCountry(country = "RU", nsuids = euNsuids)
//    val pricesGB = PricesApi.fetchPricesForCountry(country = "GB", nsuids = euNsuids)


    // JP get data
    //    TODO merge into jp games model
//    val jpGames = api.StoreApiJP.fetchGames()
//    val jpNsuids = jpGames.map { it.nsuid }
//    val pricesJP = PricesApi.fetchPricesForCountry(country = "JP", nsuids = jpNsuids)

    //  NA get data
//    val naGames = api.StoreApiNA.fetchGames()
//    val naNsuids = naGames.map { it.nsuid }
//    val pricesUS = PricesApi.fetchPricesForCountry("US", naNsuids)

    //    val hkGames = api.StoreApiHK.fetchGames()


    println("Get all items completed successfully")
    println("Total time elapsed: ${System.currentTimeMillis() - startTime} ms")
}