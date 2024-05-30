package com.example.wbmissingfound


import android.content.Intent
import android.os.Bundle

import android.util.Log
import android.widget.Toast
import com.example.wbmissingfound.Helper.AppInfoHelper
import com.example.wbmissingfound.Helper.Constants
import com.example.wbmissingfound.RetroClient.RetroApi.APIService
import com.example.wbmissingfound.RetroClient.RetroApi.ApiUtils
import com.example.wbmissingfound.RetroClient.RetroModel.GetLatestApkModel
import com.example.wbmissingfound.RetroClient.RetroModel.LoginAPIModel

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.wbmissingfound.databinding.ActivityLoginBinding
import com.example.wbmissingfound.sharedStorage.SharedPreferenceStorage
import com.google.gson.JsonParser

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val activity = this@LoginActivity

    val version = BuildConfig.VERSION_NAME


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)



        Log.e("version_name...",AppInfoHelper.getInfo(baseContext, AppInfoHelper.VERSION_NAME))
        Log.e("version_Code...",AppInfoHelper.getInfo(baseContext, AppInfoHelper.VERSION_CODE))
       // getLatestApk()
        binding.tvVersion.text = "Version:" + version

        if (SharedPreferenceStorage.getValue(
                applicationContext,
                SharedPreferenceStorage.JWT_TOKEN,
                ""
            )!!.isNotEmpty()
        ) {
            Log.e("version_name",AppInfoHelper.getInfo(baseContext, AppInfoHelper.VERSION_NAME))
            loadDashActivity()
        }
        binding.login.setOnClickListener {
            // loadMainActivity()
            if(checkForInternet(this@LoginActivity)){
               // progressDialogCall(this@LoginActivity)
                checkLogIn()
            }else{
                Toast.makeText(
                    this@LoginActivity,
                    "Please Check Your Internet Connection and Re-try.",
                    Toast.LENGTH_LONG
                ).show()
            }


        }

        if (Constants.TEST.TESTING)
            setTestData()
    }

    private fun loadMainActivity() {

        //  intent = Intent(baseContext, MainActivity::class.java)
        // intent = Intent(baseContext, SubmitDeadBodyInformationActivity::class.java)
        intent = Intent(baseContext, PermissionActivity::class.java)
        startActivity(intent)
    }

    private fun loadDashActivity() {
        intent = Intent(baseContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkLogIn() {
        var mAPIService: APIService? = null
        progressDialogCall(this@LoginActivity)
        mAPIService = ApiUtils.apiService

        mAPIService.LoginApi(

            binding.tieUsername.text.toString().trim(),
            binding.tiePassword.text.toString().trim()

//          binding.tieUsername.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull()),
//          binding.tiePassword.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
        )
            .enqueue(object : Callback<LoginAPIModel> {

                override fun onResponse(
                    call: Call<LoginAPIModel>,
                    response: Response<LoginAPIModel>
                ) {
                    if (response.code() == 400) {
                        closeProgressDialogCall()
                        Toast.makeText(
                            this@LoginActivity,
                            "Some issue in server end",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        try {
                            if (response.isSuccessful) {
                                closeProgressDialogCall()
                                Log.e("LogIn_Status", response.body()!!.message.toString())

                                if (response.body()!!.message.toString()
                                        .equals("Successfully Login")
                                ) {
                                    showToastMessage(response.body()!!.message)
                                    SharedPreferenceStorage.setValue(
                                        activity,
                                        SharedPreferenceStorage.JWT_TOKEN,
                                        response.body()!!.token
                                    )
                                    SharedPreferenceStorage.setValue(
                                        activity,
                                        SharedPreferenceStorage.USERID,
                                        response.body()!!.userId
                                    )
                                    SharedPreferenceStorage.setValue(
                                        activity,
                                        SharedPreferenceStorage.USERTYPE,
                                        response.body()!!.userType
                                    )
                                    val accountsIntent = Intent(activity, MainActivity::class.java)
                                    startActivity(accountsIntent)
                                    finish()

                                } else {
                                    //showToastMessage(response.body()!!.message)
                                   // closeProgressDialogCall()
                                    showAlertDialogMessage(
                                        this@LoginActivity,
                                        response.body()!!.message.toString()
                                    )
                                }

                            }
                           // closeProgressDialogCall()
                        } catch (exception: java.lang.Exception) {
                            closeProgressDialogCall()
                            Toast.makeText(
                                this@LoginActivity,
                                "Some issue in server end",
                                Toast.LENGTH_LONG
                            ).show()

                        }
                    }
                }

                override fun onFailure(call: Call<LoginAPIModel>, t: Throwable) {
                    closeProgressDialogCall()
                    showAlertDialogMessage(this@LoginActivity, "SERVER ERROR")
                }
            })


    }

    private fun setTestData() {
         binding.tieUsername.setText("test@missing")
         binding.tiePassword.setText("missing@123")
    }

   /* private fun getLatestApk() {
        var mAPIService: APIService? = null
        //  progressDialogCall(this@MainActivity)
        mAPIService = ApiUtils.apiService
        val jwt_token =
            SharedPreferenceStorage.getValue(this, SharedPreferenceStorage.JWT_TOKEN, "").toString()
       Log.e("JsonObject", SharedPreferenceStorage.getValue(
           applicationContext,
           "JsonObject",
           ""
       ).toString()
       )

        Log.e("FcmToken", SharedPreferenceStorage.getValue(
            applicationContext,
            SharedPreferenceStorage.Login.FCM_TOKEN,
            ""
        ).toString()
        )
        mAPIService.getLatestApk(
            //jwt_token,
            Constants.ApplicationPlatform.app.toString(),
            AppInfoHelper.getInfo(baseContext, AppInfoHelper.VERSION_NAME),

        ).enqueue(object : Callback<GetLatestApkModel> {
            override fun onResponse(
                call: Call<GetLatestApkModel>, response: Response<GetLatestApkModel>
            ) {
                if (response.code() == 400) {
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@LoginActivity, "Some issue in server end", Toast.LENGTH_LONG
                    ).show()
                } else if (response.code() == 420) {
                    callForLogout()
                }
                else if (response.code() == 450) {

                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        val errorBodyString = errorBody.string()
                        val errorJson = JsonParser.parseString(errorBodyString).asJsonObject
                        // Here you can display or log the error body
                        val success = errorJson.get("success").toString()
                        var url = errorJson.get("updateLink").toString()
                        url = url.replace("\"", "")
                        if (success == "false"){
                            var downloadLine =
                                Constants.BASE.BASE_URL + url
                            //downloadLine = downloadLine.replace("\"", "");
                            System.out.println(downloadLine);
                            var msg =
                                "New version"  + " App available. Your App need to Update"
                            Log.e("downLoadLine",downloadLine)
                            showUpdateAlertDialogMessage(
                                this@LoginActivity,
                                msg,
                                downloadLine,
                                true
                            )
                        }
                    } else {

                    }
                }
                else {
                    try {
                        if (response.isSuccessful) {
                            Log.e("getLatestApk", response.body()!!.success.toString())
//                                Log.e("LogIn_Token", response.body()!!.token.toString())
                            if (response.body()!!.success) {
                                // closeProgressDialogCall()
//                                    showToastMessage(response.body()!!.message)

                                if (response.body()!!.isHardUpdate) {
                                    var downloadLine =
                                        Constants.BASE.BASE_URL + response.body()!!.updateLink
                                    downloadLine = downloadLine.replace("\"", "");
                                    System.out.println(downloadLine);
                                    var msg =
                                        "New version "  + " App available. Your App need to Update"
                                    showUpdateAlertDialogMessage(
                                        this@LoginActivity,
                                        msg,
                                        downloadLine,
                                        response.body()!!.isHardUpdate
                                    )
                                }
                            }

                        }
                        closeProgressDialogCall()
                    } catch (exception: java.lang.Exception) {
                        //closeProgressDialogCall()
                        Toast.makeText(
                            this@LoginActivity,
                            "Some issue in server end",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }
            }

            override fun onFailure(call: Call<GetLatestApkModel>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(this@LoginActivity, "SERVER ERROR")
            }
        })
    }*/

    fun callForLogout() {
        SharedPreferenceStorage.setValue(
            activity, SharedPreferenceStorage.JWT_TOKEN, ""
        )
        SharedPreferenceStorage.setValue(
            activity, SharedPreferenceStorage.USERID, ""
        )
        SharedPreferenceStorage.setValue(
            activity, SharedPreferenceStorage.Login.FCM_TOKEN, ""
        )
        SharedPreferenceStorage.clearSharedPreferences(this)
        val accountsIntent = Intent(activity, LoginActivity::class.java)
        accountsIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(accountsIntent)
        finish()
    }
}