package uz.techie.mexmash.fragments

import android.content.Intent
import android.net.Uri
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_cabinet.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.main_prize.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.techie.mexmash.MainActivity
import uz.techie.mexmash.R
import uz.techie.mexmash.adapters.SliderAdapter
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.dialog.TermsBottomSheetDialog
import uz.techie.mexmash.models.Prize
import uz.techie.mexmash.util.*


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {
    private val TAG = "HomeFragment"
    val viewModel: AppViewModel by viewModels()
    lateinit var sliderAdapter: SliderAdapter
    private lateinit var myCarousel: MyCarousel
    private var userPoint = 0L
    private var userKg = 0L
    private var nearPrizePoint = 0L
    private var nearPrizeKg = 0L

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

        initSwipeRefresh()

        if (viewModel.getUser().isEmpty()){
            findNavController().navigate(HomeFragmentDirections.actionGlobalLoginFragment())
            return
        }
        else{
            val user = viewModel.getUser()[0]
            Constants.TOKEN = "Token "+user.token
            Constants.USER_ID = user.id

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
                userPoint = user.point?:0
                userKg = user.total_kg?:0
                home_user_point.text = userPoint.toString()
                home_progress_text_point.text = "$userPoint/$nearPrizePoint"
                home_progressView_point.progress = userPoint.toFloat()
                home_progressView_kg.progress = userKg.toFloat()
                home_progress_text_kg.text = "$userKg/$nearPrizeKg"
                getPrizeByPoint(userPoint)
            }
        }


        viewModel.profile.observe(viewLifecycleOwner){response->
            when(response){
                is Resource.Loading->{
                    home_user_progress.isVisible = true
                }
                is Resource.Error->{
                    hideRefresh()
                    home_user_progress.visibility = View.INVISIBLE
                    response.message?.let {
//                        Utils.toastIconError(requireActivity(), it)
                    }
                }
                is Resource.Success->{
                    hideRefresh()
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

        home_user_point.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToPromoCodeHistoryFragment())
        }

        prize_layout.setOnClickListener {
            val menu = (activity as MainActivity).bottomNavigationView
            menu.selectedItemId = R.id.giftFragment
        }

        home_terms.setOnClickListener {
            val termsDialog = TermsBottomSheetDialog(requireContext())
            termsDialog.show()

            viewModel.getTerms().observe(viewLifecycleOwner){
                var date = ""
                var message = ""

                if (it.isNotEmpty()){

                    it[0].data?.let { m->
                        message = m
                    }
                    it[0].updated_at?.let { d->
                        date = d
                    }
                    termsDialog.setMessage(message, date)

                }
            }


        }

        home_telegram.setOnClickListener {
            if (Constants.TELEGRAM_URL.isNotEmpty()){
                openTelegram(Constants.TELEGRAM_URL)
            }
        }




    }

    override fun onStart() {
        super.onStart()
        initSliders()

        observeSLiders()
        observePrizes()
        observeTerms()

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
            launch {
                viewModel.loadTerms(Constants.TOKEN)
            }
        }
    }

    fun loadTelegram(){
        viewModel.telegramResponse.observe(this){ response->
            when(response){
                is Resource.Loading->{

                }
                is Resource.Error->{
                    response.message?.let {
//                        Utils.toastIconError(this, it)
                    }
                }
                is Resource.Success->{
                    response.data?.let {
                        if (it.status == 200){
                            it.data?.url?.let { url->
                                Constants.TELEGRAM_URL = url
                            }
                        }

                        println("loadTelegram data "+it.data?.url)
                    }
                }

            }
        }

        viewModel.loadPrizes(Constants.TOKEN)
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
                    nearPrizeKg = kg
                    initProgressKgText(kg)
                }

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

        home_prize_prize_name.text = prize.level_name
        Glide.with(home_prize_cup)
            .load(prize.level_image)
            .apply(options)
            .into(home_prize_cup)

    }



    fun observeSLiders(){
        viewModel.sliders.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{

                }
                is Resource.Error->{
                    hideRefresh()
                    response.message?.let {
//                        Utils.toastIconError(requireActivity(), "loadSliders "+it)
                    }
                }
                is Resource.Success->{
                    hideRefresh()
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
                    hideRefresh()
                    response.message?.let {
//                        Utils.toastIconError(requireActivity(), "loadPrizes "+it)
                    }
                }
                is Resource.Success->{
                    hideRefresh()
                    response.data?.let {
                        viewModel.insertPrizes(it)
                    }
                }

            }
        }
    }


    private fun observeTerms(){
        viewModel.termsResponse.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{

                }
                is Resource.Error->{
                    hideRefresh()
                    response.message?.let {
//                        Utils.toastIconError(requireActivity(), "loadTerms "+it)
                    }
                }
                is Resource.Success->{
                    hideRefresh()
                    response.data?.let {
                        if (it.status == 200){
                            viewModel.insertTerms(it)
                        }
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

    private fun initSwipeRefresh() {
        home_refresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                loadAllData()
            }

        })
    }

    private fun hideRefresh(){
        home_refresh.isRefreshing = false
    }


    private fun openTelegram(url:String){
        var mUrl = url
        if (!mUrl.contains("http")){
            mUrl = "https://$mUrl"
        }

        val webUrl = Uri.parse(mUrl)
        val intent = Intent(Intent.ACTION_VIEW, webUrl)
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null){
            startActivity(intent)
        }

    }


    override fun onStop() {
        super.onStop()
        viewModel.prizes = MutableLiveData()
        viewModel.sliders = MutableLiveData()
        viewModel.profile = MutableLiveData()
    }


}