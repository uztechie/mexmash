package uz.techie.mexmash.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_cabinet.*
import kotlinx.android.synthetic.main.fragment_news.*
import kotlinx.android.synthetic.main.horizontal_progressbar.*
import uz.techie.mexmash.MainActivity
import uz.techie.mexmash.R
import uz.techie.mexmash.adapters.NewsAdapter
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.dialog.ConfirmDialog
import uz.techie.mexmash.dialog.CustomProgressDialog
import uz.techie.mexmash.models.News
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.Resource
import uz.techie.mexmash.util.SharedPref
import uz.techie.mexmash.util.Utils

@AndroidEntryPoint
class NewsFragment:Fragment(R.layout.fragment_news){

    val  viewModel:AppViewModel by viewModels()
    lateinit var newsAdapter: NewsAdapter
    lateinit var customProgressDialog: CustomProgressDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        customProgressDialog = CustomProgressDialog(requireContext())
        newsAdapter = NewsAdapter()

        news_recyclerview.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = newsAdapter
        }



        viewModel.news.observe(viewLifecycleOwner){ response->
            when(response){
                is Resource.Loading->{
                    horizontal_progressbar.visibility = View.VISIBLE
//                    customProgressDialog.show()
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
                        viewModel.insertNews(it)
                    }
                }

            }
        }
        viewModel.loadNews(Constants.TOKEN)

        viewModel.getNews().observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                customProgressDialog.dismiss()
            }
            newsAdapter.differ.submitList(it)

        }


    }



    private fun initToolbar() {
//        toolbar_layout.setBackgroundColor(Color.TRANSPARENT)
        toolbar_title.text = getString(R.string.yangiliklar)
        toolbar_btn_back.setOnClickListener {
            findNavController().popBackStack()
        }
//        toolbar_btn_back.isVisible = false
    }

}