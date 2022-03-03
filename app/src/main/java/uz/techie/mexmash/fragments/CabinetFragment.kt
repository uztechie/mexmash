package uz.techie.mexmash.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_cabinet.*
import kotlinx.android.synthetic.main.fragment_update_user.*
import uz.techie.mexmash.BuildConfig
import uz.techie.mexmash.R
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.dialog.ConfirmDialog
import uz.techie.mexmash.dialog.CustomProgressDialog
import uz.techie.mexmash.models.User
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.Resource
import uz.techie.mexmash.util.SharedPref
import uz.techie.mexmash.util.Utils

@AndroidEntryPoint
class CabinetFragment:Fragment(R.layout.fragment_cabinet), ConfirmDialog.ConfirmDialogListener {

    private val  viewModel:AppViewModel by viewModels()
    lateinit var confirmDialog:ConfirmDialog
    private  val TAG = "CabinetFragment"
    private var user:User? = null
    private lateinit var customProgressDialog: CustomProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        confirmDialog = ConfirmDialog(requireContext(), this)
        customProgressDialog = CustomProgressDialog(requireContext())

        viewModel.getUserLive().observe(viewLifecycleOwner) { users ->
            if (users.isNotEmpty()) {
                user = users[0]
                Log.d(TAG, "getUser: " + user?.phone)
                cabinet_user_fullname.text = user?.first_name+ " " +user?.last_name
                cabinet_user_phone.text = user?.phone

                user?.first_name?.let {
                    cabinet_user_image.text = Utils.getFirstLetter(it).toString()
                }

            }
        }



        cabinet_logout.setOnClickListener {
            confirmDialog.show()
            confirmDialog.setTitle(getString(R.string.tizimdan_chiqish))
            confirmDialog.setMessage(getString(R.string.siz_rostdan_tizimdan_chiqmoqchimisiz))
        }

        cabinet_history.setOnClickListener {
            findNavController().navigate(CabinetFragmentDirections.actionCabinetFragmentToPromoCodeHistoryFragment())
        }

        cabinet_user_card.setOnClickListener {
            updateUserBottomSheet()
        }

        cabinet_version_tv.text = getString(R.string.versiya)+" "+BuildConfig.VERSION_NAME

    }

    override fun onOkClick() {
        viewModel.deleteUser()
        Constants.TOKEN = ""
        Constants.USER_TYPE = ""

        user?.type_name?.let {
            Firebase.messaging.unsubscribeFromTopic(it)
        }
        Firebase.messaging.unsubscribeFromTopic(Constants.TOPIC_ALL)

        findNavController().navigate(CabinetFragmentDirections.actionGlobalLoginFragment())
    }

    private fun initToolbar() {
//        toolbar_layout.setBackgroundColor(Color.TRANSPARENT)
        toolbar_title.text = getString(R.string.kabinet)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
        toolbar_btn_back.visibility = View.INVISIBLE
    }



    private fun updateUserBottomSheet() {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.fragment_update_user)
        dialog.show()
        user?.let {
            dialog.update_name.setText(it.first_name)
            dialog.update_lastname.setText(it.last_name)
        }

        dialog.update_btn_save.setOnClickListener {
            val name = dialog.update_name.text.toString().trim()
            val lastname = dialog.update_lastname.text.toString().trim()


            if (name.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.ism_kiritmadingiz))
                return@setOnClickListener
            }
            if (lastname.isEmpty()){
                Utils.toastIconError(requireActivity(), getString(R.string.familiya_kiritmadingiz))
                return@setOnClickListener
            }

            val map = HashMap<String, Any>()
            map["first_name"] = name
            map["last_name"] = lastname

            viewModel.updateProfile(Constants.TOKEN, map)

            viewModel.updateProfileResponse.observe(viewLifecycleOwner){response->
                when(response){
                    is Resource.Loading->{
                        customProgressDialog.show()
                    }
                    is Resource.Error->{
                        customProgressDialog.dismiss()
                        response.message?.let {
                            Utils.toastIconError(requireActivity(), it)
                        }
                    }
                    is Resource.Success->{
                        customProgressDialog.dismiss()
                        response.data?.let { login->
                            if (login.status == 200){
                                Utils.toastIconSuccess(requireActivity(), getString(R.string.malumotlar_saqlandi))
                                user?.first_name = name
                                user?.last_name = lastname
                                viewModel.insertUser(user!!)
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }


        }

    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }


}