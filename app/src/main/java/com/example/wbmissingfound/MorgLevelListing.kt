package com.example.wbmissingfound

import android.R
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wbmissingfound.DBHelper.DatabaseDb
import com.example.wbmissingfound.Model.MorgListRecycleViewCustomAdapter
import com.example.wbmissingfound.Model.UDListRecycleViewCustomAdapter
import com.example.wbmissingfound.Model.UDListRecycleViewModel
import com.example.wbmissingfound.RetroClient.RetroApi.APIService
import com.example.wbmissingfound.RetroClient.RetroApi.ApiUtils
import com.example.wbmissingfound.RetroClient.RetroModel.DistrictsAPIModel
import com.example.wbmissingfound.RetroClient.RetroModel.GetAllDataAPIModel
import com.example.wbmissingfound.RetroClient.RetroModel.GetCaseSubByPsModelClass
import com.example.wbmissingfound.RetroClient.RetroModel.MorgeListDemoModelClass
import com.example.wbmissingfound.RetroClient.RetroModel.PSAll
import com.example.wbmissingfound.RetroClient.RetroModel.UnIdentificationGetAllDataAPIModel
import com.example.wbmissingfound.databinding.ActivityMorgLevelListingAndSubmitBinding
import com.example.wbmissingfound.sharedStorage.SharedPreferenceStorage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MorgLevelListing : BaseActivity() {
    private var progressDialog: ProgressDialog? = null

    private  lateinit var binding:ActivityMorgLevelListingAndSubmitBinding
    var infoArrayList: ArrayList<GetCaseSubByPsModelClass.Information> = ArrayList<GetCaseSubByPsModelClass.Information>()
    var infoArrayListGRP: ArrayList<GetCaseSubByPsModelClass.Information> = ArrayList<GetCaseSubByPsModelClass.Information>()
    var infoArrayListPS: ArrayList<GetCaseSubByPsModelClass.Information> = ArrayList<GetCaseSubByPsModelClass.Information>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMorgLevelListingAndSubmitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

        binding.recyclerviewList.layoutManager = layoutManager

        //val recyclerview = findViewById<RecyclerView>(R.id.recyclerview_list)


        binding.recyclerviewList.layoutManager = LinearLayoutManager(this)

     Log.e("Test","test")
        if (checkForInternet(this@MorgLevelListing)) {
            getCaseSubByPs()
        }
        else{
            Toast.makeText(
                this@MorgLevelListing,
                "Please Check Your Internet Connection for show deadbody list",
                Toast.LENGTH_LONG
            ).show()
        }
        //getCaseSubByPs()

//         val questions: List<MorgeListDemoModelClass> = listOf(
//             MorgeListDemoModelClass("AVCF455",1),
//             MorgeListDemoModelClass("AGGSH2536", 2),
//             MorgeListDemoModelClass("AGGSH2536", 3),
//             MorgeListDemoModelClass("AGGSH2536", 3),
//             MorgeListDemoModelClass("AGGSH2536", 3),
//             MorgeListDemoModelClass("AGGSH2536", 3),
//             MorgeListDemoModelClass("AGGSH2536", 3),
//             MorgeListDemoModelClass("AGGSH2536", 3),
//             MorgeListDemoModelClass("AGGSH2536", 3),
//             MorgeListDemoModelClass("AGGSH2536", 3),
//             MorgeListDemoModelClass("AGGSH2536", 3),
//             MorgeListDemoModelClass("AGGSH2536", 4)
//         )


        val data = ArrayList<MorgeListDemoModelClass>()
       // val adapter = MorgListRecycleViewCustomAdapter(this,questions)






    }


    private fun getCaseSubByPs() {

        var mAPIService: APIService? = null
        progressDialogCall(this@MorgLevelListing)
        mAPIService = ApiUtils.apiService
        SharedPreferenceStorage.getValue(applicationContext, SharedPreferenceStorage.JWT_TOKEN, "")
            ?.let {
                mAPIService.getCaseSubByPs(it)
                    .enqueue(object : Callback<GetCaseSubByPsModelClass> {

                        override fun onResponse(
                            call: Call<GetCaseSubByPsModelClass>,
                            response: Response<GetCaseSubByPsModelClass>
                        ) {
                            try {
                                if (response.isSuccessful) {

                                  //  Log.e("LogIn_Status", response.body()!!.error_code.toString())

                                    if(response.body()!!.status.equals("success")){
                                        infoArrayList = response.body()!!.data as ArrayList<GetCaseSubByPsModelClass.Information>

                                      /*  for (x in infoArrayList) {
                                            if(x.ps_name.contains("G.R.P")){
                                                infoArrayListGRP.add(x)
                                                Log.e("list", infoArrayListGRP.size.toString())
                                                val adapter = MorgListRecycleViewCustomAdapter(this@MorgLevelListing,infoArrayList)
                                                if(infoArrayListGRP.size>0){
                                                    binding.recyclerviewList.adapter=adapter
                                                    binding.linearLyNoResultList.visibility=View.GONE
                                                }else{
                                                    binding.linearLyNoResultList.visibility= View.VISIBLE
                                                    binding.recyclerviewList.visibility=View.GONE
                                                }
                                            }else{

                                            }
                                        }
*/
                                        Log.e("list", infoArrayList.size.toString())
                                        val adapter = MorgListRecycleViewCustomAdapter(this@MorgLevelListing,infoArrayList)
                                        if(infoArrayList.size>0){
                                            binding.recyclerviewList.adapter=adapter
                                            binding.linearLyNoResultList.visibility=View.GONE
                                        }else{
                                            binding.linearLyNoResultList.visibility= View.VISIBLE
                                            binding.recyclerviewList.visibility=View.GONE
                                        }
                                    }
//                                    if (response.body()!!.status.toString().equals("success")) {
//                                        val preferences =
//                                            getSharedPreferences("DATA", Context.MODE_PRIVATE)
//                                        val preferencesEditor = preferences.edit()
//                                        preferencesEditor.putBoolean("saved", true)
//                                        preferencesEditor.putString(
//                                            "case_datils",
//                                            response.body()!!.data.toString()
//                                        )
//
//
//                                        preferencesEditor.apply()
////                                        districtArrayList = response.body()!!.district
//
//
//
//
//                                        closeProgressDialogCall()
//
//                                    } else {
//                                        Toast.makeText(
//                                            this@MorgLevelListing,
//                                            response.body()!!.status.toString(),
//                                            Toast.LENGTH_LONG
//                                        ).show()
//                                    }
                                    closeProgressDialogCall()

                                }
                            } catch (exception: java.lang.Exception) {
                                closeProgressDialogCall()
                                Toast.makeText(
                                    this@MorgLevelListing,
                                    "Some issue in server end",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                        }

                        override fun onFailure(
                            call: Call<GetCaseSubByPsModelClass>,
                            t: Throwable
                        ) {
                            intent = Intent(baseContext, MorgLevelListing::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
            }


    }

    /*fun progressDialogCall(activity: Activity) {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.show()
    }

    fun closeProgressDialogCall() {
        if (progressDialog != null) {
            progressDialog!!.cancel()

        }

    }*/
}