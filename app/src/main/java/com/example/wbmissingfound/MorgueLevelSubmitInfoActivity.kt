package com.example.wbmissingfound

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.wbmissingfound.DBHelper.DatabaseDb
import com.example.wbmissingfound.Helper.Constants
import com.example.wbmissingfound.Model.Item
import com.example.wbmissingfound.Model.PecuMarksModel
import com.example.wbmissingfound.Model.PersonalItemsModel
import com.example.wbmissingfound.Model.SpecialIDMarksModel
import com.example.wbmissingfound.Model.SyncForBurnImages
import com.example.wbmissingfound.Model.SyncImageModelClass
import com.example.wbmissingfound.Model.SyncPersonalItemMorgueLevel
import com.example.wbmissingfound.RetroClient.RetroApi.APIService
import com.example.wbmissingfound.RetroClient.RetroApi.ApiUtils
import com.example.wbmissingfound.RetroClient.RetroApi.ApiUtilsScalarConverterFactory
import com.example.wbmissingfound.RetroClient.RetroModel.BurnMarksModel
import com.example.wbmissingfound.RetroClient.RetroModel.CaseDetails
import com.example.wbmissingfound.RetroClient.RetroModel.DistrictAll
import com.example.wbmissingfound.RetroClient.RetroModel.HairTypeApiModel
import com.example.wbmissingfound.RetroClient.RetroModel.ImageUploadApiResponseModel
import com.example.wbmissingfound.RetroClient.RetroModel.PSAll
import com.example.wbmissingfound.RetroClient.RetroModel.PersonalItemSaveResponseModel
import com.example.wbmissingfound.RetroClient.RetroModel.TypeBurnmarksResponse
import com.example.wbmissingfound.RetroClient.RetroModel.UnIdentificationGetAllDataAPIModel
import com.example.wbmissingfound.databinding.ActivityMorgLevelSubmitInfoBinding
import com.example.wbmissingfound.sharedStorage.SharedPreferenceStorage
import com.example.wbmissingfound.utils.FileUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import id.zelory.compressor.Compressor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
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
import java.util.Arrays


class MorgueLevelSubmitInfoActivity :  AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var binding: ActivityMorgLevelSubmitInfoBinding
    var form_status = 0
    var selectedBurnmarkstype: String = ""
    var selectedBurnmarkstype11: String = ""

    private var generalCondition: Int = 0
    private var used_current_loc: Boolean = false
    private val STORAGE_PERMISSION_CODE = 101
    private val CAMERA_PERMISSION_CODE = 100
    val pItemImagesPart = ArrayList<MultipartBody.Part>()

    var jwt_token: String = ""
    var bannerText: String = ""
    private val permissionId = 2
    var pecuid: String = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private var progressDialog: ProgressDialog? = null
    private val specialIDMarksModelArryList = ArrayList<SpecialIDMarksModel>()

    private val visibleIdMarksImage = ArrayList<String>()
    private val pItemImage = ArrayList<String>()
    private val faceImage = ArrayList<String>()
    private val bodyImage = ArrayList<String>()
    private val wApparelsImage = ArrayList<String>()
    private val footwareApparelsImage = ArrayList<String>()
    private val othersImage = ArrayList<String>()
    private val SpecialMarksImage = ArrayList<String>()
    private val hairTypesAfterSelected = ArrayList<String>()
    private val hairColorsAfterSelected = ArrayList<String>()

    private val burnTypeMarksAfterSelected = ArrayList<String>()
    private val burnTypeMarksAfterSelected1 = ArrayList<String>()

    var districtArrayList: List<DistrictAll> = ArrayList()
    var districtArrayAdapter: ArrayAdapter<DistrictAll>? = null

    private var hairTypeArray = ArrayList<String>()

    var inFirstHashMap = ArrayList<HashMap<String, String>>()
    var inNoseHashMap = ArrayList<HashMap<String, String>>()
    var spcIdntiHashMap = ArrayList<HashMap<String, String>>()

    var pecuHashMap = ArrayList<HashMap<String, String>>()
    var inFaceHashMap = ArrayList<HashMap<String, String>>()
    var spcIdTypeHashMap = ArrayList<HashMap<String, String>>()
    var spcIdLocHashMap = ArrayList<HashMap<String, String>>()
    var hairHashMap = ArrayList<HashMap<String, String>>()
    var hairColorHashMap = ArrayList<HashMap<String, String>>()

    val seletedIdFirstid = arrayListOf<String>()

    val seletedIdFaceid = arrayListOf<String>()

    var checkedIdHair: BooleanArray? = null
    val seletedIdHairItems = arrayListOf<String>()
    val seletedIdHairid = arrayListOf<String>()
    val seletedIdHairindex = arrayListOf<Int>()
    val previousSeletedIdHairindex = arrayListOf<Int>()

    var checkedIdHairColor: BooleanArray? = null
    val seletedIdHairColorItems = arrayListOf<String>()
    val seletedIdHairColorid = arrayListOf<String>()
    val seletedIdHairColorindex = arrayListOf<Int>()
    val previousSeletedIdHairColorindex = arrayListOf<Int>()

    var checkedIdNose: BooleanArray? = null
    val seletedIdNoseItems = arrayListOf<String>()
    val seletedIdNoseid = arrayListOf<String>()
    val seletedIdNoseindex = arrayListOf<Int>()
    val previousSeletedIdNoseindex = arrayListOf<Int>()

    val selectedLists = ArrayList<String>()
    val piselectedLists = ArrayList<String>()
    val pecuLists = ArrayList<String>()
    val splMarksModelArrayList = ArrayList<SpecialIDMarksModel>()
    val personalItemsArrayList = ArrayList<PersonalItemsModel>()
    val PecuModelArrayList = ArrayList<PecuMarksModel>()
    var height_feet: String? = null
    var height_inch: String? = null
    lateinit var selectClostText :String
    lateinit var selectFoorWearText :String
     var selectPriverPart :String =""
    lateinit var genaraleCondition :String
    var age_from: String? = null
    var age_to: String? = null
    lateinit var valStrID: String
    lateinit var valStr: String
    lateinit var valStrLocationID: String
    lateinit var valStrLocation: String
    var valTypeId = ""
    var valTypeColor = ""
    var receivedData=""
    var valAID: Int = 0
    lateinit var valA: String
    var valBID: Int = 0
    lateinit var valB: String
    var valCID: Int = 0
    lateinit var valC: String
    var valDID: Int = 0
    lateinit var valD: String



    var AgeRange = 0
    var Wearingapp = 0
    var FootwareApp = 0

    lateinit var imagePath: String
    lateinit var imageCategory: String
    var bodyImageUploadedFlag:Boolean = false

    var _imageUri: Uri? = null
    private val hairTypes = arrayOf(
        "Short Hair",
        "Long Hair",
        "Curly Hair",
        "Straight Hair",
        "Bald Full",
        "Bald Partial"
    )


    private val hairColors = arrayOf(
        "Black",
        "White",
        "Red",
        "Brown",
        "Gray",
        "Black & white"
    )

    private val typeMarkBurn = arrayOf(
        "Black",
        "White",
        "Red",
        "Brown",
        "Gray",
        "Black & white"
    )

    var values = arrayOf(
        "Burn Mark", "Tattoo", "Leucoderma",
        "Mole", "Scar", "Piercing",

        )

    var values2 = arrayOf(
        "Face", "Front Portion Of Body", "Back Portion Of Body",
        "Left Leg", "Right Leg", "Left Hand","Right Hand"

    )
    val checkedItems = BooleanArray(hairTypes.size)
    val checkedItemsHairColor = BooleanArray(hairColors.size)
    var arrayListforSyncPersonalItem: MutableList <SyncPersonalItemMorgueLevel> = ArrayList()
    var arrayListforSyncBurnDetals: MutableList <SyncForBurnImages> = ArrayList()
    var arraylistForImageSync: MutableList <SyncImageModelClass> = ArrayList()
    // copy the items from the main list to the selected item list for the preview
    // if the item is checked then only the item should be displayed for the user
    //val selectedItems = mutableListOf(*hairTypes)

    //val checkedItems = BooleanArray(hairTypes.size)
    val selectedItems = mutableListOf(*hairTypes)
    val selectedItemsHairColor = mutableListOf(*hairColors)


    companion object {
        private const val FIRST_ACTIVITY_REQUEST_CODE = 1
        private const val SECOND_ACTIVITY_REQUEST_CODE = 2
        const val PATH = "path"
        const val TYPE = "type"
    }
    private val apiHandler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMorgLevelSubmitInfoBinding.inflate(layoutInflater)

        // binding.root returns the root layout,
        // which is activity_main.xml file itself
        setContentView(binding.root)
        //  binding=DataBindingUtil.setContentView(this,R.layout.activity_submit_dead_body_information)

        supportActionBar?.title = "Un-identified Form"
        // startAPICalls()
        // sync()
        // syncImageNew(this)
        //syncImageBurnType(this)
        //syncPersonalItem(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        //startService(Intent(this, APICallService::class.java))


        receivedData = intent.getStringExtra("CaseId").toString()

        binding.radioGroup1.setOnCheckedChangeListener(
            RadioGroup.OnCheckedChangeListener { group, checkedId ->
                val radio: RadioButton = findViewById(checkedId)

                selectPriverPart=radio.text.toString()
//                Toast.makeText(applicationContext," On checked change :"+
//                        selectPriverPart,
//                    Toast.LENGTH_SHORT).show()
            })

        var id: Int = binding.radioGroup1.checkedRadioButtonId

        Log.e("CaseId",id.toString());
        // HairType()

        // chechLocation()
        var preferences = getSharedPreferences("DATA", 0)
        val value: Boolean = preferences.getBoolean("saved", false)
        val district: String? = preferences.getString("district", false.toString())
        if (value == true) {
            Toast.makeText(this, "got data", Toast.LENGTH_SHORT).show()
            if (district != null) {
                Log.e("district_saikat", district)
            }
        } else {
            // Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show()
        }

//        if (db.CheckIsDataAlreadyInDBorNot()) {
//            val cursor = db.getDistrict()
//
//            val columnsQty = cursor!!.columnCount
//            cursor.moveToFirst()
//            var j: Int = 0
//
//            while (cursor.moveToNext()) {
//                var id = cursor.getString(0)
//                var name = cursor.getString(1)
//
//                val cursorps = db.getPsdata(Integer.parseInt(id))
//                var psArrayList: List<PSAll> = ArrayList()
//                cursorps!!.moveToFirst()
//                while (cursorps.moveToNext()) {
//                    var id = cursorps.getString(0)
//                    var name = cursorps.getString(1)
//                    psArrayList += PSAll(Integer.parseInt(id), name)
//                }
//
//                districtArrayList += DistrictAll(
//                    Integer.parseInt(cursor.getString(0)),
//                    cursor.getString(1),
//                    psArrayList
//                )
//                j = j + 1
//            }
//
//
//            districtArrayAdapter = ArrayAdapter(
//                baseContext,
//                android.R.layout.simple_spinner_dropdown_item,
//                districtArrayList
//            )
//
//
//            val spc_type = db.getSiLoctype()
//            var l: Int = 0
//            spc_type!!.moveToFirst()
//            do {
//                val ID = spc_type.getString(0)
//                val INNAME = spc_type.getString(1)
//                val inListHash = java.util.HashMap<String, String>()
//                inListHash.put("IN_ID", ID)
//                inListHash.put("IN_NAME", INNAME)
//                spcIdTypeHashMap.add(inListHash)
//            } while (spc_type.moveToNext())
//            val spcIdTypeHas = java.util.HashMap<String, String>()
//            spcIdTypeHas.put("IN_ID", "-999")
//            spcIdTypeHas.put("IN_NAME", "Select")
//            spcIdTypeHashMap.add(0, spcIdTypeHas)
//
//            val spc_loc = db.getSiLoc()
//            var m: Int = 0
//            spc_loc!!.moveToFirst()
//            do {
//                val ID = spc_loc.getString(0)
//                val INNAME = spc_loc.getString(1)
//                val inListHash = java.util.HashMap<String, String>()
//
//                inListHash.put("IN_ID", ID)
//                inListHash.put("IN_NAME", INNAME)
//                spcIdLocHashMap.add(inListHash)
//                m = m + 1
//            } while (spc_loc.moveToNext())
//            val inListHash = java.util.HashMap<String, String>()
//            inListHash.put("IN_ID", "-999")
//            inListHash.put("IN_NAME", "Select")
//            spcIdLocHashMap.add(0, inListHash)
//
//
//            val vhair = db.getHair()
//            var n: Int = 0
//            vhair!!.moveToFirst()
//            do {
//                val ID = vhair.getString(0)
//                val INNAME = vhair.getString(1)
//                val inListHash = java.util.HashMap<String, String>()
//                inListHash.put("IN_ID", ID)
//                inListHash.put("IN_NAME", INNAME)
//                System.out.println(INNAME)
//                hairHashMap.add(inListHash)
//                n = n + 1
//            } while (vhair.moveToNext())
//
//            val vHairColor = db.getHairColor()
//            var o: Int = 0
//            vHairColor!!.moveToFirst()
//            do {
//                val ID = vHairColor.getString(0)
//                val INNAME = vHairColor.getString(1)
//                val inListHash = java.util.HashMap<String, String>()
//                inListHash.put("IN_ID", ID)
//                inListHash.put("IN_NAME", INNAME)
//
//                hairColorHashMap.add(inListHash)
//                o = o + 1
//            } while (vHairColor.moveToNext())
//        }
//        else {
//           // GetAllUnIdentifiedData()
//           // Toast.makeText(this, "data not exist", Toast.LENGTH_SHORT).show()
//
//        }

        /* peculiaritiesApiCall()
         idfirstApiCall()
         idfaceApiCall()
         idnoseApiCall()*/

        //getLocation()

        // showSeekAlertDialogButtonClicked("test", "abc", null, null)

        /*binding.ckbCaseDetails.setOnCheckedChangeListener(this)*/
        /*binding.ckbDeadBodyDetails.setOnCheckedChangeListener(this)
        binding.ckbPhotographFingerprint.setOnCheckedChangeListener(this)
        binding.ckbFingerprint.setOnCheckedChangeListener(this)
        binding.ckbMoreDetails.setOnCheckedChangeListener(this)*/

        binding.tilFootware.visibility = View.GONE

        accordionView()

        binding.spnAgerange.onItemSelectedListener = this

        val agerange = resources.getStringArray(R.array.age_range)

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item, agerange
        )
        binding.spnAgerange.adapter = adapter

        binding.spnAgerange.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (binding.spnAgerange.selectedItemPosition == 0) {
                    AgeRange = binding.spnAgerange.selectedItemPosition
                } else {
                    AgeRange = binding.spnAgerange.selectedItemPosition
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.spnWearingapp.onItemSelectedListener = this

        val wearapp = resources.getStringArray(R.array.clothesparams)

        val waadapter = ArrayAdapter(
            this,
            R.layout.spinner_item, wearapp
        )
        binding.spnWearingapp.adapter = waadapter
        binding.btnPicWa.visibility = View.GONE

        binding.spnWearingapp.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if(position==0){
                    selectClostText=""
                }else if(position==1){
                    selectClostText="Yes"
                }
                else if(position==2){
                    selectClostText="No"
                }

                when (binding.spnWearingapp.selectedItemPosition) {
                    0 -> {
                        Wearingapp = binding.spnWearingapp.selectedItemPosition
                        binding.tvPhotowearappTxt.text = resources.getString(R.string.uploading_photograph_of_wearing_apparels)
                        binding.tvPhotowearappTxt.setTextColor(resources.getColor(R.color.black))
                        binding.btnPicWa.visibility = View.GONE
                        wApparelsImage.clear()
                    }
                    1 -> {
                        Wearingapp = binding.spnWearingapp.selectedItemPosition
                        binding.tvPhotowearappTxt.append("(* Please choose at least one image)")
                        binding.tvPhotowearappTxt.setTextColor(resources.getColor(R.color.red))
                        binding.btnPicWa.visibility = View.VISIBLE
                    }
                    2 -> {
                        Wearingapp = binding.spnWearingapp.selectedItemPosition
                        binding.tvPhotowearappTxt.text = resources.getString(R.string.uploading_photograph_of_wearing_apparels)
                        binding.tvPhotowearappTxt.setTextColor(resources.getColor(R.color.black))
                        binding.btnPicWa.visibility = View.GONE
                        wApparelsImage.clear()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        binding.spnFootware.onItemSelectedListener = this

        val footparams = resources.getStringArray(R.array.yes_no)

        val fwadapter = ArrayAdapter(
            this,
            R.layout.spinner_item, footparams
        )
        binding.spnFootware.adapter = fwadapter
        binding.btnFootware.visibility = View.GONE

        binding.spnFootware.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                selectFoorWearText=footparams.get(position)
                when (binding.spnFootware.selectedItemPosition) {
                    0 -> {
                        FootwareApp = binding.spnFootware.selectedItemPosition
                        binding.tvFootwareTxt.text = resources.getString(R.string.footware_apparels)
                        binding.tvFootwareTxt.setTextColor(resources.getColor(R.color.black))
                        binding.btnFootware.visibility = View.GONE
                        footwareApparelsImage.clear()
                    }
                    1 -> {
                        FootwareApp = binding.spnFootware.selectedItemPosition
                        binding.tvFootwareTxt.text = "* Whether footwear is available"
                        binding.tvFootwareTxt.setTextColor(resources.getColor(R.color.red))
                        binding.tilFootware.visibility = View.VISIBLE
                        binding.btnFootware.visibility = View.VISIBLE
                    }
                    2 -> {
                        FootwareApp = binding.spnFootware.selectedItemPosition
                        binding.tvFootwareTxt.text = resources.getString(R.string.footware_apparels)
                        binding.tvFootwareTxt.setTextColor(resources.getColor(R.color.black))
                        binding.tilFootware.visibility = View.GONE
                        binding.btnFootware.visibility = View.GONE
                        footwareApparelsImage.clear()
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        var str = resources.getString(R.string.district_result)
        //  binding.ckbCaseDetails.setText(str)

        /*ckb_case_details!!.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(this,isChecked.toString(),Toast.LENGTH_SHORT).show()
        }*/

        binding.btnPersonalItemsAdd.setOnClickListener {
            showPersonalItemsDialogButtonClicked()
        }

        binding.btnSpcAdd.setOnClickListener {
            showMarkselectionDialogButtonClicked()
        }

        binding.btnPecuAdd.setOnClickListener {
            showPecularitiesSelectionDialogButtonClicked()
        }


        /*binding.llFormAndToDate.setOnClickListener()
        {
            showSeekAlertDialogButtonClicked("Select From Date and To Date", "", null, null)
        }*/
        binding.llHeight.setOnClickListener()
        {
            showHeightSelectionDialogButtonClicked("Select Height of Dead Body", "", null, null)
        }


        binding.btnIdenticalMarks.setOnClickListener {

            if (checkFileReadWritePermission() && checkCameraPermission()) {
                /*ImagePicker.with(this@SubmitDeadBodyInformationActivity)
                    .crop() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080, 1080
                    )
                    .start(IMAGE_REQUEST_IDENTICAL_MARKS)*/
                bannerText = "Please Capture Photo of Identification Marks"
                val type = "IDENTIMARKS"
                openPostActivity.launch(
                    CameraXActivity.getIntent(this, 1, type, bannerText)
                )
            }
        }

        /*binding.btnPersonalItemsOfTheBody.setOnClickListener {

            if (checkFileReadWritePermission()) {
                ImagePicker.with(this@SubmitDeadBodyInformationActivity)
                    .crop() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080, 1080
                    ) //Final image resolution will be less than 1080 x 1080(Optional)
                    .start(IMAGE_REQUEST_PERSONAL_ITEM)
            }
        }*/

        binding.btnPicFace.setOnClickListener {

            /*if (checkFileReadWritePermission()) {
                ImagePicker.with(this@SubmitDeadBodyInformationActivity)
                    .crop() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080, 1080
                    ) //Final image resolution will be less than 1080 x 1080(Optional)
                    .start(IMAGE_REQUEST_DIFF_FACE)
            }*/
            if(generalCondition == 0){
                showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please Choose Body Type.")

            }else{
                if (faceImage.size == 0) {
                    showPictureFaceAlertDialogMessage(
                        this@MorgueLevelSubmitInfoActivity,
                        "Please Capture Photo of Front Side of Face"
                    )
                    bannerText = "Please Capture Photo of Front Side of Face"
                }
                else if (faceImage.size == 1) {
                    showPictureFaceAlertDialogMessage(
                        this@MorgueLevelSubmitInfoActivity,
                        "Please Capture Photo of Left Side of Face"
                    )
                    bannerText = "Please Capture Photo of Left Side of Face"
                }
                else if (faceImage.size == 2) {
                    showPictureFaceAlertDialogMessage(
                        this@MorgueLevelSubmitInfoActivity,
                        "Please Capture Photo of Right Side of Face"
                    )
                    bannerText = "Please Capture Photo of Right Side of Face"
                }
                else if (faceImage.size > 3) {
                    showPictureFaceAlertDialogMessage(
                        this@MorgueLevelSubmitInfoActivity,
                        "Please Capture Photo of Other Angle of Face"
                    )
                    bannerText = "Please Capture Photo of Other Angle of Face"
                }
            }


        }

        binding.btnPicFaceUoload.setOnClickListener{
            try{
                if(generalCondition==1){
                    if(faceImage.size>2){
                        if (checkForInternet(this@MorgueLevelSubmitInfoActivity)) {
                            SubmitImageFace()
                        }
                        else{
                            var db1 = DatabaseDb(this, null)

                            val case_id=receivedData
                            var type="Picture"
                            val filedName=type
                            for (i in 0..(bodyImage.size - 1)) {
                                // val file = File(wApparelsImage.get(i))
                                val file: File = FileUtils.getFile(this, Uri.parse(bodyImage.get(i)))


                                val filePart = MultipartBody.Part.createFormData(
                                    "Picture",
                                    file.name,
                                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                )
                                System.out.println("FILES" + filePart)
                                db1.addImageMorgueOffline(bodyImage,filedName,case_id)
                            }
                            //  db1.addImageMorgueOffline("asfggdhhdj",filedName,case_id)
                            showAlertDialogMessageSuccess(this@MorgueLevelSubmitInfoActivity,resources.getString(R.string.save_local_stroage))
                            binding.llPicFace.removeAllViews()
                            binding.btnPicFaceUoload.visibility=View.GONE
                            // showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please Check your internet connection")
                        }
                    }else{
                        showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please add image.")
                    }
                }
                else{
                    if(faceImage.size>1){
                        if (checkForInternet(this@MorgueLevelSubmitInfoActivity)) {
                            SubmitImageFace()
                        }
                        else{
                            var db1 = DatabaseDb(this, null)

                            val case_id=receivedData
                            var type="Picture"
                            val filedName=type
                            for (i in 0..(faceImage.size - 1)) {
                                // val file = File(wApparelsImage.get(i))
                                val file: File = FileUtils.getFile(this, Uri.parse(faceImage.get(i)))


                                val filePart = MultipartBody.Part.createFormData(
                                    "Picture",
                                    file.name,
                                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                )
                                System.out.println("FILES" + filePart)

                            }
                            db1.addImageMorgueOffline(faceImage,filedName,case_id)
                            //  db1.addImageMorgueOffline("asfggdhhdj",filedName,case_id)
                            showAlertDialogMessageSuccess(this@MorgueLevelSubmitInfoActivity,resources.getString(R.string.save_local_stroage))
                            binding.llPicFace.removeAllViews()
                            binding.btnPicFaceUoload.visibility=View.GONE
                            //showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please Check your internet connection")
                        }
                    }else{
                        showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please add image.")
                    }
                }

            }catch(exception:ArrayIndexOutOfBoundsException){
                showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please add image.")
            }




        }

        binding.btnPicBody.setOnClickListener {

            /*if (checkFileReadWritePermission()) {
                ImagePicker.with(this@SubmitDeadBodyInformationActivity)
                    .crop() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080, 1080
                    ) //Final image resolution will be less than 1080 x 1080(Optional)
                    .start(IMAGE_REQUEST_DIFF_ANGLE)
            }*/

            if (bodyImage.size == 0) {
                showPictureBodyAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "Please Capture Photo of Front Side of Dead Body"
                )
                bannerText = "Please Capture Photo of Front Side of Dead Body"
            }
            else if (bodyImage.size == 1) {
                showPictureBodyAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "Please Capture Photo of Back Side of Dead Body"
                )
                bannerText = "Please Capture Photo of Back Side of Dead Body"
            }
            else if (bodyImage.size == 2) {
                showPictureBodyAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "Please Capture Photo of Left Side of Dead Body"
                )
                bannerText = "Please Capture Photo of Left Side of Dead Body"
            }
            else if (bodyImage.size == 3) {
                showPictureBodyAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "Please Capture Photo of Right Side of Dead Body"
                )
                bannerText = "Please Capture Photo of Right Side of Dead Body"
            }
            else if (bodyImage.size > 4) {
                showPictureBodyAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "Please Capture Photo of Other Angle of Dead Body"
                )
                bannerText = "Please Capture Photo of Other Angle of Dead Body"
            }


        }

        binding.btnPicBodyUoload.setOnClickListener{
            try{
                if(generalCondition==2){
                    if(bodyImage.size>0){
                        if (checkForInternet(this@MorgueLevelSubmitInfoActivity)) {
                            SubmitImageBody()
                        }
                        else{
                            var db1 = DatabaseDb(this, null)

                            val case_id=receivedData
                            var type="PictureBody"
                            val filedName=type
                            for (i in 0..(bodyImage.size - 1)) {
                                // val file = File(wApparelsImage.get(i))
                                val file: File = FileUtils.getFile(this, Uri.parse(bodyImage.get(i)))


                                val filePart = MultipartBody.Part.createFormData(
                                    "Picture",
                                    file.name,
                                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                )
                                System.out.println("FILES" + filePart)
                                db1.addImageMorgueOffline(bodyImage,filedName,case_id)
                            }
                            //  db1.addImageMorgueOffline("asfggdhhdj",filedName,case_id)
                            showAlertDialogMessageSuccess(this@MorgueLevelSubmitInfoActivity,resources.getString(R.string.save_local_stroage))
                            //showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please Check your internet connection")
                            binding.btnPicBodyUoload.visibility=View.GONE
                            binding.llPicBody.removeAllViews()
                        }
                    }else{
                        showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please add image.")
                    }
                }
                else{
                    if(bodyImage.size>1){
                        if (checkForInternet(this@MorgueLevelSubmitInfoActivity)) {
                            SubmitImageBody()
                        }else{
                            var db1 = DatabaseDb(this, null)

                            val case_id=receivedData
                            var type="PictureBody"
                            val filedName=type
                            for (i in 0..(bodyImage.size - 1)) {
                                // val file = File(wApparelsImage.get(i))
                                val file: File = FileUtils.getFile(this, Uri.parse(bodyImage.get(i)))


                                val filePart = MultipartBody.Part.createFormData(
                                    "Picture",
                                    file.name,
                                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                                )
                                System.out.println("FILES" + filePart)

                            }
                            db1.addImageMorgueOffline(bodyImage,filedName,case_id)
                            //  db1.addImageMorgueOffline("asfggdhhdj",filedName,case_id)
                            showAlertDialogMessageSuccess(this@MorgueLevelSubmitInfoActivity,resources.getString(R.string.save_local_stroage))
                            // showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please Check your internet connection")
                            binding.btnPicBodyUoload.visibility=View.GONE
                            binding.llPicBody.removeAllViews()
                        }
                    }else{

                        showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please add image.")
                    }
                }

            }catch(exception:ArrayIndexOutOfBoundsException){
                showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please add image.")
            }



        }

        binding.btnPicWa.setOnClickListener {

            if (checkFileReadWritePermission()) {
                /*ImagePicker.with(this@SubmitDeadBodyInformationActivity)
                    .crop() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080, 1080
                    ) //Final image resolution will be less than 1080 x 1080(Optional)
                    .start(IMAGE_REQUEST_DIFF_WAPP)*/
                bannerText = "Please Capture Photo of WEARING APPAREL"
                val type = "WEARINGAPP"
                openPostActivity.launch(
                    CameraXActivity.getIntent(this, 1, type, bannerText)
                )
            }
        }
        binding.btnPicClothUpload.setOnClickListener{
            if (checkForInternet(this@MorgueLevelSubmitInfoActivity)) {
                SubmitImageWearingCloth()
            }
            else{
                var db1 = DatabaseDb(this, null)

                val case_id=receivedData
                var type="PictureWearing"
                val filedName=type
                for (i in 0..(wApparelsImage.size - 1)) {
                    // val file = File(wApparelsImage.get(i))
                    val file: File = FileUtils.getFile(this, Uri.parse(wApparelsImage.get(i)))


                    val filePart = MultipartBody.Part.createFormData(
                        "Picture",
                        file.name,
                        file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    System.out.println("FILES" + filePart)
                }
                db1.addImageMorgueOffline(wApparelsImage,filedName,case_id)

                //  db1.addImageMorgueOffline("asfggdhhdj",filedName,case_id)
                showAlertDialogMessageSuccess(this@MorgueLevelSubmitInfoActivity,resources.getString(R.string.save_local_stroage))
                wApparelsImage.clear()
                binding.btnPicClothUpload.visibility=View.GONE
                binding.llPicWa.removeAllViews()
            }

        }

        binding.btnFootware.setOnClickListener {

            if (checkFileReadWritePermission()) {
                /*ImagePicker.with(this@SubmitDeadBodyInformationActivity)
                    .crop() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080, 1080
                    ) //Final image resolution will be less than 1080 x 1080(Optional)
                    .start(IMAGE_REQUEST_FOOTWARE)*/
                bannerText = "Please Capture Photo of Footwear"
                val type = "FOOTWARE"
                openPostActivity.launch(
                    CameraXActivity.getIntent(this, 1, type, bannerText)
                )
            }
        }
        binding.btnPicFootwearUpload.setOnClickListener{
            if (checkForInternet(this@MorgueLevelSubmitInfoActivity)) {
                SubmitImageWearinFootWear()
            }
            else{
                var db1 = DatabaseDb(this, null)

                val case_id=receivedData
                var type="FootImage"
                val filedName=type
                for (i in 0..(footwareApparelsImage.size - 1)) {
                    // val file = File(wApparelsImage.get(i))
                    val file: File = FileUtils.getFile(this, Uri.parse(footwareApparelsImage.get(i)))


                    val filePart = MultipartBody.Part.createFormData(
                        "Picture",
                        file.name,
                        file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    System.out.println("FILES" + filePart)
                }
                db1.addImageMorgueOffline(footwareApparelsImage,filedName,case_id)

                //  db1.addImageMorgueOffline("asfggdhhdj",filedName,case_id)
                showAlertDialogMessageSuccess(this@MorgueLevelSubmitInfoActivity,resources.getString(R.string.save_local_stroage))
                //showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please Check your internet connection")
                footwareApparelsImage.clear()
                binding.btnPicFootwearUpload.visibility=View.GONE
                binding.llFootwarePic.removeAllViews()
            }

        }

        binding.btnPicOthers.setOnClickListener {

            if (checkFileReadWritePermission()) {
                /*ImagePicker.with(this@SubmitDeadBodyInformationActivity)
                    .crop() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080, 1080
                    ) //Final image resolution will be less than 1080 x 1080(Optional)
                    .start(IMAGE_REQUEST_OTHERS)*/
                bannerText = "Please Capture Photo of Other Documents"
                val type = "OTHERS"
                openPostActivity.launch(
                    CameraXActivity.getIntent(this, 1, type, bannerText)
                )
            }
        }
        binding.btnPicOthersUpload.setOnClickListener{
            if (checkForInternet(this@MorgueLevelSubmitInfoActivity)) {
                SubmitImageOthers()
            }
            else{
                var db1 = DatabaseDb(this, null)
                val case_id=receivedData
                var type="PictureOther"
                val filedName=type
                for (i in 0..(othersImage.size - 1)) {
                    // val file = File(wApparelsImage.get(i))
                    val file: File = FileUtils.getFile(this, Uri.parse(othersImage.get(i)))


                    val filePart = MultipartBody.Part.createFormData(
                        "Picture",
                        file.name,
                        file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    )
                    System.out.println("FILES" + filePart)

                }
                db1.addImageMorgueOffline(othersImage,filedName,case_id)
                //  db1.addImageMorgueOffline("asfggdhhdj",filedName,case_id)
                showAlertDialogMessageSuccess(this@MorgueLevelSubmitInfoActivity,resources.getString(R.string.save_local_stroage))
                //showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please Check your internet connection")
                othersImage.clear()
                binding.btnPicOthersUpload.visibility=View.GONE
                binding.llPicOthers.removeAllViews()
            }

        }


        binding.tvSelecthair.setOnClickListener {
            //
            // HairTypeDialog()
            val builder = AlertDialog.Builder(this)

            // set the title for the alert dialog
            builder.setTitle("Choose Hair Types")

            // set the icon for the alert dialog


            // now this is the function which sets the alert dialog for multiple item selection ready
            builder.setMultiChoiceItems(hairTypes, checkedItems) { dialog, which, isChecked ->
                checkedItems[which] = isChecked
                val currentItem = selectedItems[which]

            }

            // alert dialog shouldn't be cancellable
            builder.setCancelable(false)

            // handle the positive button of the dialog
            builder.setPositiveButton("Done") { dialog, which ->
                for (i in checkedItems.indices) {
                    if (checkedItems[i]) {
                        //tvSelectedItemsPreview.text = String.format("%s%s, ", tvSelectedItemsPreview.text, selectedItems[i])

                        hairTypesAfterSelected.add(selectedItems[i])
                    }
                }
                val hair=getHairTypeList()
                Log.e("All value",hair)

                binding.tvSelecthair.isClickable=true
                hairTypesAfterSelected.clear()
                //binding.tvSelecthair.text=hair
                binding.tvShowhair.text=hair
            }

            // handle the negative button of the alert dialog
            builder.setNegativeButton("CANCEL") { dialog, which -> }

            // handle the neutral button of the dialog to clear the selected items boolean checkedItem
            builder.setNeutralButton("CLEAR ALL") { dialog: DialogInterface?, which: Int ->
                Arrays.fill(checkedItems, false)
            }

            // create the builder
            builder.create()

            // create the alert dialog with the alert dialog builder instance
            val alertDialog = builder.create()
            alertDialog.show()
        }

        binding.tvSelecthaircolor.setOnClickListener {
            //HairColorDialog()
            val builder = AlertDialog.Builder(this)

            // set the title for the alert dialog
            builder.setTitle("Choose Hair Colors")

            // set the icon for the alert dialog


            // now this is the function which sets the alert dialog for multiple item selection ready
            builder.setMultiChoiceItems(hairColors, checkedItemsHairColor) { dialog, which, isChecked ->
                checkedItemsHairColor[which] = isChecked
                val currentItem = selectedItemsHairColor[which]

            }

            // alert dialog shouldn't be cancellable
            builder.setCancelable(false)

            // handle the positive button of the dialog
            builder.setPositiveButton("Done") { dialog, which ->
                for (i in checkedItemsHairColor.indices) {
                    if (checkedItemsHairColor[i]) {
                        //tvSelectedItemsPreview.text = String.format("%s%s, ", tvSelectedItemsPreview.text, selectedItems[i])

                        hairColorsAfterSelected.add(selectedItemsHairColor[i])
                    }
                }
                val hairColor=getHairColorList()
                Log.e("All value",hairColor)

                binding.tvSelecthaircolor.isClickable=true
                hairColorsAfterSelected.clear()
               // binding.tvSelecthaircolor.text=hairColor
                binding.tvShowhaircolor.text=hairColor
            }

            // handle the negative button of the alert dialog
            builder.setNegativeButton("CANCEL") { dialog, which -> }

            // handle the neutral button of the dialog to clear the selected items boolean checkedItem
            builder.setNeutralButton("CLEAR ALL") { dialog: DialogInterface?, which: Int ->
                Arrays.fill(checkedItemsHairColor, false)
            }

            // create the builder
            builder.create()

            // create the alert dialog with the alert dialog builder instance
            val alertDialog = builder.create()
            alertDialog.show()
        }

        binding.tvSelectidnose.setOnClickListener {
//            IdNoseDialog()
        }
        binding.btnSubmit.setOnClickListener {

            if (checkForInternet(this@MorgueLevelSubmitInfoActivity)) {

                if (checkValidation())
                    updateMorguecase()
              /*  if(bodyImageUploadedFlag) {
                    if (checkValidation())
                        updateMorguecase()
                }else{
                    Toast.makeText(this@MorgueLevelSubmitInfoActivity,"Please add body image",Toast.LENGTH_LONG).show()
                }*/

            }
            else{
                //Toast.makeText(this@MorgueLevelSubmitInfoActivity,"Please Check Your Internet Connection",Toast.LENGTH_LONG)
                try{
                    receivedData = intent.getStringExtra("CaseId").toString()
                    var ageRange=binding.spnAgerange.selectedItem.toString().trim()
                    val input = ageRange
                    Log.e("AgeReange",input)

                    //val valString = "80-90"
                    val valString = ageRange

                    val (firstNumberNew, secondNumberNew) = valString.split("-").map { it.toInt() }

                    println("firstNumberNew: $firstNumberNew")
                    println("secondNumberNew: $secondNumberNew")

                    val value= getPeculiartiesInJSONString()
                    val list=value.getString("marks")
                    Log.e("peculist",list)
                    val gson = Gson()
                    val requestBodyJson = gson.toJson(list)


                    val hairtype = getHairTypeList()
                    val haircolor = getHairColorList()

                    val gen = getGender()
                    var db1 = DatabaseDb(this, null)
                    db1.submitDataMorgueLevelOffline(
                        receivedData,
                        firstNumberNew,
                        secondNumberNew,
                        binding.tvHeight.text.toString().trim(),
                        gen,
                        genaraleCondition.toString(),
                        selectFoorWearText,
                        selectPriverPart,
                        selectClostText,
                        value,
                        hairtype,
                        haircolor
                    )

                    val cursor = db1.getMorgueData()
                    var id: Int = 0
                    if (cursor != null) {
                        if (cursor.moveToLast()) {
                            //name = cursor.getString(column_index);//to get other values
                            if (cursor != null) {
                                id = cursor.getInt(0)
                                // Log.d("CID WB","ID-->"+id +"CASE NO-->"+   binding.tietCaseNumber.text.toString())
                            }//to get id, 0 is the column index
                        }
                    }
                    //saveImageNew(imagePath, "IMAGE",id)
                    //db1.addImageMorgueOffline("asfggdhhdj","23",receivedData)

                    showAlertDialogMessageSuccess(this@MorgueLevelSubmitInfoActivity,
                        resources.getString(R.string.save_local_stroage)
                    )
                    clearallField()
                    faceImage.clear()
                    bodyImage.clear()

                }catch (exception: java.lang.Exception){
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "Please Check Your Internet Connection ",
                        Toast.LENGTH_LONG
                    ).show()

                }

            }
//            }else{
//                showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,"Please Give All Information Properly")
//
//            }

        }

        if (generalCondition == 0) {
            HideBodyFeatures()
        }

        binding.rbGeneralCondition.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rb_complete_body -> {
                    generalCondition = 1
                    ShowBodyFeatures()
                    genaraleCondition="Complete Body"
                }

                R.id.rb_incomplete_body -> {
                    generalCondition = 2
                    HideBodyFeatures()
                    genaraleCondition="Incomplete Body"
                }

                R.id.rb_decomposed -> {
                    generalCondition = 3
                    HideBodyFeatures()
                    genaraleCondition="Decomposed Body"
                }

                R.id.rb_partially_skeletonized -> {
                    generalCondition = 4
                    HideBodyFeatures()
                    genaraleCondition="Partial Skeleton"
                }

                R.id.rb_fully_skeletonized -> {
                    generalCondition = 5
                    HideBodyFeatures()
                    genaraleCondition="Full Skeleton"
                }

                R.id.rb_burnt -> {
                    generalCondition = 6
                    HideBodyFeatures()
                    genaraleCondition="Burnt Body"
                }
            }
        }


        if (Constants.TEST.TESTING)
            setTestValue()
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

    fun sync():Boolean {
        val db = DatabaseDb(this, null)
        val cursor1 = db.getMorgueData()
        val cursorImage=db.getImagedetailsfromOfflineDatabase()
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



    fun syncImageNew(context: Context): Boolean {
        val db = DatabaseDb(context, null)

        val cursorImage = db.getImagedetailsfromOfflineDatabase()
        val dBodyImagesPart = ArrayList<MultipartBody.Part>()

        cursorImage?.use { cursor ->
            while (cursor.moveToNext()) {
                val case_id = cursor.getString(1)
                val imageList = cursor.getString(2)
                val imageType = cursor.getString(3)

                val objectForSyncImage=SyncImageModelClass(case_id,imageList,imageType)
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



    private fun startAPICalls() {
        GlobalScope.launch(Dispatchers.IO) {
            // Start the initial API call
            makeAPICall()

            // Schedule the next API call after 5 minutes
            apiHandler.postDelayed({
                startAPICalls()
            }, 10000) // 5 minutes in milliseconds
        }
    }

    private suspend fun makeAPICall() {
        // Make your API call here
        // Replace this with your actual API call implementation
        // Example:
        // val response = YourAPIService.makeAPIRequest()
        // Handle the API response accordingly
        // For simplicity, let's just print a log
        println("API called")
        // Toast.makeText(this@MorgueLevelSubmitInfoActivity, "API Called....!", Toast.LENGTH_LONG).show()
    }



    @SuppressLint("SuspiciousIndentation")
    fun updateMorguecase() {

        progressDialogCall(this@MorgueLevelSubmitInfoActivity)

        // if (checkForInternet(this@PSLevelSubmitDeadBodyInfoActivity)) {

        var jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()
        receivedData = intent.getStringExtra("CaseId").toString()

        val gen = getGender()
        val malepvp = getMalePrivatePC()

        val hairtype = getHairTypeList()
        val haircolor = getHairColorList()


        Log.e("hairtype",hairtype)
        Log.e("haircolor",haircolor)
        Log.e("malepvp",malepvp)

        val value= getPeculiartiesInJSONString()
        var ageRange=binding.spnAgerange.selectedItem.toString().trim()



        val list=value.getString("marks")
        Log.e("peculist",list)
        val gson = Gson()
        val requestBodyJson = list.toRequestBody("text/plain".toMediaTypeOrNull())

        Log.e("peculist json", requestBodyJson.toString())
        val input = ageRange
        Log.e("AgeReange",input)


        //val valString = "80-90"
        val valString = ageRange

        val (firstNumberNew, secondNumberNew) = valString.split("-").map { it.toInt() }

        println("firstNumberNew: $firstNumberNew")
        println("secondNumberNew: $secondNumberNew")

        // Split the input string by the "-" delimiter
        // val parts = input.split("–") // Note: Ensure that you are using the correct dash character here

        // Extract the integers from the parts
        // val firstNumber = parts[0].toInt()
        //val secondNumber = parts[1].toInt()




        // println("First Number: $firstNumber")
        //println("Second Number: $secondNumber")




        var mAPIService: APIService? = null
        mAPIService = ApiUtilsScalarConverterFactory.apiService

        val call = mAPIService.updateCaseMorgue(
            jwt_token,
            receivedData.toRequestBody("text/plain".toMediaTypeOrNull()),
            firstNumberNew,
            secondNumberNew,
            binding.tvHeight.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull()),
            gen.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            genaraleCondition.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            selectFoorWearText.toRequestBody("text/plain".toMediaTypeOrNull()),
            selectPriverPart.toRequestBody("text/plain".toMediaTypeOrNull()),
            selectClostText.toRequestBody("text/plain".toMediaTypeOrNull()),
            requestBodyJson,
            hairtype.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
            haircolor.toRequestBody("text/plain".toMediaTypeOrNull())
        ).enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>, response: Response<String>
            ) {
                if (response.code() == 200) {
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Morgue Submit", response.body()!!)
                            var result: JSONObject? = null

                            val s = response.body()

                            result = JSONObject(s)
                            Log.e("Morgue Submit", result.toString())
                            if (result!!.optString("success").equals("true")) {

                                clearallField()
                                faceImage.clear()
                                bodyImage.clear()
                                pItemImage.clear()
                                binding.cgPitem.removeAllViews()
                                binding.llPersonalItemsOfTheBody.removeAllViews()
                                piselectedLists.clear()
                                personalItemsArrayList.clear()
                                showAlertDialogMessageSuccess(
                                    this@MorgueLevelSubmitInfoActivity,
                                    result!!.optString("message").toString()
                                )
                                    var alertDialog: androidx.appcompat.app.AlertDialog? = this@MorgueLevelSubmitInfoActivity.let {
                                    val appName = getString(R.string.app_name)
                                    var builder = androidx.appcompat.app.AlertDialog.Builder(it)
                                    builder.setTitle(appName)
                                    builder.setCancelable(false)
                                    builder.setIcon(R.drawable.ok_sign)
                                    builder.setMessage(result!!.optString("message").toString())
                                    builder.apply {
                                        setPositiveButton("OK",
                                            {dialog, id ->
                                                loadMorgeListPageActivity()
                                        })

                                    }
                                    builder.create()
                                }
                                alertDialog?.show()

                                // Clear The form

                                //Clear the form
                            } else {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
                                    result!!.optString("message").toString()
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
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else if (response.code() == 400) {
                    PecuModelArrayList.clear()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
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
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error", response.body()!!)
                            var result: JSONObject? = null

                            val s = response.body()

                            result = JSONObject(s)

                            Log.e("response", "done")

                            if (result!!.optString("success").equals("true"))  {
                                val db = DatabaseDb(this@MorgueLevelSubmitInfoActivity, null)
                                db.deleteRowByCaseIdMorgeData(case_id)
//                                showAlertDialogMessageSuccess(
//                                    this@MorgueLevelSubmitInfoActivity,
//                                    response.body()!!.message.toString()
//                                )
                                 loadMorgeListPageActivity()
                                // Clear The form

                                //Clear the form
                            } else {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
                                    result!!.optString("message").toString()
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
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else if (response.code() == 400) {
                    PecuModelArrayList.clear()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })
    }

    private fun loadMorgeListPageActivity() {

        //  intent = Intent(baseContext, MainActivity::class.java)
        // intent = Intent(baseContext, SubmitDeadBodyInformationActivity::class.java)
        intent = Intent(baseContext, MorgLevelListing::class.java)
        startActivity(intent)
    }

    private fun clearallField() {
        PecuModelArrayList.clear()
        genaraleCondition=""
        selectFoorWearText=""
        selectPriverPart=""
        selectClostText=""
        binding.tvSelecthair.text=""
        binding.tvSelecthaircolor.text=""
        binding.cgPecu.removeAllViews()

    }

    fun convertStringToJsonArray(input: String): String {
        val parts = input.split("-")

        val jsonObject = JsonObject()

        // Define mapping rules
        val mapping = mapOf(
            "Limb" to "lf",
            "Hand" to "hl",
            "Right" to "rl",
            "Extra" to "em"
        )

        // Map parts to their corresponding keys
        parts.forEach { part ->
            mapping[part]?.let { key ->
                jsonObject.addProperty(key, part)
            }
        }

        // Convert the JsonObject to a JSON array
        val jsonArray = mutableListOf(jsonObject)

        // Convert the JSON array to string
        return Gson().toJson(jsonArray)
    }



    //    fun SubmitMorgueLevelData() {
//        progressDialogCall(this@MorgueLevelSubmitInfoActivity)
//        val vIdenticalMarksImagesPart = ArrayList<MultipartBody.Part>()
//        val pItemImagesPart = ArrayList<MultipartBody.Part>()
//        val faceImagesPart = ArrayList<MultipartBody.Part>()
//        val dBodyImagesPart = ArrayList<MultipartBody.Part>()
//        val WAImagesPart = ArrayList<MultipartBody.Part>()
//        val footwareImagesPart = ArrayList<MultipartBody.Part>()
//        val otherImagesPart = ArrayList<MultipartBody.Part>()
//
//        val simImagesPart = ArrayList<MultipartBody.Part>()
//
//
//
//        if (checkForInternet(this@MorgueLevelSubmitInfoActivity)) {
//            for (i in 0..(visibleIdMarksImage.size - 1)) {
//
//                val file: File = FileUtils.getFile(this, Uri.parse(visibleIdMarksImage.get(i)))
//
//                //var file = File(visibleIdMarksImage.get(i))
//
//                val filePart = MultipartBody.Part.createFormData(
//                    "vIdmarkImage",
//                    file.name,
//                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                )
//                System.out.println("JoyFILESPart" + filePart)
//                vIdenticalMarksImagesPart.add(filePart)
//            }
//
//            for (i in 0..(pItemImage.size - 1)) {
//                //var file = File(pItemImage.get(i))
//                var file: File = FileUtils.getFile(this, Uri.parse(pItemImage.get(i)))
//                // calling from global scope
//                GlobalScope.launch {
//                    file =
//                        Compressor.compress(applicationContext, file)
//                    System.out.println("SubmitDeadBodyInformationActivity.SubmitUDData: file compress done")
//                }
//                val filePart = MultipartBody.Part.createFormData(
//                    "pItemImage",
//                    file.name,
//                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                )
//                pItemImagesPart.add(filePart)
//                System.out.println("SubmitDeadBodyInformationActivity.SubmitUDData: FilePart attached ")
//            }
//
//            for (i in 0..(faceImage.size - 1)) {
//                // var file = File(faceImage.get(i))
//                val file: File = FileUtils.getFile(this, Uri.parse(faceImage.get(i)))
//
//                val filePart = MultipartBody.Part.createFormData(
//                    "faceImage",
//                    file.name,
//                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                )
//                faceImagesPart.add(filePart)
//            }
//
//            for (i in 0..(bodyImage.size - 1)) {
//                // val file = File(bodyImage.get(i))
//                val file: File = FileUtils.getFile(this, Uri.parse(bodyImage.get(i)))
//
//                val filePart = MultipartBody.Part.createFormData(
//                    "dBodyImage",
//                    file.name,
//                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                )
//                System.out.println("FILES" + filePart)
//                dBodyImagesPart.add(filePart)
//            }
//
//            for (i in 0..(wApparelsImage.size - 1)) {
//                // val file = File(wApparelsImage.get(i))
//                val file: File = FileUtils.getFile(this, Uri.parse(wApparelsImage.get(i)))
//
//
//                val filePart = MultipartBody.Part.createFormData(
//                    "waImage",
//                    file.name,
//                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                )
//                System.out.println("FILES" + filePart)
//                WAImagesPart.add(filePart)
//            }
//
//            for (i in 0..(footwareApparelsImage.size - 1)) {
//                // val file = File(footwareApparelsImage.get(i))
//                val file: File = FileUtils.getFile(this, Uri.parse(footwareApparelsImage.get(i)))
//
//                val filePart = MultipartBody.Part.createFormData(
//                    "footwareImage",
//                    file.name,
//                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                )
//                System.out.println("FILES" + filePart)
//                footwareImagesPart.add(filePart)
//            }
//            for (i in 0..(othersImage.size - 1)) {
//                // val file = File(othersImage.get(i))
//                val file: File = FileUtils.getFile(this, Uri.parse(othersImage.get(i)))
//
//                val filePart = MultipartBody.Part.createFormData(
//                    "othersImage",
//                    file.name,
//                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                )
//                System.out.println("FILES" + filePart)
//                otherImagesPart.add(filePart)
//            }
//
//            for (i in 0..(SpecialMarksImage.size - 1)) {
//                // val file = File(SpecialMarksImage.get(i))
//                val file: File = FileUtils.getFile(this, Uri.parse(SpecialMarksImage.get(i)))
//
//                val filePart = MultipartBody.Part.createFormData(
//                    "simImage",
//                    file.name,
//                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//                )
//                System.out.println("FILES" + filePart)
//                simImagesPart.add(filePart)
//            }
//
//
//            jwt_token = SharedPreferenceStorage.getValue(
//                applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
//            ).toString()
//
//
//            val gen = getGender()
//            val malepvp = getMalePrivatePC()
//            val persoitem = getPItemList()
//            val peculist = getPecuList()
//            val specialidlist = getSpcIdnMarkList()
//            val hairtype = getHairTypeList()
//            val haircolor = getHairColorList()
//
//
//            var mAPIService: APIService? = null
//            mAPIService = ApiUtils.apiService
//
//
//            mAPIService.UDDataSubmitApi(
//                jwt_token,
//                form_status,
//                "",
//                "",
//
//                generalCondition.toString(),
//                binding.spnAgerange.selectedItem.toString().trim(),
//                binding.tvHeight.text.toString().trim(),
//                gen,
//                malepvp,
//                simImagesPart,
//                hairtype,
//                haircolor
//
//
//            ).enqueue(object : Callback<UDDataSubmitApiResponse> {
//                override fun onResponse(
//                    call: Call<UDDataSubmitApiResponse>, response: Response<UDDataSubmitApiResponse>
//                ) {
//                    if (response.code() == 200) {
//                        closeProgressDialogCall()
//                        try {
//                            if (response.isSuccessful) {
//                                Log.e("Message", response.body()!!.message.toString())
//
//                                if (response.body()!!.message.toString().equals("Success")) {
//
//                                    MessageDialog()
//
//                                } else {
//                                    showAlertDialogMessage(
//                                        this@MorgueLevelSubmitInfoActivityxx,
//                                        "SERVER ERROR !!!"
//                                    )
//                                }
//
//                            }
//                        } catch (exception: java.lang.Exception) {
//                            Toast.makeText(
//                                this@MorgueLevelSubmitInfoActivity,
//                                "Some issue in server end",
//                                Toast.LENGTH_LONG
//                            ).show()
//                        }
//                    } else {
//                        //  progressDialog!!.cancel()
//                        Toast.makeText(
//                            this@MorgueLevelSubmitInfoActivity,
//                            "SERVER ERROR!!! Please try after sometime...",
//                            Toast.LENGTH_LONG
//                        ).show()
//
//                    }
//                }
//
//                override fun onFailure(call: Call<UDDataSubmitApiResponse>, t: Throwable) {
//                    closeProgressDialogCall()
//                    showAlertDialogMessage(
//                        this@MorgueLevelSubmitInfoActivity,
//                        "SERVER ERROR on Failure !!!" + t.message
//                    )
//                }
//            })
//        }
//        else {
////            var db1 = DatabaseDb(this, null)
////            db1.addSubmitData(
////                form_status.toString(),
////                districtArrayList.get(binding.spinnerOccurrenceDist.selectedItemPosition).district_id.toString(),
////                districtArrayList.get(binding.spinnerOccurrenceDist.selectedItemPosition).ps.get(
////                    binding.spinnerOccurrencePs.selectedItemPosition
////                ).ps_id.toString(),
////                binding.tietCaseNumber.text.toString().trim(),
////                binding.tvCaseDate.text.toString().trim(),
////                binding.tietLatitude.text.toString().trim(),
////                binding.tietLongitude.text.toString().trim(),
////                binding.tietPlaceWhereDeadBodyFound.text.toString().trim(),
////                binding.tietUdCaseOfficerName.text.toString().trim(),
////                binding.tietUdCaseOfficerContactNo.text.toString().trim(),
////                generalCondition.toString(),
////                binding.spnAgerange.selectedItem.toString().trim(),
////                binding.tvHeight.text.toString().trim(),
////                getGender(),
////                getMalePrivatePC(),
////                binding.tietIdenticalMarks.text.toString().trim(),
////                getPItemList(),
////                binding.tietFootware.text.toString(),
////                getPecuList(),
////                getSpcIdnMarkList(),
////                getHairTypeList(),
////                getHairColorList()
////            )
////
////            val cursor = db1.getData()
////            var id: Int = 0
////            if (cursor != null) {
////                if (cursor.moveToLast()) {
////                    //name = cursor.getString(column_index);//to get other values
////                    if (cursor != null) {
////                        id = cursor.getInt(0)
////                    }//to get id, 0 is the column index
////                }
////            }
////
////            if (ContextCompat.checkSelfPermission(
////                    this,
////                    Manifest.permission.WRITE_EXTERNAL_STORAGE
////                ) != PackageManager.PERMISSION_GRANTED
////            ) {
////                ActivityCompat.requestPermissions(
////                    this,
////                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
////                    1
////                )
////            }
////            for (i in 0..(visibleIdMarksImage.size - 1)) {
////                Log.e("IMAGE_TEST", visibleIdMarksImage.get(i))
////
////                saveImage(visibleIdMarksImage.get(i), "vid", id)
////            }
////
////            for (i in 0..(pItemImage.size - 1)) {
////                saveImage(pItemImage.get(i), "pit", id)
////            }
////
////            for (i in 0..(faceImage.size - 1)) {
////                saveImage(faceImage.get(i), "face", id)
////            }
////
////            for (i in 0..(bodyImage.size - 1)) {
////                saveImage(bodyImage.get(i), "body", id)
////            }
////            for (i in 0..(wApparelsImage.size - 1)) {
////                saveImage(wApparelsImage.get(i), "wa", id)
////            }
////
////            for (i in 0..(footwareApparelsImage.size - 1)) {
////
////                saveImage(footwareApparelsImage.get(i), "foot", id)
////            }
////            for (i in 0..(othersImage.size - 1)) {
////
////                saveImage(othersImage.get(i), "other", id)
////            }
////            for (i in 0..(SpecialMarksImage.size - 1)) {
////
////                saveImage(SpecialMarksImage.get(i), "sim", id)
////            }
////            closeProgressDialogCall()
////            showAlertDialogMessage(
////                this@MorgueLevelSubmitInfoActivity,
////                "Data Saved "
////            )
////            finish();
//////            startActivity(intent)
////        }
//        }
//    }
    fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun savePersonaldataItem() {
        progressDialogCall(this@MorgueLevelSubmitInfoActivity)

        val persoitem = getPItemList()
        val gson = Gson()
        val jsonString = gson.toJson(persoitem).toString()

        Log.e("jsonStringconvert",jsonString)

        val requestBodyJson = gson.toJson(persoitem).toRequestBody("text/plain".toMediaTypeOrNull())
        Log.e("Type",gson.toJson(persoitem))
        val personalDataString = jsonString.toRequestBody("text/plain".toMediaTypeOrNull())
        for (i in 0..(pItemImage.size - 1)) {
            //var file = File(pItemImage.get(i))
            var file: File = FileUtils.getFile(this, Uri.parse(pItemImage.get(i)))
            // calling from global scope
            GlobalScope.launch {
                file =
                    Compressor.compress(applicationContext, file)
            }
            val filePart = MultipartBody.Part.createFormData(
                "PersonalItem",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            pItemImagesPart.add(filePart)
        }

        val file: File = FileUtils.getFile(this, Uri.parse(pItemImage.get(0)))


        Log.e("Personal_Item",persoitem.toString())
        // val personal_item=persoitem.toRequestBody("text/plain".toMediaTypeOrNull())
        val case_id=receivedData.toRequestBody("text/plain".toMediaTypeOrNull())
        var jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()
        Log.e("AllItems",persoitem.toString()+" : "+receivedData+" : "+pItemImage.get(0)+" : "+jwt_token)
        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService
        val call = mAPIService.updatePersonaldata(
            jwt_token,
            requestBodyJson,
            pItemImagesPart,
            case_id

        ).enqueue(object : Callback<PersonalItemSaveResponseModel> {
            override fun onResponse(
                call: Call<PersonalItemSaveResponseModel>, response: Response<PersonalItemSaveResponseModel>
            ) {
                if (response.code() == 200) {
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error", response.body()!!.message.toString())

                            if (response.body()?.success == true) {
                                //pItemImage.clear()
                               // binding.cgPitem.removeAllViews()
                                // binding.llPersonalItemsOfTheBody.removeAllViews()
                               // binding.llPersonalItemsOfTheBody.removeAllViews()
                                //piselectedLists.clear()
                                personalItemsArrayList.clear()
                                showAlertDialogMessageSuccess(
                                    this@MorgueLevelSubmitInfoActivity,
                                    response.body()!!.message.toString()
                                )
                                // Clear The form

                                //Clear the form
                            } else {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
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
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (response.code() == 400) {
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<PersonalItemSaveResponseModel>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })


    }


    @SuppressLint("SuspiciousIndentation")
    private fun savePersonaldataItemFromLocalStroage(
        personaldata:String,
        personaliem: Array<String?>?,
        caseid:String) {
        // progressDialogCall(this@MorgueLevelSubmitInfoActivity)
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
            personaldata.toRequestBody("text/plain".toMediaTypeOrNull()),
            personalItemimages,
            caseid.toRequestBody("text/plain".toMediaTypeOrNull())

        ).enqueue(object : Callback<PersonalItemSaveResponseModel> {
            override fun onResponse(
                call: Call<PersonalItemSaveResponseModel>, response: Response<PersonalItemSaveResponseModel>
            ) {
                if (response.code() == 200) {
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {

                            if (response.body()?.success == true) {

//                                showAlertDialogMessage(
//                                    this@MorgueLevelSubmitInfoActivity,
//                                    response.body()!!.message.toString()
//                                )

                                // Clear The form

                                //Clear the form
                            } else {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
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
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (response.code() == 400) {
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<PersonalItemSaveResponseModel>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })


    }

    @SuppressLint("SuspiciousIndentation")
    fun SubmitImageFace() {
        progressDialogCall(this@MorgueLevelSubmitInfoActivity)
        val faceImagesPart = ArrayList<MultipartBody.Part>()
        for (i in 0..(faceImage.size - 1)) {
            // var file = File(faceImage.get(i))

            var file:File = FileUtils.getFile(this, Uri.parse(faceImage.get(i)))
            GlobalScope.launch {
                file =
                    Compressor.compress(applicationContext, file)
            }
            val filePart = MultipartBody.Part.createFormData(
                "Picture",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            faceImagesPart.add(filePart)
        }


        val file: File = FileUtils.getFile(this, Uri.parse(faceImage.get(0)))
        val filePart = MultipartBody.Part.createFormData(
            "Picture",
            file.name,
            file.asRequestBody("image/jpg".toMediaTypeOrNull())
        )

        // if (checkForInternet(this@PSLevelSubmitDeadBodyInfoActivity)) {

        saveImageNew(imagePath,"IMAGE")
        var jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()
        val case_id=receivedData.toRequestBody("text/plain".toMediaTypeOrNull())
        var type="Picture"
        val filedName=type.toRequestBody("text/plain".toMediaTypeOrNull())
        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService

        val status = "1".toRequestBody("text/plain".toMediaTypeOrNull())

        val call = mAPIService.updatePhoto(
            jwt_token,
            filedName,
            faceImagesPart,
            case_id

        ).enqueue(object : Callback<ImageUploadApiResponseModel> {
            override fun onResponse(
                call: Call<ImageUploadApiResponseModel>, response: Response<ImageUploadApiResponseModel>
            ) {
                if (response.code() == 200) {
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error", response.body()!!.message.toString())

                            if (response.body()?.success == true) {
                               // binding.llPicFace.removeAllViews()
                                binding.btnPicFaceUoload.visibility=View.GONE
                               // val img_close = inflater.findViewById<ImageButton>(R.id.btn_close)
                                //img_close.visibility=View.GONE
                                MessageDialog()
                                // Clear The form

                                //Clear the form
                            } else {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
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
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (response.code() == 400) {
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<ImageUploadApiResponseModel>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })
    }

    @SuppressLint("SuspiciousIndentation")
    fun SubmitImageBody() {
        progressDialogCall(this@MorgueLevelSubmitInfoActivity)
        val dBodyImagesPart = ArrayList<MultipartBody.Part>()

        for (i in 0..(bodyImage.size - 1)) {
            // val file = File(bodyImage.get(i))
            val file: File = FileUtils.getFile(this, Uri.parse(bodyImage.get(i)))

            val filePart = MultipartBody.Part.createFormData(
                "Picture",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            System.out.println("FILES" + filePart)
            dBodyImagesPart.add(filePart)
        }



        // if (checkForInternet(this@PSLevelSubmitDeadBodyInfoActivity)) {

        saveImageNew(imagePath,"IMAGE")
        var jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()
        val case_id=receivedData.toRequestBody("text/plain".toMediaTypeOrNull())
        var type="PictureBody"
        val filedName=type.toRequestBody("text/plain".toMediaTypeOrNull())
        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService

        val status = "1".toRequestBody("text/plain".toMediaTypeOrNull())

        val call = mAPIService.updatePhoto(
            jwt_token,
            filedName,
            dBodyImagesPart,
            case_id

        ).enqueue(object : Callback<ImageUploadApiResponseModel> {
            override fun onResponse(
                call: Call<ImageUploadApiResponseModel>, response: Response<ImageUploadApiResponseModel>
            ) {
                if (response.code() == 200) {
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error", response.body()!!.message.toString())

                            if (response.body()?.success == true) {

                                binding.btnPicBodyUoload.visibility=View.GONE
                                bodyImageUploadedFlag = true
                               //binding.llPicBody.removeAllViews()
                                MessageDialog()
                                // Clear The form

                                //Clear the form
                            } else {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
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
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (response.code() == 400) {
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<ImageUploadApiResponseModel>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })
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
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error", response.body()!!.message.toString())

                            if (response.body()?.success == true) {

                                binding.btnPicBodyUoload.visibility=View.GONE
                                binding.llPicBody.removeAllViews()
                                val db = DatabaseDb(this@MorgueLevelSubmitInfoActivity, null)
                                Log.e("syncimagestatus", "success")
                                val code=db.deleteRowByCaseId(case_id,imageType)
                                Log.e("syncimag...estatus", code.toString())
                                // MessageDialog()
                                // Clear The form

                                //Clear the form
                            } else {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
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
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (response.code() == 400) {
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<ImageUploadApiResponseModel>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })
    }

    @SuppressLint("SuspiciousIndentation")
    fun SubmitImageWearingCloth() {
        progressDialogCall(this@MorgueLevelSubmitInfoActivity)
        val WAImagesPart = ArrayList<MultipartBody.Part>()
        for (i in 0..(wApparelsImage.size - 1)) {
            // val file = File(wApparelsImage.get(i))
            val file: File = FileUtils.getFile(this, Uri.parse(wApparelsImage.get(i)))


            val filePart = MultipartBody.Part.createFormData(
                "Picture",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            System.out.println("FILES" + filePart)
            WAImagesPart.add(filePart)
        }
        val file: File = FileUtils.getFile(this, Uri.parse(wApparelsImage.get(0)))
        val filePart = MultipartBody.Part.createFormData(
            "Picture",
            file.name,
            file.asRequestBody("image/jpg".toMediaTypeOrNull())
        )

        // if (checkForInternet(this@PSLevelSubmitDeadBodyInfoActivity)) {

        saveImageNew(imagePath,"PictureWearing")
        var jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()
        val case_id=receivedData.toRequestBody("text/plain".toMediaTypeOrNull())
        var type="PictureWearing"
        val filedName=type.toRequestBody("text/plain".toMediaTypeOrNull())
        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService

        val status = "1".toRequestBody("text/plain".toMediaTypeOrNull())

        val call = mAPIService.updatePhoto(
            jwt_token,
            filedName,
            WAImagesPart,
            case_id

        ).enqueue(object : Callback<ImageUploadApiResponseModel> {
            override fun onResponse(
                call: Call<ImageUploadApiResponseModel>, response: Response<ImageUploadApiResponseModel>
            ) {
                if (response.code() == 200) {
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error", response.body()!!.message.toString())

                            if (response.body()?.success == true) {
                                wApparelsImage.clear()
                                binding.btnPicClothUpload.visibility=View.GONE
                               // binding.llPicWa.removeAllViews()
                                MessageDialog()
                                // Clear The form

                                //Clear the form
                            } else {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
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
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (response.code() == 400) {
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<ImageUploadApiResponseModel>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })
    }

    @SuppressLint("SuspiciousIndentation")
    fun SubmitImageWearinFootWear() {
        progressDialogCall(this@MorgueLevelSubmitInfoActivity)
        val footwareImagesPart = ArrayList<MultipartBody.Part>()
        for (i in 0..(footwareApparelsImage.size - 1)) {
            // val file = File(footwareApparelsImage.get(i))
            val file: File = FileUtils.getFile(this, Uri.parse(footwareApparelsImage.get(i)))

            val filePart = MultipartBody.Part.createFormData(
                "Picture",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            System.out.println("FILES" + filePart)
            footwareImagesPart.add(filePart)
        }
        val file: File = FileUtils.getFile(this, Uri.parse(footwareApparelsImage.get(0)))
        val filePart = MultipartBody.Part.createFormData(
            "Picture",
            file.name,
            file.asRequestBody("image/jpg".toMediaTypeOrNull())
        )

        // if (checkForInternet(this@PSLevelSubmitDeadBodyInfoActivity)) {

        saveImageNew(imagePath,"FootImage")
        var jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()
        val case_id=receivedData.toRequestBody("text/plain".toMediaTypeOrNull())
        var type="FootImage"
        val filedName=type.toRequestBody("text/plain".toMediaTypeOrNull())
        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService

        val status = "1".toRequestBody("text/plain".toMediaTypeOrNull())

        val call = mAPIService.updatePhoto(
            jwt_token,
            filedName,
            footwareImagesPart,
            case_id

        ).enqueue(object : Callback<ImageUploadApiResponseModel> {
            override fun onResponse(
                call: Call<ImageUploadApiResponseModel>, response: Response<ImageUploadApiResponseModel>
            ) {
                if (response.code() == 200) {
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error", response.body()!!.message.toString())

                            if (response.body()?.success == true) {
                                footwareApparelsImage.clear()
                                binding.btnPicFootwearUpload.visibility=View.GONE
                              //  binding.llFootwarePic.removeAllViews()
                                MessageDialog()
                                // Clear The form

                                //Clear the form
                            } else {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
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
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (response.code() == 400) {
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<ImageUploadApiResponseModel>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })
    }


    @SuppressLint("SuspiciousIndentation")
    fun SubmitImageOthers() {
        progressDialogCall(this@MorgueLevelSubmitInfoActivity)
        val otherImagesPart = ArrayList<MultipartBody.Part>()
        for (i in 0..(othersImage.size - 1)) {
            // val file = File(othersImage.get(i))
            val file: File = FileUtils.getFile(this, Uri.parse(othersImage.get(i)))

            val filePart = MultipartBody.Part.createFormData(
                "Picture",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            System.out.println("FILES" + filePart)
            otherImagesPart.add(filePart)
        }
        val file: File = FileUtils.getFile(this, Uri.parse(othersImage.get(0)))
        val filePart = MultipartBody.Part.createFormData(
            "Picture",
            file.name,
            file.asRequestBody("image/jpg".toMediaTypeOrNull())
        )

        // if (checkForInternet(this@PSLevelSubmitDeadBodyInfoActivity)) {

        saveImageNew(imagePath,"PictureOther")
        var jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()
        val case_id=receivedData.toRequestBody("text/plain".toMediaTypeOrNull())
        var type="PictureOther"
        val filedName=type.toRequestBody("text/plain".toMediaTypeOrNull())
        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService


        val call = mAPIService.updatePhoto(
            jwt_token,
            filedName,
            otherImagesPart,
            case_id

        ).enqueue(object : Callback<ImageUploadApiResponseModel> {
            override fun onResponse(
                call: Call<ImageUploadApiResponseModel>, response: Response<ImageUploadApiResponseModel>
            ) {
                if (response.code() == 200) {
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error", response.body()!!.message.toString())

                            if (response.body()?.success == true) {
                                othersImage.clear()
                                binding.btnPicOthersUpload.visibility=View.GONE
                              //  binding.llPicOthers.removeAllViews()
                                MessageDialog()
                                // Clear The form

                                //Clear the form
                            } else {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
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
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end. Error Core : "+response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (response.code() == 400) {
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else {
                    //  progressDialog!!.cancel()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

            override fun onFailure(call: Call<ImageUploadApiResponseModel>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })
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
    private fun  callburnmarks(callback: (Array<String>?) -> Unit)
    {
        val apiService = ApiUtils.apiService
        val call = apiService.burnmarks(

        )
        call.enqueue(object : Callback<TypeBurnmarksResponse> {
            override fun onResponse(
                call: Call<TypeBurnmarksResponse>,
                response: Response<TypeBurnmarksResponse>
            ) {

                if (response.code() == 400) {
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "Some issue in server end",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (response.code() == 420) {
                    Log.e("Message_error", response.code().toString())
                }else if (response.code() == 450) {
                    Log.e("Message_error", response.code().toString())
                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        // Here you can display or log the error body
                        val errorBodyString = errorBody.string()
                    }
                }else {
                    if (response.isSuccessful) {
                        val typemarksResponse = response.body()
                        if (typemarksResponse != null && typemarksResponse.status == "success") {
                            // Extract the type values from the data list
                            val  typemarks = typemarksResponse.data.map { it.type }.toTypedArray()

                            // Print the array of hair types
                            typemarks.forEach { println(it) }

                            Log.e("typesMarks",typemarks.size.toString())
                            callback(typemarks)

                        } else {
                            // Handle unsuccessful response or null body
                        }

                    } else {
                        val errorCode = response.code() // HTTP error code
                        val errorMessage = response.message() // Error message
                        Log.e("Message_error", errorMessage.toString())
                        // Handle error response
                        Log.e("Message_error", response.code().toString())
                        Toast.makeText(this@MorgueLevelSubmitInfoActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        Toast.makeText(this@MorgueLevelSubmitInfoActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<TypeBurnmarksResponse>, t: Throwable) {
                // Log the error message or handle it as needed
                Log.e("API Error", "Error: ${t.message}", t)
                callback(null)
                // Example: Display a toast message with the error


                // Example: Check the type of error and handle it accordingly
                if (t is IOException) {
                    // Network error
                    // Handle network-related errors
                } else {
                    // Other types of errors
                    // Handle other types of errors
                }
                Toast.makeText(this@MorgueLevelSubmitInfoActivity, "API call failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun  calltypemarks(callback: (Array<String>?) -> Unit)
    {
        val apiService = ApiUtils.apiService
        val call = apiService.typemarks(

        )
        call.enqueue(object : Callback<TypeBurnmarksResponse> {
            override fun onResponse(
                call: Call<TypeBurnmarksResponse>,
                response: Response<TypeBurnmarksResponse>
            ) {

                if (response.code() == 400) {
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "Some issue in server end",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (response.code() == 420) {
                    Log.e("Message_error", response.code().toString())
                }else if (response.code() == 450) {
                    Log.e("Message_error", response.code().toString())
                    val errorBody = response.errorBody()
                    if (errorBody != null) {
                        // Here you can display or log the error body
                        val errorBodyString = errorBody.string()
                    }
                }else {


                    if (response.isSuccessful) {
                        val typemarksResponse = response.body()
                        if (typemarksResponse != null && typemarksResponse.status == "success") {
                            // Extract the type values from the data list
                            val  typemarks = typemarksResponse.data.map { it.type }.toTypedArray()

                            // Print the array of hair types
                            typemarks.forEach { println(it) }
                            callback(typemarks)

                        } else {
                            // Handle unsuccessful response or null body
                        }

                    } else {
                        val errorCode = response.code() // HTTP error code
                        val errorMessage = response.message() // Error message
                        Log.e("Message_error", errorMessage.toString())
                        // Handle error response
                        Log.e("Message_error", response.code().toString())
                        Toast.makeText(this@MorgueLevelSubmitInfoActivity, errorMessage, Toast.LENGTH_SHORT).show()
                        Toast.makeText(this@MorgueLevelSubmitInfoActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }


                }
            }

            override fun onFailure(call: Call<TypeBurnmarksResponse>, t: Throwable) {
                // Log the error message or handle it as needed
                Log.e("API Error", "Error: ${t.message}", t)
                callback(null)
                // Example: Display a toast message with the error


                // Example: Check the type of error and handle it accordingly
                if (t is IOException) {
                    // Network error
                    // Handle network-related errors
                } else {
                    // Other types of errors
                    // Handle other types of errors
                }
                Toast.makeText(this@MorgueLevelSubmitInfoActivity, "API call failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    @SuppressLint
    fun updateBurnMarks(){
        progressDialogCall(this@MorgueLevelSubmitInfoActivity)
        val simImagesPart = ArrayList<MultipartBody.Part>()
        for (i in 0..(SpecialMarksImage.size - 1)) {
            // val file = File(SpecialMarksImage.get(i))
            val file: File = FileUtils.getFile(this, Uri.parse(SpecialMarksImage.get(i)))

            val filePart = MultipartBody.Part.createFormData(
                "Burnmarksimage",
                file.name,
                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            )
            System.out.println("FILES" + filePart)
            simImagesPart.add(filePart)
        }
//        val file: File = FileUtils.getFile(this, Uri.parse(SpecialMarksImage.get(0)))
//
//        val filePart = MultipartBody.Part.createFormData(
//            "Burnmarksimage",
//            file.name,
//            file.asRequestBody("image/jpeg".toMediaTypeOrNull())
//        )
        val gson = Gson()
        val value1= getSelectedMarksInJSONStringSpecial()
        val list1=value1.getString("marksBurn")
        Log.e("List",list1)
        val requestBodyJson = list1.toRequestBody("text/plain".toMediaTypeOrNull())


        var jwtToken = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()

        Log.e("TOKEN",jwtToken)
        Log.e("CASE_ID",receivedData)
        val case_id=receivedData.toRequestBody("text/plain".toMediaTypeOrNull())

        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService
        val Call = mAPIService.updateBurningMarks(
            jwtToken,
            requestBodyJson,
            simImagesPart,
            case_id
        ).enqueue(object : Callback<BurnMarksModel> {
            override fun onResponse(
                call: Call<BurnMarksModel>, response:
                Response<BurnMarksModel>
            ){
                if (response.code() == 200) {
                    splMarksModelArrayList.clear()
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error",
                                response.body()!!.message.toString())
                            if (response.body()?.success == true) {
                               // SpecialMarksImage.clear()
                                // binding.llSpcIdenMarks.removeAllViews()
                               // binding.llSpcIdenMarks.removeAllViews()
                                showAlertDialogMessageSuccess(
                                    this@MorgueLevelSubmitInfoActivity,
                                    response.body()!!.message.toString()
                                )
                            }
                            else
                            {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
                                    response.body()!!.message.toString()
                                )
                            }
                        }
                    }
                    catch (exception: java.lang.Exception)
                    {
                        closeProgressDialogCall()
                        Toast.makeText(
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end. Error Core : " + response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else if (response.code() == 400)
                {splMarksModelArrayList.clear()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else{
                    splMarksModelArrayList.clear()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            override fun onFailure(call: Call<BurnMarksModel>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })
    }

    @SuppressLint
    fun updateBurnMarksFromdataBaseLocal(
        burnmarkstype:String, Burnmarksimage: Array<String?>?,
        case_id:String){
        // progressDialogCall(this@MorgueLevelSubmitInfoActivity)
        val simImagesPart = ArrayList<MultipartBody.Part>()
        val db = DatabaseDb(this@MorgueLevelSubmitInfoActivity, null)
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
        Log.e("CASE_ID",receivedData)


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
                    closeProgressDialogCall()
                    try {
                        if (response.isSuccessful) {
                            Log.e("Message_error",
                                response.body()!!.message.toString())
                            if (response.body()?.success == true) {
                                db.deleteRowByCaseIdBurncase(case_id)
                                showAlertDialogMessageSuccess(
                                    this@MorgueLevelSubmitInfoActivity,
                                    response.body()!!.message.toString()
                                )
                            }
                            else
                            {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
                                    response.body()!!.message.toString()
                                )
                            }
                        }
                    }
                    catch (exception: java.lang.Exception)
                    {
                        closeProgressDialogCall()
                        Toast.makeText(
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end. Error Core : " + response.code().toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                else if (response.code() == 400)
                {splMarksModelArrayList.clear()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR 400 !!! Please try after sometime.. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
                else{
                    splMarksModelArrayList.clear()
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "SERVER ERROR!!! Please try after sometime. Error Core : "+response.code().toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            override fun onFailure(call: Call<BurnMarksModel>, t: Throwable) {
                closeProgressDialogCall()
                showAlertDialogMessage(
                    this@MorgueLevelSubmitInfoActivity,
                    "SERVER ERROR on Failure !!!" + t.message
                )
            }
        })
    }


    protected fun saveImageNew(imageUri: String, type: String) {

        val appFolderPath = "${applicationContext.filesDir.absolutePath}/images/"
//        var db = DatabaseDb(this, null)
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun showAlertDialogMessage(activity: Activity, msg: String) {
        var alertDialog: androidx.appcompat.app.AlertDialog? = activity.let {
            val appName = getString(R.string.app_name)
            var builder = androidx.appcompat.app.AlertDialog.Builder(it)
            builder.setTitle(appName)
            builder.setIcon(R.drawable.error)
            builder.setMessage(msg)
            builder.apply {

                setNegativeButton(R.string.close,
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            }
            builder.create()
        }
        alertDialog?.show()
    }
    fun showAlertDialogMessageSuccess(activity: Activity, msg: String) {
        var alertDialog: androidx.appcompat.app.AlertDialog? = activity.let {
            val appName = getString(R.string.app_name)
            var builder = androidx.appcompat.app.AlertDialog.Builder(it)
            builder.setTitle(appName)
            builder.setIcon(R.drawable.ok_sign)
            builder.setMessage(msg)
            builder.apply {

                setNegativeButton(R.string.close,
                    DialogInterface.OnClickListener { dialog, id ->
                    })
            }
            builder.create()
        }
        alertDialog?.show()
    }

    private fun MessageDialog() {
        val alertDialog: AlertDialog.Builder =
            AlertDialog.Builder(this@MorgueLevelSubmitInfoActivity)
        alertDialog.setTitle(" ")
        alertDialog.setIcon(R.drawable.ok_sign)
        alertDialog.setMessage("Successfully Submitted...")
        alertDialog.setPositiveButton(
            "OK"
        ) { dialog, id ->

        }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }

    fun checkCameraPermission(): Boolean {

        return if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED
            ) {
                Log.e("camera2", "camera check")
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
            false
        } else {
            true
        }
    }

    fun showPersonalItemsDialogButtonClicked() {
        // Create an alert builder
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        //builder.setCancelable(false)
        // set the custom layout
        val customLayout: View =
            layoutInflater.inflate(R.layout.custom_personal_items_alert_dialoglayout, null)
        builder.setView(customLayout)

        val spn_pi = customLayout.findViewById<Spinner>(R.id.spinner_personal_items)
        val tiet_pi = customLayout.findViewById<TextInputEditText>(R.id.tiet_personal_items)
        val btn_pi_add = customLayout.findViewById<Button>(R.id.btn_personal_items)
        val btnPIPic = customLayout.findViewById<Button>(R.id.btn_personal_items_pic)
        val btn_pi_cancel = customLayout.findViewById<Button>(R.id.btn_personal_items_cancel)

        val pesoItem = resources.getStringArray(R.array.personal_item)

        val adapterPI = ArrayAdapter(
            this,
            R.layout.spinner_item, pesoItem
        )
        spn_pi.adapter = adapterPI

        spn_pi.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                valB = pesoItem.get(position)!!.toString()
                valBID = spn_pi.selectedItemPosition

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        btnPIPic.setOnClickListener {
            if (checkFileReadWritePermission()) {
                /*ImagePicker.with(this@SubmitDeadBodyInformationActivity)
                    .crop() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080, 1080
                    )
                    .start(IMAGE_REQUEST_PERSONAL_ITEM)*/
                val type = "PERSONALITEM"
//                bannerText = "Please Capture Photo of Personal Items"
                bannerText = "Please Capture Photo of " +tiet_pi.text.toString()
                openPostActivity.launch(
                    CameraXActivity.getIntent(this, 1, type, bannerText)
                )
            }
        }

        btn_pi_cancel.setOnClickListener()
        {
            dialog.dismiss()

        }

        btn_pi_add.setOnClickListener()
        {

            if (spn_pi.selectedItemPosition == 0) {
                showToastMessage("PLease select Specific Type")
            } else if (spn_pi.selectedItemPosition == 0) {
                showToastMessage("PLease select Specific Location")
            } else if (pItemImage.size == 0) {
                showToastMessage("PLease Add Picture")
            } else {


                binding.cgPitem.removeAllViews()
                piselectedLists.add(spn_pi.selectedItem.toString() + "-" + tiet_pi.text)
                personalItemsArrayList.add(
                    PersonalItemsModel(
                        spn_pi.selectedItem.toString(),
                        tiet_pi.text.toString().trim()
                    )
                )
                if(checkForInternet(this@MorgueLevelSubmitInfoActivity)){
                    savePersonaldataItem()
                }
                else{
                    var db1 = DatabaseDb(this, null)
                    val persoitem = getPItemList()
                    val gson = Gson()
                    val jsonString = gson.toJson(persoitem).toString()

                    Log.e("jsonStringconvert",jsonString)

                    val requestBodyJson = gson.toJson(persoitem).toRequestBody("text/plain".toMediaTypeOrNull())
                    val personalDataString = jsonString.toRequestBody("text/plain".toMediaTypeOrNull())

                    val file: File = FileUtils.getFile(this, Uri.parse(pItemImage.get(0)))


                    Log.e("Personal_Item",persoitem.toString())

                    val case_id=receivedData.toRequestBody("text/plain".toMediaTypeOrNull())
                    var jwt_token = SharedPreferenceStorage.getValue(
                        applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
                    ).toString()

                    db1.addPersonalItemDataOffline(jwt_token,gson.toJson(persoitem),pItemImage,receivedData)
                    showAlertDialogMessageSuccess(this@MorgueLevelSubmitInfoActivity,resources.getString(R.string.save_local_stroage))
                    pItemImage.clear()
                    binding.cgPitem.removeAllViews()
                    // binding.llPersonalItemsOfTheBody.removeAllViews()
                    binding.llPersonalItemsOfTheBody.removeAllViews()
                    piselectedLists.clear()
                    personalItemsArrayList.clear()
                }



                //  getSelectedMarksInJSONString()
                var j: Int = 0
                for (i in piselectedLists) {
                    addChipIcon(binding.cgPitem, piselectedLists[j])
                    j = j + 1
                }

                dialog.dismiss()
            }

            println(personalItemsArrayList)

        }

        dialog = builder.create()
        dialog.show()
    }

    //    fun showMarkselectionDialogButtonClicked() {
//        // Create an alert builder
//        lateinit var dialog: AlertDialog
//        val builder = AlertDialog.Builder(this)
//        builder.setCancelable(false)
//        // set the custom layout
//        val customLayout: View =
//            layoutInflater.inflate(R.layout.custom_special_marks_alert_dialoglayout, null)
//        builder.setView(customLayout)
//
//        val spn_select_spec_type = customLayout.findViewById<Spinner>(R.id.spinner_select_spec_type)
//        val spn_select_spec_location =
//            customLayout.findViewById<Spinner>(R.id.spinner_select_spec_location)
//        val tiet_Specify_mark = customLayout.findViewById<TextInputEditText>(R.id.tiet_Specify_mark)
//        val btn_spc_add = customLayout.findViewById<Button>(R.id.btn_spc_add)
//        val btnSpcAddPic = customLayout.findViewById<Button>(R.id.btn_spc_add_pic)
//        val btn_spc_cancel = customLayout.findViewById<Button>(R.id.btn_spc_cancel)
//
//        val spinnerArrayType = arrayOfNulls<String>(spcIdTypeHashMap.size)
//        val spinnerArrayTypeId = arrayOfNulls<String>(spcIdTypeHashMap.size)
//        val spinnerArrayLoca = arrayOfNulls<String>(spcIdLocHashMap.size)
//        val spinnerArrayLocaId = arrayOfNulls<String>(spcIdLocHashMap.size)
//        var z = 0
//        for (i in spcIdTypeHashMap) {
//            spinnerArrayType.set(z, spcIdTypeHashMap.get(z).get("IN_NAME"))
//            spinnerArrayTypeId.set(z, spcIdTypeHashMap.get(z).get("IN_ID"))
//            z = z + 1
//        }
//        var y = 0
//        for (i in spcIdLocHashMap) {
//            spinnerArrayLoca.set(y, spcIdLocHashMap.get(y).get("IN_NAME"))
//            spinnerArrayLocaId.set(y, spcIdLocHashMap.get(y).get("IN_ID"))
//            y = y + 1
//        }
//
//        val adapter = ArrayAdapter(
//            this,
//            R.layout.spinner_item, spinnerArrayType
//        )
//        spn_select_spec_type.adapter = adapter
//
//        spn_select_spec_type.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long
//            ) {
//                /*Toast.makeText(this@MainActivity,
//                    getString(R.string.selected_item) + " " +
//                            "" + languages[position], Toast.LENGTH_SHORT).show()*/
//                valStr = spinnerArrayType.get(position)!!.toString()
//                valStrID = spinnerArrayTypeId.get(position)!!.toInt().toString()
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }
//
//        val adapterLoc = ArrayAdapter(
//            this,
//            R.layout.spinner_item, spinnerArrayLoca
//        )
//        spn_select_spec_type.prompt = "Select Type"
//        spn_select_spec_location.adapter = adapterLoc
//
//        spn_select_spec_location.onItemSelectedListener = object :
//            AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>,
//                view: View, position: Int, id: Long
//            ) {
//                //  valStrLocation = spinnerArrayLocaId.get(spinnerArrayLoca[position]!!.toInt()).toString()
//                valStrLocationID = spinnerArrayLocaId.get(position)!!.toInt().toString()
//                valStrLocation = spinnerArrayLoca.get(position)!!.toString()
//
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>) {
//                // write code to perform some action
//            }
//        }
//
//        btnSpcAddPic.setOnClickListener {
//            if (checkFileReadWritePermission()) {
//                /*ImagePicker.with(this@SubmitDeadBodyInformationActivity)
//                    .crop() //Crop image(Optional), Check Customization for more option
//                    .compress(1024) //Final image size will be less than 1 MB(Optional)
//                    .maxResultSize(
//                        1080, 1080
//                    )
//                    .start(IMAGE_REQUEST_SPECIALMARKS)*/
//                val type = "SPECLMARKS"
//                openPostActivity.launch(
//                    CameraXActivity.getIntent(this, 1, type, bannerText)
//                )
//            }
//        }
//
//        btn_spc_cancel.setOnClickListener()
//        {
//            dialog.dismiss()
//
//        }
//
//        btn_spc_add.setOnClickListener()
//        {
//
//            if (spn_select_spec_type.selectedItemPosition == 0) {
//                showToastMessage("PLease select Specific Type")
//            } else if (spn_select_spec_location.selectedItemPosition == 0) {
//                showToastMessage("PLease select Specific Location")
//            } else if (SpecialMarksImage.size == 0) {
//                showToastMessage("PLease Add Picture")
//            } else {
//
//
//                binding.cgSpcIden.removeAllViews()
//                selectedLists.add(valStr + "-" + valStrLocation + "-" + tiet_Specify_mark.text)
//                splMarksModelArrayList.add(
//                    SpecialIDMarksModel(
//                        valStrID.toInt(),
//                        valStr,
//                        valStrLocationID.toInt(),
//                        valStrLocation,
//                        tiet_Specify_mark.text.toString().trim()
//                    )
//                )
//
//
//                getSelectedMarksInJSONString()
//                var j: Int = 0
//                for (i in selectedLists) {
//                    addChipIcon(binding.cgSpcIden, selectedLists[j])
//                    j = j + 1
//                }
//
//                dialog.dismiss()
//            }
//
//            println(splMarksModelArrayList)
//
//        }
//
//        dialog = builder.create()
//        dialog.show()
//    }
    fun showMarkselectionDialogButtonClicked() {
        // Create an alert builder
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        // set the custom layout
        val customLayout: View =
            layoutInflater.inflate(R.layout.custom_special_marks_alert_dialoglayout, null)
        builder.setView(customLayout)

        val spn_select_spec_type = customLayout.findViewById<Spinner>(R.id.spinner_select_spec_type)
        val spn_select_spec_location =
            customLayout.findViewById<Spinner>(R.id.spinner_select_spec_location)
        val tiet_Specify_mark = customLayout.findViewById<TextInputEditText>(R.id.tiet_Specify_mark)
        val btn_spc_add = customLayout.findViewById<Button>(R.id.btn_spc_add)
        val btnSpcAddPic = customLayout.findViewById<Button>(R.id.btn_spc_add_pic)
        val btn_spc_cancel = customLayout.findViewById<Button>(R.id.btn_spc_cancel)

        val spinnerArrayType = arrayOfNulls<String>(spcIdTypeHashMap.size)
        val spinnerArrayTypeId = arrayOfNulls<String>(spcIdTypeHashMap.size)
        val spinnerArrayLoca = arrayOfNulls<String>(spcIdLocHashMap.size)
        val spinnerArrayLocaId = arrayOfNulls<String>(spcIdLocHashMap.size)
        var z = 0
        val adapter2 = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values ?: arrayOf())

        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_select_spec_type.adapter = adapter2
        spn_select_spec_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (values.isNotEmpty()) {
                    selectedBurnmarkstype = values.getOrNull(position)?.toString() ?: ""
                    Log.e("selectedBurnmarkstype",selectedBurnmarkstype)
                }
            }



            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        burnTypeMarksAfterSelected.add(selectedBurnmarkstype)




        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values2 ?: arrayOf())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_select_spec_location.adapter = adapter
        spn_select_spec_location.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                if (values2.isNotEmpty()) {

                    selectedBurnmarkstype11 = values2.getOrNull(position)?.toString() ?: ""
                    Log.e("BurnMarksType",selectedBurnmarkstype11)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        burnTypeMarksAfterSelected1.add(selectedBurnmarkstype11)


        val adapterLoc = ArrayAdapter(
            this,
            R.layout.spinner_item, values2
        )
        spn_select_spec_type.prompt = "Select Type"
        spn_select_spec_location.adapter = adapterLoc



        btnSpcAddPic.setOnClickListener {
            if (checkFileReadWritePermission()) {
                /*ImagePicker.with(this@SubmitDeadBodyInformationActivity)
                    .crop() //Crop image(Optional), Check Customization for more option
                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(
                        1080, 1080
                    )
                    .start(IMAGE_REQUEST_SPECIALMARKS)*/
                val type = "SPECLMARKS"
                openPostActivity.launch(
                    CameraXActivity.getIntent(this, 1, type, bannerText)
                )
            }
        }

        btn_spc_cancel.setOnClickListener()
        {
            dialog.dismiss()

        }

        btn_spc_add.setOnClickListener()
        {

           /* if (spn_select_spec_type.selectedItemPosition == 0) {
                showToastMessage("PLease select Specific Type")
            } else if (spn_select_spec_location.selectedItemPosition == 0) {
                showToastMessage("PLease select Specific Location")
            } else*/ if (SpecialMarksImage.size == 0) {
                showToastMessage("PLease Add Picture")
            } else {
                binding.cgSpcIden.removeAllViews()
//            selectedLists.add(valStr + "-" + valStrLocation + "-" + tiet_Specify_mark.text)
                splMarksModelArrayList.add(
                    SpecialIDMarksModel(
                        1,
                        selectedBurnmarkstype,
                        2,
                        selectedBurnmarkstype,
                        tiet_Specify_mark.text.toString().trim()
                    )
                )

                getSelectedMarksInJSONStringSpecial()
                if(checkForInternet(this@MorgueLevelSubmitInfoActivity)){
                    updateBurnMarks()
                }
                else{
                    var db1 = DatabaseDb(this, null)
                    val simImagesPart = ArrayList<MultipartBody.Part>()
                    for (i in 0..(SpecialMarksImage.size - 1)) {
                        // val file = File(SpecialMarksImage.get(i))
                        val file: File = FileUtils.getFile(this, Uri.parse(SpecialMarksImage.get(i)))

                        val filePart = MultipartBody.Part.createFormData(
                            "Burnmarksimage",
                            file.name,
                            file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                        )
                        System.out.println("FILES" + filePart)
                        simImagesPart.add(filePart)
                    }

                    val gson = Gson()
                    val value1= getSelectedMarksInJSONStringSpecial()
                    val list1=value1.getString("marksBurn")
                    Log.e("List",list1)
                    val requestBodyJson = list1.toRequestBody("text/plain".toMediaTypeOrNull())


                    var jwtToken = SharedPreferenceStorage.getValue(
                        applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
                    ).toString()
                    db1.addBurnMarksTypeData(jwtToken,list1,SpecialMarksImage,receivedData)
                    showAlertDialogMessageSuccess(this@MorgueLevelSubmitInfoActivity,resources.getString(R.string.save_local_stroage))
                    SpecialMarksImage.clear()
                    // binding.llSpcIdenMarks.removeAllViews()
                    binding.llSpcIdenMarks.removeAllViews()
                }

                // getSelectedMarksInJSONString()
                var j: Int = 0
                for (i in selectedLists) {
                    addChipIcon(binding.cgSpcIden, selectedLists[j])
                    j = j + 1
                }

                // dialog.dismiss()
            }

            println(splMarksModelArrayList)

        }

        dialog = builder.create()
        dialog.show()
    }

    fun showPecularitiesSelectionDialogButtonClicked() {
        // Create an alert builder
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        // set the custom layout
        val customLayout: View =
            layoutInflater.inflate(R.layout.custom_peculiarities_alert_dialoglayout, null)
        builder.setView(customLayout)

        val spn_select_pecu_one = customLayout.findViewById<Spinner>(R.id.spinner_select_pecuone)
        val spn_select_pecu_two = customLayout.findViewById<Spinner>(R.id.spinner_select_pecutwo)
        val spn_select_pecu_three =
            customLayout.findViewById<Spinner>(R.id.spinner_select_pecuthree)
        val spn_select_pecu_four = customLayout.findViewById<Spinner>(R.id.spinner_select_pecufour)
        val btn_pecu_add = customLayout.findViewById<Button>(R.id.btn_pecu_add)
        val btn_pecu_cancel = customLayout.findViewById<Button>(R.id.btn_pecu_cancel)

        val pecuA = resources.getStringArray(R.array.pecu_one)
        val pecuB = resources.getStringArray(R.array.pecu_two)
        val pecuC = resources.getStringArray(R.array.pecu_three)
        val pecuD = resources.getStringArray(R.array.pecu_four)

        val adapterOne = ArrayAdapter(
            this,
            R.layout.spinner_item, pecuB
        )
        spn_select_pecu_one.adapter = adapterOne

        spn_select_pecu_one.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                valB = pecuA.get(position)!!.toString()
                valBID = spn_select_pecu_one.selectedItemPosition

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val adapterTwo = ArrayAdapter(
            this,
            R.layout.spinner_item, pecuC
        )
        spn_select_pecu_two.adapter = adapterTwo

        spn_select_pecu_two.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                valC = pecuB.get(position)!!.toString()
                valCID = spn_select_pecu_two.selectedItemPosition

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val adapterThree = ArrayAdapter(
            this,
            R.layout.spinner_item, pecuA
        )
        spn_select_pecu_three.adapter = adapterThree

        spn_select_pecu_three.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                valA = pecuC.get(position)!!.toString()
                valAID = spn_select_pecu_three.selectedItemPosition
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val adapterFour = ArrayAdapter(
            this,
            R.layout.spinner_item, pecuD
        )
        spn_select_pecu_four.adapter = adapterFour

        spn_select_pecu_four.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                valD = pecuD.get(position)!!.toString()
                valDID = spn_select_pecu_four.selectedItemPosition
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }
        btn_pecu_cancel.setOnClickListener()
        {
            dialog.dismiss()

        }
        btn_pecu_add.setOnClickListener {
            if (spn_select_pecu_one.selectedItemPosition == 0) {
                showToastMessage("PLease select Type")
            } else
                if (spn_select_pecu_two.selectedItemPosition == 0) {
                    showToastMessage("PLease select Specific Location")
                } else
                    if (spn_select_pecu_three.selectedItemPosition == 0) {
                        showToastMessage("PLease select Specific Type")
                    } else
                        if (spn_select_pecu_four.selectedItemPosition == 0) {
                            showToastMessage("PLease specify ")
                        } else {
                            binding.cgPecu.removeAllViews()
                            pecuLists.add(valA + "-" + valB + "-" + valC + "-" + valD)
                            PecuModelArrayList.add(
                                PecuMarksModel(
                                    valBID.toInt(),
                                    valB,
                                    valCID.toInt(),
                                    valC,
                                    valAID.toInt(),
                                    valA,
                                    valDID.toInt(),
                                    valD
                                )
                            )
                            getPeculiartiesInJSONString()
                            var j: Int = 0
                            for (i in pecuLists) {
                                addChipIcon(binding.cgPecu, pecuLists[j])
                                j = j + 1
                            }
                            dialog.dismiss()
                        }
            println(PecuModelArrayList)
        }
        dialog = builder.create()
        dialog.show()
    }

    fun getSelectedMarksInJSONString() {
        val json = JSONObject().apply {
            put("marks",
                JSONArray().apply {

                    for (item: SpecialIDMarksModel in splMarksModelArrayList) {
                        put(JSONObject().apply {
                            put("type_id", item.type_name)
                            put("loc_body_id", item.loc_body_name)
                            put("specify", item.specify)
                        })
                    }
                })
        }
        println(json)
    }

    fun getSelectedMarksInJSONStringSpecial() :JSONObject{
        val json = JSONObject().apply {
            put("marksBurn",
                JSONArray().apply {

                    for (item: SpecialIDMarksModel in splMarksModelArrayList) {
                        put(JSONObject().apply {
                            put("type", item.type_name)
                            put("lbody", item.loc_body_name)
                            put("parts", item.specify)
                        })
                    }
                })
        }
        println(json)
        return json
    }

    fun getPeculiartiesInJSONString():JSONObject {
        val json = JSONObject().apply {
            put("marks",
                JSONArray().apply {

                    for (item: PecuMarksModel in PecuModelArrayList) {
                        put(JSONObject().apply {
                            put("hl", item.pecu_nameTwo)
                            put("rl", item.pecu_nameThree)
                            put("lf", item.pecu_nameOne)
                            put("em", item.pecu_nameFour)
                        })
                    }

                })
        }

        println(json)
        return  json
    }


    fun display_icon_with_animation(imageView: ImageView, iconID: Int) {
        imageView.setImageResource(iconID)
        val rotate = RotateAnimation(
            0f, 180f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f
        )

        rotate.duration = 500
        rotate.repeatCount = Animation.ABSOLUTE
        imageView.startAnimation(rotate)
        imageView.setImageResource(iconID)


    }

    private fun HairTypeDialog() {
        var j = 0
        val list: MutableList<String> = ArrayList()
        val idlist: MutableList<String> = ArrayList()
        val namelist: MutableList<String> = ArrayList()
        for (i in hairTypeArray) {
            val p_name = hairTypeArray.get(j)
            list.add("" + p_name)
            namelist.add(p_name.toString().trim())

            j = j + 1
        }
        val listItems = list.toTypedArray()
        val namelistItems = namelist.toTypedArray()
        val idlistItems = idlist.toTypedArray()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Hair Type")
        if (!seletedIdHairindex.isEmpty()) {
            checkedIdHair =
                BooleanArray(listItems.size) { false } //this will checked the items when user open the dialog
            var K: Int = 0
            for (i in seletedIdHairindex) {
                Log.e("TAG", seletedIdHairindex[K].toString())
                checkedIdHair!!.set(seletedIdHairindex[K], true)
                K = K + 1
            }
            previousSeletedIdHairindex.clear()
            previousSeletedIdHairindex.addAll(seletedIdHairindex)
        }

        builder.setMultiChoiceItems(listItems,
            checkedIdHair,
            DialogInterface.OnMultiChoiceClickListener { dialog, indexSelected, isChecked ->
                if (isChecked) {
                    seletedIdHairItems.add(namelistItems[indexSelected].toString())
                    seletedIdHairid.add(idlistItems[indexSelected].toString())
                    seletedIdHairindex.add(indexSelected)

                } else if (!isChecked) {
                    seletedIdHairItems.remove(namelistItems[indexSelected].toString())
                    seletedIdHairid.remove(idlistItems[indexSelected].toString())
                    seletedIdHairindex.remove(indexSelected)
                }
            }).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->


            if (seletedIdHairItems.contains(resources.getString(R.string.others))) {
                //openOtherEnterValueDialog()
                showAlertDialogButtonClicked(
                    "Hair Other Value",
                    "Description",
                    binding.llOtherHair,
                    binding.tvOtherHair
                )
            } else {
                binding.llOtherHair.visibility = View.GONE
                binding.tvOtherHair.text = ""
            }

            binding.cgHair.removeAllViews()
            var j: Int = 0
            for (i in seletedIdHairItems) {
                addChipIcon(binding.cgHair, seletedIdHairItems[j])
                j = j + 1
            }
        }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->

            seletedIdHairindex.clear()
            seletedIdHairindex.addAll(previousSeletedIdHairindex)
            dialog.dismiss()
        })

        val dialog = builder.create()
        dialog.show()
    }

    private fun HairColorDialog() {
        var j = 0
        val list: MutableList<String> = ArrayList()
        val idlist: MutableList<String> = ArrayList()
        val namelist: MutableList<String> = ArrayList()
        for (i in hairColorHashMap) {
            val p_id = hairColorHashMap.get(j).get("IN_ID")
            val p_name = hairColorHashMap.get(j).get("IN_NAME")
            list.add("" + p_name)
            idlist.add(p_id.toString().trim())
            namelist.add(p_name.toString().trim())
            Log.e("ALL", " @ " + p_id + " # " + " $ " + p_name)
            j = j + 1
        }
        val listItems = list.toTypedArray()
        val namelistItems = namelist.toTypedArray()
        val idlistItems = idlist.toTypedArray()
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Hair Color")
        if (!seletedIdHairColorindex.isEmpty()) {
            checkedIdHairColor =
                BooleanArray(listItems.size) { false } //this will checked the items when user open the dialog
            var K: Int = 0
            for (i in seletedIdHairColorindex) {
                Log.e("TAG", seletedIdHairColorindex[K].toString())
                checkedIdHairColor!!.set(seletedIdHairColorindex[K], true)
                K = K + 1
            }
            previousSeletedIdHairColorindex.clear()
            previousSeletedIdHairColorindex.addAll(seletedIdHairColorindex)
        }

        builder.setMultiChoiceItems(listItems,
            checkedIdHairColor,
            DialogInterface.OnMultiChoiceClickListener { dialog, indexSelected, isChecked ->
                if (isChecked) {
                    seletedIdHairColorItems.add(namelistItems[indexSelected].toString())
                    seletedIdHairColorid.add(idlistItems[indexSelected].toString())
                    seletedIdHairColorindex.add(indexSelected)

                } else if (!isChecked) {
                    seletedIdHairColorItems.remove(namelistItems[indexSelected].toString())
                    seletedIdHairColorid.remove(idlistItems[indexSelected].toString())
                    seletedIdHairColorindex.remove(indexSelected)
                }
            }).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->


            if (seletedIdHairColorItems.contains(resources.getString(R.string.others))) {
                //openOtherEnterValueDialog()
                showAlertDialogButtonClicked(
                    "Hair Color Other Value",
                    "Description",
                    binding.llOtherHaircolor,
                    binding.tvOtherHaircolor
                )
            } else {
                binding.llOtherHaircolor.visibility = View.GONE
                binding.tvOtherHaircolor.text = ""
            }

            binding.cgHaircolor.removeAllViews()
            var j: Int = 0
            for (i in seletedIdHairColorItems) {
                addChipIcon(binding.cgHaircolor, seletedIdHairColorItems[j])
                j = j + 1
            }
        }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->

            seletedIdHairColorindex.clear()
            seletedIdHairColorindex.addAll(previousSeletedIdHairColorindex)
            dialog.dismiss()
        })

        val dialog = builder.create()
        dialog.show()

    }



    private fun getGender(): String {
        var gen = ""
        if (binding.rbMale.isChecked)
            gen = "Male"
        else if (binding.rbFemale.isChecked)
            gen = "Female"
        else if (binding.rbOther.isChecked)
            gen = "Others"

        return gen
    }

    private fun getMalePrivatePC(): String {
        var mppc = ""
        if (binding.rbYes.isSelected)
            mppc = "Yes"
        else if (binding.rbNo.isSelected)
            mppc = "No"

        return mppc
    }

    private fun getPecuList(): String {
        var pecu = ""
        val pecuidbuilder = StringBuilder()
        var m: Int = 0
        for (i in PecuModelArrayList) {
            pecuidbuilder.append(PecuModelArrayList[m].toString() + ",")
            m = m + 1
        }
        pecu = pecuidbuilder.toString()

        return pecu

    }

    private fun getSpcIdnMarkList(): String {
        var sim = ""
        val simidbuilder = StringBuilder()
        var m: Int = 0
        for (i in splMarksModelArrayList) {
            simidbuilder.append(splMarksModelArrayList[m].toString() + ",")
            m = m + 1
        }
        sim = simidbuilder.toString()

        return sim

    }


    private fun getPItemList(): List<Item> {
        var pitem = ""
        val simidbuilder = StringBuilder()
        var m: Int = 0
        for (i in personalItemsArrayList.indices) {
            if(i == 0)
                simidbuilder.append(personalItemsArrayList[m].toString())
            else
                simidbuilder.append(","+ personalItemsArrayList[m].toString() )
            m = m + 1
        }
        pitem = simidbuilder.toString()

        val parts = pitem.split(",")

        // Create an ArrayList to store JSON objects
        val itemList = ArrayList<Item>()

        // Iterate through the parts and create Item objects
        for (i in 0 until parts.size step 2) {
            val item = Item(parts[i], parts[i + 1])
            itemList.add(item)
        }

        // Convert the ArrayList to JSON
        val gson = Gson()
        val jsonArray = gson.toJsonTree(itemList)

        return itemList

    }

    private fun getHairTypeList(): String {
        var hType = ""
        val hTypeidbuilder = StringBuilder()
        var m: Int = 0
        for (i in hairTypesAfterSelected) {
            hTypeidbuilder.append(hairTypesAfterSelected[m].toString() + ",")
            m = m + 1
        }
        hType = hTypeidbuilder.toString()

        return hType

    }

    private fun getPecularities(): String {
        var pecula = ""
        val hTypeidbuilder = StringBuilder()

        var j: Int = 0
        for (i in pecuLists) {
            hTypeidbuilder.append(pecuLists[j]+",")
            j = j + 1
        }
        pecula = hTypeidbuilder.toString()

        return pecula

    }

    private fun getHairColorList(): String {
        var hColor = ""
        val hColoridbuilder = StringBuilder()
        var m: Int = 0
        for (i in hairColorsAfterSelected) {
            hColoridbuilder.append(hairColorsAfterSelected[m].toString() + ",")
            m = m + 1
        }
        hColor = hColoridbuilder.toString()

        return hColor

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


        /*
                try {
                    val originalFile = File(originalFilePath)
                    val copiedFile = File(copiedFilePath)
                    val inStream = FileInputStream(originalFile)
                    val outStream = FileOutputStream(copiedFile)
                    val buffer = ByteArray(1024)
                    var read: Int
                    while (inStream.read(buffer).also { read = it } != -1) {
                        outStream.write(buffer, 0, read)
                    }
                    inStream.close()
                    outStream.flush()
                    outStream.close()
                    Log.e("testpathcopy",copiedFilePath)
                    db.addImage(copiedFilePath,type,id)
                } catch (e: IOException) {
                    e.printStackTrace()
                }*/

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

//
//


    fun addChipIcon(baseView: ChipGroup, name: String) {

        var chip = Chip(this)
        chip.text = name
        chip.isCloseIconVisible = true
        chip.minWidth = 100
        chip.isCloseIconVisible = false
        chip.isClickable = false

        // binding.chipGroupEmail.addView(chip);
        baseView.addView(chip)

    }

    private fun GetAllUnIdentifiedData() {
        val db = DatabaseDb(this, null)
        var mAPIService: APIService? = null
        progressDialogCall(this@MorgueLevelSubmitInfoActivity)
        mAPIService = ApiUtils.apiService
        SharedPreferenceStorage.getValue(applicationContext, SharedPreferenceStorage.JWT_TOKEN, "")
            ?.let {
                mAPIService.UnIdentificationGetAllDataAPI(it)
                    .enqueue(object : Callback<UnIdentificationGetAllDataAPIModel> {

                        override fun onResponse(
                            call: Call<UnIdentificationGetAllDataAPIModel>,
                            response: Response<UnIdentificationGetAllDataAPIModel>
                        ) {
                            try {
                                if (response.isSuccessful) {

                                    Log.e("LogIn_Status", response.body()!!.error_code.toString())
                                    Log.e("LogIn_Token", response.body()!!.status.toString())
                                    if (response.body()!!.status.toString().equals("success")) {
                                        val preferences =
                                            getSharedPreferences("DATA", Context.MODE_PRIVATE)
                                        val preferencesEditor = preferences.edit()
                                        preferencesEditor.putBoolean("saved", true)
                                        preferencesEditor.putString(
                                            "district",
                                            response.body()!!.district.toString()
                                        )

                                        preferencesEditor.putString(
                                            "pecularities",
                                            response.body()!!.pecularities.toString()
                                        )
                                        preferencesEditor.apply()
                                        districtArrayList = response.body()!!.district
                                        var myList: ArrayList<PSAll> = arrayListOf()
                                        /*districtArrayList.toMutableList()
                                            .add(DistrictAll(-9999, "Select", myList))*/
                                        districtArrayAdapter = ArrayAdapter(
                                            baseContext,
                                            android.R.layout.simple_spinner_dropdown_item,
                                            districtArrayList
                                        )



                                        val dsList = response.body()!!.district
                                        var j: Int = 0
                                        if (!db.CheckIsDataAlreadyInDBorNot()) {

                                            for (i in dsList) {
                                                val dsID = dsList.get(j).district_id.toString()
                                                val INNAME = dsList.get(j).district_name.trim()
                                                db.addDistrict(INNAME, dsID)
                                                Log.e("LogIn_Token", INNAME)

                                                val psList = dsList.get(j).ps
                                                var k: Int = 0
                                                for (t in psList) {
                                                    val ID = psList.get(k).ps_id.toString()
                                                    val INNAME = psList.get(k).ps_name.trim()
                                                    Log.e("LogIn_Token", INNAME)
                                                    db.addPs(INNAME, ID, dsID)
                                                    k = k + 1
                                                }
                                                j = j + 1
                                            }
                                        }
                                        val inList = response.body()!!.face
                                        j = 0
                                        for (i in inList) {
                                            val ID = inList.get(j).id.toString()
                                            val INNAME = inList.get(j).name.trim()

                                            val inListHash = java.util.HashMap<String, String>()
                                            inListHash.put("IN_ID", ID)
                                            inListHash.put("IN_NAME", INNAME)
                                            inFaceHashMap.add(inListHash)
                                            j = j + 1
                                        }


                                        val pecularitiesList = response.body()!!.pecularities
                                        var k: Int = 0
                                        for (i in pecularitiesList) {
                                            val ID = pecularitiesList.get(k).id.toString()
                                            val INNAME = pecularitiesList.get(k).name.trim()
                                            val inListHash = java.util.HashMap<String, String>()
                                            inListHash.put("IN_ID", ID)
                                            inListHash.put("IN_NAME", INNAME)
                                            pecuHashMap.add(inListHash)
                                            System.out.println(INNAME)
                                            k = k + 1
                                        }


                                        val spc_type = response.body()!!.si_type
                                        var l: Int = 0
                                        for (i in spc_type) {
                                            val ID = spc_type.get(l).id.toString()
                                            val INNAME = spc_type.get(l).name.trim()
                                            val inListHash = java.util.HashMap<String, String>()
                                            db.addSiLocType(INNAME, ID.toString())
                                            inListHash.put("IN_ID", ID)
                                            inListHash.put("IN_NAME", INNAME)
                                            spcIdTypeHashMap.add(inListHash)
                                            l = l + 1
                                        }
                                        val spcIdTypeHas = java.util.HashMap<String, String>()
                                        spcIdTypeHas.put("IN_ID", "-999")
                                        spcIdTypeHas.put("IN_NAME", "Select")
                                        spcIdTypeHashMap.add(0, spcIdTypeHas)

                                        val spc_loc = response.body()!!.si_loc
                                        var m: Int = 0
                                        for (i in spc_loc) {
                                            val ID = spc_loc.get(m).id.toString()
                                            val INNAME = spc_loc.get(m).name.trim()
                                            db.addSiLoc(INNAME, ID.toString())
                                            val inListHash = java.util.HashMap<String, String>()

                                            inListHash.put("IN_ID", ID)
                                            inListHash.put("IN_NAME", INNAME)
                                            spcIdLocHashMap.add(inListHash)
                                            m = m + 1
                                        }
                                        val inListHash = java.util.HashMap<String, String>()
                                        inListHash.put("IN_ID", "-999")
                                        inListHash.put("IN_NAME", "Select")
                                        spcIdLocHashMap.add(0, inListHash)


                                        val vhair = response.body()!!.hair
                                        var n: Int = 0
                                        for (i in vhair) {
                                            val ID = vhair.get(n).id.toString()
                                            val INNAME = vhair.get(n).name.trim()
                                            db.addHair(INNAME, ID.toString())
                                            val inListHash = java.util.HashMap<String, String>()
                                            inListHash.put("IN_ID", ID)
                                            inListHash.put("IN_NAME", INNAME)
                                            System.out.println(INNAME)
                                            hairHashMap.add(inListHash)
                                            n = n + 1
                                        }

                                        val vHairColor = response.body()!!.hair_color
                                        var o: Int = 0
                                        for (i in vHairColor) {
                                            val ID = vHairColor.get(o).id.toString()
                                            val INNAME = vHairColor.get(o).name.trim()
                                            db.addHairColor(INNAME, ID.toString())
                                            val inListHash = java.util.HashMap<String, String>()
                                            inListHash.put("IN_ID", ID)
                                            inListHash.put("IN_NAME", INNAME)
                                            System.out.println(INNAME)
                                            hairColorHashMap.add(inListHash)
                                            o = o + 1
                                        }
                                        closeProgressDialogCall()

                                    } else {
                                        Toast.makeText(
                                            this@MorgueLevelSubmitInfoActivity,
                                            response.body()!!.status.toString(),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    closeProgressDialogCall()

                                }
                            } catch (exception: java.lang.Exception) {
                                closeProgressDialogCall()
                                Toast.makeText(
                                    this@MorgueLevelSubmitInfoActivity,
                                    "Some issue in server end",
                                    Toast.LENGTH_LONG
                                ).show()

                            }
                        }

                        override fun onFailure(
                            call: Call<UnIdentificationGetAllDataAPIModel>,
                            t: Throwable
                        ) {
                            intent = Intent(baseContext, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    })
            }


    }


    private fun HairType() {
        var mAPIService: APIService? = null
        progressDialogCall(this@MorgueLevelSubmitInfoActivity)
        mAPIService = ApiUtils.apiService
        mAPIService.idhair(

        )
            .enqueue(object : Callback<HairTypeApiModel> {

                override fun onResponse(
                    call: Call<HairTypeApiModel>,
                    response: Response<HairTypeApiModel>
                ) = if (response.code() == 400) {
                    closeProgressDialogCall()
                    Toast.makeText(
                        this@MorgueLevelSubmitInfoActivity,
                        "Some issue in server end",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    try {
                        if (response.isSuccessful) {

                            if (response.body()!!.status.toString()
                                    .equals("success")
                            ) {


                                Log.e("hairType",hairTypeArray.size.toString())
                            } else {
                                showAlertDialogMessage(
                                    this@MorgueLevelSubmitInfoActivity,
                                    response.body()!!.status.toString()
                                )
                            }

                        }
                        closeProgressDialogCall()
                    } catch (exception: java.lang.Exception) {
                        Toast.makeText(
                            this@MorgueLevelSubmitInfoActivity,
                            "Some issue in server end",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

                override fun onFailure(call: Call<HairTypeApiModel>, t: Throwable) {
                    closeProgressDialogCall()
                    showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity, "SERVER ERROR")
                }
            })


    }
    private fun accordionView() {

        binding.mcvDeadBodyDetails.visibility = View.GONE
        binding.llPhotographFingerprint.visibility = View.GONE
        binding.mcvMoreDetails.visibility = View.GONE

        binding.llDeatilsBody.setOnClickListener {
            if (binding.mcvDeadBodyDetails.isVisible) {
                binding.mcvDeadBodyDetails.visibility = View.GONE
                binding.ivDeadBodyDetails.setImageResource(R.drawable.down_icon)


            } else if (binding.mcvDeadBodyDetails.isGone) {
                binding.mcvDeadBodyDetails.visibility = View.VISIBLE
                binding.ivDeadBodyDetails.setImageResource(R.drawable.up_icon)

            }
        }

        binding.llPhotographBody.setOnClickListener {
            if (binding.llPhotographFingerprint.isVisible) {
                binding.llPhotographFingerprint.visibility = View.GONE
                binding.ivDeadBodyPhoto.setImageResource(R.drawable.down_icon)


            } else if (binding.llPhotographFingerprint.isGone) {
                binding.llPhotographFingerprint.visibility = View.VISIBLE
                binding.ivDeadBodyPhoto.setImageResource(R.drawable.up_icon)

            }
        }

        binding.llMoreDetailsBody.setOnClickListener {
            if (binding.mcvMoreDetails.isVisible) {
                binding.mcvMoreDetails.visibility = View.GONE
                binding.ivMoreDetails.setImageResource(R.drawable.down_icon)


            } else if (binding.mcvMoreDetails.isGone) {
                binding.mcvMoreDetails.visibility = View.VISIBLE
                binding.ivMoreDetails.setImageResource(R.drawable.up_icon)

            }
        }
    }

    private fun HideBodyFeatures() {
        binding.tvAgeTxt.text = "Select approx age range of dead body"
        binding.tvAgeTxt.setTextColor(resources.getColor(R.color.black))

        binding.tvHeightTxt.text = "Select approx height of dead body"
        binding.tvHeightTxt.setTextColor(resources.getColor(R.color.black))
        binding.tvHeightTxt.visibility = View.GONE
        binding.llHeight.visibility = View.GONE

        binding.tvGenderTxt.text = "Gender"
        binding.tvGenderTxt.setTextColor(resources.getColor(R.color.black))

        binding.tvPhotoFaceTxt.text = "Photographs of Face"
        binding.tvPhotoFaceTxt.setTextColor(resources.getColor(R.color.black))

        binding.tvPhotobodyTxt.text = "Photographs of Body"
        binding.tvPhotobodyTxt.setTextColor(resources.getColor(R.color.black))
    }

    private fun ShowBodyFeatures() {
        binding.tvAgeTxt.text = "* Select approx age range of dead body"
        binding.tvAgeTxt.setTextColor(resources.getColor(R.color.red))
        binding.tvAgeTxt.visibility = View.VISIBLE
        binding.spnAgerange.visibility = View.VISIBLE

        binding.tvHeightTxt.text = "* Select approx height of dead body"
        binding.tvHeightTxt.setTextColor(resources.getColor(R.color.red))
        binding.tvHeightTxt.visibility = View.VISIBLE
        binding.llHeight.visibility = View.VISIBLE

        binding.tvGenderTxt.text = "* Gender"
        binding.tvGenderTxt.setTextColor(resources.getColor(R.color.red))
        binding.tvGenderTxt.visibility = View.VISIBLE
        binding.rgGender.visibility = View.VISIBLE

        binding.tvPhotoFaceTxt.text = "* Photographs of Face(Front, Left and Right Side)"
        binding.tvPhotoFaceTxt.setTextColor(resources.getColor(R.color.red))
        binding.tvPhotoFaceTxt.visibility = View.VISIBLE

        binding.tvPhotobodyTxt.text = "* Photographs of Body(Front Side and Back Side)"
        binding.tvPhotobodyTxt.setTextColor(resources.getColor(R.color.red))
        binding.llPhotoBody.visibility = View.VISIBLE
    }

    fun showPictureFaceAlertDialogMessage(activity: Activity, msg: String) {
        var alertDialog: AlertDialog? = activity.let {
            var builder = AlertDialog.Builder(it)
            builder.setIcon(R.drawable.error)
            builder.setMessage(msg)
            builder.apply {
                setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->


                        if (checkFileReadWritePermission()) {
                            /*ImagePicker.with(this@SubmitDeadBodyInformationActivity)
                                    .crop() //Crop image(Optional), Check Customization for more option
                                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                                    .maxResultSize(
                                        1080, 1080
                                    ) //Final image resolution will be less than 1080 x 1080(Optional)
                                    .start(IMAGE_REQUEST_DIFF_FACE)*/
                            val type = "FACE"
                            openPostActivity.launch(
                                CameraXActivity.getIntent(context, 1, type, bannerText)
                            )
                        }
                    })
                setNegativeButton(R.string.close,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            }
            // Create the AlertDialog
            builder.create()
        }
        alertDialog?.show()
    }

    fun showPictureBodyAlertDialogMessage(activity: Activity, msg: String) {
        var alertDialog: AlertDialog? = activity.let {
            var builder = AlertDialog.Builder(it)
            builder.setIcon(R.drawable.error)
            builder.setMessage(msg)
            builder.apply {
                setPositiveButton(R.string.ok,
                    DialogInterface.OnClickListener { dialog, id ->
                        if (checkFileReadWritePermission()) {
                            /*ImagePicker.with(this@SubmitDeadBodyInformationActivity)
                                    .crop() //Crop image(Optional), Check Customization for more option
                                    .compress(1024) //Final image size will be less than 1 MB(Optional)
                                    .maxResultSize(
                                        1080, 1080
                                    ) //Final image resolution will be less than 1080 x 1080(Optional)
                                    .start(IMAGE_REQUEST_DIFF_ANGLE)*/
                            val type = "BODY"
                            openPostActivity.launch(
                                CameraXActivity.getIntent(context, 1, type, bannerText)
                            )
                        }
                    })
                setNegativeButton(R.string.close,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            }
            // Create the AlertDialog
            builder.create()
        }
        alertDialog?.show()
    }

    fun checkFileReadWritePermission(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.READ_MEDIA_IMAGES),
                    STORAGE_PERMISSION_CODE
                )
            }
            return true
        }

        return if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    STORAGE_PERMISSION_CODE
                )
            }
            false
        } else {
            true
        }
    }
    private val openPostActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagePath = result.data?.getStringExtra("path").toString()
                val file: File = FileUtils.getFile(this, Uri.parse(imagePath))
                val imageFlag = ImageCheck(file)
                if (imageFlag == 2) {
//                    Toast.makeText(
//                        this@MorgueLevelSubmitInfoActivity,
//                        "Please Capture Clear Image ",
//                        Toast.LENGTH_LONG
//                    ).show()
                    showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,
                        "Please Capture Clear Image ")

                }
                if (imageFlag == 3) {
//                    Toast.makeText(
//                        this@MorgueLevelSubmitInfoActivity,
//                        "Please Capture Bright Image ",
//                        Toast.LENGTH_LONG
//                    ).show()show
                    showAlertDialogMessage(this@MorgueLevelSubmitInfoActivity,
                        "Please Capture Bright Image ")


                }
                if (imageFlag == 1) {
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

                    Log.e("SANKHA", imagePath.toString())
                    Log.e("SANKHA", imageCategory.toString())
                    Log.e("SANKHA", faceImage.size.toString())
                    if (imageCategory.equals("WEARINGAPP")) {
                        binding.btnPicClothUpload.visibility = View.VISIBLE
                        wApparelsImage.add(imagePath)
                        binding.llPicWa.addView(inflater)
                        img_close.setOnClickListener {
                            wApparelsImage.remove(imagePath)

                            binding.llPicWa.removeView(inflater)
                            // (binding.llPicWa.parent as ViewGroup).removeView(binding.llPicWa)
                        }
                    } else if (imageCategory.equals("FOOTWARE")) {

                        footwareApparelsImage.add(imagePath)
                        binding.btnPicFootwearUpload.visibility = View.VISIBLE
                        binding.llFootwarePic.addView(inflater)
                        img_close.setOnClickListener {
                            footwareApparelsImage.remove(imagePath)

                            binding.llFootwarePic.removeView(inflater)
                            //(binding.llFootwarePic.parent as ViewGroup).removeView(binding.llFootwarePic)
                        }
                    } else if (imageCategory.equals("OTHERS")) {
                        othersImage.add(imagePath)
                        binding.btnPicOthersUpload.visibility = View.VISIBLE
                        binding.llPicOthers.addView(inflater)
                        img_close.setOnClickListener {
                            othersImage.remove(imagePath)

                            binding.llPicOthers.removeView(inflater)
                            //(binding.llPicOthers.parent as ViewGroup).removeView(binding.llPicOthers)
                        }
                    } else if (imageCategory.equals("FACE")) {

                        val options = FaceDetectorOptions.Builder()
                            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                            .setMinFaceSize(0.15f)
                            .enableTracking()
                            .build()
                        val detector = FaceDetection.getClient(options)
                        val image: InputImage
                        try {
                            image = InputImage.fromFilePath(this, Uri.parse(uri))
                            val result = detector.process(image)
                                .addOnSuccessListener { faces ->
                                    if (faces.isEmpty())
                                        Toast.makeText(this, "Not a Face image", Toast.LENGTH_SHORT)
                                            .show()
                                    else {
                                        faceImage.add(imagePath)
                                        binding.llPicFace.addView(inflater)
                                        img_close.setOnClickListener {
                                            faceImage.remove(imagePath)
                                            binding.llPicFace.removeView(inflater)
                                            // binding.llPicFace.removeAllViews()
                                            // (binding.llPicFace.getParent() as ViewGroup).removeView(binding.llPicFace)
                                        }

                                    }
                                }
                                .addOnFailureListener { e ->
                                    // Task failed with an exception
                                    // ...
                                }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        binding.btnPicFaceUoload.visibility = View.VISIBLE
                    } else if (imageCategory.equals("BODY")) {


                        bodyImage.add(imagePath)
                        binding.llPicBody.addView(inflater)
                        img_close.setOnClickListener {
                            bodyImage.remove(imagePath)
                            //binding.llPicBody.removeAllViews()
                            binding.llPicBody.removeView(inflater)
                            //(binding.llPicBody.parent as ViewGroup).removeView(binding.llPicBody)
                        }
                        binding.btnPicBodyUoload.visibility = View.VISIBLE
                    } else if (imageCategory.equals("PERSONALITEM")) {
                        pItemImage.add(imagePath)
                        binding.llPersonalItemsOfTheBody.addView(
                            inflater,
                            binding.llPersonalItemsOfTheBody.childCount
                        )


                        img_close.setOnClickListener {
                            pItemImage.remove(imagePath)
                            // binding.llPersonalItemsOfTheBody.removeAllViews()
                            binding.llPersonalItemsOfTheBody.removeView(inflater)
//                        (binding.llPersonalItemsOfTheBody.parent as ViewGroup).removeView(
//                            binding.llPersonalItemsOfTheBody
//                        )
                        }
                    } else if (imageCategory.equals("SPECLMARKS")) {
                        SpecialMarksImage.add(imagePath)
                        binding.llSpcIdenMarks.addView(inflater)
                        img_close.setOnClickListener {
                            SpecialMarksImage.remove(imagePath)
                            // binding.llSpcIdenMarks.removeAllViews()
                            binding.llSpcIdenMarks.removeView(inflater)
                            //(binding.llSpcIdenMarks.parent as ViewGroup).removeView(binding.llSpcIdenMarks)
                        }
                    } else if (imageCategory.equals("IDENTIMARKS")) {
                        visibleIdMarksImage.add(imagePath)
                        Log.e("JOYIMAGE", imagePath)


                        binding.llIdenticalMarksPic.addView(inflater)
                        img_close.setOnClickListener {
                            visibleIdMarksImage.remove(imagePath)
                            //binding.llIdenticalMarksPic.removeAllViews()
                            binding.llIdenticalMarksPic.removeView(inflater)
                            //(binding.llIdenticalMarksPic.parent as ViewGroup).removeView(binding.llIdenticalMarksPic)
                        }
                    }
                    Log.e("SANKHA", faceImage.size.toString())
                }
            }
        }

    fun showAlertDialogButtonClicked(
        title: String,
        hintText: String,
        parent_layout: LinearLayout,
        textView: TextView
    ) {
        // Create an alert builder
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.custom_alert_layout, null)
        builder.setView(customLayout)

        val til_other_description =
            customLayout.findViewById<TextInputLayout>(R.id.til_other_description)
        val tiet_other_description =
            customLayout.findViewById<TextInputEditText>(R.id.tiet_other_description)
        val tv_title = customLayout.findViewById<TextView>(R.id.tv_title)
        val submitButton = customLayout.findViewById<MaterialButton>(R.id.btn_submit)
        tv_title.text = title
        til_other_description.hint = hintText
        submitButton.setOnClickListener()
        {
            if (tiet_other_description.text!!.isEmpty())
                showToastMessage("Cannot Empty")
            else {
                parent_layout.visibility = View.VISIBLE
                textView.visibility = View.VISIBLE
                textView.text = tiet_other_description.text.toString().trim()
                dialog.dismiss()
            }

        }

        dialog = builder.create()
        dialog.show()
    }

    fun showToastMessage(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }
    fun setTestValue() {



//        binding.tietAge.setText("32")
        binding.rbOther.isSelected = true
//        binding.tietDetailsOfInjuryMark.setText("Buke lal lal dag ache")
//        binding.tietWearingApparels.setText("Lungi with fata pant")
        binding.rbDecomposed.isSelected = true
    }

    fun showHeightSelectionDialogButtonClicked(
        title: String,
        hintText: String?,
        parent_layout: LinearLayout?, textView: TextView?
    ) {
        // Create an alert builder
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
       // builder.setCancelable(false)
        // set the custom layout
        val customLayout: View =
            layoutInflater.inflate(R.layout.custom_height_seek_alert_layout, null)
        builder.setView(customLayout)

        val sk_bar_feet = customLayout.findViewById<SeekBar>(R.id.sk_bar_feet)
        val sk_bar_inch = customLayout.findViewById<SeekBar>(R.id.sk_bar_inch)
        val til_other_description =
            customLayout.findViewById<TextInputLayout>(R.id.til_other_description)
        val tiet_other_description =
            customLayout.findViewById<TextInputEditText>(R.id.tiet_other_description)
        val tv_title = customLayout.findViewById<TextView>(R.id.tv_title)
        val tv_description = customLayout.findViewById<TextView>(R.id.tv_description)
        val tv_progress_inch = customLayout.findViewById<TextView>(R.id.tv_progress_inch)
        val tv_progress_feet = customLayout.findViewById<TextView>(R.id.tv_progress_feet)
        val submitButton = customLayout.findViewById<MaterialButton>(R.id.btn_submit)




        sk_bar_feet?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                //  Toast.makeText(applicationContext, "seekbar progress: $progress", Toast.LENGTH_SHORT).show()
                tv_progress_feet.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //    Toast.makeText(applicationContext, "seekbar touch started!", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //   Toast.makeText(applicationContext, "seekbar touch stopped!", Toast.LENGTH_SHORT).show()
            }
        })

        sk_bar_inch?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                //  Toast.makeText(applicationContext, "seekbar progress: $progress", Toast.LENGTH_SHORT).show()
                tv_progress_inch.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //    Toast.makeText(applicationContext, "seekbar touch started!", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //   Toast.makeText(applicationContext, "seekbar touch stopped!", Toast.LENGTH_SHORT).show()
            }
        })



        tv_title.text = title
        til_other_description.hint = hintText
        submitButton.setOnClickListener()
        {

//            if (tv_progress_feet.text.isEmpty() || tv_progress_inch.text.isEmpty()) {
//                showToastMessage("Invalid")
//            } else
                if (tv_progress_feet.text.toString().toInt() == 0) {
                showToastMessage("Invalid height")
            } else {

                binding.tvHeight.text = tv_progress_feet.text.toString() + "." + tv_progress_inch.text.toString() + ""
                height_feet = tv_progress_feet.text.toString().trim()
                height_inch = tv_progress_inch.text.toString().trim()

                /*  parent_layout.visibility=View.VISIBLE
                  textView.visibility=View.VISIBLE
                  textView.text= tiet_other_description.text.toString().trim()
            */      dialog.dismiss()
            }

        }


        // create and show the alert dialog
        //  val dialog = builder.create()
        dialog = builder.create()
        dialog.show()
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



            }

    }

    fun closeProgressDialogCall() {
        if (progressDialog != null) {
            progressDialog!!.cancel()

        }

    }

    fun progressDialogCall(activity: Activity) {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.show()
    }

    override fun onItemSelected(adpterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (adpterView?.id) {
            R.id.spinner_occurrence_dist -> {
                val district = districtArrayList[position]
                val policeStationArrayAdapter: ArrayAdapter<PSAll> = ArrayAdapter<PSAll>(
                    baseContext, android.R.layout.simple_spinner_dropdown_item, district.ps
                )

            }

            else -> {
                showToastMessage("Joy")
            }

        }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    fun checkValidation(): Boolean {
        var noError = true
        if (Wearingapp == 0) {
            binding.tvPhotowearappTxt.isFocusableInTouchMode = true
            binding.tvPhotowearappTxt.requestFocus()
            showToastMessage("Please select Wearing Apparels")
            noError = false
        }
//         else if (Wearingapp == 1 && wApparelsImage.size == 0) {
//            binding.tvPhotowearappTxt.isFocusableInTouchMode = true
//            binding.tvPhotowearappTxt.requestFocus()
//            showToastMessage("Please choose Wearing Apparels photo")
//            noError = false
//        }
        else if (FootwareApp == 0) {
            binding.tvFootwareTxt.isFocusableInTouchMode = true
            binding.tvFootwareTxt.requestFocus()
            showToastMessage("Please select Footwear")
            noError = false
        }
//         else if (FootwareApp == 1 && footwareApparelsImage.size == 0) {
//            binding.tvFootwareTxt.isFocusableInTouchMode = true
//            binding.tvFootwareTxt.requestFocus()
//            showToastMessage("Please choose Footwear photo")
//            noError = false
//        }
        else if (generalCondition == 1) {
            if (AgeRange == 0) {
                binding.spnAgerange.isFocusableInTouchMode = true
                binding.spnAgerange.requestFocus()
                showToastMessage("Please select approx age range of dead body")
                noError = false
            } else if (!binding.rbMale.isChecked && !binding.rbFemale.isChecked && !binding.rbOther.isChecked) {
                binding.rgGender.isFocusableInTouchMode = true
                binding.rgGender.requestFocus()
                showToastMessage("Please select Gender")
                noError = false
            }
            /*else if (faceImage.size < 3) {
                binding.llPicFace.isFocusableInTouchMode = true
                binding.llPicFace.requestFocus()
                showToastMessage("Please choose Front, Left and right side of face photo ")
                noError = false
            }*/
            else if (bodyImage.size < 2) {
                binding.llPicBody.isFocusableInTouchMode = true
                binding.llPicBody.requestFocus()
                showToastMessage("Please choose Front and Back side photo")
                noError = false
            }



        }
        else if(AgeRange == 0) {
            binding.spnAgerange.isFocusableInTouchMode = true
            binding.spnAgerange.requestFocus()
            showToastMessage("Please select approx age range of dead body")
            noError = false
        }
//        else if(binding.spnAgerange.selectedItem.toString().trim().equals("Select")){
//             binding.spnAgerange.isFocusableInTouchMode = true
//             binding.spnAgerange.requestFocus()
//             showToastMessage("Please Age range.")
//             noError = false
//         }


        return noError

    }
}