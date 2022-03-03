package uz.techie.mexmash.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_cabinet.*
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.fragment_promocode_hsitory.*
import uz.techie.mexmash.MainActivity
import uz.techie.mexmash.R
import uz.techie.mexmash.adapters.PromoCodeAdapter
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.dialog.CustomProgressDialog
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.Resource
import uz.techie.mexmash.util.SharedPref
import uz.techie.mexmash.util.Utils

@AndroidEntryPoint
class PromoCodeHistoryFragment:Fragment(R.layout.fragment_promocode_hsitory){

    val  viewModel:AppViewModel by viewModels()
    lateinit var promoCodeAdapter: PromoCodeAdapter
    lateinit var customProgressDialog: CustomProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        customProgressDialog = CustomProgressDialog(requireContext())
        promoCodeAdapter = PromoCodeAdapter()

        promocode_history_recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = promoCodeAdapter
        }

        if (Constants.USER_TYPE == Constants.USER_TYPE_DEALER){
            promocode_history_code_tv.text = getString(R.string.kg)
        }


        viewModel.promoCodeHistory.observe(viewLifecycleOwner){ response->
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
                    response.data?.let { promoCodeHistory->
                        if (promoCodeHistory.status == 200 || promoCodeHistory.status == 201){
                            promoCodeHistory.data?.let {
                                promoCodeAdapter.differ.submitList(it)
                            }
                        }
                        else{
                            Utils.toastIconError(requireActivity(), promoCodeHistory.message)
                        }
                    }
                }

            }
        }
        viewModel.loadPromoCodeHistory(Constants.TOKEN)

    }



    private fun initToolbar() {
//        toolbar_layout.setBackgroundColor(Color.TRANSPARENT)
        toolbar_title.text = getString(R.string.tarix)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
//        toolbar_btn_back.isVisible = false
    }

}