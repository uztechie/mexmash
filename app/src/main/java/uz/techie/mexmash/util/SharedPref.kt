package uz.techie.mexmash.util

import com.chibatching.kotpref.KotprefModel
import uz.techie.mexmash.models.Counter

object SharedPref:KotprefModel() {
    var token by stringPref("")
    var phone by stringPref("")
    var userType by stringPref("")

    var counterDate by longPref(0)
    var counterValue by intPref(0)
    var counterIdByUserId by intPref(-1)
}