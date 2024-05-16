package com.example.wbmissingfound

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import com.example.wbmissingfound.DBHelper.DatabaseDb
import com.example.wbmissingfound.Model.ListAdapter
import com.example.wbmissingfound.RetroClient.RetroApi.APIService
import com.example.wbmissingfound.RetroClient.RetroApi.ApiUtilsSubmitOffline
import com.example.wbmissingfound.databinding.DisplayOfflineBinding
import com.example.wbmissingfound.sharedStorage.SharedPreferenceStorage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class DisplayOfflineActivity : BaseActivity() {

    var sharedpreferences: SharedPreferences? = null
    val PREF_NAME = "LOGINPREFSHARE"

    var jwt_token: String = ""

    private var progressDialog: ProgressDialog? = null

    lateinit var binding: DisplayOfflineBinding

    private val activity = this@DisplayOfflineActivity

    var isAllFabsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.display_offline)

        supportActionBar?.title = "Offline Data"

        val db = DatabaseDb(this, null)
        val cursor = db.getData()

        Log.i("SANKHA1", cursor?.getColumnName(0).toString())
        Log.i("SANKHA2", cursor?.getColumnName(4).toString())
        Log.i("SANKHA3", cursor?.getColumnName(5).toString())
        Log.i("SANKHA4", cursor?.count.toString())
        Log.i("SANKHA5", cursor?.columnCount.toString())

        var valueone = ArrayList<String>(cursor!!.count)
        var valuetwo = ArrayList<String>(cursor.count)
        var valuethree = ArrayList<String>(cursor.count)
        var valuefour = ArrayList<String>(cursor.count)
        val counter = cursor.count

        if (cursor.count > 0) {
            cursor.moveToFirst()
            do {
                val a1 = cursor.getString(9)
                val b1 = cursor.getString(4)
                val c1 = cursor.getString(5)

                var udate = "UD Case Date :" + c1
                var uno = "UD Case No :" + b1
                var offName = "UD Case Officer Name :" + a1

                valueone.add(offName)
                valuetwo.add(uno)
                valuethree.add(udate)
                valuefour.add("")
            } while (cursor.moveToNext())
            cursor.close()
        }
        val myListAdapter = ListAdapter(this, valueone, valuetwo, valuethree, valuefour)
        val mListView = findViewById<ListView>(R.id.list)
        mListView.adapter = myListAdapter

        binding.submit.setOnClickListener {
            if(checkForInternet(this@DisplayOfflineActivity)) {
                if (counter != 0){
                    submitdata()
                }
                else{
                    Toast.makeText(
                        this@DisplayOfflineActivity,
                        "No Data !!!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }else {
                Toast.makeText(
                        this@DisplayOfflineActivity,
                "Please check your internet connection!!!",
                Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun submitdata() {
        progressDialogCall(this@DisplayOfflineActivity)
        val db = DatabaseDb(this, null)
        val cursor = db.getData()

        Log.i("OFFLINEDATA", cursor?.getColumnName(0).toString())
        Log.i("OFFLINEDATA", cursor?.getColumnName(4).toString())
        Log.i("OFFLINEDATA", cursor?.getColumnName(5).toString())
        Log.i("OFFLINEDATA", cursor?.count.toString())
        Log.i("OFFLINEDATA", cursor?.columnCount.toString())

        var vIdenticalMarksImagesPart = ArrayList<MultipartBody.Part>()
        var pItemImagesPart = ArrayList<MultipartBody.Part>()

        var faceImagesPart = ArrayList<MultipartBody.Part>()
        var dBodyImagesPart = ArrayList<MultipartBody.Part>()
        var WAImagesPart = ArrayList<MultipartBody.Part>()
        var footwareImagesPart = ArrayList<MultipartBody.Part>()
        var otherImagesPart = ArrayList<MultipartBody.Part>()
        var simImagesPart = ArrayList<MultipartBody.Part>()




        if (cursor?.count!! > 0) {
            cursor.moveToFirst()
            do {
                //Log.e("id",cursor.getString(cursor.count).toString())
                val id = cursor.getString(0)
                val formStatus = cursor.getString(1)
                val dist_id = cursor.getString(2)
                val ps_id = cursor.getString(3)
                val case_no = cursor.getString(4)
                val case_date = cursor.getString(5)
                val lat = cursor.getString(6)
                val longi = cursor.getString(7)
                val place_dbf = cursor.getString(8)
                val udOffName = cursor.getString(9)
                val udOffPhone = cursor.getString(10)
                val gen_con = cursor.getString(11)
                val ageRange = cursor.getString(12)
                val height = cursor.getString(13)
                val gender = cursor.getString(14)
                val malePPC = cursor.getString(15)
                val idMarks = cursor.getString(16)
                val perItem = cursor.getString(17)
                val footwear = cursor.getString(18)
                val peculiarities = cursor.getString(19)
                val specialIdMarks = cursor.getString(20)
                val hairType = cursor.getString(21)
                val hairColor = cursor.getString(22)
                println(
                    id + " ; " + formStatus + " ; " + dist_id + " ; " + ps_id + " ; " + case_no + " ; " + case_date + " ; " + lat + " ; " + longi + " ; " + place_dbf + " ; " + udOffName + " ; " + udOffPhone + " ; " + gen_con + " ; " + ageRange + " ; " + height + " ; " + gender + " ; " + malePPC + " ; " + idMarks + " ; " + perItem + " ; " + footwear + " ; " + peculiarities
                )

                val cursorimage = db.getImagepath("vid", Integer.parseInt(id))
                Log.e("image count12 vid", cursorimage!!.count.toString())
                if (cursorimage.count > 0) {
                    cursorimage.moveToFirst()
                    do {
                        val file = File(cursorimage.getString(2))
                        val filePart = MultipartBody.Part.createFormData(
                            "vIdmarkImage",
                            file.name,
                            file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        )
                        vIdenticalMarksImagesPart.add(filePart)
                    } while (cursorimage.moveToNext())
                }
                cursorimage.close()
                val cursorimage1 = db.getImagepath("pit", Integer.parseInt(id))
                Log.e("image count12 pit", cursorimage1!!.count.toString())
                if (cursorimage1.count > 0) {
                    cursorimage1.moveToFirst()
                    do {
                        Log.e("ImageGetPath", cursorimage1.count.toString())
                        val file = File(cursorimage1.getString(2))
                        val filePart = MultipartBody.Part.createFormData(
                            "pItemImage",
                            file.name,
                            file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        )
                        pItemImagesPart.add(filePart)
                    } while (cursorimage1.moveToNext())
                }
                cursorimage1.close()
                val cursorimage2 = db.getImagepath("face", Integer.parseInt(id))
//                Log.e("image count12 face", cursorimage2!!.getString(2))
                if (cursorimage2 != null) {
                    if (cursorimage2.count > 0) {
                        cursorimage2.moveToFirst()
                        do {
                            val file = File(cursorimage2.getString(2))
                            println(cursorimage2.getString(2))
                            val filePart = MultipartBody.Part.createFormData(
                                "faceImage",
                                file.name,
                                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            )

                            faceImagesPart.add(filePart)
                        } while (cursorimage2.moveToNext())
                    }
                }
                cursorimage2?.close()
                val cursorimage3 = db.getImagepath("body", Integer.parseInt(id))
                Log.e("image count12", cursorimage3!!.count.toString())
                if (cursorimage3.count > 0) {
                    cursorimage3.moveToFirst()
                    do {
                        val file = File(cursorimage3.getString(2))
                        val filePart = MultipartBody.Part.createFormData(
                            "dBodyImage",
                            file.name,
                            file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        )
                        dBodyImagesPart.add(filePart)
                    } while (cursorimage3.moveToNext())
                }
                cursorimage3.close()
                val cursorimage4 = db.getImagepath("wa", Integer.parseInt(id))
                Log.e("image count12", cursorimage4!!.count.toString())
                if (cursorimage4.count > 0) {
                    cursorimage4.moveToFirst()
                    do {
                        val file = File(cursorimage4.getString(2))
                        val filePart = MultipartBody.Part.createFormData(
                            "waImage",
                            file.name,
                            file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        )
                        WAImagesPart.add(filePart)
                    } while (cursorimage4.moveToNext())
                }
                cursorimage4.close()
                val cursorimage5 = db.getImagepath("foot", Integer.parseInt(id))
                Log.e("image count12", cursorimage5!!.count.toString())
                if (cursorimage5.count > 0) {
                    cursorimage5.moveToFirst()
                    do {
                        val file = File(cursorimage5.getString(2))
                        val filePart = MultipartBody.Part.createFormData(
                            "footwareImage",
                            file.name,
                            file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        )
                        footwareImagesPart.add(filePart)
                    } while (cursorimage5.moveToNext())
                }
                cursorimage5.close()
                val cursorimage6 = db.getImagepath("other", Integer.parseInt(id))
                Log.e("image count12", cursorimage6!!.count.toString())
                if (cursorimage6.count > 0) {
                    cursorimage6.moveToFirst()
                    do {
                        val file = File(cursorimage6.getString(2))
                        val filePart = MultipartBody.Part.createFormData(
                            "othersImage",
                            file.name,
                            file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        )
                        otherImagesPart.add(filePart)
                    } while (cursorimage6.moveToNext())
                }
                cursorimage6.close()
                val jwt_token =
                    SharedPreferenceStorage.getValue(this, SharedPreferenceStorage.JWT_TOKEN, "")
                        .toString()

                var mAPIService: APIService? = null
                mAPIService = ApiUtilsSubmitOffline.apiService
                mAPIService.UDDataSubmitApiOffline(
                    jwt_token,
                    formStatus.toInt(),
                    dist_id,
                    ps_id,
                    case_no,
                    case_date,
                    lat,
                    longi,
                    place_dbf,
                    udOffName,
                    udOffPhone,
                    gen_con,
                    ageRange,
                    height,
                    gender.toInt(),
                    malePPC,
                    vIdenticalMarksImagesPart,
                    idMarks,
                    pItemImagesPart,
                    perItem,
                    faceImagesPart,
                    dBodyImagesPart,
                    WAImagesPart,
                    footwareImagesPart,
                    footwear,
                    otherImagesPart,
                    peculiarities,
                    specialIdMarks,
                    simImagesPart,
                    hairType,
                    hairColor


                ).enqueue(object : Callback<String> {
                    @SuppressLint("SuspiciousIndentation")
                    override fun onResponse(
                        call: Call<String>,
                        response: Response<String>
                    ) {
                        if (response.code() == 200) {
                              closeProgressDialogCall()
                            try {
                                if (response.isSuccessful) {
                                    var result: JSONObject? = null

                                    val s = response.body()

                                    result = JSONObject(s)

                                    Log.e("response", "done")
                                    if (result!!.getString("message").equals("Success")) {               ///message
                                        val cursorimage7 =
                                            db.getImagepath("vid", Integer.parseInt(id))

                                        if (cursorimage7!!.count > 0) {
                                            cursorimage7.moveToFirst()
                                            do {
                                                val file = File(cursorimage7.getString(2))
                                                if (file.exists()) {
                                                    if (file.delete()) {
                                                        println(
                                                            "file Deleted :" + cursorimage7.getString(
                                                                2
                                                            )
                                                        )
                                                    } else {
                                                        println(
                                                            "file not Deleted :" + cursorimage7.getString(
                                                                2
                                                            )
                                                        )
                                                    }
                                                }
                                            } while (cursorimage7.moveToNext())
                                        }
                                        cursorimage7.close()
                                        val cursorimage8 =
                                            db.getImagepath("pit", Integer.parseInt(id))
                                        if (cursorimage8!!.count > 0) {
                                            cursorimage8.moveToFirst()
                                            do {
                                                val file = File(cursorimage8.getString(2))
                                                if (file.exists()) {
                                                    if (file.delete()) {
                                                        println(
                                                            "file Deleted :" + cursorimage8.getString(
                                                                2
                                                            )
                                                        )
                                                    } else {
                                                        println(
                                                            "file not Deleted :" + cursorimage8.getString(
                                                                2
                                                            )
                                                        )
                                                    }
                                                }
                                            } while (cursorimage8.moveToNext())
                                        }
                                        cursorimage8.close()

                                        val cursorimage9 =
                                            db.getImagepath("face", Integer.parseInt(id))
                                        if (cursorimage9!!.count > 0) {
                                            cursorimage9.moveToFirst()
                                            do {
                                                val file = File(cursorimage9.getString(2))
                                                if (file.exists()) {
                                                    if (file.delete()) {
                                                        println(
                                                            "file Deleted :" + cursorimage9.getString(
                                                                2
                                                            )
                                                        )
                                                    } else {
                                                        println(
                                                            "file not Deleted :" + cursorimage9.getString(
                                                                2
                                                            )
                                                        )
                                                    }
                                                }
                                            } while (cursorimage9.moveToNext())
                                        }
                                        cursorimage9.close()
                                        val cursorimage10 =
                                            db.getImagepath("body", Integer.parseInt(id))
                                        if (cursorimage10!!.count > 0) {
                                            cursorimage10.moveToFirst()
                                            do {
                                                val file = File(cursorimage10.getString(2))
                                                if (file.exists()) {
                                                    if (file.delete()) {
                                                        println(
                                                            "file Deleted :" + cursorimage10.getString(
                                                                2
                                                            )
                                                        )
                                                    } else {
                                                        println(
                                                            "file not Deleted :" + cursorimage10.getString(
                                                                2
                                                            )
                                                        )
                                                    }
                                                }
                                            } while (cursorimage10.moveToNext())
                                        }
                                        cursorimage10.close()
                                        val cursorimage11 =
                                            db.getImagepath("wa", Integer.parseInt(id))
                                        if (cursorimage11!!.count > 0) {
                                            cursorimage11.moveToFirst()
                                            do {
                                                val file = File(cursorimage11.getString(2))
                                                if (file.exists()) {
                                                    if (file.delete()) {
                                                        println(
                                                            "file Deleted :" + cursorimage11.getString(
                                                                2
                                                            )
                                                        )
                                                    } else {
                                                        println(
                                                            "file not Deleted :" + cursorimage11.getString(
                                                                2
                                                            )
                                                        )
                                                    }
                                                }
                                            } while (cursorimage11.moveToNext())
                                        }
                                        cursorimage11.close()
                                        val cursorimage12 =
                                            db.getImagepath("foot", Integer.parseInt(id))
                                        if (cursorimage12!!.count > 0) {
                                            cursorimage12.moveToFirst()
                                            do {
                                                val file = File(cursorimage12.getString(2))
                                                if (file.exists()) {
                                                    if (file.delete()) {
                                                        println(
                                                            "file Deleted :" + cursorimage12.getString(
                                                                2
                                                            )
                                                        )
                                                    } else {
                                                        println(
                                                            "file not Deleted :" + cursorimage12.getString(
                                                                2
                                                            )
                                                        )
                                                    }
                                                }
                                            } while (cursorimage12.moveToNext())
                                        }
                                        cursorimage12.close()
                                        val cursorimage13 =
                                            db.getImagepath("other", Integer.parseInt(id))
                                        if (cursorimage13!!.count > 0) {
                                            cursorimage13.moveToFirst()
                                            do {

                                                val file = File(cursorimage13.getString(2))
                                                if (file.exists()) {
                                                    if (file.delete()) {
                                                        println(
                                                            "file Deleted :" + cursorimage13.getString(
                                                                2
                                                            )
                                                        )
                                                    } else {
                                                        println(
                                                            "file not Deleted :" + cursorimage13.getString(
                                                                2
                                                            )
                                                        )
                                                    }
                                                }
                                            } while (cursorimage13.moveToNext())
                                        }
                                        cursorimage13.close()
                                        db.deleteCase(Integer.parseInt(id))
                                        MessageDialog()
                                    } else {
                                        showAlertDialogMessage(
                                            this@DisplayOfflineActivity, "SERVER ERROR !!!"
                                        )
                                    }
                                }
                            } catch (exception: java.lang.Exception) {
                                Toast.makeText(
                                    this@DisplayOfflineActivity,
                                    "Some issue in server end",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            //  progressDialog!!.cancel()
                            Toast.makeText(
                                this@DisplayOfflineActivity,
                                "SERVER ERROR!!! Please try after sometime...",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        closeProgressDialogCall()
                        showAlertDialogMessage(
                            this@DisplayOfflineActivity, "SERVER ERROR !!!"
                        )
                    }
                })
            } while (cursor.moveToNext())
            cursor.close()
        } else {
            Toast.makeText(
                this@DisplayOfflineActivity, "No DATA !!!", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun MessageDialog() {
        val alertDialog: android.app.AlertDialog.Builder =
            android.app.AlertDialog.Builder(this@DisplayOfflineActivity)
        alertDialog.setTitle(" ")
        alertDialog.setIcon(R.drawable.ok_sign)
        alertDialog.setMessage("Successfully Submitted...")
        alertDialog.setPositiveButton(
            "OK"
        ) { dialog, id ->
            val accountsIntent = Intent(this@DisplayOfflineActivity, MainActivity::class.java)
            startActivity(accountsIntent)
            finish()
        }
        val alert: android.app.AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }
}
