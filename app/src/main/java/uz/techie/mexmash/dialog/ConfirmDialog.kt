package uz.techie.mexmash.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import kotlinx.android.synthetic.main.dialog_confirmation.*
import uz.techie.mexmash.R

class ConfirmDialog(context: Context, val listener:ConfirmDialogListener):Dialog(context) {

    init {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(0))
        setContentView(R.layout.dialog_confirmation)

        dialog_conf_btn_cancel.setOnClickListener {
            dismiss()
        }

        dialog_conf_btn_ok.setOnClickListener {
            dismiss()
            listener.onOkClick()
        }


    }

    fun setTitle(title:String){
        dialog_conf_title.text = title
    }
    fun setMessage(text:String){
        dialog_conf_text.text = text
    }


    interface ConfirmDialogListener{
        fun onOkClick()
    }


}