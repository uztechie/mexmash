package uz.techie.mexmash.util

import android.app.Activity
import android.graphics.PorterDuff
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.fragment_home.*
import uz.techie.mexmash.R
import uz.techie.mexmash.adapters.SliderAdapter
import uz.techie.mexmash.models.Slider
import kotlin.math.abs

class MyCarousel(val activity:Activity, val view:View) {
    private lateinit var viewPager:ViewPager2
    private lateinit var sliderAdapter: SliderAdapter
    private lateinit var linearDots: LinearLayout
    private lateinit var handler:Handler
    private var sliderDelay:Long = 3000
    private var isAutoStart:Boolean = false
    private  val TAG = "MyCarousel"
    private var sliderList = mutableListOf<Slider>()

    init {
        viewPager = view.findViewById(R.id.myslider_viewpager)
        linearDots = view.findViewById(R.id.myslider_linear_dots)
        sliderAdapter = SliderAdapter()
        handler = Handler(Looper.getMainLooper())


        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(10))
        transformer.addTransformer(ViewPager2.PageTransformer { page, position ->
            val r = 1 - abs(position)
            val scaleY = 0.85f + r * 0.15f
            page.scaleY = scaleY

            Log.d(TAG, "onViewCreated: transformer r " + r)
            Log.d(TAG, "onViewCreated: transformer scaley " + scaleY)
        })

        sliderAdapter = SliderAdapter()
        viewPager.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            adapter = sliderAdapter
            setPageTransformer(transformer)
        }
        sliderAdapter.differ.submitList(sliderList)


        viewPager.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                addBottomDots(sliderList.size, position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == 1 && isAutoStart){
                    stopSlider()
                    startSlider()
                }
            }
        })


    }

    fun setData(list:MutableList<Slider>){
        sliderList.clear()
        sliderList.addAll(list)
        sliderAdapter.differ.submitList(list)

        if (sliderList.isNotEmpty()){
            addBottomDots(sliderList.size, 0)
        }


    }

    fun autoStart(autoStart:Boolean){
        isAutoStart = autoStart
        if (autoStart){
            startSlider()
        }
    }
    fun sliderDelay(delay:Long){
        sliderDelay = delay
    }



    private fun addBottomDots(size:Int, currentPosition:Int){
        val dots = arrayOfNulls<ImageView>(size)

        linearDots.removeAllViews()
        for (i in dots.indices){
            dots[i] = ImageView(activity)
            val widthHeight = 18
            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams(widthHeight, widthHeight))
            params.setMargins(10,0,10,0)
            dots[i]?.layoutParams = params
            dots[i]?.setImageResource(R.drawable.dots_bg)

            linearDots.addView(dots[i])
        }

        if (dots.isNotEmpty()){
            dots[currentPosition]?.setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP)
        }
    }

    private fun startSlider(){
        Log.d(TAG, "startSlider")
        handler.postDelayed(runnable, sliderDelay)
    }

    private fun stopSlider(){
        Log.d(TAG, "stopSlider: ")
        handler.removeCallbacks(runnable)
    }


    private val runnable = object:Runnable{
        override fun run() {
            var currentPasition = viewPager.currentItem?:0
            val totalItems = sliderList.size

            currentPasition+=1

            if (currentPasition>= totalItems){
                currentPasition = 0
            }
            viewPager.currentItem = currentPasition
            handler.postDelayed(this, sliderDelay)

        }

    }



}