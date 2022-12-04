// https://github.com/fedecalendino/nintendeals
// https://github.com/fedecalendino/nintendeals/blob/main/nintendeals/commons/classes/eshops.py

// https://gist.github.com/GabrielMMelo/b88f3d4d68a8883f17f1c3300e58a23f

// too old api
// https://github.com/briansamuel/eshop-game


suspend fun main() {
    val startTime = System.currentTimeMillis()

    val usGames = StoreApiNA.fetchGames()
    val euGames = StoreApiEU.fetchGames()
    val jpGames = StoreApiJP.fetchGames()
    val hkGames = StoreApiHK.fetchGames()


    println("Get all items completed successfully")
    println("Total time elapsed: ${System.currentTimeMillis() - startTime} ms")
}


//{
//    - Новый айди игры (общий)
//    - Название_ен
//    - Описание (сначала заполняем из евро, например Британии. Потом перезаписываем на русское, если в русском присутствует. Не все игры присутствуют в русском магазине, поэтому нужна модель перезаписи
//            - ссылка на изображение квадратное (из Европы). Если нет квадратного - брать любое имеющиеся
//            - ссылка на изображение 2к1, если есть (из Европы)
//            - ссылка на Ютюб трейлер. Если не найдешь в апишках - ничего, я знаю источник, где можно взять их.
//            - рейтинг на метакритик. Аналогично с ютюбом, знаю, где взять
//            - количество игроков До (из евро модели легко взять)
//            - [] категории
//            - [] названия категорий из русского. Если нет в русском - из евро
//            - [ цены {
//                - валюта (пока интересуют только GBP, HKG, USD, JPY, EUR, PLN)
//                - регулярная цена
//                        - цена по скидке, если есть
//                - дата окончания скидки
//            }]
//            - регионы [{
//        - код региона (JP, EU, NA, HK)
//        - ссылка на игру в этом регионе
//                - поддерживаемые языки в этом регионе
//    }]