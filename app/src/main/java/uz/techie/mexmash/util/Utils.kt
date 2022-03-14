package uz.techie.mexmash.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.material.snackbar.Snackbar
import uz.techie.mexmash.R
import java.sql.Time
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

object Utils {


    fun toMoney(value:Int):String{
        val numberFormat = NumberFormat.getNumberInstance()
        return numberFormat.format(value)
    }


//    fun hasInternetConnection(context: Context): Boolean {
//        return try {
//            val manager =
//                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            var networkInfo: NetworkInfo? = null
//            run {
//                networkInfo = manager.activeNetworkInfo
//                networkInfo != null && networkInfo!!.isConnected
//            }
//        } catch (e: NullPointerException) {
//            false
//        }
//    }

     fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw      = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        } else {
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }

    fun reformatDate(date: Date):String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        return dateFormat.format(date)
    }

    fun reformatDateFromString(previousDate: String):String {
        var dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val date = dateFormat.parse(previousDate)
        dateFormat = SimpleDateFormat("dd.MM.yyyy hh:mm")
        return dateFormat.format(date!!)
    }

    fun reformatDateFromStringLocale(previousDate: String?):String {
        return try {
            var dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val date = dateFormat.parse(previousDate)
            dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            dateFormat.format(date!!)
        }catch (e:Exception){
            previousDate?:""
        }
    }

    fun reformatDateFromStringLocaleDividedTime(previousDate: String):String {
        return try {
            var dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            val date = dateFormat.parse(previousDate)
            dateFormat = SimpleDateFormat("dd MMM yyyy\nHH:mm", Locale.getDefault())
            dateFormat.format(date!!)
        }catch (e:Exception){
            previousDate
        }
    }


    fun reformatDateOnlyFromString(previousDate: String):String {
        var dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
        val date = dateFormat.parse(previousDate)
        dateFormat = SimpleDateFormat("dd.MM.yyyy")
        return dateFormat.format(date!!)
    }


    fun reformatTimeOnlyFromMillis(millis: Long):String {
        var dateFormat = SimpleDateFormat("HH:mm:ss")
        val date = Date(millis)
        return dateFormat.format(date)
    }

    fun reformatDateTimeOnlyFromMillis(millis: Long):String {
        var dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date(millis)
        return dateFormat.format(date)
    }

    fun getTimeFromCalendar(millis: Long):String{
        var remainingMillis = millis
        val hour = TimeUnit.MILLISECONDS.toHours(remainingMillis)
        remainingMillis -= TimeUnit.HOURS.toMillis(hour)

        val min = TimeUnit.MILLISECONDS.toMinutes(remainingMillis)
        remainingMillis -= TimeUnit.MINUTES.toMillis(min)

        val sec = TimeUnit.MILLISECONDS.toSeconds(remainingMillis)

        return String.format("%02d:%02d:%02d",hour, min, sec)
    }


    fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
    }

    fun copyTextToClipboard(context: Context, url: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("url", url)
        clipboardManager.setPrimaryClip(clipData)
    }

    fun orderStatus(status:Int):String{
        var s = ""
        when(status){
            1->{
                s = "Yangi"
            }
            2->{
                s = "Qabul qilindi"
            }
            3->{
                s = "Yetkazilmoqda"
            }
            4->{
                s = "Sotildi"
            }
            5->{
                s = "Bekor qilindi"
            }
            6->{
                s = "Arxivlandi"
            }
            7->{
                s = "Qayta qo'ng'iroq"
            }
            8->{
                s = "Arxivlanmoqda"
            }
            else ->{
                s = ""
            }
        }
        return s
    }

    fun streamStatus(statusCode:Int):String{
        var status = ""
        when(statusCode){
            1->{
                status = "Yangi"
            }
            2->{
                status = "Qabul qilindi"
            }
            3->{
                status = "Yetkazilmoqda"
            }
            4->{
                status = "Yetqazib berildi"
            }
            5->{
                status = "Nosoz mahsulot"
            }
            6->{
                status = "Bekor qilindi"
            }
            7->{
                status = "Arxivlandi"
            }
            8->{
                status = "Qayta qo'ng'iroq"
            }
            9->{
                status = "Spam"
            }
            10->{
                status = "Arxivlanmoqda"
            }
            11->{
                status = "Muzlatildi"
            }
        }
        return status
    }

    fun hidePhoneNumber(phone:String):String{
        var newPhone = phone

        if (!phone.contains('+')){
            newPhone = "+$phone"
            Log.d("TAG", "hidePhoneNumber: not contains "+newPhone)
        }

        if (newPhone.length>12){
            newPhone = newPhone.replaceRange(8,11, "****")
        }
        return newPhone
    }









    fun toastIconSuccess(activity: Activity, message: String?) {
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_LONG

        //inflate view
        val custom_view: View = activity.layoutInflater.inflate(R.layout.toast_icon_text, null)
        (custom_view.findViewById<View>(R.id.message) as TextView).text = message
        (custom_view.findViewById<View>(R.id.icon) as ImageView).setImageResource(R.drawable.ic_baseline_done_24)
        (custom_view.findViewById<View>(R.id.parent_view) as CardView).setCardBackgroundColor(
            activity.resources.getColor(R.color.green)
        )
        toast.setView(custom_view)
        toast.show()
    }

    fun toastIconError(activity: Activity, message: String?) {
        val toast = Toast(activity)
        toast.duration = Toast.LENGTH_LONG

        //inflate view
        val custom_view: View = activity.layoutInflater.inflate(R.layout.toast_icon_text, null)
        (custom_view.findViewById<View>(R.id.message) as TextView).text = message
        (custom_view.findViewById<View>(R.id.icon) as ImageView).setImageResource(R.drawable.ic_baseline_close_24)
        (custom_view.findViewById<View>(R.id.parent_view) as CardView).setCardBackgroundColor(
            activity.resources.getColor(R.color.red)
        )
        toast.setView(custom_view)
        toast.show()
    }

    fun getFirstLetter(text:String):Char{
        return text.first()
    }

    fun numberToCardFormat(number:String):String{
        return number.replace("..(?!$)", "$0 ")
    }
    fun hideCardNumbers(number:String):String{
        try {
            var s = number
            s = s.replaceRange(4,12, " **** **** ")
            return s
        }catch (e:Exception){
            return ""
        }



        return number.replace("..(?!$)", "$0 ")
    }



}