package uz.techie.mexmash.util

import com.chibatching.kotpref.KotprefModel

object SharedPref:KotprefModel() {
    var token by stringPref("")
    var phone by stringPref("")
    var userType by stringPref("")

}