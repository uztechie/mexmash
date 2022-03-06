package uz.techie.mexmash.dialog

import android.content.Context
import android.os.Bundle
import android.text.Html
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.dialog_terms.*
import uz.techie.mexmash.R
import uz.techie.mexmash.util.Utils

class TermsBottomSheetDialog(context: Context):BottomSheetDialog(context, R.style.bottomSheetStyle) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_terms)


        dialog_terms_close.setOnClickListener {
            dismiss()
        }
    }

    fun setMessage(message:String, date:String){
        dialog_terms_text.text = Html.fromHtml(message)
        dialog_terms_date.text = Utils.reformatDateFromStringLocale(date)
    }

}