package com.example.wbmissingfound

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wbmissingfound.RetroClient.RetroApi.APIService
import com.example.wbmissingfound.RetroClient.RetroApi.ApiUtils
import com.example.wbmissingfound.RetroClient.RetroModel.*
import com.example.wbmissingfound.custom.DatePicker
import com.example.wbmissingfound.databinding.ActivityIdentifiedFormBinding
import com.example.wbmissingfound.sharedStorage.SharedPreferenceStorage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IdentifiedFormActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: ActivityIdentifiedFormBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private var used_current_loc: Boolean = false

    var jwt_token: String = ""

    var form_status = 1

    var districtArrayList: List<GetAllDataDistrictAll> = ArrayList()
    var districtArrayAdapter: ArrayAdapter<GetAllDataDistrictAll>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIdentifiedFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Identified Form"

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.locationSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                used_current_loc = isChecked
                checkLocation()
            } else {
                used_current_loc = isChecked
                binding.tietLatitude.setText("")
                binding.tietLongitude.setText("")
            }
        })

        DistrictAPICall()

        binding.spinnerOccurrenceDist.onItemSelectedListener = this

        binding.llTvCaseDate.setOnClickListener {
            val myCase = DatePicker(
                this@IdentifiedFormActivity,
                binding.tvCaseDate,
                binding.tvCaseDate.text.toString()
            )
            myCase.selectDate()
            myCase.setFutureDateEnable(false)
        }


        binding.btnSubmit.setOnClickListener {
            if (checkValidation())
                SubmitIDData()
        }
    }

    fun checkLocation() {
        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            check_and_set_currentLocation()
        }
    }

    fun check_and_set_currentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Got last known location. In some rare situations this can be null.

                if (binding.locationSwitch.isChecked) {
                    binding.tietLatitude.setText(location!!.latitude.toString())
                    binding.tietLongitude.setText(location.longitude.toString())
                } else {
                    binding.tietLatitude.setText("")
                    binding.tietLongitude.setText("")
                }

            }

    }

    fun checkValidation(): Boolean {
        var noError = true
        if (binding.spinnerOccurrenceDist.selectedItemPosition.equals(-1)) {
            binding.spinnerOccurrenceDist.requestFocus()
            showToastMessage("Please provide District")
            noError = false
        } else if (binding.spinnerOccurrencePs.selectedItemPosition.equals(-1)) {
            binding.spinnerOccurrencePs.requestFocus()
            showToastMessage("Please provide Police Station")
            noError = false
        } else if (binding.tietCaseNumber.text.toString().isEmpty()) {
            binding.tietCaseNumber.isFocusableInTouchMode = true
            binding.tietCaseNumber.requestFocus()
            showToastMessage("Please provide Case Reference NO")
            noError = false
        } else if (binding.tvCaseDate.text.toString().isEmpty()) {
            binding.tvCaseDate.isFocusableInTouchMode = true
            binding.tvCaseDate.requestFocus()
            showToastMessage("Please provide Case Date")
            noError = false
        } else if (binding.tietPlaceWhereDeadBodyFound.text.toString().isEmpty()) {
            binding.tietPlaceWhereDeadBodyFound.isFocusableInTouchMode = true
            binding.tietPlaceWhereDeadBodyFound.requestFocus()
            showToastMessage("Please provide Place Where Deadbody Found")
            noError = false
        }  else if (binding.tietUdCaseOfficerName.text.toString().isEmpty()) {
            binding.tietUdCaseOfficerName.isFocusableInTouchMode = true
            binding.tietUdCaseOfficerName.requestFocus()
            showToastMessage("Please provide Ud Case Officer Name ")
            noError = false
        }else if (binding.tietVictimName.text.toString().isEmpty()){
            binding.tietVictimName.isFocusableInTouchMode = true
            binding.tietVictimName.requestFocus()
            showToastMessage("Please provide Victim's Name ")
            noError = false
        }else if (binding.tietVictimAge.text.toString().isEmpty()){
            binding.tietVictimAge.isFocusableInTouchMode = true
            binding.tietVictimAge.requestFocus()
            showToastMessage("Please provide Victim's Age ")
            noError = false
        }else if (!binding.rbMale.isChecked && !binding.rbFemale.isChecked && !binding.rbOther.isChecked) {
            binding.rgGender.isFocusableInTouchMode = true
            binding.rgGender.requestFocus()
            showToastMessage("Please select Gender")
            noError = false
        }


        return noError

    }

    private fun DistrictAPICall() {
        var mAPIService: APIService? = null
        progressDialogCall(this@IdentifiedFormActivity)
        mAPIService = ApiUtils.apiService
        jwt_token =
            SharedPreferenceStorage.getValue(this, SharedPreferenceStorage.JWT_TOKEN, "").toString()
        mAPIService.GetAllDataAPI(jwt_token)
            .enqueue(object : Callback<GetAllDataAPIModel> {

                override fun onResponse(
                    call: Call<GetAllDataAPIModel>,
                    response: Response<GetAllDataAPIModel>
                ) {
                    if (response.code() == 400) {
                        closeProgressDialogCall()
                        showAlertDialogMessage(this@IdentifiedFormActivity, "SERVER ERROR !!!")
                    } else {
                        try {
                            if (response.isSuccessful) {
                                Log.e("LogIn_Status", response.body()!!.error_code.toString())
                                Log.e("LogIn_Token", response.body()!!.status.toString())
                                if (response.body()!!.status.toString().equals("success")) {

                                    districtArrayList = response.body()!!.district

                                    districtArrayAdapter = ArrayAdapter(
                                        baseContext,
                                        android.R.layout.simple_spinner_dropdown_item,
                                        districtArrayList
                                    )

                                    binding.spinnerOccurrenceDist.adapter = districtArrayAdapter


                                } else {
                                    Toast.makeText(
                                        this@IdentifiedFormActivity,
                                        response.body()!!.status.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }
                            closeProgressDialogCall()
                        } catch (exception: java.lang.Exception) {
                            Toast.makeText(
                                this@IdentifiedFormActivity,
                                "Some issue in server end",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }
                }

                override fun onFailure(call: Call<GetAllDataAPIModel>, t: Throwable) {
                    showAlertDialogMessage(this@IdentifiedFormActivity, "SERVER ERROR !!!")
                }
            })


    }

    private fun getGender(): Int {
        var gen = 0
        if (binding.rbMale.isChecked)
            gen = 1
        else if (binding.rbFemale.isChecked)
            gen = 2
        else if (binding.rbOther.isChecked)
            gen = 3

        return gen
    }

    fun SubmitIDData() {
        progressDialogCall(this@IdentifiedFormActivity)
        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService
        mAPIService.IDDataSubmitApi(
            jwt_token,
            form_status,
            districtArrayList.get(binding.spinnerOccurrenceDist.selectedItemPosition).district_id.toString(),
            districtArrayList.get(binding.spinnerOccurrenceDist.selectedItemPosition).ps.get(binding.spinnerOccurrencePs.selectedItemPosition).ps_id.toString(),
            binding.tietCaseNumber.text.toString().trim(),
            binding.tvCaseDate.text.toString().trim(),
            binding.tietLatitude.text.toString().trim(),
            binding.tietLongitude.text.toString().trim(),
            binding.tietPlaceWhereDeadBodyFound.text.toString().trim(),
            binding.tietUdCaseOfficerName.text.toString().trim(),
            binding.tietUdCaseOfficerContactNo.text.toString().trim(),
            binding.tietVictimName.text.toString().trim(),
            binding.tietVictimAge.text.toString().trim(),
            getGender()

        ).enqueue(object : Callback<UDDataSubmitApiResponse> {
            override fun onResponse(
                call: Call<UDDataSubmitApiResponse>, response: Response<UDDataSubmitApiResponse>
            ) {
                if (response.code() == 200) {
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message", response.body()!!.message.toString())

                            if (response.body()!!.message.toString().equals("Success")) {

                                MessageDialog()

                            } else {
                                showAlertDialogMessage(
                                    this@IdentifiedFormActivity,
                                    "SERVER ERROR !!!"
                                )
                            }

                        }
                    } catch (exception: java.lang.Exception) {
                        Toast.makeText(
                            this@IdentifiedFormActivity,
                            "Some issue in server end",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                } else {
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@IdentifiedFormActivity,
                        "SERVER ERROR!!! Please try after sometime...",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<UDDataSubmitApiResponse>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@IdentifiedFormActivity,
                    "SERVER ERROR !!!"
                )
            }
        })
    }

    private fun MessageDialog() {
        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@IdentifiedFormActivity)
        alertDialog.setTitle(" ")
        alertDialog.setIcon(R.drawable.ok_sign)
        alertDialog.setMessage("Successfully Submitted...")
        alertDialog.setPositiveButton(
            "OK"
        ) { dialog, id ->
            val accountsIntent = Intent(this@IdentifiedFormActivity, MainActivity::class.java)
            startActivity(accountsIntent)
            finish()
        }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    override fun onItemSelected(adpterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

        when (adpterView?.id) {
            R.id.spinner_occurrence_dist -> {
                val district = districtArrayList[position]
                val policeStationArrayAdapter: ArrayAdapter<GetAllDataPSAll> = ArrayAdapter<GetAllDataPSAll>(
                    baseContext, android.R.layout.simple_spinner_dropdown_item, district.ps
                )
                binding.spinnerOccurrencePs.adapter = policeStationArrayAdapter
            }

            else -> {
                showToastMessage("Joy")
            }

        }


    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}