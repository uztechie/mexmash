package uz.techie.mexmash.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.adapter_news.view.*
import kotlinx.android.synthetic.main.dialog_confirmation.*
import kotlinx.android.synthetic.main.dialog_image.*
import uz.techie.mexmash.R

class ImageDialog(context:Context):Dialog(context) {

    init {
//        setCancelable(false)
//        setCanceledOnTouchOutside(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(0))
        setContentView(R.layout.dialog_image)

        dialog_image_close.setOnClickListener {
            dismiss()
        }
    }

    fun setImage(imageUrl:String){
        Glide.with(dialog_image)
            .load(imageUrl)
            .apply(options)
            .into(dialog_image)
    }

    private var options: RequestOptions = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.progress_animation)
        .error(R.drawable.no_image)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .priority(Priority.HIGH)

}