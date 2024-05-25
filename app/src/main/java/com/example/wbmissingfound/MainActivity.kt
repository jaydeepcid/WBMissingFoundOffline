package com.example.wbmissingfound

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.wbmissingfound.DBHelper.DatabaseDb
import com.example.wbmissingfound.Helper.CheckNetworkConnection
import com.example.wbmissingfound.Helper.Constants
import com.example.wbmissingfound.Model.PsSubmitDataModel
import com.example.wbmissingfound.Model.SyncForBurnImages
import com.example.wbmissingfound.Model.SyncImageModelClass
import com.example.wbmissingfound.Model.SyncPersonalItemMorgueLevel
import com.example.wbmissingfound.RetroClient.RetroApi.APIService
import com.example.wbmissingfound.RetroClient.RetroApi.ApiUtils
import com.example.wbmissingfound.RetroClient.RetroModel.AllPsResponseModelClass
import com.example.wbmissingfound.RetroClient.RetroModel.BurnMarksModel
import com.example.wbmissingfound.RetroClient.RetroModel.CaseDataModel
import com.example.wbmissingfound.RetroClient.RetroModel.CaseDetails
import com.example.wbmissingfound.RetroClient.RetroModel.DistrictAll
import com.example.wbmissingfound.RetroClient.RetroModel.DistrictsAPIModel
import com.example.wbmissingfound.RetroClient.RetroModel.ImageUploadApiResponseModel
import com.example.wbmissingfound.RetroClient.RetroModel.LoginCheckModel
import com.example.wbmissingfound.RetroClient.RetroModel.MorgueDetails
import com.example.wbmissingfound.RetroClient.RetroModel.MorgueListApiModelClass
import com.example.wbmissingfound.RetroClient.RetroModel.PSAllOnly
import com.example.wbmissingfound.RetroClient.RetroModel.PersonalItemSaveResponseModel
import com.example.wbmissingfound.custom.DatePicker
import com.example.wbmissingfound.databinding.ActivityMainNewBinding
import com.example.wbmissingfound.sharedStorage.SharedPreferenceStorage
import com.example.wbmissingfound.utils.FileUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.opencv.android.OpenCVLoader
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : BaseActivity() {

    var sharedpreferences: SharedPreferences? = null

    val PREF_NAME = "LOGINPREFSHARE"

    val version = BuildConfig.VERSION_NAME

    var jwt_token: String = ""

    private var progressDialog: ProgressDialog? = null

    lateinit var binding: ActivityMainNewBinding

    private val activity = this@MainActivity

    var isAllFabsVisible = false

    private lateinit var checkNetworkConnection: CheckNetworkConnection

    var CaseDataHashMap = ArrayList<HashMap<String, String>>()

    var morgueArrayList: ArrayList<MorgueDetails> = ArrayList()

    var psSubmitDataModelArrayList: MutableList <PsSubmitDataModel> = ArrayList()


    var morgueArrayListNew: List<MorgueDetails> = ArrayList()
    var morgueListarrayAdapter: ArrayAdapter<MorgueDetails>? = null
    var districtArrayList: List<MorgueDetails> = java.util.ArrayList()


    private val placeofoccurrence = ArrayList<String>()

    private val placeofoccurrenceimages= ArrayList<MultipartBody.Part>()

    var arrayListforSyncPersonalItem: MutableList <SyncPersonalItemMorgueLevel> = ArrayList()
    var arrayListforSyncBurnDetals: MutableList <SyncForBurnImages> = ArrayList()

    var policeStationArrayList: List<PSAllOnly> = ArrayList()

    private val morgueListStr = ArrayList<String>()

    var arraylistForImageSync: MutableList <SyncImageModelClass> = ArrayList()
    var db1 = DatabaseDb(this, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        /*binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)*/
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_main_new)
        //getLatestApk()
        if(SharedPreferenceStorage.getValue(baseContext,SharedPreferenceStorage.USERTYPE,"").equals(Constants.USER_TYPE.MORGUE)){
            binding.mcvUnidentifiedPerson.visibility = View.VISIBLE
        }else if(SharedPreferenceStorage.getValue(baseContext,SharedPreferenceStorage.USERTYPE,"").equals(Constants.USER_TYPE.POLICESTATION)){
            binding.mcvPoliceData.visibility = View.VISIBLE
            val cursor = db1.getPSData()
            val rowQty=cursor!!.count
            if(rowQty>0){
                binding.mcvOfflineForSyn!!.visibility= View.VISIBLE
                binding.tvUnSynDatacount!!.setText(rowQty.toString()+" Unsynchronized data")
            }
        }

        if(OpenCVLoader.initDebug())
            Log.d("openCv","openCv configure Success")

        else
            Log.d("openCv","openCv configure error occured")


        /*binding.fabAddDeadBody.setOnClickListener {


            if (!isAllFabsVisible) {
                // when isAllFabsVisible becomes true make all
                // the action name texts and FABs VISIBLE
                binding.fabAddUnidentified.show();
                binding.fabAddIndentified.show();
                binding.tvAddIdentifiedText.visibility = View.VISIBLE;
                binding.tvAddUnidentifiedText.visibility = View.VISIBLE;

                // make the boolean variable true as we
                // have set the sub FABs visibility to GONE
                isAllFabsVisible = true;
            } else {
                // when isAllFabsVisible becomes true make
                // all the action name texts and FABs GONE.


                binding.fabAddUnidentified.hide();
                binding.fabAddIndentified.hide();
                binding.tvAddIdentifiedText.visibility = View.GONE;
                binding.tvAddUnidentifiedText.visibility = View.GONE;


                // make the boolean variable false as we
                // have set the sub FABs visibility to GONE
                isAllFabsVisible = false;
            }

        }
        binding.fabAddUnidentified.setVisibility(View.GONE);
        binding.fabAddIndentified.setVisibility(View.GONE);
        binding.tvAddIdentifiedText.setVisibility(View.GONE);
        binding.tvAddUnidentifiedText.setVisibility(View.GONE);

        binding.tvUdlistView.setOnClickListener() {
            val intent = Intent(baseContext, UnidentifiedListViewActivity::class.java)
            startActivity(intent)
        }*/

        // make the boolean variable as false, as all the
        // action name texts and all the sub FABs are invisible

        // We will make all the FABs and action name texts
        // visible only when Parent FAB button is clicked So
        // we have to handle the Parent FAB button first, by
        // using setOnClickListener you can see below
        // below is the sample action to handle add person FAB. Here it shows simple Toast msg.
        // The Toast will be shown only when they are visible and only when user clicks on them
        binding.mcvUnidentifiedPerson.setOnClickListener {
            val intent = Intent(baseContext, MorgLevelListing::class.java)
            startActivity(intent)
        }

        // below is the sample action to handle add alarm FAB. Here it shows simple Toast msg
        // The Toast will be shown only when they are visible and only when user clicks on them


        binding.mcvIdentifiedPerson.setOnClickListener {
            val intent = Intent(baseContext, IdentifiedFormActivity::class.java)
            startActivity(intent)
        }
       /* binding.mcvOffline2.setOnClickListener {
            val intent = Intent(baseContext, DisplayOfflineActivity::class.java)
            startActivity(intent)
        }*/

        binding.mcvPoliceData.setOnClickListener {
            //SearchAlertDialog()
            var intent = Intent(baseContext,PSLevelSubmitDeadBodyInfoActivity::class.java)
            startActivity(intent)
        }

        binding.tvVersion.text = "Version:" + version + "\n © Developed by CID West Bengal"

        if (checkForInternet(this)) {
            logInValidityCheck()
            if(!db1.CheckIsDataAlreadyInDBorNotMorgueList()){
                getMorgueListForPS()
            }
            GetPsByUserIDForUser()
        }
     //   setOfflineDataUserWise()
        binding.mcvOfflineForSyn.setOnClickListener{
            if(checkForInternet(this)) {
                setOfflineDataUserWise()
            }else{
                Toast.makeText(
                    this@MainActivity,
                    "No internet Please try after some time.",
                    Toast.LENGTH_LONG
                ).show()

            }
        }
        callNetworkConnection()

        // DistrictAPICall()
       // getSHA1Fingerprint(activity)//
        //callNetworkConnection()

    }
    private fun setOfflineDataUserWise (){
        if(SharedPreferenceStorage.getValue(baseContext,SharedPreferenceStorage.USERTYPE,"").equals(Constants.USER_TYPE.POLICESTATION)){
            //var db1 = DatabaseDb(this, null)
            psSubmitDataModelArrayList.clear()
            val cursor = db1.getPSData()
            val rowQty=cursor!!.count
            Log.d("CID WB","NO OF ROW-->"+rowQty )
            var id: Int = 0
            if (rowQty>0) {
                while (cursor.moveToNext()) {
                    //name = cursor.getString(column_index);//to get other values
                    if (cursor != null) {
                        id = cursor.getInt(0)
                        if(id>0){
                            Log.d("CID WB","ID-->"+id +"Image Path-->"+  cursor.getString(12))
                            var jwt_token = SharedPreferenceStorage.getValue(
                                applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
                            ).toString()
                            val id =cursor.getInt(0)

                            val psID =cursor.getString(1)
                            var morgueID=cursor.getString(2)
                            var ud_number=cursor.getString(3)
                            var ud_date=cursor.getString(4)
                            var officer_name=cursor.getString(5)
                            var officer_contact=cursor.getString(6)
                            var lat=cursor.getString(7)
                            var long=cursor.getString(8)
                            var vic_gen_val = cursor.getString(9)
                            var placeDescription=cursor.getString(10)
                            var status =cursor.getString(11)
                            var poPhoto = cursor.getString(12)
                            var deadbodyType = cursor.getString(13)
                            var vic_name = cursor.getString(14)
                            var vic_age = cursor.getString(15)
                            var vic_address=cursor.getString(16)
//                            *//* val cleanString =  poPhoto.removePrefix("[").removeSuffix("]")
//                             val bodyImageFromTable = cleanString.split(",").map { it.trim() }
//                             System.out.println("CID WEST BENGAL  "+ bodyImageFromTable)*//*
                            val psSubmitDataModel = PsSubmitDataModel(id,psID,morgueID,ud_number,ud_date,officer_name,officer_contact,lat,long,vic_gen_val,placeDescription,status,poPhoto,deadbodyType,vic_name,vic_age,vic_address)
                            psSubmitDataModelArrayList.add(psSubmitDataModel)

                        }
                        //Log.d("CID WB","ID-->"+id +"CASE NO-->"+   binding.tietCaseNumber.text.toString())
                    }//to get id, 0 is the column index
                }

            }

            psSubmitApiCalling()

        }

    }
    private fun GetPsByUserIDForUser() {
        val db = DatabaseDb(this, null)
        var mAPIService: APIService? = null
        // progressDialogCall(this@PSLevelSubmitDeadBodyInfoActivity)
        mAPIService = ApiUtils.apiService
        Log.e("Token",SharedPreferenceStorage.getValue(applicationContext, SharedPreferenceStorage.JWT_TOKEN, "").toString())
        SharedPreferenceStorage.getValue(applicationContext, SharedPreferenceStorage.JWT_TOKEN, "")
            ?.let {
                mAPIService.getPsByUserID(it)
                    .enqueue(object : Callback<AllPsResponseModelClass> {

                        override fun onResponse(
                            call: Call<AllPsResponseModelClass>,
                            response: Response<AllPsResponseModelClass>
                        ) {
                            try {
                                if (response.isSuccessful) {
                                   // closeProgressDialogCall()
                                    Log.e("LogIn_Status", response.body()!!.data.toString())
                                    // Log.e("LogIn_Token", response.body()!!.status.toString())
                                    if (response.body()!!.status.equals("success")) {
                                        policeStationArrayList = response.body()!!.data
                                        val policeStationName= policeStationArrayList.get(0).ps_name
                                        val psid = policeStationArrayList.get(0).ps_id
                                        SharedPreferenceStorage.setValue(
                                            activity,
                                            "PSName",
                                            policeStationName
                                        )
                                        SharedPreferenceStorage.setValue(
                                            activity,
                                            "PSID",
                                            psid
                                        )
                                       /* val preferences =
                                            getSharedPreferences("DATA", Context.MODE_PRIVATE)
                                        val preferencesEditor = preferences.edit()
                                        preferencesEditor.putBoolean("saved", true)
                                        preferencesEditor.putString(
                                            "police_station",
                                            response.body()!!.toString()
                                        )

                                        Log.e("districtPS", response.body()!!.data.toString())
                                        preferencesEditor.apply()
                                        policeStationArrayList = response.body()!!.data

                                        *//*districtArrayList.toMutableList()
                                            .add(DistrictAll(-9999, "Select", myList))*//*
                                        policeStationArrayAdapter = ArrayAdapter(
                                            baseContext,
                                            android.R.layout.simple_spinner_dropdown_item,
                                            policeStationArrayList
                                        )
                                        Log.e("ArralistPoliceStation",policeStationArrayList.get(0).ps_name)
                                        val policeStationNAme= policeStationArrayList.get(0).ps_name
                                        binding.spinnerOccurrenceDist.adapter = policeStationArrayAdapter
                                        binding.textViewPolice.text=policeStationNAme*/


                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            response.body()!!.data.toString(),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }


                                }
                            } catch (exception: java.lang.Exception) {
                                //closeProgressDialogCall()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Some issue in server end",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                        }

                        override fun onFailure(
                            call: Call<AllPsResponseModelClass>,
                            t: Throwable
                        ) {
                            intent = Intent(baseContext, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
            }


    }
    private fun callNetworkConnection() {
        checkNetworkConnection = CheckNetworkConnection(application)
        checkNetworkConnection.observe(this,{ isConnected ->
            if (isConnected){
                Toast.makeText(applicationContext, "Network Available", Toast.LENGTH_LONG).show()
//               if(SharedPreferenceStorage.getValue(baseContext,SharedPreferenceStorage.USERTYPE,"").equals(Constants.USER_TYPE.POLICESTATION)){
//                    //var db1 = DatabaseDb(this, null)
//                    psSubmitDataModelArrayList.clear()
//                    val cursor = db1.getPSData()
//                    val rowQty=cursor!!.count
//                    Log.d("CID WB","NO OF ROW-->"+rowQty )
//                    var id: Int = 0
//                    if (rowQty>0) {
//                        while (cursor.moveToNext()) {
//                            //name = cursor.getString(column_index);//to get other values
//                            if (cursor != null) {
//                                id = cursor.getInt(0)
//                                if(id!=0){
//                                    Log.d("CID WB","ID-->"+id +"Image Path-->"+  cursor.getString(12))
//                                    var jwt_token = SharedPreferenceStorage.getValue(
//                                        applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
//                                    ).toString()
//                                    val id =cursor.getInt(0)
//
//                                    val psID =cursor.getString(1)
//                                    var morgueID=cursor.getString(2)
//                                    var ud_number=cursor.getString(3)
//                                    var ud_date=cursor.getString(4)
//                                    var officer_name=cursor.getString(5)
//                                    var officer_contact=cursor.getString(6)
//                                    var lat=cursor.getString(7)
//                                    var long=cursor.getString(8)
//                                    var vic_gen_val = cursor.getString(9)
//                                    var placeDescription=cursor.getString(10)
//                                    var status =cursor.getString(11)
//                                    var poPhoto = cursor.getString(12)
//                                    var deadbodyType = cursor.getString(13)
//                                    var vic_name = cursor.getString(14)
//                                    var vic_age = cursor.getString(15)
//                                    var vic_address=cursor.getString(16)
//                                    /* val cleanString =  poPhoto.removePrefix("[").removeSuffix("]")
//                                     val bodyImageFromTable = cleanString.split(",").map { it.trim() }
//                                     System.out.println("CID WEST BENGAL  "+ bodyImageFromTable)*/
//                                    val psSubmitDataModel = PsSubmitDataModel(id,psID,morgueID,ud_number,ud_date,officer_name,officer_contact,lat,long,vic_gen_val,placeDescription,status,poPhoto,deadbodyType,vic_name,vic_age,vic_address)
//                                    psSubmitDataModelArrayList.add(psSubmitDataModel)
//
//                                }
//                                //Log.d("CID WB","ID-->"+id +"CASE NO-->"+   binding.tietCaseNumber.text.toString())
//                            }//to get id, 0 is the column index
//                       }
//
//                    }
//
//                    psSubmitApiCalling()
//
//                }
                if(SharedPreferenceStorage.getValue(baseContext,SharedPreferenceStorage.USERTYPE,"").equals(Constants.USER_TYPE.MORGUE)){
                    sync()
                    syncImageNew(this)
                    syncImageBurnType(this)
                    syncPersonalItem(this)
                }
            }else{
                Toast.makeText(applicationContext, "Network Not Available", Toast.LENGTH_LONG).show()
            }
        })

    }
    fun sync():Boolean {
        val db = DatabaseDb(this, null)
        val cursor1 = db.getMorgueData()
        val columnsQty = cursor1!!.columnCount
        val rowQty = cursor1!!.count
        Log.e("CursorDataBase", rowQty.toString())
        while (cursor1.moveToNext()) {
            var id = cursor1.getString(0)
            var name = cursor1.getString(1)
            Log.e("Value..", cursor1.getString(0))
            Log.e("Value..", cursor1.getInt(1).toString())
            Log.e("Value..", cursor1.getString(2))
            Log.e("Value..", cursor1.getString(3))
            Log.e("Value..", cursor1.getString(4))
            Log.e("Value..", cursor1.getString(5))
            Log.e("Value..", cursor1.getString(6))
            Log.e("Value..", cursor1.getString(7))
            Log.e("Value..", cursor1.getString(8))
            Log.e("Value..", cursor1.getString(9))
            Log.e("Value..", cursor1.getString(10))

            val case_id = cursor1.getString(0)
            val age_min = cursor1.getInt(1)
            val age_max = cursor1.getInt(2)
            val height = cursor1.getString(3)
            val gender = cursor1.getString(4)
            val general_condition = cursor1.getString(5)
            val foot_dsc = cursor1.getString(6)
            val male_privet = cursor1.getString(7)
            val cloth = cursor1.getString(8)
            val peculiarities = cursor1.getString(9)
            val hair = cursor1.getString(10)
            val haircolor = cursor1.getString(11)

            updateMorguecaseFromDatabase(
                case_id, age_min, age_max, height, gender, general_condition, foot_dsc,
                male_privet, cloth, peculiarities, hair, haircolor
            )




        }

        return true
    }
    fun syncImageNew(context: Context): Boolean {
        val db = DatabaseDb(context, null)

        val cursorImage = db.getImagedetailsfromOfflineDatabase()
        val dBodyImagesPart = ArrayList<MultipartBody.Part>()

        cursorImage?.use { cursor ->
            while (cursor.moveToNext()) {
                val case_id = cursor.getString(1)
                val imageList = cursor.getString(2)
                val imageType = cursor.getString(3)

                val objectForSyncImage= SyncImageModelClass(case_id,imageList,imageType)
                arraylistForImageSync.add(objectForSyncImage)




                //db.deleteCaseImageFromTable(case_id)
            }


        }
        startSyncForImage()
        return true
    }

    private fun startSyncForImage() {
        if(arraylistForImageSync.size>0){
            var i = 0
            while (i<arraylistForImageSync.size){


                //val cleanString = psSubmitDataModelArrayList.get(i).poPhoto.removePrefix("[").removeSuffix("]")
                val elements = arraylistForImageSync.get(i).image_list

                var photopath = stringSplitManual(elements)

                /* val cleanString =  psSubmitDataModelArrayList.get(i).poPhoto.removePrefix("[").removeSuffix("]")
                 val bodyImageFromTable = cleanString.split(",").map { it.trim() }



                 System.out.println("CID WEST BENGAL "+i.toString()+"imagePath"+bodyImageFromTable.get(0))*/
                //SubmitUDPSData(i,photopath)
                SubmitImageFromLocalStroage(arraylistForImageSync.get(i).case_id,
                    photopath,
                    arraylistForImageSync.get(i).image_type)
                i++
            }

        }
    }
    fun syncImageBurnType(context: Context): Boolean {
        val db = DatabaseDb(context, null)
        val simImagesPart = ArrayList<MultipartBody.Part>()

        val cursorImage = db.getBurnDetailsfromOfflineDatabase()
        cursorImage?.use { cursor ->
            while (cursor.moveToNext()) {
                val case_id_burn = cursor.getString(3)
                val imageList_burn = cursor.getString(2)
                val imageType_burn = cursor.getString(1)

                val objectSyncForBurnMarksModel=SyncForBurnImages(imageType_burn,imageList_burn,case_id_burn)
                arrayListforSyncBurnDetals.add(objectSyncForBurnMarksModel)

                //updateBurnMarksFromdataBaseLocal(imageType_burn, simImagesPart, case_id_burn)
            }
        }
        burnImagesDetails()
        return true
    }
    fun syncPersonalItem(context: Context): Boolean {
        val db = DatabaseDb(context, null)
        val cursorImage = db.getPersonalItemfromLocalStroage()
        cursorImage?.use { cursor ->
            while (cursor.moveToNext()) {
                val case_id = cursor.getString(3)
                val imageList_personalItem = cursor.getString(2)
                val personal_data = cursor.getString(1)
                val inputString = cursor.getString(2)


                val objectSyncForPersonalItem=SyncPersonalItemMorgueLevel(personal_data,imageList_personalItem,case_id)
                arrayListforSyncPersonalItem.add(objectSyncForPersonalItem)

                //savePersonaldataItemFromLocalStroage(personal_data, imageList, case_id)
            }
        }
        syncPersonalItemFromDatabase()
        return true
    }
    private fun syncPersonalItemFromDatabase(){
        if(arrayListforSyncPersonalItem.size>0){
            var i = 0
            while (i<arrayListforSyncPersonalItem.size){


                //val cleanString = psSubmitDataModelArrayList.get(i).poPhoto.removePrefix("[").removeSuffix("]")
                val elements = arrayListforSyncPersonalItem.get(i).personalitem

                var photopath = stringSplitManual(elements)

                /* val cleanString =  psSubmitDataModelArrayList.get(i).poPhoto.removePrefix("[").removeSuffix("]")
                 val bodyImageFromTable = cleanString.split(",").map { it.trim() }



                 System.out.println("CID WEST BENGAL "+i.toString()+"imagePath"+bodyImageFromTable.get(0))*/
                //SubmitUDPSData(i,photopath)
                savePersonaldataItemFromLocalStroage(arrayListforSyncPersonalItem.get(i).personaldata,
                    photopath,
                    arrayListforSyncPersonalItem.get(i).caseid)
                i++
            }

        }


    }
    @SuppressLint("SuspiciousIndentation")
    private fun savePersonaldataItemFromLocalStroage(
        personaldata:String,
        personaliem: Array<String?>?,
        caseid:String) {
       // progressDialogCall(this@MainActivity)
        val personalItemimages= ArrayList<MultipartBody.Part>()

        for (i in 0 until personaliem!!.size) {

            val file: File = FileUtils.getFile(this, Uri.parse(personaliem!!.get(i)))
            //var file = File(visibleIdMarksImage.get(i))

            val filePart = MultipartBody.Part.createFormData(
                "PersonalItem",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            System.out.println("JoyFILESPart" + filePart)
            personalItemimages.add(filePart)
        }
        // val personal_item=persoitem.toRequestBody("text/plain".toMediaTypeOrNull())

        var jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()
        val personalDataRequestBody = personaldata.toRequestBody("text/plain".toMediaTypeOrNull())
        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService
        val call = mAPIService.updatePersonaldata(
            jwt_token,
            personalDataRequestBody,
            personalItemimages,
            caseid.toRequestBody("text/plain".toMediaTypeOrNull())

        ).enqueue(object : Callback<PersonalItemSaveResponseModel> {
            override fun onResponse(
                call: Call<PersonalItemSaveResponseModel>, response: Response<PersonalItemSaveResponseModel>
            ) {
                if (response.code() == 200) {
                   // closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error", response.body()!!.message.toString())

                            if (response.body()?.success == true) {

                              db1.deleteRowByCaseIdPersonalItem(caseid)
                                // Clear The form

                                //Clear the form
                            } else {
                                showAlertDialogMessage(
                                    this@MainActivity,
                                    response.body()!!.message.toString()
                                )
//                                    showAlertDialogMessage(
////                                        this@PSLevelSubmitDeadBodyInfoActivity,
////                                        "SERVER ERROR !!!"
////                                    )
                            }


                        }
                    } catch (exception: java.lang.Exception) {
                        //closeProgressDialogCall()
                        Toast.makeText(
                            this@MainActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (response.code() == 400) {
                    //closeProgressDialogCall()
                    Toast.makeText(
                        this@MainActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                    //closeProgressDialogCall()
                    Toast.makeText(
                        this@MainActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<PersonalItemSaveResponseModel>, t: Throwable) {
               // closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MainActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })


    }
    private fun burnImagesDetails(){
        if(arrayListforSyncBurnDetals.size>0){
            var i = 0
            while (i<arrayListforSyncBurnDetals.size){


                //val cleanString = psSubmitDataModelArrayList.get(i).poPhoto.removePrefix("[").removeSuffix("]")
                val elements = arrayListforSyncBurnDetals.get(i).Burnmarksimage

                var photopath = stringSplitManual(elements)

                /* val cleanString =  psSubmitDataModelArrayList.get(i).poPhoto.removePrefix("[").removeSuffix("]")
                 val bodyImageFromTable = cleanString.split(",").map { it.trim() }



                 System.out.println("CID WEST BENGAL "+i.toString()+"imagePath"+bodyImageFromTable.get(0))*/
                //SubmitUDPSData(i,photopath)
                updateBurnMarksFromdataBaseLocal(arrayListforSyncBurnDetals.get(i).burnmarkstype,
                    photopath,
                    arrayListforSyncBurnDetals.get(i).caseid)
                i++
            }

        }


    }
    @SuppressLint
    fun updateBurnMarksFromdataBaseLocal(
        burnmarkstype:String, Burnmarksimage: Array<String?>?,
        case_id:String){
        // progressDialogCall(this@MorgueLevelSubmitInfoActivity)
        val simImagesPart = ArrayList<MultipartBody.Part>()
        for (i in 0..(Burnmarksimage!!.size - 1)) {
            // val file = File(SpecialMarksImage.get(i))
            val file: File = FileUtils.getFile(this, Uri.parse(Burnmarksimage.get(i)))

            val filePart = MultipartBody.Part.createFormData(
                "Burnmarksimage",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            System.out.println("FILES" + filePart)
            simImagesPart.add(filePart)
        }

        var jwtToken = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()

        Log.e("TOKEN",jwtToken)


        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService
        val Call = mAPIService.updateBurningMarks(
            jwtToken,
            burnmarkstype.toRequestBody("text/plain".toMediaTypeOrNull()),
            simImagesPart,
            case_id.toRequestBody("text/plain".toMediaTypeOrNull())
        ).enqueue(object : Callback<BurnMarksModel> {
            override fun onResponse(
                call: Call<BurnMarksModel>, response:
                Response<BurnMarksModel>
            ){
                if (response.code() == 200) {
                    //  splMarksModelArrayList.clear()
                   // closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error",
                                response.body()!!.message.toString())
                            if (response.body()?.success == true) {
                                db1.deleteRowByCaseIdBurncase(case_id)
                                // SpecialMarksImage.clear()
                                // binding.llSpcIdenMarks.removeAllViews()
                                // binding.llSpcIdenMarks.removeAllViews()
//                                showAlertDialogMessageSuccess(
//                                    this@MorgueLevelSubmitInfoActivity,
//                                    response.body()!!.message.toString()
//                                )
                            }
                            else
                            {
                                showAlertDialogMessage(
                                    this@MainActivity,
                                    response.body()!!.message.toString()
                                )
                            }
                        }
                    }
                    catch (exception: java.lang.Exception)
                    {
                        //closeProgressDialogCall()
                        Toast.makeText(
                            this@MainActivity,
                            "Some issue in server end. Error Core : " + response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else if (response.code() == 400)
                {
                   // closeProgressDialogCall()
                    Toast.makeText(
                        this@MainActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else{

                    //closeProgressDialogCall()
                    Toast.makeText(
                        this@MainActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            override fun onFailure(call: Call<BurnMarksModel>, t: Throwable) {
               // closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MainActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })
    }

    fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File.createTempFile("temp_image", null, context.cacheDir)
            tempFile.deleteOnExit()
            tempFile.outputStream().use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun SubmitImageFromLocalStroage(case_id:String, imageList: Array<String?>?, imageType:String) {
        // progressDialogCall(this@MorgueLevelSubmitInfoActivity)


        val imagesMultipartBody = ArrayList<MultipartBody.Part>()
        for (i in 0..(imageList!!.size - 1)) {
            // val file = File(SpecialMarksImage.get(i))
            val file: File = FileUtils.getFile(this, Uri.parse(imageList.get(i)))

            val filePart = MultipartBody.Part.createFormData(
                "Picture",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            System.out.println("FILES" + filePart)
            imagesMultipartBody.add(filePart)
        }


        // if (checkForInternet(this@PSLevelSubmitDeadBodyInfoActivity)) {

        // saveImageNew(imagePath,"IMAGE")
        var jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()

        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService

        val status = "1".toRequestBody("text/plain".toMediaTypeOrNull())

        val call = mAPIService.updatePhoto(
            jwt_token,
            imageType.toRequestBody("text/plain".toMediaTypeOrNull()),
            imagesMultipartBody,
            case_id.toRequestBody("text/plain".toMediaTypeOrNull())

        ).enqueue(object : Callback<ImageUploadApiResponseModel> {
            override fun onResponse(
                call: Call<ImageUploadApiResponseModel>, response: Response<ImageUploadApiResponseModel>
            ) {
                if (response.code() == 200) {
                    //closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error", response.body()!!.message.toString())

                            if (response.body()?.success == true) {

                                val code=db1.deleteRowByCaseId(case_id,imageType)
                                Log.e("syncimagestatus", code.toString())
                                Log.e("syncimagestatus", "success")
                                //db.deleteCaseImageFromTable(case_id)
                                // MessageDialog()
                                // Clear The form

                                //Clear the form
                            } else {
                                showAlertDialogMessage(
                                    this@MainActivity,
                                    response.body()!!.message.toString()
                                )
//                                    showAlertDialogMessage(
////                                        this@PSLevelSubmitDeadBodyInfoActivity,
////                                        "SERVER ERROR !!!"
////                                    )
                            }


                        }
                    } catch (exception: java.lang.Exception) {
                       // closeProgressDialogCall()
                        Toast.makeText(
                            this@MainActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (response.code() == 400) {
                  //  closeProgressDialogCall()
                    Toast.makeText(
                        this@MainActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                   // closeProgressDialogCall()
                    Toast.makeText(
                        this@MainActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<ImageUploadApiResponseModel>, t: Throwable) {
               // closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MainActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })
    }
    @SuppressLint("SuspiciousIndentation")
    fun updateMorguecaseFromDatabase(case_id:String,agemin:Int,agemax:Int,height:String,gender:String,
                                     general_condition:String,foot_des:String,male_private:String,cloth:String,
                                     peculiarities:String,hair:String,haircolor:String) {
        // progressDialogCall(this@MorgueLevelSubmitInfoActivity)

        // if (checkForInternet(this@PSLevelSubmitDeadBodyInfoActivity)) {

        var jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()


        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService

        val call = mAPIService.updateCaseMorgue(
            jwt_token,
            case_id.toRequestBody("text/plain".toMediaTypeOrNull()),
            agemin,
            agemax,
            height.toRequestBody("text/plain".toMediaTypeOrNull()),
            gender.toRequestBody("text/plain".toMediaTypeOrNull()),
            general_condition.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            foot_des.toRequestBody("text/plain".toMediaTypeOrNull()),
            male_private.toRequestBody("text/plain".toMediaTypeOrNull()),
            cloth.toRequestBody("text/plain".toMediaTypeOrNull()),
            peculiarities.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            hair.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            haircolor.toRequestBody("text/plain".toMediaTypeOrNull())
        ).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>, response: Response<String>
            ) {
                if (response.code() == 200) {
                   // closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {

                           var result: JSONObject? = null

                            val s = response.body()

                            result = JSONObject(s)

                            Log.e("response", "done")

                            if (result!!.optString("success").equals("true"))  {
                                db1.deleteRowByCaseIdMorgeData(case_id)

                            } else {
                                showAlertDialogMessage(
                                    this@MainActivity,
                                    result!!.optString("message").toString()
                                )

                            }

                        }
                    } catch (exception: java.lang.Exception) {
                       // closeProgressDialogCall()
                        Toast.makeText(
                            this@MainActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else if (response.code() == 400) {

                    //closeProgressDialogCall()
                    Toast.makeText(
                        this@MainActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                    //closeProgressDialogCall()
                    Toast.makeText(
                        this@MainActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                //closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MainActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })
    }


    private fun psSubmitApiCalling(){
        var i = 0
        if(psSubmitDataModelArrayList.size>0){

            val elements = psSubmitDataModelArrayList.get(i).poPhoto
            val psSubmitDataModel = psSubmitDataModelArrayList.get(i)
            var photopath = stringSplitManual(elements)
            /* val cleanString =  psSubmitDataModelArrayList.get(i).poPhoto.removePrefix("[").removeSuffix("]")
             val bodyImageFromTable = cleanString.split(",").map { it.trim() }
             System.out.println("CID WEST BENGAL "+i.toString()+"imagePath"+bodyImageFromTable.get(0))*/
            Log.d("PS API CALL","BEFORE API CALL")
            SubmitUDPSData(i,photopath,psSubmitDataModel)
            Log.d("PS API CALL","AFTER API CALL")
           /* while (i<psSubmitDataModelArrayList.size){
                //val cleanString = psSubmitDataModelArrayList.get(i).poPhoto.removePrefix("[").removeSuffix("]")
                    val elements = psSubmitDataModelArrayList.get(i).poPhoto
                    val psSubmitDataModel = psSubmitDataModelArrayList.get(i)
                    var photopath = stringSplitManual(elements)
                   *//* val cleanString =  psSubmitDataModelArrayList.get(i).poPhoto.removePrefix("[").removeSuffix("]")
                    val bodyImageFromTable = cleanString.split(",").map { it.trim() }
                    System.out.println("CID WEST BENGAL "+i.toString()+"imagePath"+bodyImageFromTable.get(0))*//*
                     Log.d("PS API CALL","BEFORE API CALL")
                    SubmitUDPSData(i,photopath,psSubmitDataModel)
                    Log.d("PS API CALL","AFTER API CALL")

            }*/
            //psSubmitDataModelArrayList.clear()

        }


    }
 /*   fun stringSplit(s: String): Array<String> {
        val str = s.split(',').toTypedArray()
        for (i in str.indices) {
            str[i] = str[i].replace('[', ' ').replace(']', ' ').trim()
        }
        return str
    }*/

    fun stringSplitManual(s: String): Array<String?>? {
        var k = 0
        var len = 0
        for (p in 0 until s.length) {
            if (s[p] == ',') {
                len++
            }
        }
        val str = arrayOfNulls<String>(len + 1)
        var newStr = ""
        val ch = CharArray(s.length)
        println("S Length = " + s.length)
        for (i in 0 until s.length) {
            ch[i] = s[i]
            println("ch[" + i + "] = " + ch[i])
        }
        for (j in ch.indices) {
            if (ch[j] == '[' || ch[j] == ']' || ch[j] == ' ') {
                if (j == ch.size - 1) {
                    str[k] = newStr.trim { it <= ' ' }
                    println("str[] = " + str[k])
                    newStr = ""
                }
                continue
            } else {
                if (ch[j] != ',') {
                    newStr = newStr + ch[j]
                    println("newStr = $newStr")
                } else {
                    str[k] = newStr.trim { it <= ' ' }
                    println("str[] = " + str[k])
                    newStr = ""
                    k++
                }
            }
        }
        return str
    }

    private fun getMorgueListForPS() {

        var mAPIService: APIService? = null
       // progressDialogCall(this@MainActivity)
        mAPIService = ApiUtils.apiService
        Log.e("Token",SharedPreferenceStorage.getValue(applicationContext, SharedPreferenceStorage.JWT_TOKEN, "").toString())
        SharedPreferenceStorage.getValue(applicationContext, SharedPreferenceStorage.JWT_TOKEN, "")
            ?.let {
                mAPIService.getMorgueList(it)
                    .enqueue(object : Callback<MorgueListApiModelClass> {

                        override fun onResponse(
                            call: Call<MorgueListApiModelClass>,
                            response: Response<MorgueListApiModelClass>
                        ) {
                            try {
                                if (response.isSuccessful) {
                                    //closeProgressDialogCall()
                                    Log.e("MorgueList", response.body()!!.data.toString())
                                    // Log.e("LogIn_Token", response.body()!!.status.toString())
                                    if (response.body()!!.status.equals("success")) {
                                      /*  val preferences =
                                            getSharedPreferences("DATAMORGUE", Context.MODE_PRIVATE)
                                        val preferencesEditor = preferences.edit()
                                        preferencesEditor.putBoolean("Morgue", true)
                                        preferencesEditor.putString(
                                            "morgueList",
                                            response.body()!!.data.toString()

                                        )


                                        preferencesEditor.apply()*/

                                     //   Log.d("M SIZE",districtArrayList.size.toString())

                                   /*   for (j in morgueArrayList1) {
                                            val mi = morgueArrayList1.get(j).id
                                            val mname = morgueArrayList1.get(j).name
                                            db1.addMorgue(mname,mi)

                                        }*/
                                        val  morgueArrayList1 = response.body()!!.data
                                        // Log.d("M SIZE",morgueArrayList1.toString())
                                        db1.addMorgue("0","Select Morgue")
                                        for (j in morgueArrayList1) {
                                            val id = j.id.toString()
                                            val mname = j.name.toString()
                                            Log.d("Morgue id",id)
                                            Log.d("Morgue name",mname)
                                            db1.addMorgue(id,mname)

                                        }


                                        /*SharedPreferenceStorage.setValue(
                                            activity,
                                            "morgueList",
                                            response.body()!!.data.toString()
                                        )*/


                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            response.body()!!.data.toString(),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                   // closeProgressDialogCall()

                                }
                            }
                            catch (exception: java.lang.Exception) {
                               // closeProgressDialogCall()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Some issue in server end",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                        }

                        override fun onFailure(
                            call: Call<MorgueListApiModelClass>,
                            t: Throwable
                        ) {
                            //closeProgressDialogCall()
                            intent = Intent(baseContext, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
            }


    }
    fun getSHA1Fingerprint(context: Context) {
        try {
            val packageInfo = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )

            // Get signatures from the package info
            val signatures: Array<Signature> = packageInfo.signatures

            // Iterate through signatures
            for (signature in signatures) {
                // Create a MessageDigest instance for SHA1
                val md = MessageDigest.getInstance("SHA1")

                // Update the digest with the signature
                md.update(signature.toByteArray())

                // Generate the SHA1 fingerprint
                val digest = md.digest()

                // Convert the byte array to a hex string
                val hexString = StringBuilder()
                for (b in digest) {
                    hexString.append(String.format("%02X", b))
                }

                // Return the SHA1 fingerprint
                System.out.println("SHA1-->"+hexString.toString())

            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("UDCASE", "Error obtaining SHA1 fingerprint", e)
        } catch (e: NoSuchAlgorithmException) {
            Log.e("UDCASE", "Error obtaining SHA1 fingerprint", e)
        }

        // Return null if unable to obtain the fingerprint

    }


    private fun SearchAlertDialog() {
        // Create an alert builder
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.custom_search_alert_layout, null)
        builder.setView(customLayout)

        val tiet_start_date = customLayout.findViewById<TextInputEditText>(R.id.tiet_start_date)
        val tiet_end_date = customLayout.findViewById<TextInputEditText>(R.id.tiet_end_date)

        val btn_submit = customLayout.findViewById<MaterialButton>(R.id.btn_submit)
        val btn_close = customLayout.findViewById<MaterialButton>(R.id.btn_close)

        tiet_start_date.setOnClickListener {
            val myCase = DatePicker(
                activity, tiet_start_date, tiet_start_date.text.toString()
            )
            myCase.selectDate()
            myCase.setFutureDateEnable(false)
        }

        tiet_end_date.setOnClickListener {
            val myCase = DatePicker(
                activity, tiet_end_date, tiet_end_date.text.toString()
            )
            myCase.selectDate()
            myCase.setFutureDateEnable(false)
        }



        btn_submit.setOnClickListener {
            if (tiet_start_date.text!!.isEmpty() || tiet_end_date.text!!.isEmpty()) {
                showToastMessage("Invalid Input")
            } else {
                val sdf = SimpleDateFormat("dd/MM/yyyy")

                val firstDate: Date? = sdf.parse(tiet_start_date.text.toString())
                val secondDate: Date? = sdf.parse(tiet_end_date.text.toString())

                val d1 = sdf.format(firstDate)
                val d2 = sdf.format(secondDate)
                if (firstDate != null) {
                    if (firstDate > secondDate) {
                        Log.e("DATE1", firstDate.toString())
                        Log.e("DATE2", secondDate.toString())
                        showToastMessage("From date can not be greater then To date")
                    } else {
                        Log.e("DATE1", firstDate.toString())
                        Log.e("DATE2", secondDate.toString())
                        CaseDataAPICall(d1, d2)
                    }
                }
            }
        }

        btn_close.setOnClickListener {
            dialog.dismiss()
        }

        dialog = builder.create()
        dialog.show()
    }

    private fun CaseDataAPICall(startDate: String, endDate: String) {
        var mAPIService: APIService? = null
       // progressDialogCall()
        mAPIService = ApiUtils.apiService
        Log.i("FROMDATE", startDate)
        Log.i("ENDDATE", endDate)
        jwt_token =
            SharedPreferenceStorage.getValue(this, SharedPreferenceStorage.JWT_TOKEN, "").toString()
        mAPIService.CaseDataApi(jwt_token, startDate, endDate)
            .enqueue(object : Callback<CaseDataModel> {

                override fun onResponse(
                    call: Call<CaseDataModel>, response: Response<CaseDataModel>
                ) {
                    if (response.code() == 400) {
                        progressDialog!!.cancel()
                        Toast.makeText(
                            this@MainActivity, "Some issue in server end", Toast.LENGTH_LONG
                        ).show()
                    } else {
                        try {
                            if (response.isSuccessful) {
                                if (response.body()!!.message.toString().equals("success")) {
                                    Log.e("LogIn_Status", response.body()!!.result.toString())
                                    Log.e("LogIn_Token", response.body()!!.message.toString())
                                    if (response.body()!!.message.toString().equals("success")) {

                                        Toast.makeText(
                                            this@MainActivity,
                                            response.body()!!.message.toString(),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        CaseDataHashMap.clear()
                                        val inList = response.body()!!.result
                                        var j: Int = 0
                                        for (i in inList) {
                                            var c_id = ""
                                            var ud_number = ""
                                            var ud_date = ""
                                            var officer_name = ""
                                            var officer_phone = ""
                                            var latitude = ""
                                            var longitude = ""
                                            var place = ""
                                            var ud_officer = ""
                                            var udofficer_phone = ""
                                            var male_private = ""
                                            var status = ""
                                            var district_id = ""
                                            var ps_id = ""
                                            var submit_time = ""
                                            if (inList.get(j).case_id.toString()
                                                    .isNullOrEmpty()
                                            ) c_id = "0"
                                            else c_id = inList.get(j).case_id.toString()

                                            if (inList.get(j).ud_number.isNullOrEmpty()) ud_number =
                                                "0"
                                            else ud_number = inList.get(j).ud_number.toString()

                                            if (inList.get(j).ud_date.isNullOrEmpty()) ud_date = "0"
                                            else ud_date = inList.get(j).ud_date.toString()

                                            if (inList.get(j).officer_name.isNullOrEmpty()) officer_name =
                                                "0"
                                            else officer_name =
                                                inList.get(j).officer_name.toString()

                                            if (inList.get(j).officer_phone.isNullOrEmpty()) officer_phone =
                                                "0"
                                            else officer_phone =
                                                inList.get(j).officer_phone.toString()

                                            if (inList.get(j).latitude.isNullOrEmpty()) latitude =
                                                "0"
                                            else latitude = inList.get(j).latitude.toString()

                                            if (inList.get(j).longitude.isNullOrEmpty()) longitude =
                                                "0"
                                            else longitude = inList.get(j).longitude.toString()

                                            if (inList.get(j).place.isNullOrEmpty()) place = "0"
                                            else place = inList.get(j).place.toString()

                                            if (inList.get(j).ud_officer.isNullOrEmpty()) ud_officer =
                                                "0"
                                            else ud_officer = inList.get(j).ud_officer.toString()

                                            if (inList.get(j).udofficer_phone.isNullOrEmpty()) udofficer_phone =
                                                "0"
                                            else udofficer_phone =
                                                inList.get(j).udofficer_phone.toString()

                                            if (inList.get(j).male_private.toString()
                                                    .isNullOrEmpty()
                                            ) male_private = "0"
                                            else male_private =
                                                inList.get(j).male_private.toString()

                                            if (inList.get(j).status.toString()
                                                    .isNullOrEmpty()
                                            ) status = "0"
                                            else status = inList.get(j).status.toString()

                                            if (inList.get(j).district_id.isNullOrEmpty()) district_id =
                                                "0"
                                            else district_id = inList.get(j).district_id.toString()

                                            if (inList.get(j).ps_id.isNullOrEmpty()) ps_id = "0"
                                            else ps_id = inList.get(j).ps_id.toString()

                                            if (inList.get(j).submit_time.isNullOrEmpty()) submit_time =
                                                "0"
                                            else submit_time = inList.get(j).submit_time.toString()


                                            val inListHash = java.util.HashMap<String, String>()
                                            inListHash.put("c_id", c_id)
                                            inListHash.put("ud_number", ud_number)
                                            inListHash.put("ud_date", ud_date)
                                            inListHash.put("officer_name", officer_name)
                                            inListHash.put("officer_phone", officer_phone)
                                            inListHash.put("latitude", latitude)
                                            inListHash.put("longitude", longitude)
                                            inListHash.put("place", place)
                                            inListHash.put("ud_officer", ud_officer)
                                            inListHash.put("udofficer_phone", udofficer_phone)
                                            inListHash.put("male_private", male_private)
                                            inListHash.put("status", status)
                                            inListHash.put("district_id", district_id)
                                            inListHash.put("ps_id", ps_id)
                                            inListHash.put("submit_time", submit_time)
                                            CaseDataHashMap.add(inListHash)
                                            j = j + 1
                                        }

                                        val iIntent = Intent(
                                            activity, UnidentifiedListViewActivity::class.java
                                        )
                                        iIntent.putExtra("CaseData", CaseDataHashMap)
                                        startActivity(iIntent)
                                        progressDialog!!.cancel()


                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            response.body()!!.message.toString(),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                } else {
                                    progressDialog!!.cancel()
                                    showToastMessage(response.body()!!.message)

                                }
                            }
                            progressDialog!!.cancel()

                        } catch (exception: java.lang.Exception) {
                            progressDialog!!.cancel()
                            Toast.makeText(
                                this@MainActivity, "Some issue in server end", Toast.LENGTH_LONG
                            ).show()

                        }

                    }
                }

                override fun onFailure(call: Call<CaseDataModel>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity, "SERVER ERROR !!!", Toast.LENGTH_LONG
                    ).show()
                }
            })
    }


//    fun showToastMessage(msg: String) {
//        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
//    }

    private fun logInValidityCheck() {
        var mAPIService: APIService? = null
        //progressDialogCall()
        mAPIService = ApiUtils.apiService
        jwt_token =
            SharedPreferenceStorage.getValue(this, SharedPreferenceStorage.JWT_TOKEN, "").toString()
        mAPIService.LogInValidityCheckAPI(jwt_token).enqueue(object : Callback<LoginCheckModel> {

                override fun onResponse(
                    call: Call<LoginCheckModel>, response: Response<LoginCheckModel>
                ) {
                    if (response.code() == 400) {
                        //progressDialog!!.cancel()
                        logoutfinal()
                    } else {
                        try {
                            if (response.isSuccessful) {
                                Log.e("LogIn_Status", response.body()!!.status.toString())
                                Log.e("LogIn_Token", response.body()!!.version.toString())
                                if (response.body()!!.status.toString().equals("success")) {

                                    Toast.makeText(
                                        this@MainActivity,
                                        response.body()!!.status.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    if (version.toString() != response.body()!!.version.toString()) updateVersion()


                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        response.body()!!.status.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    logoutfinal()
                                }

                            }
                           // progressDialog!!.cancel()
                        } catch (exception: java.lang.Exception) {
                            Toast.makeText(
                                this@MainActivity, "Some issue in server end", Toast.LENGTH_LONG
                            ).show()

                        }
                    }
                }

                override fun onFailure(call: Call<LoginCheckModel>, t: Throwable) {
                    logoutfinal()
                }
            })


    }

    private fun updateVersion() {
        val alertDialog: android.app.AlertDialog.Builder =
            android.app.AlertDialog.Builder(this@MainActivity)
        alertDialog.setTitle(" ")
        alertDialog.setIcon(R.drawable.attention_icon)
        alertDialog.setMessage("Please Update the App Version.../n You are using $version version")
        alertDialog.setPositiveButton(
            "OK"
        ) { dialog, id ->
            logoutfinal()
            finish()
        }
        val alert: android.app.AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

//    private fun getLatestApk() {
//        var mAPIService: APIService? = null
//          progressDialogCall(this@MainActivity)
//        mAPIService = ApiUtils.apiService
//        val jwt_token =
//            SharedPreferenceStorage.getValue(this, SharedPreferenceStorage.JWT_TOKEN, "").toString()
//        mAPIService.getLatestApk(
//            jwt_token,
//            Constants.ApplicationPlatform.app.toString(),
//            //getDeviceID().toString(),
//            AppInfoHelper.getInfo(baseContext, AppInfoHelper.VERSION_NAME)
//            // SharedPreferenceStorage.getValue(baseContext, SharedPreferenceStorage.USERID, "")
//        ).enqueue(object : Callback<GetLatestApkModel> {
//            override fun onResponse(
//                call: Call<GetLatestApkModel>, response: Response<GetLatestApkModel>
//            ) {
//                if (response.code() == 400) {
//                    closeProgressDialogCall()
//                    Toast.makeText(
//                        this@MainActivity, "Some issue in server end", Toast.LENGTH_LONG
//                    ).show()
//                }
//                else if (response.code() == 420) {
//                   // callForLogout()
//                } else if(response.code()==404){
//                    closeProgressDialogCall()
//                    Toast.makeText(
//                        this@MainActivity, "Api not found..", Toast.LENGTH_LONG
//                    ).show()
//                }
//                else if (response.code() == 450) {
//
//                    val errorBody = response.errorBody()
//                    if (errorBody != null) {
//                        // Here you can display or log the error body
//                        var downloadLine =
//                            Constants.BASE.BASE_URL + response.body()!!.updateLink
//                        var msg =
//                            "New version " + response.body()!!.appVersion + " App available. Your App need to Update"
//                        showUpdateAlertDialogMessage(
//                            this@MainActivity,
//                            msg,
//                            downloadLine,
//                            response.body()!!.isHardUpdate
//                        )
//
//                    } else {
//
//                    }
//                }
//                else {
//                    try {
//                        if (response.isSuccessful) {
//                            Log.e("getLatestApk", response.body()!!.success.toString())
////                                Log.e("LogIn_Token", response.body()!!.token.toString())
//                            if (response.body()!!.success) {
//                                // closeProgressDialogCall()
////                                    showToastMessage(response.body()!!.message)
//
//                                if (response.body()!!.isHardUpdate) {
//                                    var downloadLine =
//                                        Constants.BASE.BASE_URL + response.body()!!.updateLink
//                                    var msg =
//                                        "New version "  + " App available. Your App need to Update"
//                                    showUpdateAlertDialogMessage(
//                                        this@MainActivity,
//                                        msg,
//                                        downloadLine,
//                                        response.body()!!.isHardUpdate
//                                    )
//                                }
//                            }
//
//                        }
//                        closeProgressDialogCall()
//                    } catch (exception: Exception) {
//                        //closeProgressDialogCall()
//                        Toast.makeText(
//                            this@MainActivity,
//                            "Some issue in server end",
//                            Toast.LENGTH_LONG
//                        ).show()
//
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<GetLatestApkModel>, t: Throwable) {
//                closeProgressDialogCall()
//                showAlertDialogMessage(this@MainActivity, "SERVER ERROR")
//            }
//        })
//    }



    private fun DistrictAPICall() {
        var mAPIService: APIService? = null
        //progressDialogCall()
        mAPIService = ApiUtils.apiService
        jwt_token =
            SharedPreferenceStorage.getValue(this, SharedPreferenceStorage.JWT_TOKEN, "").toString()
        mAPIService.DistrictApi(jwt_token).enqueue(object : Callback<DistrictsAPIModel> {

                override fun onResponse(
                    call: Call<DistrictsAPIModel>, response: Response<DistrictsAPIModel>
                ) {
                    if (response.code() == 400) {
                        //progressDialog!!.cancel()
                        logoutfinal()
                    } else {
                        try {
                            if (response.isSuccessful) {
                                Log.e("LogIn_Status", response.body()!!.error_code.toString())
                                Log.e("LogIn_Token", response.body()!!.status.toString())
                                if (response.body()!!.status.toString().equals("success")) {

                                    var distList = response.body()!!.districts


                                } else {
                                    Toast.makeText(
                                        this@MainActivity,
                                        response.body()!!.status.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            }
                            //progressDialog!!.cancel()
                        } catch (exception: java.lang.Exception) {
                            Toast.makeText(
                                this@MainActivity, "Some issue in server end", Toast.LENGTH_LONG
                            ).show()

                        }
                    }
                }

                override fun onFailure(call: Call<DistrictsAPIModel>, t: Throwable) {
                    logoutfinal()
                }
            })


    }

    private fun progressDialogCall() {
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.logout -> {
                logout()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to exit?").setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                logoutfinal()
            }).setNegativeButton(
                "No",
                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun logoutfinal() {
        SharedPreferenceStorage.setValue(
            activity, SharedPreferenceStorage.JWT_TOKEN, ""
        )
        SharedPreferenceStorage.setValue(
            activity, SharedPreferenceStorage.USERID, ""
        )
        val accountsIntent = Intent(activity, LoginActivity::class.java)
        startActivity(accountsIntent)
        finish()
    }

   @SuppressLint("SuspiciousIndentation")
    fun SubmitUDPSData(i:Int, photoPath: Array<String?>?,psSubmitDataModel: PsSubmitDataModel) {
        progressDialogCall(this@MainActivity)
        Log.d("PS API CALL","WITH IN SubmitUDPSData for object" )
        try{
            for (l in  0..(photoPath!!.size - 1)){
                System.out.println("poPhotoPath" + photoPath[l]!!)
                placeofoccurrence.add(l, photoPath[l]!!)
            }

            for (i in 0..(placeofoccurrence.size - 1)) {

                val file: File = FileUtils.getFile(this, Uri.parse(placeofoccurrence.get(i)))
                //var file = File(visibleIdMarksImage.get(i))

                val filePart = MultipartBody.Part.createFormData(
                    "poPhoto",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                System.out.println("JoyFILESPart" + filePart)
                placeofoccurrenceimages.add(filePart)
            }


            //  saveImageNew(imagePath, "IMAGE")

            var jwt_token = SharedPreferenceStorage.getValue(
                applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
            ).toString()

            val PSID = psSubmitDataModelArrayList.get(i).psid.toInt()


            var morgueID= psSubmitDataModelArrayList.get(i).morgueID

            var ud_number=psSubmitDataModelArrayList.get(i).ud_number
            var ud_date=psSubmitDataModelArrayList.get(i).ud_date
            var lat=psSubmitDataModelArrayList.get(i).lat
            var long= psSubmitDataModelArrayList.get(i).long
            var placeDescription= psSubmitDataModelArrayList.get(i).placeDescription
            var officer_name=psSubmitDataModelArrayList.get(i).officer_name
            var officer_contact=psSubmitDataModelArrayList.get(i).officer_contact
            var deadbodytype = psSubmitDataModelArrayList.get(i).deadbodyType
            var vic_name = psSubmitDataModelArrayList.get(i).vic_name
            var vic_age = psSubmitDataModelArrayList.get(i).vic_age
            var vic_address=psSubmitDataModelArrayList.get(i).vic_address

            if(vic_age.equals("")) {
                vic_age = "0"
            }
            var vic_gender = psSubmitDataModelArrayList.get(i).vic_gen_val
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            val psIds = PSID
            val udNumber = ud_number.toRequestBody("text/plain".toMediaTypeOrNull())
            val udDate   = ud_date.toRequestBody("text/plain".toMediaTypeOrNull())
            val udOfficer = officer_name.toRequestBody("text/plain".toMediaTypeOrNull())
            val udOfficerPhone = officer_contact.toRequestBody("text/plain".toMediaTypeOrNull())
            val latitude = lat.toRequestBody("text/plain".toMediaTypeOrNull())
            val longitude = long.toRequestBody("text/plain".toMediaTypeOrNull())
            val place = placeDescription.toRequestBody("text/plain".toMediaTypeOrNull())
            val status =  psSubmitDataModelArrayList.get(i).deadbodyType.toRequestBody("text/plain".toMediaTypeOrNull())

            val vic_name_val = vic_name.toRequestBody("text/plain".toMediaTypeOrNull())
            val vic_gen_val = vic_gender.toRequestBody("text/plain".toMediaTypeOrNull())
            val morgue_id_val = morgueID.toRequestBody("text/plain".toMediaTypeOrNull())
            val vic_address_val=vic_address.toRequestBody("text/plain".toMediaTypeOrNull())

            Log.e("Details Data..","  jwtToken : "+jwt_token+
                    "    psID : "+psIds+
                    "     udNumber : "+ud_number+
                    "     udDate : "+ud_date+
                    "      udOfficerName : "+officer_name+
                    "      udOfficerContact : "+officer_contact+
                    "      lat : "+lat+
                    "      long : "+long+
                    "      place : "+placeDescription+
                    "      status : "+"1"+
                    "      vicName : "+vic_name+
                    "      vicAge : "+vic_age+
                    "      vicGender : "+vic_gender+
                    "      vicddress : "+vic_address)
            val call = mAPIService.caseDatailsPSLevel(
                jwt_token,
                psIds,
                udNumber,
                udDate,
                udOfficer,
                udOfficerPhone,
                latitude = latitude,
                longitude = longitude,
                place = place,
                status = status,
                placeofoccurrenceimages,
                deadbodytype.toInt(),
                vic_name_val,
                Integer.parseInt(vic_age),
                vic_gen_val,
                morgue_id_val,
               // vic_address_val
            ).enqueue(object : Callback<CaseDetails> {
                override fun onResponse(
                    call: Call<CaseDetails>, response: Response<CaseDetails>
                ) {
                    if (response.code() == 200) {
                        closeProgressDialogCall()
                        try {
                            if (response.isSuccessful) {
                                Log.e("Message_error", response.body()!!.message.toString())

                                if (response.body()?.success == true) {
                                    placeofoccurrence.clear()
                                    db1.deleteRowByIdPSData(psSubmitDataModelArrayList.get(i).id)
                                    Log.d("PS DATA","PS DATA Submitted successfully")
                                   // psSubmitDataModelArrayList.removeAt(i)
                                    val cursor = db1.getPSData()
                                    val rowQty=cursor!!.count
                                    if(rowQty>0){
                                        binding.mcvOfflineForSyn!!.visibility= View.VISIBLE
                                        binding.tvUnSynDatacount!!.setText(rowQty.toString()+" Unsynchronized data")
                                    }else{
                                        binding.mcvOfflineForSyn!!.visibility= View.GONE
                                    }
                                    showAlertDialogMessageSuccess(
                                        this@MainActivity,
                                       "Your local storage data is synchronizing with online database"
                                    )

                                } else {

                                    Toast.makeText(
                                        this@MainActivity,
                                        response.body()!!.message.toString(),
                                        Toast.LENGTH_LONG
                                    ).show()
//                                    showAlertDialogMessage(
////                                        this@PSLevelSubmitDeadBodyInfoActivity,
////                                        "SERVER ERROR !!!"
////                                    )
                                }


                            }
                        } catch (exception: java.lang.Exception) {
                        closeProgressDialogCall()
                            Toast.makeText(
                                this@MainActivity,
                                "Some issue in server end. Error Core : "+response.code().toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else if (response.code() == 400) {
                        closeProgressDialogCall()
                        Toast.makeText(
                            this@MainActivity,
                            "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else {
                        //  progressDialog!!.cancel()
                        closeProgressDialogCall()
                        Toast.makeText(
                            this@MainActivity,
                            "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

                override fun onFailure(call: Call<CaseDetails>, t: Throwable) {
                   closeProgressDialogCall()

                    Toast.makeText(
                        this@MainActivity,
                        "SERVER ERROR on Failure !!!" + t.message,
                        Toast.LENGTH_LONG
                    ).show()

                }
            })
        }catch (exception: IOException){
            Log.e("Exception",exception.toString())
        }

    }

    override fun onResume() {
        super.onResume()
        /*Toast.makeText(
            this@MainActivity,
            "ON RESUME",
            Toast.LENGTH_LONG
        ).show()*/
       // callNetworkConnection()
        val cursor = db1.getPSData()
        val rowQty=cursor!!.count
        if(rowQty>0){
            binding.mcvOfflineForSyn!!.visibility= View.VISIBLE
            binding.tvUnSynDatacount!!.setText(rowQty.toString()+" Unsynchronized data")
        }else{
            binding.mcvOfflineForSyn!!.visibility= View.GONE
        }


    }


    /*override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu., menu)
        return true
    }*/
}