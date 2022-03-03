package uz.techie.mexmash.models

data class PromoCodeHistory(
    val message:String? = null,
    val status:Int? = null,
    val total_used:Int? = null,
    val total_point:Long? = null,
    val total_kg:Long? = null,
    val data:List<Point>? = null
)
