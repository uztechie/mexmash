package uz.techie.mexmash.fragments

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.main_prize.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.techie.mexmash.R
import uz.techie.mexmash.adapters.SliderAdapter
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.models.Prize
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.MyCarousel
import uz.techie.mexmash.util.Resource
import uz.techie.mexmash.util.Utils


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val TAG = "HomeFragment"
    val viewModel: AppViewModel by viewModels()
    lateinit var sliderAdapter: SliderAdapter
    private lateinit var myCarousel: MyCarousel
    private var userPoint = 0L
    private var userKg = 0L
    private var nearPrizePoint = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.getUser().isEmpty()){
            findNavController().navigate(HomeFragmentDirections.actionGlobalLoginFragment())
            return
        }
        else{
            val user = viewModel.getUser()[0]
            Constants.TOKEN = "Token "+user.token

            user.type_name?.let {
                Constants.USER_TYPE = it
                subscribeTopic(it)
                subscribeTopic(Constants.TOPIC_ALL)
            }

        }

        viewModel.getUserLive().observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                val user = it[0]
                Log.d(TAG, "getUser: " + user.phone)
                userPoint = user.point
                userKg = user.kg
                home_user_point.text = userPoint.toString()
                home_progress_text_point.text = "$userPoint/$nearPrizePoint"
                home_progressView_point.progress = userPoint.toFloat()
                home_progressView_kg.progress = userKg.toFloat()
                getPrizeByPoint(userPoint)
            }
        }




        viewModel.profile.observe(viewLifecycleOwner){response->
            when(response){
                is Resource.Loading->{
                    home_user_progress.isVisible = true
                }
                is Resource.Error->{
                    home_user_progress.visibility = View.INVISIBLE
                    response.message?.let {
                        Utils.toastIconError(requireActivity(), it)
                    }
                }
                is Resource.Success->{
                    home_user_progress.visibility = View.INVISIBLE
                    response.data?.let { profile->
                        if (profile.status == 200){
                            profile.data?.let {
                                viewModel.insertUser(it)
                            }
                        }
                        else{
                            profile.message?.let {
                                Utils.toastIconError(requireActivity(), it)
                            }
                        }
                    }
                }
            }
        }

        myCarousel = MyCarousel(requireActivity(), view)
        myCarousel.autoStart(true)
        myCarousel.sliderDelay(4000)
        myCarousel.setData(mutableListOf())

        home_news.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionGlobalNewsFragment())
        }



    }

    override fun onStart() {
        super.onStart()
        initSliders()

        observeSLiders()
        observePrizes()

        loadAllData()
    }

    private fun loadAllData(){
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                viewModel.loadProfile(Constants.TOKEN)
            }
            launch {
                viewModel.loadSliders(Constants.TOKEN)
            }
            launch {
                viewModel.loadPrizes(Constants.TOKEN)
            }
        }
    }


    fun getPrizeByPoint(point: Long){
        if (Constants.USER_TYPE == Constants.USER_TYPE_DEALER){
            home_progressKgLayout.visibility = View.VISIBLE
            home_prize_kg.visibility = View.VISIBLE
        }

        viewModel.getPrizeByPoint(point).observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                it[0].point?.let { point->
                    nearPrizePoint = point
                    initProgressPointText(point)
                }
                it[0].kg?.let { kg->
                    initProgressKgText(kg)
                }

                var name = "<strong>"+it[0].name+"</strong>"
                home_prize_tv.text = Html.fromHtml(getString(R.string.sizda_quyidagi_sovrinni_yutish))
                initPrizeToView(it[0])
            }
        }




    }


    fun initSliders(){
        viewModel.getSliders().observe(viewLifecycleOwner){
            myCarousel.setData(it.toMutableList())
        }
    }

    fun initProgressPointText(point:Long){
        home_progress_text_point.text = "$userPoint/$nearPrizePoint ${getString(R.string.bal)}"
        home_progressView_point.max = point.toFloat()
        home_progressView_point.progress = userPoint.toFloat()
    }
    fun initProgressKgText(kg:Long){
        home_progress_text_kg.text = "$userKg/$kg ${getString(R.string.kg)}"
        home_progressView_kg.max = kg.toFloat()
        home_progressView_kg.progress = userKg.toFloat()
    }

    fun initPrizeToView(prize: Prize){
        Glide.with(home_prize_image)
            .load(prize.image)
            .apply(options)
            .into(home_prize_image)

        home_prize_title.text = prize.name
        home_prize_point.text = "${prize.point} ${getString(R.string.bal)}"
        home_prize_kg.text = "${prize.kg} ${getString(R.string.kg)}"

        if (prize.level_id == Constants.LEVEL_SILVER){
            home_prize_cup.setColorFilter(ContextCompat.getColor(requireContext(), R.color.silver))
        }
        else if (prize.level_id == Constants.LEVEL_BRONZE){
            home_prize_cup.setColorFilter(ContextCompat.getColor(requireContext(), R.color.bronze))
        }
        else if (prize.level_id == Constants.LEVEL_GOLD){
            home_prize_cup.setColorFilter(ContextCompat.getColor(requireContext(), R.color.gold))
        }


    }



    fun observeSLiders(){
        viewModel.sliders.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{

                }
                is Resource.Error->{
                    response.message?.let {
                        Utils.toastIconError(requireActivity(), "loadSliders "+it)
                    }
                }
                is Resource.Success->{
                    response.data?.let {
                        viewModel.insertSliders(it)
                    }
                }

            }
        }

    }

    fun observePrizes(){
        viewModel.prizes.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{

                }
                is Resource.Error->{
                    response.message?.let {
                        Utils.toastIconError(requireActivity(), "loadPrizes "+it)
                    }
                }
                is Resource.Success->{
                    response.data?.let {
                        viewModel.insertPrizes(it)
                    }
                }

            }
        }
    }

    private var options: RequestOptions = RequestOptions()
        .centerCrop()
        .placeholder(R.drawable.loading_gif)
        .error(R.drawable.no_image)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .priority(Priority.HIGH)


    private fun subscribeTopic(topic:String){
        Log.d(TAG, "subscribeTopic: topic $topic")
        Firebase.messaging.subscribeToTopic(topic)
    }


    override fun onStop() {
        super.onStop()
        viewModel.prizes = MutableLiveData()
        viewModel.sliders = MutableLiveData()
        viewModel.profile = MutableLiveData()
    }


}