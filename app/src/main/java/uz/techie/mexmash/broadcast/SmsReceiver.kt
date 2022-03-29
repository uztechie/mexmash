package uz.techie.mexmash.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsReceiver(
    val callback:SmsReceiverCallBack
):BroadcastReceiver() {

    override fun onReceive(p0: Context?, intent: Intent?) {
        println("SmsReceiver onReceive "+intent?.action)

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action){
            val extras = intent.extras
            val smsRetrieverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when(smsRetrieverStatus.statusCode){
                CommonStatusCodes.SUCCESS->{
                    val messageIntent = extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    callback.onSuccess(messageIntent)
                }
                CommonStatusCodes.TIMEOUT->{
                    callback.onFailure()
                }
            }
        }



    }

    interface SmsReceiverCallBack{
        fun onSuccess(intent: Intent?)
        fun onFailure()
    }
}