import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

// Links:
// https://gist.github.com/GabrielMMelo/b88f3d4d68a8883f17f1c3300e58a23f
// https://github.com/briansamuel/eshop-game
// https://github.com/fedecalendino/nintendeals

// constants
// https://github.com/fedecalendino/nintendeals

// endpoints
//https://github.com/fedecalendino/nintendeals/blob/main/nintendeals/commons/classes/eshops.py


// TODO american store link
//TODO Europe store link
//TODO Europe store link
suspend fun main(args: Array<String>) {
    println("Hello World!")

    // Try adding program arguments via Run/Debug configuration.
    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
    val client = HttpClient(CIO)

    // HK store
    val response: HttpResponse = client.get("https://www.nintendo.com.hk/data/json/switch_software.json")
    println(response.status)
    client.close()
}

// HK store:
// https://www.nintendo.com.hk/data/json/switch_software.json

//{
//    "r_date": 44873,
//    "release_date": "2022.11.8",
//    "r_date_tw": "",
//    "release_date_tw": "",
//    "title": "《Football Manager 2023 Touch》",
//    "title_sc": "",
//    "sale_until": "",
//    "pickup": "",
//    "only_for": "hk",
//    "media": "eshop",
//    "lang": "",
//    "maker_publisher": "SEGA",
//    "thumb_img": "HAC-P-BANFA.jpeg",
//    "thumb_img_sc": "",
//    "thumb_img_tw": "",
//    "link": "https://store.nintendo.com.hk/70010000059393",
//    "link_sc": "",
//    "link_tw": "",
//    "link_target": "",
//    "platform": "Nintendo Switch",
//    "rating": "",
//    "adult_hk": "",
//    "category": "",
//    "category_sc": "",
//    "copyright": "",
//    "copyright_sc": "",
//    "memo1": "",
//    "memo2": "",
//    "memo1_sc": "",
//    "memo2_sc": "",
//    "memo1_tw": "",
//    "memo2_tw": "",
//    "product_code": "",
//    "item_code": "",
//    "price": "",
//    "": ""
//},


//JP store xml format
// https://www.nintendo.co.jp/data/software/xml/switch.xml

//https://www.nintendo.co.jp/data/software/xml-system/switch-onsale.xml

//<TitleInfo>
//<InitialCode>HACA99DA</InitialCode>
//<TitleName>Jitsu Squad - ジツ　スクワット </TitleName>
//<MakerName>ININ Games</MakerName>
//<MakerKana/>
//<Price>4,378円(税込)</Price>
//<SalesDate>2023.3.16</SalesDate>
//<SoftType>dl</SoftType>
//<PlatformID>2001</PlatformID>
//<DlIconFlg>1</DlIconFlg>
//<LinkURL>/titles/70010000058096</LinkURL>
//<ScreenshotImgFlg>1</ScreenshotImgFlg>
//<ScreenshotImgURL>
//https://img-eshop.cdn.nintendo.net/i/910af85f7a248926a619ed0c25a855c9ea4a28e9c706a7010fe51ee6e1809908.jpg
//</ScreenshotImgURL>
//</TitleInfo>

// Price query
//  https://api.ec.nintendo.com/v1/price?country=JP&ids=70010000009922&lang=jp
//{
//    "personalized": false,
//    "country": "JP",
//    "prices": [
//    {
//        "title_id": 70010000009922,
//        "sales_status": "onsale",
//        "regular_price": {
//        "amount": "1,620円",
//        "currency": "JPY",
//        "raw_value": "1620"
//    },
//        "gold_point": {
//        "basic_gift_gp": "81",
//        "basic_gift_rate": "0.05",
//        "consume_gp": "0",
//        "extra_gold_points": [],
//        "gift_gp": "81",
//        "gift_rate": "0.05"
//    }
//    }
//    ]
//}