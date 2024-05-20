package com.example.wbmissingfound

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.wbmissingfound.DBHelper.DatabaseDb
import com.example.wbmissingfound.Model.MorgueName
import com.example.wbmissingfound.RetroClient.RetroApi.APIService
import com.example.wbmissingfound.RetroClient.RetroApi.ApiUtils
import com.example.wbmissingfound.RetroClient.RetroModel.AllPsResponseModelClass
import com.example.wbmissingfound.RetroClient.RetroModel.CaseDetails
import com.example.wbmissingfound.RetroClient.RetroModel.DistrictAllPS
import com.example.wbmissingfound.RetroClient.RetroModel.MorgueDetails
import com.example.wbmissingfound.RetroClient.RetroModel.MorgueListApiModelClass
import com.example.wbmissingfound.RetroClient.RetroModel.PSAllOnly
import com.example.wbmissingfound.custom.DatePicker
import com.example.wbmissingfound.databinding.ActivityPslevelSubmitDeadBodyInfoBinding
import com.example.wbmissingfound.sharedStorage.SharedPreferenceStorage
import com.example.wbmissingfound.utils.FileUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfDouble
import org.opencv.imgproc.Imgproc
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat

import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.MutableList


class PSLevelSubmitDeadBodyInfoActivity : BaseActivity() , AdapterView.OnItemSelectedListener  {

    private  lateinit var binding:ActivityPslevelSubmitDeadBodyInfoBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private var used_current_loc: Boolean = false
    lateinit var fileUriPath: Uri

    var districtArrayList: List<DistrictAllPS> = ArrayList()
    var districtArrayAdapter: ArrayAdapter<DistrictAllPS>? = null

    var morgueArrayList: MutableList<MorgueName> = ArrayList()
    private val morgueArrayListStrng = ArrayList<String>()


    var morgueArrayListNew: List<MorgueDetails> = ArrayList()
    var morgueListarrayAdapter: ArrayAdapter<MorgueName>? = null

    var policeStationArrayList: List<PSAllOnly> = ArrayList()
    var policeStationArrayAdapter: ArrayAdapter<PSAllOnly>? = null

    var spcIdTypeHashMap = ArrayList<HashMap<String, String>>()
    var spcIdLocHashMap = ArrayList<HashMap<String, String>>()
    var bannerText: String = ""
    val psImagePart = ArrayList<MultipartBody.Part>()

    var db = DatabaseDb(this, null)

    lateinit var imagePath: String
    lateinit var imageCategory: String
    var selectedBodyType:String=""



    private val placeofoccurrence = ArrayList<String>()
    private val pItemImage = ArrayList<String>()
    private val faceImage = ArrayList<String>()
    private val placeofoccurrenceimages= ArrayList<MultipartBody.Part>()
    private val morgueListStr = ArrayList<String>()



    companion object {
        private const val FIRST_ACTIVITY_REQUEST_CODE = 1
        private const val SECOND_ACTIVITY_REQUEST_CODE = 2
        const val PATH = "path"
        const val TYPE = "type"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPslevelSubmitDeadBodyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //getMorgueListForPS()
        binding.llCaseDetailsCard?.setOnClickListener {
            if (binding.mcvCaseDetails.isVisible) {
                binding.mcvCaseDetails.visibility = View.GONE
                binding.ivCaseDetails.setImageResource(R.drawable.down_icon)


            } else if (binding.mcvCaseDetails.isGone) {
                binding.mcvCaseDetails.visibility = View.VISIBLE
                binding.ivCaseDetails.setImageResource(R.drawable.up_icon)

            }
        }

        binding.tietUdCaseOfficerContactNo.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                if (s.length==10){

                    binding.tietUdCaseOfficerContactNo.setError(null)
                }else if (s.length<10
                    || s.length>10 ){

                    binding.tietUdCaseOfficerContactNo.setError(getString(R.string.invalid_phoneno))
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
                if (s.length==10){

                    binding.tietUdCaseOfficerContactNo.setError(null)
                }else if (s.length<10 || s.length>10){
                    binding.tietUdCaseOfficerContactNo.setError(getString(R.string.invalid_phoneno))
                }
            }
        })

        var preferences = getSharedPreferences("DATA", 0)
        val value: Boolean = preferences.getBoolean("saved", false)
        val district: String? = preferences.getString("police_station", false.toString())
        if (value == true) {
            //Toast.makeText(this, "got data", Toast.LENGTH_SHORT).show()
            if (district != null) {
                Log.e("district_saikat", district)
            }
        } else {
            // Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show()
        }

        //val db = DatabaseDb(this, null)

        binding.llTvCaseDate.setOnClickListener {
            val myCase = DatePicker(
                this@PSLevelSubmitDeadBodyInfoActivity,
                binding.tvCaseDate,
                binding.tvCaseDate.text.toString()
            )
            myCase.selectDate()
            myCase.setFutureDateEnable(false)
        }

        // Set PS name from SharedPreference
         SetPsByUserIDForUser()
        //Thread.sleep(300)
        // Set Morgue List from SharedPreference
         setMorgueListForPS()

        // Call API for getting PS list by USER ID

        binding.spinnerOccurrenceDist.onItemSelectedListener = this
        binding.spinnerMorgue!!.onItemSelectedListener=this
        binding.rbUnidentified.isChecked=true

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
        binding.btnIdenticalMarks.setOnClickListener {

            if (checkFileReadWritePermission() && checkCameraPermission()) {
                /*ImagePicker.with(this@SubmitDeadBodyInformationActivity)
                    .crop() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080, 1080
                    )
                    .start(IMAGE_REQUEST_IDENTICAL_MARKS)*/
                bannerText = "Please Capture Photo of Place Of Occurrence"
                val type = "IDENTIMARKS"
                openPostActivity.launch(
                    CameraXActivity.getIntent(this, 1, type, bannerText)
//                    CameraProcessActivity.getIntent(this, 1, type, bannerText)
                )
            }
        }
        binding.rgDeadbodytype!!.setOnCheckedChangeListener { group, checkedId ->
            val radioButton: RadioButton = group.findViewById(checkedId)
            // Do something with the selected RadioButton
            println("Selected: ${radioButton.text}")
            if(radioButton.text.equals("Identified"))  {
                binding.identifiedBodyDetails!!.visibility = View.VISIBLE
                selectedBodyType="00"
                Log.e("body",selectedBodyType)
            }
            else{
                binding.identifiedBodyDetails!!.visibility = View.GONE

                selectedBodyType="11"
                Log.e("body",selectedBodyType)
                binding.tietVictimName.setText("")
                binding.tietVictimAge.setText("")
                binding.tietVictimAddress.setText("")
            }
        }
        binding.btnSubmit.setOnClickListener{
            if(placeofoccurrence.size>0) {
                if(checkForInternet(this@PSLevelSubmitDeadBodyInfoActivity)){
                    if(checkValidation())
                        SubmitUDPSData()
                }else{


                    Log.d("CID WB", morgueArrayList.get(binding.spinnerMorgue.selectedItemPosition).name)
                    var selectedMorgueName= morgueArrayList.get(binding.spinnerMorgue.selectedItemPosition).name
                    Log.d("MorgueId",selectedMorgueName)
                    var cursor1 = db.getmorgueIdbyName(selectedMorgueName)
                    var morgueid = ""
                    while (cursor1!!.moveToNext()) {
                        morgueid=cursor1.getString(0)

                    }
                    cursor1.close()
                     Log.d("MorgueId",morgueid)
                    var psid=SharedPreferenceStorage.getValue(applicationContext,"PSID",0)
                    if(checkValidation()) {
                        db.addSubmitPSData(
                            psid,
                            morgueid,
                            binding.tietCaseNumber.text.toString(),
                            binding.tvCaseDate.text.toString(),
                            binding.tietUdCaseOfficerName.text.toString(),
                            binding.tietUdCaseOfficerContactNo.text.toString(),
                            binding.tietLatitude.text.toString(),
                            binding.tietLongitude.text.toString(),
                            getGender(),
                            binding.tietPlaceWhereDeadBodyFound.text.toString(),
                            getDeadBodyType().toString(),
                            placeofoccurrence,
                            getDeadBodyType(),
                            binding.tietVictimName.text.toString(),
                            binding.tietVictimAge.text.toString(),
                            binding.tietVictimAddress.text.toString()
                        )
                        val cursor = db.getPSData()
                        var id: Int = 0
                        if (cursor != null) {
                            if (cursor.moveToLast()) {
                                //name = cursor.getString(column_index);//to get other values
                                if (cursor != null) {
                                    id = cursor.getInt(0)
                                    //Log.d("CID WB","ID-->"+id +"CASE NO-->"+   binding.tietCaseNumber.text.toString())
                                }//to get id, 0 is the column index
                            }
                        }
                        for (i in 0..(placeofoccurrence.size - 1)) {

                            Log.e("IMAGE_TEST", placeofoccurrence.get(i))

                            saveImage(placeofoccurrence.get(i), "PSLEVELIMAGE", id)

                        }
                        /*Toast.makeText(
                            this@PSLevelSubmitDeadBodyInfoActivity,
                            "As no internet we are submitting data offline ",
                            Toast.LENGTH_LONG
                        ).show()*/
                        showAlertDialogMessageSuccess(
                            this@PSLevelSubmitDeadBodyInfoActivity,
                            resources.getString(R.string.save_local_stroage)                        )
                        placeofoccurrence.clear()
                        placeofoccurrenceimages.clear()
                        //binding.btnIdenticalMarks.visibility=View.VISIBLE
                        binding.llIdenticalMarksPic.removeAllViews()
                        clearAllField()
                    }





                }
            } else{
                Toast.makeText(
                    this@PSLevelSubmitDeadBodyInfoActivity,
                    "Please add images ",
                    Toast.LENGTH_LONG
                ).show()
            }
            // Submit Form data in by calling API


        }


    }
    private  fun   SetPsByUserIDForUser(){

        val policeStationNAme= SharedPreferenceStorage.getValue(applicationContext, "PSName", "").toString()

        binding.textViewPolice.text=policeStationNAme

    }
    private  fun setMorgueListForPS(){

      //  val inputString=SharedPreferenceStorage.getValue(applicationContext, "morgueList", "").toString()

       /* val newMorgueDetails = MorgueName("Select Morgue")
        morgueArrayList.add(0, newMorgueDetails)*/
        val cursor = db.getmorgueList()
        while (cursor!!.moveToNext()) {
            val mgname=MorgueName(cursor.getString(1))
            morgueArrayList.add(mgname)
        }

 /*       val newMorgueDetails = MorgueName("Select Morgue")

        morgueArrayList.add(0, newMorgueDetails)*/

   /*     val cleanString = inputString.removePrefix("[, ").removeSuffix("]")
        val elements = cleanString.split(",").map { it.trim() }
        val morgueArrayList: MutableList<MorgueDetails> = ArrayList()

        elements.forEachIndexed { index, name ->
            val morgueDetail = MorgueDetails(index.toString(), name)
            morgueArrayList.add(morgueDetail)
        }

*/

        morgueListarrayAdapter = ArrayAdapter(
            baseContext,
            android.R.layout.simple_spinner_dropdown_item,
            morgueArrayList
        )

        binding.spinnerMorgue!!.adapter = morgueListarrayAdapter

    }

    private fun checkValidation(): Boolean {
        var noError = true
        /*if (binding.tietCaseNumber.text.toString().isEmpty()) {
            binding.tietCaseNumber.isFocusableInTouchMode = true
            binding.tietCaseNumber.requestFocus()
            showToastMessage("Please provide Case Reference NO")
            noError = false
        }*/
         if (binding.tvCaseDate.text.toString().isEmpty()) {
            binding.tvCaseDate.isFocusableInTouchMode = true
            binding.tvCaseDate.requestFocus()
            showToastMessage("Please provide Case Date")
            noError = false
        }
         else if (binding.tietPlaceWhereDeadBodyFound.text.toString().isEmpty()) {
             binding.tvCaseDate.isFocusableInTouchMode = true
             binding.tvCaseDate.requestFocus()
             showToastMessage("Please provide the place of occurance(P.O)")
             noError = false
         }
         else if (selectedBodyType.equals("00")) {

             var name_vald=binding.tietVictimName.text
             var age_vald=binding.tietVictimAge.text
             var address_vald=binding.tietVictimAddress.text

             if(name_vald.isNullOrBlank()){
                 showToastMessage("Please give Name of Deceased")
                 noError = false
                 Log.e("Filed_Status"," Blank")
             }
             else if(age_vald.isNullOrBlank()){
                 showToastMessage("Please give Age of Deceased")
                 noError = false
                 Log.e("Filed_Status"," Blank")
             }
             else if(address_vald.isNullOrBlank()
             ){
                 showToastMessage("Please give  Address of Deceased")
                 noError = false
                 Log.e("Filed_Status"," Blank")
             }


         }
       /* else if (binding.tietUdCaseOfficerName.text.toString().isEmpty()) {
            binding.tvCaseDate.isFocusableInTouchMode = true
            binding.tvCaseDate.requestFocus()
            showToastMessage("Please provide UD case officer name")
            noError = false
        }
        else if (binding.tietUdCaseOfficerContactNo.text.toString().isEmpty()) {
            binding.tvCaseDate.isFocusableInTouchMode = true
            binding.tvCaseDate.requestFocus()
            showToastMessage("Please provide UD case officer phone number")
            noError = false
        }*/

//        else if (selectedBodyType.equals("11")){
//            noError=true
//        }


        return noError
    }
    fun ImageCheck(imagefile:File): Int{
        val bitmap = BitmapFactory.decodeFile(imagefile.absolutePath)
        val sourceMatImage = Mat()
        val destination = Mat()
        val matGray = Mat()
        Utils.bitmapToMat(bitmap, sourceMatImage)
        Imgproc.cvtColor(sourceMatImage, matGray, Imgproc.COLOR_BGR2GRAY)
        Imgproc.Laplacian(matGray, destination, 3)
        val median = MatOfDouble()
        val std = MatOfDouble()
        Core.meanStdDev(destination, median, std)
        val variance= DecimalFormat("0.00").format(Math.pow(std.get(0, 0)[0], 2.0)).toDouble()
        val hsvImage = Mat()
        Imgproc.cvtColor(sourceMatImage, hsvImage, Imgproc.COLOR_BGR2HSV)
        val channels: List<Mat> = ArrayList(3)
        Core.split(hsvImage, channels)
        val valueChannel = channels.get(2)
        val averageBrightness = Core.mean(valueChannel).`val`[0]
        if(variance<100)
            return 2
        else if(averageBrightness<100)
            return 3
        else
            return 1
    }
    private val openPostActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {

                binding.llPsLevelImageview!!.visibility = View.VISIBLE
                imagePath = result.data?.getStringExtra("path").toString()

                val file: File = FileUtils.getFile(this, Uri.parse(imagePath))
                val imageFlag = ImageCheck(file)
                if(imageFlag == 2)
                {
//                    Toast.makeText(
//                        this@PSLevelSubmitDeadBodyInfoActivity,
//                        "Please Capture Clear Image ",
//                        Toast.LENGTH_LONG
//                    ).show()
                    showAlertDialogMessage(this@PSLevelSubmitDeadBodyInfoActivity,
                        "Please Capture Clear Image ")

                }
                if(imageFlag == 3)
                {
//                    Toast.makeText(
//                        this@PSLevelSubmitDeadBodyInfoActivity,
//                        "Please Capture Bright Image ",
//                        Toast.LENGTH_LONG
//                    ).show()
                    showAlertDialogMessage(this@PSLevelSubmitDeadBodyInfoActivity,
                        "Please Capture Bright Image ")

                }
                if(imageFlag == 1) {
                    Log.e("imagePathOnActivity", imagePath);
                    imageCategory = result.data?.getStringExtra("type").toString()
                    // _imageUri = Uri.parse(result.data?.getStringExtra("imageUri"));


                    val uri: String? = result.data?.getStringExtra("path")
                    if (uri != null) {
                        imagePath = uri
                    }
                    val inflater =
                        LayoutInflater.from(this).inflate(R.layout.custom_imageview_layout, null)
                    val img_main = inflater.findViewById<ImageView>(R.id.img_photo)
                    val img_close = inflater.findViewById<ImageButton>(R.id.btn_close)
                    img_main.setImageURI(Uri.parse(uri))
                    //  binding.btnIdenticalMarks.visibility=View.GONE
//                val file: File = FileUtils.getFile(this, Uri.parse(imagePath))
//
//                if (file.exists()) {
//
//                    // on below line we are creating an image bitmap variable
//                    // and adding a bitmap to it from image file.
//                    val imgBitmap = BitmapFactory.decodeFile(file.absolutePath)
//
//                    // on below line we are setting bitmap to our image view.
//                    img_main.setImageBitmap(setDefaultValues(imgBitmap))
//                }


                    Log.e("SANKHA", imagePath.toString())
                    Log.e("SANKHA", imageCategory.toString())
                    Log.e("SANKHA", faceImage.size.toString())
                    if (imageCategory.equals("IDENTIMARKS")) {
                        placeofoccurrence.add(imagePath)
                        Log.e("UDCASE", imagePath)

                        binding.llIdenticalMarksPic.addView(inflater)
                        img_close.setOnClickListener {
                            placeofoccurrence.remove(imagePath)
                            imagePath = ""
                            //binding.btnIdenticalMarks.visibility=View.VISIBLE
                            binding.llIdenticalMarksPic.removeView(inflater)
                            //(binding.llIdenticalMarksPic.parent as ViewGroup).removeView(binding.llIdenticalMarksPic)
                        }
                    }
                    Log.e("SANKHA", faceImage.size.toString())
                }
            }
        }

   /* private fun GetPsByUserIDForUser() {
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
                                    closeProgressDialogCall()
                                    Log.e("LogIn_Status", response.body()!!.data.toString())
                                    // Log.e("LogIn_Token", response.body()!!.status.toString())
                                    if (response.body()!!.status.equals("success")) {
                                        val preferences =
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
                                        binding.textViewPolice.text=policeStationNAme


                                    } else {
                                        Toast.makeText(
                                            this@PSLevelSubmitDeadBodyInfoActivity,
                                            response.body()!!.data.toString(),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }


                                }
                            } catch (exception: java.lang.Exception) {
                                closeProgressDialogCall()
                                Toast.makeText(
                                    this@PSLevelSubmitDeadBodyInfoActivity,
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


    }*/


    fun getDeadBodyType():Int {
        var deadbodyType = 0
        if (binding.rbIdentifed!!.isChecked)
            deadbodyType = 1
        else if (binding.rbUnidentified!!.isChecked)
            deadbodyType = 0


        return deadbodyType

    }
    private fun getGender(): String {
        var gen = ""
        if (binding.rbMale!!.isChecked)
            gen = "Male"
        else if (binding.rbFemale!!.isChecked)
            gen = "Female"
        else if (binding.rbOther!!.isChecked)
            gen = "Others"

        return gen
    }
    @SuppressLint("SuspiciousIndentation")
    fun SubmitUDPSData() {
        progressDialogCall(this@PSLevelSubmitDeadBodyInfoActivity)

        placeofoccurrenceimages.clear()

        try{
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

            Log.e("Image list size",placeofoccurrence.size.toString())
            Log.e("Image list ",placeofoccurrence.toString())
          //  saveImageNew(imagePath, "IMAGE")

            var jwt_token = SharedPreferenceStorage.getValue(
                applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
            ).toString()

           /* val PSID =
                policeStationArrayList.get(binding.spinnerOccurrenceDist.selectedItemPosition).ps_id*/
            var PSID=SharedPreferenceStorage.getValue(applicationContext,"PSID",0)

            var selectedMorgueName= morgueArrayList.get(binding.spinnerMorgue.selectedItemPosition).name
            var cursor1 = db.getmorgueIdbyName(selectedMorgueName)
            var morgueid = ""
            while (cursor1!!.moveToNext()) {
                morgueid=cursor1.getString(0)

            }
            cursor1.close()

            var morgueID = morgueid
            Log.e("MorgueId",morgueID)

            var ud_number=binding.tietCaseNumber.text.toString().trim()
            var ud_date=binding.tvCaseDate.text.toString().trim()
            var lat=binding.tietLatitude.text.toString()
            var long= binding.tietLongitude.text.toString().trim()
            var placeDescription= binding.tietPlaceWhereDeadBodyFound.text.toString().trim()
            var officer_name=binding.tietUdCaseOfficerName.text.toString().trim()
            var officer_contact=binding.tietUdCaseOfficerContactNo.text.toString().trim()
            var deadbodytype = getDeadBodyType()
            var vic_name = binding.tietVictimName.text.toString()
            var vic_age = binding.tietVictimAge.text.toString()
            var vic_address=binding.tietVictimAddress.text.toString()

            if(vic_age.equals("")) {
                vic_age = "0"
            }
            var vic_gender = getGender()
            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            Log.e("CaseNumber",binding.tietCaseNumber.text.toString().trim())
            val psIds = PSID
            val udNumber = ud_number.toRequestBody("text/plain".toMediaTypeOrNull())
            val udDate   = ud_date.toRequestBody("text/plain".toMediaTypeOrNull())
            val udOfficer = officer_name.toRequestBody("text/plain".toMediaTypeOrNull())
            val udOfficerPhone = officer_contact.toRequestBody("text/plain".toMediaTypeOrNull())
            val latitude = lat.toRequestBody("text/plain".toMediaTypeOrNull())
            val longitude = long.toRequestBody("text/plain".toMediaTypeOrNull())
            val place = placeDescription.toRequestBody("text/plain".toMediaTypeOrNull())
            val status = deadbodytype.toString().toRequestBody("text/plain".toMediaTypeOrNull())

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
                deadbodytype,
                vic_name_val,
                Integer.parseInt(vic_age),
                vic_gen_val,
                morgue_id_val,
                //vic_address_val
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
                                    //binding.btnIdenticalMarks.visibility=View.VISIBLE
                                    binding.llIdenticalMarksPic.removeAllViews()
                                    clearAllField()
                                    showAlertDialogMessageSuccess(
                                        this@PSLevelSubmitDeadBodyInfoActivity,
                                        response.body()!!.message.toString()
                                    )
                                    // Clear The form

                                    //Clear the form
                                } else {
                                    showAlertDialogMessage(
                                        this@PSLevelSubmitDeadBodyInfoActivity,
                                        response.body()!!.message.toString()
                                    )
//                                    showAlertDialogMessage(
////                                        this@PSLevelSubmitDeadBodyInfoActivity,
////                                        "SERVER ERROR !!!"
////                                    )
                                }


                            }
                        } catch (exception: java.lang.Exception) {
                            closeProgressDialogCall()
                            Toast.makeText(
                                this@PSLevelSubmitDeadBodyInfoActivity,
                                "Some issue in server end. Error Core : "+response.code().toString(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else if (response.code() == 400) {
                        closeProgressDialogCall()
                        Toast.makeText(
                            this@PSLevelSubmitDeadBodyInfoActivity,
                            "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    else {
                        //  progressDialog!!.cancel()
                        closeProgressDialogCall()
                        Toast.makeText(
                            this@PSLevelSubmitDeadBodyInfoActivity,
                            "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

                override fun onFailure(call: Call<CaseDetails>, t: Throwable) {
                    closeProgressDialogCall()
                    showAlertDialogMessage(
                        this@PSLevelSubmitDeadBodyInfoActivity,
                        "SERVER ERROR on Failure !!!" + t.message
                    )
                }
            })
        }catch (exception:IOException){
            Log.e("Exception",exception.toString())
        }

    }
    private fun clearAllField() {
        binding.tietCaseNumber.text!!.clear()
        binding.tietUdCaseOfficerName.text!!.clear()
        binding.tietUdCaseOfficerContactNo.text!!.clear()
        binding.tietPlaceWhereDeadBodyFound.text!!.clear()
        binding.tietVictimName.text!!.clear()
        binding.tietVictimAge.text!!.clear()
        binding.tietUdCaseOfficerContactNo.setError(null)
        binding.tvCaseDate.text=""
        binding.spinnerMorgue.setSelection(0)
        binding.tietVictimAddress.text!!.clear()
    }


    private fun saveImage(imageUri: String, type: String, id: Int) {

        val appFolderPath = "${applicationContext.filesDir.absolutePath}/images/"
        var db = DatabaseDb(this, null)
        Log.e("testpath", appFolderPath)
        val appFolder = File(appFolderPath)
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }

        val originalFilePath = imageUri
        val copiedFileName = "copied_${System.currentTimeMillis()}.jpg"
        val copiedFilePath = appFolderPath + copiedFileName

        try {
            var _imageUri = Uri.parse(imageUri)
            var file = File(copiedFilePath)
            var bitmap = uriToBitmap(_imageUri!!)
            val quality = 100
            val fos: FileOutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, fos)
            fos.close()

            println("joyImge::" + file.path)
            db.addImage(copiedFilePath, type, id)
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    protected fun saveImageNew(imageUri: String, type: String,id: Int) {
        val appFolderPath = "${applicationContext.filesDir.absolutePath}/images/"
        var db = DatabaseDb(this, null)
        Log.e("testpath", appFolderPath)
        val appFolder = File(appFolderPath)
        if (!appFolder.exists()) {
            appFolder.mkdirs()
        }
        val originalFilePath = imageUri
        val copiedFileName = "copied_${System.currentTimeMillis()}.jpg"
        val copiedFilePath = appFolderPath + copiedFileName
        try {
            var _imageUri = Uri.parse(imageUri)
            var file = File(copiedFilePath)
            var bitmap = uriToBitmap(_imageUri!!)
            val quality = 100
            val fos: FileOutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.JPEG, quality, fos)
            fos.close()
            println("SankhaImage::" + file.path)
            db.addImage(copiedFilePath, type, id)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor: FileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }
    private fun MessageDialog() {
        val alertDialog: AlertDialog.Builder =
            AlertDialog.Builder(this@PSLevelSubmitDeadBodyInfoActivity)
        alertDialog.setTitle(" ")
        alertDialog.setIcon(R.drawable.ok_sign)
        alertDialog.setMessage("Successfully Submitted...")
        alertDialog.setPositiveButton(
            "OK"
        ) { dialog, id ->
            val accountsIntent =
                Intent(this@PSLevelSubmitDeadBodyInfoActivity, MainActivity::class.java)
            startActivity(accountsIntent)
            finish()
        }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                check_and_set_currentLocation()
            } else {
                Toast.makeText(this, "Permission is denied!", Toast.LENGTH_SHORT).show()
                binding.locationSwitch.isChecked = false
            }
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

    override fun onItemSelected(adpterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (adpterView?.id) {
            R.id.spinner_occurrence_dist -> {
                val police_station = policeStationArrayList[position]
                val policeStationArrayAdapter: ArrayAdapter<PSAllOnly> = ArrayAdapter<PSAllOnly>(
                    baseContext, android.R.layout.simple_spinner_dropdown_item,
                )
                binding.spinnerOccurrencePs.adapter = policeStationArrayAdapter
            }

            else -> {
                // showToastMessage("UDCASE")
            }

        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }




}