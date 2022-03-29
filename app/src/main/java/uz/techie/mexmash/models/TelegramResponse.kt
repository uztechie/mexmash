package uz.techie.mexmash.models

data class TelegramResponse(
    val status:Int? = null,
    val data:TelegramData? = null
)

data class TelegramData(
    val url:String? = null
)