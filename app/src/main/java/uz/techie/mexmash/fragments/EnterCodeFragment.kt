package uz.techie.mexmash.fragments

import android.os.Bundle
import android.util.Log
import android.util.TimeUtils
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_enter_code.*
import uz.techie.mexmash.R
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.dialog.CustomProgressDialog
import uz.techie.mexmash.dialog.PositiveNegativeDialog
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.Resource
import uz.techie.mexmash.util.SharedPref
import uz.techie.mexmash.util.Utils
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class EnterCodeFragment:Fragment(R.layout.fragment_enter_code),
    PositiveNegativeDialog.PositiveNegativeListener {

    private val  viewModel:AppViewModel by viewModels()
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var positiveNegativeDialog: PositiveNegativeDialog


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        customProgressDialog = CustomProgressDialog(requireContext())
        positiveNegativeDialog = PositiveNegativeDialog(requireContext(), this)


        viewModel.promoCode.observe(viewLifecycleOwner){ response->
            Log.d("TAG", "onViewCreated: obs")
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    println("errorrr "+response.message)
                    response.message?.let {
                        positiveNegativeDialog.show()
                        positiveNegativeDialog.setData(getString(R.string.kodni_yuborish), it, false, false)
                        positiveNegativeDialog.changeButtonTitle(getString(R.string.qayta_urinish))
                    }

                }

                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 200){
                            it.message?.let {  message->
                                resetCounter()
                                positiveNegativeDialog.show()
                                positiveNegativeDialog.setData(getString(R.string.kodni_yuborish), message, true, true)
                                positiveNegativeDialog.changeButtonTitle(getString(R.string.orqaga))
                            }
                        }
                        else {
                            it.message?.let {  message->
                                setCounter()
                                positiveNegativeDialog.show()
                                positiveNegativeDialog.setData(getString(R.string.kodni_yuborish), message, false, false)
                                positiveNegativeDialog.changeButtonTitle(getString(R.string.qayta_urinish))
                            }
                        }
                    }
                }
            }
        }

        enter_code_btn_send.setOnClickListener {
            val code = enter_code_tv.text.toString().trim()
            if (code.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.promo_kodni_kiriting))
                return@setOnClickListener
            }



            if (getCounter() >= 5){
                val remain = SharedPref.counterDate-System.currentTimeMillis()

                val remainingTime = Utils.getTimeFromCalendar(remain)
                println("getCounter "+ (SharedPref.counterDate-System.currentTimeMillis()))


                positiveNegativeDialog.show()
                positiveNegativeDialog.setData(getString(R.string.kodni_yuborish), "${getString(R.string.siz_kodni_5_marta)}\n $remainingTime", false, true)
                positiveNegativeDialog.changeButtonTitle(getString(R.string.qayta_urinish))
                return@setOnClickListener
            }

            viewModel.sendPromoCode(Constants.TOKEN, code)
        }



    }


    private fun initToolbar() {
//        toolbar_layout.setBackgroundColor(Color.TRANSPARENT)
        toolbar_title.text = getString(R.string.kodni_yuborish)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
//        toolbar_btn_back.isVisible = false
    }


    override fun onBackBtnClick(shouldGoBack: Boolean) {
        if (shouldGoBack){
            findNavController().navigate(EnterCodeFragmentDirections.actionGlobalHomeFragment())
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.promoCode = MutableLiveData()
    }


    fun setCounter(){
        val calendarNow = Calendar.getInstance()
        println("setCounter TimeUnit.HOURS.toMillis(24) "+TimeUnit.HOURS.toMillis(24))
        println("setCounter 1000*60*60*24 "+1000*60*60*24)

        SharedPref.counterDate = calendarNow.timeInMillis + TimeUnit.HOURS.toMillis(24)
        println("setCounter counter value "+SharedPref.counterValue)
        println("setCounter counter value "+Utils.reformatTimeOnlyFromMillis(SharedPref.counterDate))
        println("setCounter counter value "+Utils.reformatDateTimeOnlyFromMillis(SharedPref.counterDate))
        SharedPref.counterValue += 1
    }

    fun getCounter():Int{
        val calendarNow = Calendar.getInstance()
        val calendarCounter = Calendar.getInstance()
        calendarCounter.timeInMillis = SharedPref.counterDate

        println("getCounter now "+calendarNow.timeInMillis)
        println("getCounter now "+System.currentTimeMillis())
        println("getCounter counter "+calendarCounter.timeInMillis)
        println("getCounter counter value "+SharedPref.counterValue)

        if ((SharedPref.counterDate - calendarNow.timeInMillis) <= 0){
            resetCounter()
        }

        return SharedPref.counterValue
    }

    fun resetCounter(){
        SharedPref.counterValue = 0
    }


}