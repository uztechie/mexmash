package uz.techie.mexmash.fragments

import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.adapter_prize.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_show_image.*
import kotlinx.android.synthetic.main.main_prize.*
import uz.techie.mexmash.MainActivity
import uz.techie.mexmash.R
import uz.techie.mexmash.adapters.SliderAdapter
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.models.Prize
import uz.techie.mexmash.util.*

class ShowImageFragment : Fragment(R.layout.fragment_show_image) {
    private val TAG = "ShowImageFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val url = ShowImageFragmentArgs.fromBundle(it).imageUrl

            Glide.with(show_image_imageview)
                .load(url)
                .apply(options)
                .into(show_image_imageview)




            Handler().postDelayed(object:Runnable{
                override fun run() {
                    try {
                        show_image_imageview.performClick()
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }

            },100)


        }


    }


    private var options: RequestOptions = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.loading_gif)
        .error(R.drawable.no_image)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .priority(Priority.HIGH)


}