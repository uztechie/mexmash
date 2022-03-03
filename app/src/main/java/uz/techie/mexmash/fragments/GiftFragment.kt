package uz.techie.mexmash.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_gift.*
import kotlinx.android.synthetic.main.horizontal_progressbar.*
import uz.techie.mexmash.R
import uz.techie.mexmash.adapters.PrizeAdapter
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.dialog.CustomProgressDialog
import uz.techie.mexmash.models.Prize
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.Resource
import uz.techie.mexmash.util.SharedPref
import uz.techie.mexmash.util.Utils

@AndroidEntryPoint
class GiftFragment:Fragment(R.layout.fragment_gift) {

    private val viewModel by viewModels<AppViewModel>()
    private lateinit var prizeAdapter: PrizeAdapter
    private lateinit var customProgressDialog: CustomProgressDialog
    private var token = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()


        customProgressDialog = CustomProgressDialog(requireContext())
        prizeAdapter = PrizeAdapter(object : PrizeAdapter.PrizeAdapterCallback {
            override fun onItemClick(url: String) {
                findNavController().navigate(GiftFragmentDirections.actionGiftFragmentToShowImageFragment(url))
            }
        })

        prize_recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = prizeAdapter
        }

        getPrizes()



    }

    override fun onStart() {
        super.onStart()
        loadPrizes()
    }

    private fun getPrizes(){
        viewModel.getPrizes().observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                customProgressDialog.dismiss()
            }
            prizeAdapter.differ.submitList(it)
        }
    }

    fun loadPrizes(){
        viewModel.prizes.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    horizontal_progressbar.visibility = View.VISIBLE
                    customProgressDialog.show()
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

                        var prizeList = it.toMutableList()

                        viewModel.getUserLive().observe(viewLifecycleOwner){
                            if (it.isNotEmpty()){
                                val user = it[0]
                                val prize = Prize(-1)
                                prize.kg = user.kg
                                prize.image = ""
                                prize.point = user.point
                                prize.name = user.first_name + " "+user.last_name

                                prizeList.add(prize)
                                viewModel.insertPrizes(prizeList)

                                println("addPrizes "+prizeList)
                            }
                        }



                    }
                }

            }
        }

        viewModel.loadPrizes(Constants.TOKEN)
    }


    private fun initToolbar() {
//        toolbar_layout.setBackgroundColor(Color.TRANSPARENT)
        toolbar_title.text = getString(R.string.sovrinlar)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
        toolbar_btn_back.visibility = View.INVISIBLE
    }


    override fun onStop() {
        super.onStop()
        viewModel.prizes = MutableLiveData()
    }
}