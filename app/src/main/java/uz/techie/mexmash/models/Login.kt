package uz.techie.mexmash.models

data class Login(
    val otp_token: Int? = null,
    val status: Int? = null,
    val message: String? = null,
    val data:User? = null
)
