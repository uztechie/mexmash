package uz.techie.mexmash.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.*
import uz.techie.mexmash.R
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.dialog.CustomProgressDialog
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.Resource
import uz.techie.mexmash.util.SharedPref
import uz.techie.mexmash.util.Utils

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private val TAG = "LoginFragment"
    private lateinit var customProgressDialog: CustomProgressDialog
    private lateinit var countDownTimer: CountDownTimer
    private var mMode = MODE_PHONE_INPUT

    private val START_TIME = 120000L
    private var leftTime = START_TIME
    private var isTimerRunning = false

    private val viewModel by viewModels<AppViewModel>()

    private var phone = ""
    private var agentCode = ""
    private var smsCode = ""

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
        stepInputPhone()
        customProgressDialog = CustomProgressDialog(requireContext())


        viewModel.checkPhone.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    Log.d(TAG, "onViewCreated: loading")
                    customProgressDialog.show()
                }
                is Resource.Error -> {
                    customProgressDialog.dismiss()
                    Log.e(TAG, "onViewCreated: error" + response.message)
                    Utils.toastIconError(requireActivity(), response.message)
                }
                is Resource.Success -> {
                    customProgressDialog.dismiss()
                    Log.d(TAG, "onViewCreated: success " + response.data)

                    response.data?.let {
                        if (it.status == 200) {

                            resetTimer()
                            startTimer()

                            it.otp_token?.let { otp->
                                login_otp.text = "OTP: $otp"
                            }

                            stepInputSmsCode()
                            mMode = MODE_SMS_CODE_INPUT
                        } else if (it.status == 201) {
                            stepInputAgentCode()
                            mMode = MODE_AGENT_CODE_INPUT
                        }
                        else if (it.status == 202){
                            it.data?.let { user ->
                                Log.d(TAG, "onViewCreated: insertUser " + user)
                                viewModel.insertUser(user)
                                user.type_name?.let {
                                    Constants.USER_TYPE = it
                                }
                                user.token?.let {
                                    Constants.TOKEN = "Token $it"
                                }

                                Handler().postDelayed(object:Runnable {
                                    override fun run() {
                                        findNavController().navigate(LoginFragmentDirections.actionGlobalHomeFragment())
                                    }
                                },500)

                            }

                        }
                        else{
                            it.message?.let {
                                Utils.toastIconError(requireActivity(), it)
                            }
                        }

                    }

                }
            }
        }

        viewModel.checkAgentCode.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    Log.d(TAG, "onViewCreated: loading")
                    customProgressDialog.show()
                }
                is Resource.Error -> {
                    customProgressDialog.dismiss()
                    Log.e(TAG, "onViewCreated: error" + response.message)
                    Utils.toastIconError(requireActivity(), response.message)
                }
                is Resource.Success -> {
                    customProgressDialog.dismiss()
                    Log.d(TAG, "onViewCreated: success " + response.data)

                    response.data?.let {
                        if (it.status == 200) {

                            resetTimer()
                            startTimer()


                            it.otp_token?.let { otp->
                                login_otp.text = "OTP: $otp"
                            }

                            stepInputSmsCode()
                            mMode = MODE_SMS_CODE_INPUT
                            Utils.toastIconSuccess(requireActivity(), it.message)
                        } else {
                            stepInputAgentCode()
                            mMode = MODE_AGENT_CODE_INPUT
                            Utils.toastIconError(requireActivity(), it.message)
                        }

                    }

                }
            }
        }

        viewModel.checkSmsCode.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    customProgressDialog.show()
                    Log.d(TAG, "onViewCreated: loading")
                }
                is Resource.Error -> {
                    customProgressDialog.dismiss()
                    Log.e(TAG, "onViewCreated: error" + response.message)
                    Utils.toastIconError(requireActivity(), response.message)
                }
                is Resource.Success -> {
                    customProgressDialog.dismiss()
                    Log.d(TAG, "onViewCreated: success " + response.data)

                    response.data?.let {
                        if (it.status == 200) {
                            stepInputSmsCode()
                            //navigate to homeScreen
                            mMode = MODE_SMS_CODE_INPUT
                            Utils.toastIconSuccess(requireActivity(), it.message)

                            it.data?.let { user ->
                                Log.d(TAG, "onViewCreated: insertUser " + user)
                                viewModel.insertUser(user)
                                user.type_name?.let {
                                    Constants.USER_TYPE = it
                                }
                                user.token?.let {
                                    Constants.TOKEN = "Token $it"
                                }

                                Handler().postDelayed(object:Runnable {
                                    override fun run() {
                                        findNavController().navigate(LoginFragmentDirections.actionGlobalHomeFragment())
                                    }

                                },500)




                            }


                        } else if (it.status == 201) {
                            findNavController().navigate(
                                LoginFragmentDirections.actionLoginFragmentToRegisterFragment(
                                    phone,
                                    agentCode
                                )
                            )
                        } else {
                            stepInputSmsCode()
                            //navigate to register
                            mMode = MODE_SMS_CODE_INPUT
                            Utils.toastIconError(requireActivity(), it.message)
                        }

                    }

                }
            }
        }




        login_btn.setOnClickListener {
            Log.d(TAG, "onViewCreated: login_btn " + mMode)
            if (mMode == MODE_PHONE_INPUT) {
                phone = login_phone_et.text.toString()
                if (phone.length < 9) {
                    Utils.toastIconError(
                        requireActivity(),
                        getString(R.string.telefon_raqamni_toliq_kiriting)
                    )
                    return@setOnClickListener
                }
                phone = "998$phone"
                viewModel.checkPhone(phone)
            } else if (mMode == MODE_AGENT_CODE_INPUT) {
                agentCode = login_agent_code_et.text.toString()
                if (agentCode.isEmpty()) {
                    Utils.toastIconError(requireActivity(), getString(R.string.agent_kodini_toliq_kiriting))
                    return@setOnClickListener
                }

                viewModel.checkAgentCode(phone, agentCode)
            } else if (mMode == MODE_SMS_CODE_INPUT) {
                smsCode = login_code_et.text.toString()
                if (smsCode.length < 6) {
                    Utils.toastIconError(requireActivity(), getString(R.string.tasdiqlash_kodini_toliq_kiriting))
                    return@setOnClickListener
                }

                viewModel.checkSmsCode(phone, smsCode)
            }

        }

        login_re_request_code_tv.setOnClickListener {
            Log.d(TAG, "onViewCreated: login_re_request_code_tv " + mMode)
            if (phone.isNotEmpty() && agentCode.isEmpty()) {
                viewModel.checkPhone(phone)
                stepInputSmsCode()
            } else if (phone.isNotEmpty() && agentCode.isNotEmpty()) {
                viewModel.checkAgentCode(phone, agentCode)
                stepInputSmsCode()
            }
        }

        login_change_phone_tv.setOnClickListener {
            pauseTimer()
            stepInputPhone()
            mMode = MODE_PHONE_INPUT
        }


    }


    private fun stepInputPhone() {
        login_phone_layout.visibility = View.VISIBLE
        login_agent_code_et.visibility = View.GONE
        login_code_et.visibility = View.GONE
        login_desc_tv.visibility = View.VISIBLE
        login_timer_tv.visibility = View.GONE
        login_change_phone_tv.visibility = View.GONE
        login_re_request_code_tv.visibility = View.GONE

        login_desc_tv.text = getString(R.string.enter_phone_number_touse_the_app)

    }

    private fun stepInputAgentCode() {
        login_phone_layout.visibility = View.GONE
        login_agent_code_et.visibility = View.VISIBLE
        login_code_et.visibility = View.GONE
        login_desc_tv.visibility = View.VISIBLE
        login_timer_tv.visibility = View.GONE
        login_change_phone_tv.visibility = View.VISIBLE
        login_re_request_code_tv.visibility = View.GONE

        login_desc_tv.text = getString(R.string.ushbu_raqamdan_foydalanib) + " " + phone

    }

    private fun stepInputSmsCode() {
        login_phone_layout.visibility = View.GONE
        login_agent_code_et.visibility = View.GONE
        login_code_et.visibility = View.VISIBLE
        login_desc_tv.visibility = View.VISIBLE
        login_timer_tv.visibility = View.VISIBLE
        login_change_phone_tv.visibility = View.VISIBLE
        login_re_request_code_tv.visibility = View.GONE

        login_desc_tv.text =
            getString(R.string.tasdiqlash_kodi_ushbu_raqamga_yuborildi) + " " + phone
    }


    private fun startTimer() {
        countDownTimer = object : CountDownTimer(leftTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                leftTime = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                isTimerRunning = false
                login_change_phone_tv.visibility = View.VISIBLE
                login_re_request_code_tv.visibility = View.VISIBLE
            }

        }.start()
        isTimerRunning = true
    }

    private fun updateTimer() {
        val minute = leftTime / 1000 / 60
        val seconds = leftTime / 1000 % 60

        val formattedTime = String.format("%02d:%02d", minute, seconds)
        login_timer_tv.text = formattedTime
    }

    private fun pauseTimer() {
        try {
            countDownTimer.cancel()
            isTimerRunning = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun resetTimer() {
        leftTime = START_TIME
        updateTimer()
    }


    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        pauseTimer()
    }

    companion object {
        val MODE_PHONE_INPUT = "MODE_PHONE_INPUT"
        val MODE_SMS_CODE_INPUT = "MODE_SMS_CODE_INPUT"
        val MODE_AGENT_CODE_INPUT = "MODE_AGENT_CODE_INPUT"
    }

}