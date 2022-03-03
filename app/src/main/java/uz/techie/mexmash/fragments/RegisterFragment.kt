package uz.techie.mexmash.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.dialog_region_list.*
import kotlinx.android.synthetic.main.fragment_register.*
import uz.techie.mexmash.R
import uz.techie.mexmash.adapters.DistrictAdapter
import uz.techie.mexmash.adapters.QuarterAdapter
import uz.techie.mexmash.adapters.RegionAdapter
import uz.techie.mexmash.data.AppViewModel
import uz.techie.mexmash.dialog.CustomProgressDialog
import uz.techie.mexmash.dialog.PositiveNegativeDialog
import uz.techie.mexmash.models.District
import uz.techie.mexmash.models.Quarter
import uz.techie.mexmash.models.Region
import uz.techie.mexmash.util.Constants
import uz.techie.mexmash.util.Resource
import uz.techie.mexmash.util.SharedPref
import uz.techie.mexmash.util.Utils
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register),
    PositiveNegativeDialog.PositiveNegativeListener {
    private val TAG = "RegisterFragment"
    lateinit var customProgressDialog: CustomProgressDialog
    lateinit var positiveNegativeDialog: PositiveNegativeDialog

    private val viewModel by viewModels<AppViewModel>()
    var phone = ""
    var agentCode = ""

    private var regionId = -1
    private var districtId = -1
    private var quarterId = -1
    private var birthDay = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                   findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        customProgressDialog = CustomProgressDialog(requireContext())
        positiveNegativeDialog = PositiveNegativeDialog(requireContext(), this)

        arguments?.let {
            phone = RegisterFragmentArgs.fromBundle(it).phone
            agentCode = RegisterFragmentArgs.fromBundle(it).agentCode

            register_phone_tv.text = phone

            Log.d(TAG, "onViewCreated: phone "+phone)
            Log.d(TAG, "onViewCreated: agentCode "+agentCode)
        }

        loadRegions()
        loadDistricts()
        loadQuarters()



        register_region_tv.setOnClickListener {
            regionBottomSheet()
        }

        register_district_tv.setOnClickListener {
            districtBottomSheet()
        }

        register_quarter_tv.setOnClickListener {
            quarterBottomSheet()
        }

        register_birthday_tv.setOnClickListener {
            showDateDialog()
        }


        register_btn_register.setOnClickListener {
            register()
        }

        register_agreement.setOnCheckedChangeListener { compoundButton, b ->
            register_btn_register.isEnabled = b
        }



    }

    private fun register() {
        val name = register_name.text.toString().trim()
        val lastname = register_lastname.text.toString().trim()
        val street = register_street_et.text.toString().trim()

        if (name.isEmpty()){
            Utils.toastIconError(requireActivity(), getString(R.string.ism_kiritmadingiz))
            return
        }
        if (lastname.isEmpty()){
            Utils.toastIconError(requireActivity(), getString(R.string.familiya_kiritmadingiz))
            return
        }
        if (regionId<0){
            Utils.toastIconError(requireActivity(), getString(R.string.viloyatni_tanlang))
            return
        }
        if (districtId<0){
            Utils.toastIconError(requireActivity(), getString(R.string.tuman_tanlang))
            return
        }
        if (quarterId<0){
            Utils.toastIconError(requireActivity(), getString(R.string.mahalla_tanlang))
            return
        }
        if (birthDay.isEmpty()){
            Utils.toastIconError(requireActivity(), getString(R.string.tugulgan_kuningizni_tanlang))
            return
        }

        val map = HashMap<String, Any>()
        map["phone_number"] = phone
        map["first_name"] = name
        map["last_name"] = lastname
        map["birthday"] = birthDay
        map["address"] = quarterId
        map["my_agent_code"] = agentCode
        map["street_name"] = street

        viewModel.register = MutableLiveData()
        viewModel.register(map)

        viewModel.register.observe(viewLifecycleOwner){response->
            when(response){
                is Resource.Loading->{
                    customProgressDialog.show()
                }
                is Resource.Error->{
                    customProgressDialog.dismiss()
                    response.message?.let {
                        Utils.toastIconError(requireActivity(), response.message)
                    }
                }
                is Resource.Success->{
                    customProgressDialog.dismiss()
                    response.data?.let {
                        if (it.status == 200 || it.status == 201){
                            it.data?.let {user->
                                viewModel.insertUser(user)

                                user.type_name?.let {
                                    Constants.USER_TYPE = it
                                }

                                user.token?.let {
                                    SharedPref.token = "Token $it"
                                }
                            }
                            Utils.toastIconSuccess(requireActivity(), it.message)
                            findNavController().navigate(RegisterFragmentDirections.actionGlobalHomeFragment())
                        }
                        else{
                            Utils.toastIconError(requireActivity(), it.message)
                        }
                    }
                }
            }
        }



    }

    private fun regionBottomSheet() {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.dialog_region_list)

        val regionAdapter = RegionAdapter(object : RegionAdapter.RegionAdapterCallBack {
            override fun onItemClick(region: Region) {
                register_region_tv.text = region.name
                regionId = region.id

                districtId = -1
                quarterId = -1
                register_district_tv.text = getString(R.string.tuman_tanlang)
                register_quarter_tv.text = getString(R.string.mahalla_tanlang)

                dialog.dismiss()
            }

        })

        viewModel.getRegions().observe(viewLifecycleOwner) {
            regionAdapter.differ.submitList(it)
            dialog.show()
            dialog.dialog_region_recyclerview.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = regionAdapter
            }


        }


    }

    private fun districtBottomSheet() {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.dialog_region_list)

        val districtAdapter = DistrictAdapter(object : DistrictAdapter.DistrictAdapterCallBack {
            override fun onItemClick(district: District) {
                register_district_tv.text = district.name
                districtId = district.id
                dialog.dismiss()

                quarterId = -1
                register_quarter_tv.text = getString(R.string.mahalla_tanlang)
            }

        })

        viewModel.getDistricts(regionId).observe(viewLifecycleOwner) {
            districtAdapter.differ.submitList(it)
            dialog.show()
            dialog.dialog_region_recyclerview.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = districtAdapter
            }


        }


    }

    private fun quarterBottomSheet() {
        val dialog = BottomSheetDialog(requireContext(), R.style.bottomSheetStyle)
        dialog.setContentView(R.layout.dialog_region_list)

        val quarterAdapter = QuarterAdapter(object : QuarterAdapter.QuarterAdapterCallBack {
            override fun onItemClick(quarter: Quarter) {
                register_quarter_tv.text = quarter.name
                quarterId    = quarter.id
                dialog.dismiss()
            }

        })

        viewModel.getQuarters(districtId).observe(viewLifecycleOwner) {
            quarterAdapter.differ.submitList(it)
            dialog.show()
            dialog.dialog_region_recyclerview.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = quarterAdapter
            }


        }


    }



    private fun loadRegions(){
        viewModel.regions.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    customProgressDialog.show()
                }
                is Resource.Error -> {
                    customProgressDialog.dismiss()

                    response.message?.let {
                        positiveNegativeDialog.show()
                        positiveNegativeDialog.setData(
                            getString(R.string.xatolik),
                            it,
                            false,
                            false
                        )
                    }

                }
                is Resource.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let {
                        Log.d(TAG, "regionBottomSheet: success submitted " + it)
                        viewModel.insertRegions(it)
                    }
                }
            }
        }
        viewModel.loadRegions()
    }
    private fun loadDistricts(){
        viewModel.districts.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    customProgressDialog.show()
                }
                is Resource.Error -> {
                    customProgressDialog.dismiss()
                    response.message?.let {
                        positiveNegativeDialog.show()
                        positiveNegativeDialog.setData(
                            getString(R.string.xatolik),
                            it,
                            false,
                            false
                        )
                    }

                }
                is Resource.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let {
                        Log.d(TAG, "regionBottomSheet: success submitted " + it)
                        viewModel.insertDistricts(it)
                    }
                }
            }
        }
        viewModel.loadDistricts()
    }
    private fun loadQuarters(){
        viewModel.quarters.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    customProgressDialog.show()
                }
                is Resource.Error -> {
                    customProgressDialog.dismiss()
                    response.message?.let {
                        positiveNegativeDialog.show()
                        positiveNegativeDialog.setData(
                            getString(R.string.xatolik),
                            it,
                            false,
                            false
                        )
                    }

                }
                is Resource.Success -> {
                    customProgressDialog.dismiss()
                    response.data?.let {
                        Log.d(TAG, "regionBottomSheet: success submitted " + it)
                        viewModel.insertQuarters(it)
                    }
                }
            }
        }
        viewModel.loadQuarters()
    }



    private fun showDateDialog(){
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)


        val dateDialog = DatePickerDialog(requireContext(),R.style.customDatePickerStyle, DatePickerDialog.OnDateSetListener{ view, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)

            val date = calendar.time

            Log.d(TAG, "showDateDialog: ${Utils.reformatDate(date)}")
            register_birthday_tv.setText(Utils.reformatDate(date))
            birthDay = Utils.reformatDate(date)

        }, currentYear, currentMonth, currentDay)
        dateDialog.show()
    }

    private fun initToolbar() {
        toolbar_layout.setBackgroundColor(Color.TRANSPARENT)
        toolbar_title.text = getString(R.string.ro_yxatdan_o_tish)
        toolbar_btn_back.setOnClickListener {
            findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }
    }


    override fun onBackBtnClick(shouldGoBack: Boolean) {

    }


}