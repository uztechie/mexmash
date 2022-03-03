package uz.techie.mexmash

import android.app.ActionBar
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.fragments.HomeFragment
import uz.techie.mexmash.fragments.HomeFragmentDirections
import uz.techie.mexmash.models.User
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.Resource
import uz.techie.mexmash.util.SharedPref
import uz.techie.mexmash.util.Utils

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var bottomNavigationView: BottomNavigationView
    val viewModel by viewModels<AppViewModel>()
    var user :User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (viewModel.getUser().isNotEmpty()){
            Constants.TOKEN = "Token "+viewModel.getUser()[0].token
            user = viewModel.getUser()[0]
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragmentContainer) as NavHostFragment
        navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment)
        )

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.background = null

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            val id = destination.id
            if (id == R.id.homeFragment || id == R.id.productFragment || id == R.id.giftFragment || id == R.id.cabinetFragment){
//                bottomNavigationView.visibility = View.VISIBLE
//                bottomBar.performShow()
                bottomBar.visibility = View.VISIBLE
                main_space.visibility = View.VISIBLE

                checkUserType()


            }else{
                main_space.visibility = View.GONE
                fab.hide()
                bottomBar.visibility = View.GONE
//                bottomBar.performHide()
//                bottomNavigationView.visibility = View.GONE

            }
        }

        fab.setOnClickListener {
            navController.navigate(R.id.enterCodeFragment)
        }

        onNewIntent(intent)

    }

    fun updateStatusLight() {
        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars = true
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

//        window.setBackgroundDrawableResource(R.drawable.blue_bg)


    }

    fun updateStatusBarDark() { // Color must be in hexadecimal fromat
        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars = false
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

    }


    fun hideStatusBar() {
        val view = window.decorView
        val uiOption = View.SYSTEM_UI_FLAG_FULLSCREEN
        view.systemUiVisibility = uiOption
        val actionBar: ActionBar? = actionBar
        actionBar?.hide()
    }

    override fun onStart() {
        super.onStart()
        loadSliders()
        loadPrizes()
        checkUserType()

    }

    fun loadSliders(){
        viewModel.sliders.observe(this){ response->
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
                        viewModel.insertSliders(it)
                    }
                }

            }
        }

        viewModel.loadSliders(Constants.TOKEN)
    }

    fun loadPrizes(){
        viewModel.prizes.observe(this){ response->
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
                        viewModel.insertPrizes(it)
                    }
                }

            }
        }

        viewModel.loadPrizes(Constants.TOKEN)
    }


     fun checkUserType(){
        if (Constants.USER_TYPE == Constants.USER_TYPE_SELLER){
            fab.show()
            bottomNavigationView.menu.getItem(2).isVisible = true
        }
        else{
            fab.hide()
            bottomNavigationView.menu.getItem(2).isVisible = false
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.extras != null){
            val clickAction = intent.getStringExtra("clickAction")
            val productId = intent.getStringExtra("productId")
            var product = -1
            productId?.let {
                if (it.isDigitsOnly()){
                    product = it.toInt()
                }
            }

            if (clickAction == Constants.ACTION_NEWS){
                fab.hide()
                Handler().postDelayed(object :Runnable{
                    override fun run() {
                        navController.navigate(HomeFragmentDirections.actionGlobalNewsFragment())
                    }
                }, 200)
            }



            Log.d("TAG", "onActivityResult: clickAction "+clickAction)
            Log.d("TAG", "onActivityResult: productId "+productId)
        }
    }


}