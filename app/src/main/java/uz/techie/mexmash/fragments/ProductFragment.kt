package uz.techie.mexmash.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_product.*
import kotlinx.android.synthetic.main.horizontal_progressbar.*
import uz.techie.mexmash.MainActivity
import uz.techie.mexmash.R
import uz.techie.mexmash.adapters.ProductAdapter
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.dialog.CustomProgressDialog
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.Resource
import uz.techie.mexmash.util.SharedPref
import uz.techie.mexmash.util.Utils

@AndroidEntryPoint
class ProductFragment:Fragment(R.layout.fragment_product) {

    var phone = ""
    val  viewModel:AppViewModel by viewModels()
    lateinit var productAdapter: ProductAdapter
    lateinit var customProgressDialog: CustomProgressDialog


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()
        customProgressDialog = CustomProgressDialog(requireContext())
        productAdapter = ProductAdapter()

        product_recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = productAdapter
        }


        viewModel.products.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
//                    customProgressDialog.show()
                    horizontal_progressbar.visibility = View.VISIBLE
                }
                is Resource.Error->{
                    horizontal_progressbar.visibility = View.GONE
                    customProgressDialog.dismiss()
                    response.message?.let {
                        Utils.toastIconError(requireActivity(), it)
                    }
                }
                is Resource.Success->{
                    horizontal_progressbar.visibility = View.GONE
                    customProgressDialog.dismiss()
                    response.data?.let {
                        viewModel.insertProducts(it)
                    }
                }

            }
        }

        viewModel.loadProducts(Constants.TOKEN)

        viewModel.getProducts().observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                customProgressDialog.dismiss()
            }
            productAdapter.differ.submitList(it)
        }


    }

    private fun initToolbar() {
//        toolbar_layout.setBackgroundColor(Color.TRANSPARENT)
        toolbar_title.text = getString(R.string.mahsulotlar)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
        toolbar_btn_back.visibility = View.INVISIBLE
    }

    override fun onStop() {
        super.onStop()
        viewModel.products = MutableLiveData()
    }

}