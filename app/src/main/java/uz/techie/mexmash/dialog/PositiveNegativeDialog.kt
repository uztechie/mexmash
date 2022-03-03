package uz.techie.mexmash.dialog

import uz.techie.mexmash.dialog.PositiveNegativeDialog.PositiveNegativeListener
import android.app.Dialog
import android.content.Context
import android.widget.TextView
import android.widget.Button
import android.widget.ImageView
import android.os.Bundle
import android.graphics.drawable.ColorDrawable
import android.graphics.Color
import uz.techie.mexmash.R
import android.view.View
import com.google.android.material.button.MaterialButton

class PositiveNegativeDialog(context: Context, var listener: PositiveNegativeListener)
    : Dialog(context) {
    lateinit var tvTitle: TextView
    lateinit var tvMessage: TextView
    lateinit var btnBack: MaterialButton
    lateinit var imageView: ImageView
    private var shouldGoBack = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setContentView(R.layout.dialog_positive_negative)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        imageView = findViewById(R.id.positive_negative_image)
        tvTitle = findViewById(R.id.positive_negative_title)
        tvMessage = findViewById(R.id.positive_negative_text)
        btnBack = findViewById(R.id.positive_negative_btnOk)
        btnBack.setOnClickListener(View.OnClickListener {
            dismiss()
            listener.onBackBtnClick(shouldGoBack)
        })
    }

    fun setData(title: String, message: String, isPositive: Boolean, needToGoBack: Boolean) {
        shouldGoBack = needToGoBack
        tvTitle.text = title
        tvMessage.text = message
        if (isPositive) {
            imageView.setImageResource(R.drawable.success)
        } else {
            imageView.setImageResource(R.drawable.error)
        }
    }

    fun changeButtonTitle(title:String){
        btnBack.text = title
    }

    interface PositiveNegativeListener {
        fun onBackBtnClick(shouldGoBack: Boolean)
    }
}