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
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.wbmissingfound.DBHelper.DatabaseDb
import com.example.wbmissingfound.Helper.Constants
import com.example.wbmissingfound.Model.PecuMarksModel
import com.example.wbmissingfound.Model.PersonalItemsModel
import com.example.wbmissingfound.Model.SpecialIDMarksModel
import com.example.wbmissingfound.RetroClient.RetroApi.APIService
import com.example.wbmissingfound.RetroClient.RetroApi.ApiUtils
import com.example.wbmissingfound.RetroClient.RetroModel.*
import com.example.wbmissingfound.custom.DatePicker
import com.example.wbmissingfound.databinding.ActivitySubmitDeadBodyInformationBinding
import com.example.wbmissingfound.sharedStorage.SharedPreferenceStorage
import com.example.wbmissingfound.utils.FileUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.*
import id.zelory.compressor.Compressor
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.IOException
import java.util.ArrayList


class SubmitDeadBodyInformationActivity : BaseActivity(), CompoundButton.OnCheckedChangeListener,
    AdapterView.OnItemSelectedListener {
    var form_status = 0

    private var generalCondition: Int = 0
    private var used_current_loc: Boolean = false
    var jwt_token: String = ""
    var bannerText: String = ""
    private val permissionId = 2
    var pecuid: String = ""

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private var progressDialog: ProgressDialog? = null


    private lateinit var binding: ActivitySubmitDeadBodyInformationBinding
    private val CAMERA_PERMISSION_CODE = 100
    private val STORAGE_PERMISSION_CODE = 101
    private val IMAGE_REQUEST = 324

    private val IMAGE_REQUEST_IDENTICAL_MARKS = 325
    private val IMAGE_REQUEST_PERSONAL_ITEM = 326
    private val IMAGE_REQUEST_DIFF_FACE = 327
    private val IMAGE_REQUEST_DIFF_ANGLE = 328
    private val IMAGE_REQUEST_DIFF_WAPP = 329
    private val IMAGE_REQUEST_FOOTWARE = 330
    private val IMAGE_REQUEST_OTHERS = 331
    private val IMAGE_REQUEST_SPECIALMARKS = 332

    private var imageCount = 0


    private val specialIDMarksModelArryList = ArrayList<SpecialIDMarksModel>()

    private val visibleIdMarksImage = ArrayList<String>()
    private val pItemImage = ArrayList<String>()
    private val faceImage = ArrayList<String>()
    private val bodyImage = ArrayList<String>()
    private val wApparelsImage = ArrayList<String>()
    private val footwareApparelsImage = ArrayList<String>()
    private val othersImage = ArrayList<String>()
    private val SpecialMarksImage = ArrayList<String>()

    /*private val fingerImage = ArrayList<String>()
    private val topWearApparelsImage = ArrayList<String>()
    private val bottomWearApparelsImage = ArrayList<String>()
    private val undergarmentsApparelsImage = ArrayList<String>()*/


    var districtArrayList: List<DistrictAll> = ArrayList()
    var districtArrayAdapter: ArrayAdapter<DistrictAll>? = null

    var inFirstHashMap = ArrayList<HashMap<String, String>>()
    var inNoseHashMap = ArrayList<HashMap<String, String>>()
    var spcIdntiHashMap = ArrayList<HashMap<String, String>>()

    var pecuHashMap = ArrayList<HashMap<String, String>>()
    var inFaceHashMap = ArrayList<HashMap<String, String>>()
    var spcIdTypeHashMap = ArrayList<HashMap<String, String>>()
    var spcIdLocHashMap = ArrayList<HashMap<String, String>>()
    var hairHashMap = ArrayList<HashMap<String, String>>()
    var hairColorHashMap = ArrayList<HashMap<String, String>>()

    var checkedPecu: BooleanArray? = null
    val seletedpecuItems = arrayListOf<String>()
    val seletedpecuid = arrayListOf<String>()
    val seletedpecuindex = arrayListOf<Int>()
    val previousSeletedpecuindex = arrayListOf<Int>()

    var checkedIdFirst: BooleanArray? = null
    val seletedIdFirstItems = arrayListOf<String>()
    val seletedIdFirstid = arrayListOf<String>()
    val seletedIdFirstindex = arrayListOf<Int>()
    val previousSeletedIdFirstindex = arrayListOf<Int>()

    var checkedIdFace: BooleanArray? = null
    val seletedIdFaceItems = arrayListOf<String>()
    val seletedIdFaceid = arrayListOf<String>()
    val seletedIdFaceindex = arrayListOf<Int>()
    val previousSeletedIdFaceindex = arrayListOf<Int>()

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

    var age_from: String? = null
    var age_to: String? = null
    lateinit var valStrID: String
    lateinit var valStr: String
    lateinit var valStrLocationID: String
    lateinit var valStrLocation: String
    var valTypeId = ""
    var valTypeColor = ""

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

    var _imageUri: Uri? = null

    companion object {
        private const val FIRST_ACTIVITY_REQUEST_CODE = 1
        private const val SECOND_ACTIVITY_REQUEST_CODE = 2
        const val PATH = "path"
        const val TYPE = "type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_submit_dead_body_information)
        // as we have only one layout activity_main.xml
        binding = ActivitySubmitDeadBodyInformationBinding.inflate(layoutInflater)

        // binding.root returns the root layout,
        // which is activity_main.xml file itself
        setContentView(binding.root)
        //  binding=DataBindingUtil.setContentView(this,R.layout.activity_submit_dead_body_information)

        supportActionBar?.title = "Un-identified Form"

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
            Toast.makeText(this, "no data", Toast.LENGTH_SHORT).show()
        }

        val db = DatabaseDb(this, null)

        if (db.CheckIsDataAlreadyInDBorNot()) {
            val cursor = db.getDistrict()

            val columnsQty = cursor!!.columnCount
            cursor.moveToFirst()
            var j: Int = 0

            while (cursor.moveToNext()) {
                var id = cursor.getString(0)
                var name = cursor.getString(1)

                val cursorps = db.getPsdata(Integer.parseInt(id))
                var psArrayList: List<PSAll> = ArrayList()
                cursorps!!.moveToFirst()
                while (cursorps.moveToNext()) {
                    var id = cursorps.getString(0)
                    var name = cursorps.getString(1)
                    psArrayList += PSAll(Integer.parseInt(id), name)
                }

                districtArrayList += DistrictAll(
                    Integer.parseInt(cursor.getString(0)),
                    cursor.getString(1),
                    psArrayList
                )
                j = j + 1
            }


            districtArrayAdapter = ArrayAdapter(
                baseContext,
                android.R.layout.simple_spinner_dropdown_item,
                districtArrayList
            )

            binding.spinnerOccurrenceDist.adapter = districtArrayAdapter

            val spc_type = db.getSiLoctype()
            var l: Int = 0
            spc_type!!.moveToFirst()
            do {
                val ID = spc_type.getString(0)
                val INNAME = spc_type.getString(1)
                val inListHash = java.util.HashMap<String, String>()
                inListHash.put("IN_ID", ID)
                inListHash.put("IN_NAME", INNAME)
                spcIdTypeHashMap.add(inListHash)
            } while (spc_type.moveToNext())
            val spcIdTypeHas = java.util.HashMap<String, String>()
            spcIdTypeHas.put("IN_ID", "-999")
            spcIdTypeHas.put("IN_NAME", "Select")
            spcIdTypeHashMap.add(0, spcIdTypeHas)

            val spc_loc = db.getSiLoc()
            var m: Int = 0
            spc_loc!!.moveToFirst()
            do {
                val ID = spc_loc.getString(0)
                val INNAME = spc_loc.getString(1)
                val inListHash = java.util.HashMap<String, String>()

                inListHash.put("IN_ID", ID)
                inListHash.put("IN_NAME", INNAME)
                spcIdLocHashMap.add(inListHash)
                m = m + 1
            } while (spc_loc.moveToNext())
            val inListHash = java.util.HashMap<String, String>()
            inListHash.put("IN_ID", "-999")
            inListHash.put("IN_NAME", "Select")
            spcIdLocHashMap.add(0, inListHash)


            val vhair = db.getHair()
            var n: Int = 0
            vhair!!.moveToFirst()
            do {
                val ID = vhair.getString(0)
                val INNAME = vhair.getString(1)
                val inListHash = java.util.HashMap<String, String>()
                inListHash.put("IN_ID", ID)
                inListHash.put("IN_NAME", INNAME)
                System.out.println(INNAME)
                hairHashMap.add(inListHash)
                n = n + 1
            } while (vhair.moveToNext())

            val vHairColor = db.getHairColor()
            var o: Int = 0
            vHairColor!!.moveToFirst()
            do {
                val ID = vHairColor.getString(0)
                val INNAME = vHairColor.getString(1)
                val inListHash = java.util.HashMap<String, String>()
                inListHash.put("IN_ID", ID)
                inListHash.put("IN_NAME", INNAME)

                hairColorHashMap.add(inListHash)
                o = o + 1
            } while (vHairColor.moveToNext())
        }
        else {
            GetAllUnIdentifiedData()
            Toast.makeText(this, "data not exist", Toast.LENGTH_SHORT).show()

        }
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

        binding.spinnerOccurrenceDist.onItemSelectedListener = this

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

        binding.tietPersonalItemsOfTheBody.setOnClickListener {
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

        binding.ivCaseDeatlsArrow.setOnClickListener {


            if (binding.mcvCaseDetails.isVisible) {
                binding.mcvCaseDetails.visibility = View.GONE
                display_icon_with_animation(binding.ivCaseDeatlsArrow, R.drawable.arrow_down)


            } else {
                binding.mcvCaseDetails.visibility = View.VISIBLE
                display_icon_with_animation(binding.ivCaseDeatlsArrow, R.drawable.arrow_up)

            }
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

            if (faceImage.size == 0) {
                showPictureFaceAlertDialogMessage(
                    this@SubmitDeadBodyInformationActivity,
                    "Please Capture Photo of Front Side of Face"
                )
                bannerText = "Please Capture Photo of Front Side of Face"
            } else if (faceImage.size == 1) {
                showPictureFaceAlertDialogMessage(
                    this@SubmitDeadBodyInformationActivity,
                    "Please Capture Photo of Left Side of Face"
                )
                bannerText = "Please Capture Photo of Left Side of Face"
            } else if (faceImage.size == 2) {
                showPictureFaceAlertDialogMessage(
                    this@SubmitDeadBodyInformationActivity,
                    "Please Capture Photo of Right Side of Face"
                )
                bannerText = "Please Capture Photo of Right Side of Face"
            } else if (faceImage.size > 3) {
                showPictureFaceAlertDialogMessage(
                    this@SubmitDeadBodyInformationActivity,
                    "Please Capture Photo of Other Angle of Face"
                )
                bannerText = "Please Capture Photo of Other Angle of Face"
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
                    this@SubmitDeadBodyInformationActivity,
                    "Please Capture Photo of Front Side of Dead Body"
                )
                bannerText = "Please Capture Photo of Front Side of Dead Body"
            } else if (bodyImage.size == 1) {
                showPictureBodyAlertDialogMessage(
                    this@SubmitDeadBodyInformationActivity,
                    "Please Capture Photo of Back Side of Dead Body"
                )
                bannerText = "Please Capture Photo of Back Side of Dead Body"
            } else if (bodyImage.size == 2) {
                showPictureBodyAlertDialogMessage(
                    this@SubmitDeadBodyInformationActivity,
                    "Please Capture Photo of Left Side of Dead Body"
                )
                bannerText = "Please Capture Photo of Left Side of Dead Body"
            } else if (bodyImage.size == 3) {
                showPictureBodyAlertDialogMessage(
                    this@SubmitDeadBodyInformationActivity,
                    "Please Capture Photo of Right Side of Dead Body"
                )
                bannerText = "Please Capture Photo of Right Side of Dead Body"
            } else if (bodyImage.size > 4) {
                showPictureBodyAlertDialogMessage(
                    this@SubmitDeadBodyInformationActivity,
                    "Please Capture Photo of Other Angle of Dead Body"
                )
                bannerText = "Please Capture Photo of Other Angle of Dead Body"
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

        binding.llTvCaseDate.setOnClickListener {
            val myCase = DatePicker(
                this@SubmitDeadBodyInformationActivity,
                binding.tvCaseDate,
                binding.tvCaseDate.text.toString()
            )
            myCase.selectDate()
            myCase.setFutureDateEnable(false)
        }

        binding.tvSelecthair.setOnClickListener {
            HairTypeDialog()
        }

        binding.tvSelecthaircolor.setOnClickListener {
            HairColorDialog()
        }

        binding.tvSelectidnose.setOnClickListener {
//            IdNoseDialog()
        }
        binding.btnSubmit.setOnClickListener {
            Toast.makeText(this,"No internate", Toast.LENGTH_SHORT).show()
           if (checkValidation())
                SubmitUDData()
        }

        if (generalCondition == 0) {
            HideBodyFeatures()
        }

        binding.rbGeneralCondition.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rb_complete_body -> {
                    generalCondition = 1
                    ShowBodyFeatures()
                }

                R.id.rb_incomplete_body -> {
                    generalCondition = 2
                    HideBodyFeatures()
                }

                R.id.rb_decomposed -> {
                    generalCondition = 3
                    HideBodyFeatures()
                }

                R.id.rb_partially_skeletonized -> {
                    generalCondition = 4
                    HideBodyFeatures()
                }

                R.id.rb_fully_skeletonized -> {
                    generalCondition = 5
                    HideBodyFeatures()
                }

                R.id.rb_burnt -> {
                    generalCondition = 6
                    HideBodyFeatures()
                }
            }
        }


        if (Constants.TEST.TESTING)
            setTestValue()

    }

    private fun accordionView() {
        binding.mcvCaseDetails.visibility = View.GONE
        binding.mcvDeadBodyDetails.visibility = View.GONE
        binding.llPhotographFingerprint.visibility = View.GONE
        binding.mcvMoreDetails.visibility = View.GONE

        binding.ivCaseDetails.setOnClickListener {
            if (binding.mcvCaseDetails.isVisible) {
                binding.mcvCaseDetails.visibility = View.GONE
                binding.ivCaseDetails.setImageResource(R.drawable.down_icon)


            } else if (binding.mcvCaseDetails.isGone) {
                binding.mcvCaseDetails.visibility = View.VISIBLE
                binding.ivCaseDetails.setImageResource(R.drawable.up_icon)

            }
        }

        binding.ivDeadBodyDetails.setOnClickListener {
            if (binding.mcvDeadBodyDetails.isVisible) {
                binding.mcvDeadBodyDetails.visibility = View.GONE
                binding.ivDeadBodyDetails.setImageResource(R.drawable.down_icon)


            } else if (binding.mcvDeadBodyDetails.isGone) {
                binding.mcvDeadBodyDetails.visibility = View.VISIBLE
                binding.ivDeadBodyDetails.setImageResource(R.drawable.up_icon)

            }
        }

        binding.ivDeadBodyPhoto.setOnClickListener {
            if (binding.llPhotographFingerprint.isVisible) {
                binding.llPhotographFingerprint.visibility = View.GONE
                binding.ivDeadBodyPhoto.setImageResource(R.drawable.down_icon)


            } else if (binding.llPhotographFingerprint.isGone) {
                binding.llPhotographFingerprint.visibility = View.VISIBLE
                binding.ivDeadBodyPhoto.setImageResource(R.drawable.up_icon)

            }
        }

        binding.ivMoreDetails.setOnClickListener {
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


    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.15f)
            .enableTracking()
            .build()
        val detector = FaceDetection.getClient(options)
        if (requestCode == IMAGE_REQUEST_IDENTICAL_MARKS && resultCode == Activity.RESULT_OK) {
            imageCount++
            val inflater = LayoutInflater.from(this).inflate(R.layout.custom_imageview_layout, null)
            val img_main = inflater.findViewById<ImageView>(R.id.img_photo)
            val img_close = inflater.findViewById<ImageButton>(R.id.btn_close)
            val uri: Uri = data?.data!!
            img_main.setImageURI(uri)
            visibleIdMarksImage.add(data.data!!.path.toString())
            img_close.setOnClickListener {
                Log.e("delete click","click")
                visibleIdMarksImage.remove(data.data!!.path.toString())
                binding.llIdenticalMarksPic.removeView(inflater)
                Log.e("delete click",visibleIdMarksImage.size.toString())
            }
            binding.llIdenticalMarksPic.addView(inflater, binding.llIdenticalMarksPic.childCount)

        } else if (requestCode == IMAGE_REQUEST_PERSONAL_ITEM && resultCode == Activity.RESULT_OK) {
            imageCount++
            val inflater = LayoutInflater.from(this).inflate(R.layout.custom_imageview_layout, null)
            val img_main = inflater.findViewById<ImageView>(R.id.img_photo)
            val img_close = inflater.findViewById<ImageButton>(R.id.btn_close)
            val uri: Uri = data?.data!!
            img_main.setImageURI(uri)
            pItemImage.add(data.data!!.path.toString())
            img_close.setOnClickListener {
                Log.e("delete click","click")
                pItemImage.remove(data.data!!.path.toString())
                binding.llPersonalItemsOfTheBody.removeView(inflater)
                Log.e("delete click",pItemImage.size.toString())
                binding.cgPitem.removeAllViews()
                piselectedLists.removeAt(binding.llPersonalItemsOfTheBody.childCount)
                var j: Int = 0
                for (i in piselectedLists) {
                    addChipIcon(binding.cgPitem, piselectedLists[j])
                    j = j + 1
                }
            }
            binding.llPersonalItemsOfTheBody.addView(inflater, binding.llPersonalItemsOfTheBody.childCount)

        } else if (requestCode == IMAGE_REQUEST_DIFF_FACE && resultCode == Activity.RESULT_OK) {
            imageCount++
            val inflater = LayoutInflater.from(this).inflate(R.layout.custom_imageview_layout, null)
            val img_main = inflater.findViewById<ImageView>(R.id.img_photo)
            val img_close = inflater.findViewById<ImageButton>(R.id.btn_close)
            val uri: Uri = data?.data!!
            img_main.setImageURI(uri)
            faceImage.add(data.data!!.path.toString())
            img_close.setOnClickListener {
                Log.e("delete click","click")
                faceImage.remove(data.data!!.path.toString())
                binding.llPicFace.removeView(inflater)
                Log.e("delete click",faceImage.size.toString())
            }
            binding.llPicFace.addView(inflater, binding.llPicFace.childCount)

        } else if (requestCode == IMAGE_REQUEST_DIFF_ANGLE && resultCode == Activity.RESULT_OK) {
            imageCount++
            val selectedImageView = ImageView(this@SubmitDeadBodyInformationActivity)
            val lp = LinearLayout.LayoutParams(200, 200)
            selectedImageView.layoutParams = lp
            val uri: Uri = data?.data!!
            selectedImageView.setImageURI(uri)
            bodyImage.add(data.data!!.path.toString())
            binding.llPicBody.addView(selectedImageView)

        } else if (requestCode == IMAGE_REQUEST_DIFF_WAPP && resultCode == Activity.RESULT_OK) {
            imageCount++
            val inflater = LayoutInflater.from(this).inflate(R.layout.custom_imageview_layout, null)
            val img_main = inflater.findViewById<ImageView>(R.id.img_photo)
            val img_close = inflater.findViewById<ImageButton>(R.id.btn_close)
            val uri: Uri = data?.data!!
            img_main.setImageURI(uri)
            wApparelsImage.add(data.data!!.path.toString())
            img_close.setOnClickListener {
                Log.e("delete click","click")
                wApparelsImage.remove(data.data!!.path.toString())
                binding.llPicWa.removeView(inflater)
                Log.e("delete click",wApparelsImage.size.toString())
            }
            binding.llPicWa.addView(inflater, binding.llPicWa.childCount)

        } else if (requestCode == IMAGE_REQUEST_FOOTWARE && resultCode == Activity.RESULT_OK) {
            imageCount++
            val inflater = LayoutInflater.from(this).inflate(R.layout.custom_imageview_layout, null)
            val img_main = inflater.findViewById<ImageView>(R.id.img_photo)
            val img_close = inflater.findViewById<ImageButton>(R.id.btn_close)
            val uri: Uri = data?.data!!
            img_main.setImageURI(uri)
            footwareApparelsImage.add(data.data!!.path.toString())
            img_close.setOnClickListener {
                Log.e("delete click","click")
                footwareApparelsImage.remove(data.data!!.path.toString())
                binding.llFootwarePic.removeView(inflater)
                Log.e("delete click",footwareApparelsImage.size.toString())
            }
            binding.llFootwarePic.addView(inflater, binding.llFootwarePic.childCount)

        } else if (requestCode == IMAGE_REQUEST_OTHERS && resultCode == Activity.RESULT_OK) {
            imageCount++
            val inflater = LayoutInflater.from(this).inflate(R.layout.custom_imageview_layout, null)
            val img_main = inflater.findViewById<ImageView>(R.id.img_photo)
            val img_close = inflater.findViewById<ImageButton>(R.id.btn_close)
            val uri: Uri = data?.data!!
            img_main.setImageURI(uri)
            othersImage.add(data.data!!.path.toString())
            img_close.setOnClickListener {
                Log.e("delete click","click")
                othersImage.remove(data.data!!.path.toString())
                binding.llPicOthers.removeView(inflater)
                Log.e("delete click",othersImage.size.toString())
            }
            binding.llPicOthers.addView(inflater, binding.llPicOthers.childCount)

        } else if (requestCode == IMAGE_REQUEST_SPECIALMARKS && resultCode == Activity.RESULT_OK) {
            imageCount++
            val inflater = LayoutInflater.from(this).inflate(R.layout.custom_imageview_layout, null)
            val img_main = inflater.findViewById<ImageView>(R.id.img_photo)
            val img_close = inflater.findViewById<ImageButton>(R.id.btn_close)
            val uri: Uri = data?.data!!
            img_main.setImageURI(uri)
            SpecialMarksImage.add(data.data!!.path.toString())
            img_close.setOnClickListener {
                Log.e("delete click","click")
                SpecialMarksImage.remove(data.data!!.path.toString())
                binding.llSpcIdenMarks.removeView(inflater)
                Log.e("delete click",SpecialMarksImage.size.toString())
                binding.cgSpcIden.removeAllViews()
                selectedLists.removeAt(binding.llSpcIdenMarks.childCount)
                var j: Int = 0
                for (i in selectedLists) {
                    addChipIcon(binding.cgSpcIden, selectedLists[j])
                    j = j + 1
                }
            }
            binding.llSpcIdenMarks.addView(inflater, binding.llSpcIdenMarks.childCount)

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }*/

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView!!.id == R.id.ckb_case_details) {
            if (isChecked) {
                binding.mcvCaseDetails.visibility = View.VISIBLE
            } else {
                binding.mcvCaseDetails.visibility = View.GONE

            }
        } else if (buttonView.id == R.id.ckb_dead_body_details) {
            if (isChecked) binding.mcvDeadBodyDetails.visibility = View.VISIBLE
            else binding.mcvDeadBodyDetails.visibility = View.GONE
        } else if (buttonView.id == R.id.ckb_photograph_fingerprint) {
            if (isChecked) {
                binding.llPhotographFingerprint.visibility = View.VISIBLE
            } else {
                binding.llPhotographFingerprint.visibility = View.GONE
            }

        } else if (buttonView.id == R.id.ckb_fingerprint) {
            var ll_fingerprint = findViewById<LinearLayout>(R.id.ll_fingerprint)
            if (isChecked) {
                ll_fingerprint.visibility = View.VISIBLE
            } else {
                ll_fingerprint.visibility = View.GONE
            }

        } else if (buttonView.id == R.id.ckb_more_details) {
            var ll_more_details = findViewById<LinearLayout>(R.id.ll_more_details)
            if (isChecked) {
                binding.mcvMoreDetails.visibility = View.VISIBLE
            } else {
                binding.mcvMoreDetails.visibility = View.GONE
            }

        }

    }


    override fun onItemSelected(adpterView: AdapterView<*>?, view: View?, position: Int, id: Long) {

        when (adpterView?.id) {
            R.id.spinner_occurrence_dist -> {
                val district = districtArrayList[position]
                val policeStationArrayAdapter: ArrayAdapter<PSAll> = ArrayAdapter<PSAll>(
                    baseContext, android.R.layout.simple_spinner_dropdown_item, district.ps
                )
                binding.spinnerOccurrencePs.adapter = policeStationArrayAdapter
            }

            else -> {
                showToastMessage("Joy")
            }

        }


    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
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

    private fun peculiaritiesApiCall() {
        // Log.e("Peculiarities_JWToken", jwt_token.toString())
        var jwt_token =
            SharedPreferenceStorage.getValue(this, SharedPreferenceStorage.JWT_TOKEN, "").toString()
        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService
        mAPIService.PeculiaritiesApi(jwt_token).enqueue(object : Callback<PeculiaritiesAPIModel> {
            override fun onResponse(
                call: Call<PeculiaritiesAPIModel>, response: Response<PeculiaritiesAPIModel>
            ) {
                if (response.isSuccessful) {
                    Log.e("Peculiarities_Status", response.body()!!.status.toString())
                    Log.e("Peculiarities_errorcode", response.body()!!.error_code.toString())
                    if (response.body()!!.status.toString().equals("success")) {
                        val inList = response.body()!!.data
                        var j: Int = 0
                        for (i in inList) {
                            val ID = inList.get(j).id.toString()
                            val INNAME = inList.get(j).name.trim()
                            val inListHash = java.util.HashMap<String, String>()
                            inListHash.put("IN_ID", ID)
                            inListHash.put("IN_NAME", INNAME)
                            pecuHashMap.add(inListHash)
                            j = j + 1
                        }
                    } else {
                        Toast.makeText(
                            this@SubmitDeadBodyInformationActivity,
                            response.body()!!.status.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }

            override fun onFailure(call: Call<PeculiaritiesAPIModel>, t: Throwable) {

            }
        })
    }

    private fun idfirstApiCall() {
        Log.e("Idfirst_JWToken", jwt_token.toString())
        jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()
        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService
        mAPIService.IdFirstApi(jwt_token).enqueue(object : Callback<IdfirstAPIModel> {
            override fun onResponse(
                call: Call<IdfirstAPIModel>, response: Response<IdfirstAPIModel>
            ) {
                if (response.isSuccessful) {
                    Log.e("Idfirst_Status", response.body()!!.status.toString())
                    Log.e("Idfirst_errorcode", response.body()!!.error_code.toString())
                    if (response.body()!!.status.toString().equals("success")) {
                        val inList = response.body()!!.data
                        var j: Int = 0
                        for (i in inList) {
                            val ID = inList.get(j).id.toString()
                            val INNAME = inList.get(j).name.trim()
                            val inListHash = java.util.HashMap<String, String>()
                            inListHash.put("IN_ID", ID)
                            inListHash.put("IN_NAME", INNAME)
                            inFirstHashMap.add(inListHash)
                            j = j + 1
                        }
                    } else {
                        Toast.makeText(
                            this@SubmitDeadBodyInformationActivity,
                            response.body()!!.status.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }

            override fun onFailure(call: Call<IdfirstAPIModel>, t: Throwable) {

            }
        })
    }

    private fun idfaceApiCall() {
        Log.e("Idface_JWToken", jwt_token.toString())
        jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()
        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService
        mAPIService.IdfaceApi(jwt_token).enqueue(object : Callback<IdfaceAPIModel> {
            override fun onResponse(
                call: Call<IdfaceAPIModel>, response: Response<IdfaceAPIModel>
            ) {
                if (response.isSuccessful) {
                    Log.e("Idface_Status", response.body()!!.status.toString())
                    Log.e("Idface_errorcode", response.body()!!.error_code.toString())
                    if (response.body()!!.status.toString().equals("success")) {
                        val inList = response.body()!!.data
                        var j: Int = 0
                        for (i in inList) {
                            val ID = inList.get(j).id.toString()
                            val INNAME = inList.get(j).name.trim()
                            val inListHash = java.util.HashMap<String, String>()
                            inListHash.put("IN_ID", ID)
                            inListHash.put("IN_NAME", INNAME)
                            inFaceHashMap.add(inListHash)
                            j = j + 1
                        }
                    } else {
                        Toast.makeText(
                            this@SubmitDeadBodyInformationActivity,
                            response.body()!!.status.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }

            override fun onFailure(call: Call<IdfaceAPIModel>, t: Throwable) {

            }
        })
    }

    private fun idnoseApiCall() {
        Log.e("Idnose_JWToken", jwt_token.toString())
        jwt_token = SharedPreferenceStorage.getValue(
            applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
        ).toString()
        var mAPIService: APIService? = null
        mAPIService = ApiUtils.apiService
        mAPIService.IdnoseApi(jwt_token).enqueue(object : Callback<IdnoseAPIModel> {
            override fun onResponse(
                call: Call<IdnoseAPIModel>, response: Response<IdnoseAPIModel>
            ) {
                if (response.isSuccessful) {
                    Log.e("Idnose_Status", response.body()!!.status.toString())
                    Log.e("Idnose_errorcode", response.body()!!.error_code.toString())
                    if (response.body()!!.status.toString().equals("success")) {
                        val inList = response.body()!!.data
                        var j: Int = 0
                        for (i in inList) {
                            val ID = inList.get(j).id.toString()
                            val INNAME = inList.get(j).name.trim()
                            val inListHash = java.util.HashMap<String, String>()
                            inListHash.put("IN_ID", ID)
                            inListHash.put("IN_NAME", INNAME)
                            inNoseHashMap.add(inListHash)
                            j = j + 1
                        }
                    } else {
                        Toast.makeText(
                            this@SubmitDeadBodyInformationActivity,
                            response.body()!!.status.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }

            override fun onFailure(call: Call<IdnoseAPIModel>, t: Throwable) {

            }
        })
    }


    /*private fun IdFirstDialog() {
        var j = 0
        val list: MutableList<String> = ArrayList()
        val idlist: MutableList<String> = ArrayList()
        val namelist: MutableList<String> = ArrayList()
        for (i in inFirstHashMap) {
            val p_id = inFirstHashMap.get(j).get("IN_ID")
            val p_name = inFirstHashMap.get(j).get("IN_NAME")
//            list.add("\n" + p_id + "\n" + p_name + "\n")
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
        builder.setTitle("Select Special Identification marks")
        if (!seletedIdFirstindex.isEmpty()) {
            checkedIdFirst =
                BooleanArray(listItems.size) { false } //this will checked the items when user open the dialog
            var K: Int = 0
            for (i in seletedIdFirstindex) {
                Log.e("TAG", seletedIdFirstindex[K].toString())
                checkedIdFirst!!.set(seletedIdFirstindex[K], true);
                K = K + 1
            }
            previousSeletedIdFirstindex.clear()
            previousSeletedIdFirstindex.addAll(seletedIdFirstindex)

        }

        builder.setMultiChoiceItems(listItems,
            checkedIdFirst,
            DialogInterface.OnMultiChoiceClickListener { dialog, indexSelected, isChecked ->
                if (isChecked) {
                    seletedIdFirstItems.add(namelistItems[indexSelected].toString())
                    seletedIdFirstid.add(idlistItems[indexSelected].toString())
                    seletedIdFirstindex.add(indexSelected)

                } else if (!isChecked) {
                    seletedIdFirstItems.remove(namelistItems[indexSelected].toString())
                    seletedIdFirstid.remove(idlistItems[indexSelected].toString())
                    seletedIdFirstindex.remove(indexSelected)
                }
            }).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->


            if (seletedIdFirstItems.contains(resources.getString(R.string.others))) {
                showAlertDialogButtonClicked(
                    "Special Identification  Other Value",
                    "Description",
                    binding.llOtherSplIdnMarks,
                    binding.tvOtherSplIdnMarks
                )
            } else {

                binding.llOtherSplIdnMarks.visibility = View.GONE
                binding.tvOtherSplIdnMarks.text = ""
            }
            binding.cgSplIdnMarks.removeAllViews()

            var j: Int = 0
            for (i in seletedIdFirstItems) {
                addChipIcon(binding.cgSplIdnMarks, seletedIdFirstItems[j])
                j = j + 1
            }
        }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
            seletedIdFirstindex.clear()
            seletedIdFirstindex.addAll(previousSeletedIdFirstindex)
            dialog.dismiss();
        })

        val dialog = builder.create()
        dialog.show()
    }*/

    private fun specialIdentiDialog() {

    }

    /*private fun PeculiaritiesDialog() {
        var j = 0
        val list: MutableList<String> = ArrayList()
        val idlist: MutableList<String> = ArrayList()
        val namelist: MutableList<String> = ArrayList()
        for (i in pecuHashMap) {
            val p_id = pecuHashMap.get(j).get("IN_ID")
            val p_name = pecuHashMap.get(j).get("IN_NAME")
//            list.add("\n" + p_id + "\n" + p_name + "\n")
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
        builder.setTitle("Select Peculiarities")
        if (!seletedpecuindex.isEmpty()) {
            checkedPecu =
                BooleanArray(listItems.size) { false } //this will checked the items when user open the dialog
            var K: Int = 0
            for (i in seletedpecuindex) {
                Log.e("TAG", seletedpecuindex[K].toString())
                checkedPecu!!.set(seletedpecuindex[K], true);
                K = K + 1
            }
            previousSeletedpecuindex.clear()
            previousSeletedpecuindex.addAll(seletedpecuindex)
        }

        builder.setMultiChoiceItems(listItems,
            checkedPecu,
            DialogInterface.OnMultiChoiceClickListener { dialog, indexSelected, isChecked ->
                if (isChecked) {
                    seletedpecuItems.add(namelistItems[indexSelected].toString())
                    seletedpecuid.add(idlistItems[indexSelected].toString())
                    seletedpecuindex.add(indexSelected)

                } else if (!isChecked) {
                    seletedpecuItems.remove(namelistItems[indexSelected].toString())
                    seletedpecuid.remove(idlistItems[indexSelected].toString())
                    seletedpecuindex.remove(indexSelected)
                }
            }).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->

            if (seletedpecuItems.contains(resources.getString(R.string.others))) {
                showAlertDialogButtonClicked(
                    "Peculiarities Other Value",
                    "Description",
                    binding.llOtherPecu,
                    binding.tvOtherPecu
                )
            } else {
                binding.llOtherPecu.visibility = View.GONE
                binding.tvOtherPecu.text = ""
            }
            binding.cgPecu.removeAllViews()
            var j: Int = 0
            for (i in seletedpecuItems) {
                addChipIcon(binding.cgPecu, seletedpecuItems[j])
                j = j + 1
            }
        }


        ).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->

            seletedpecuindex.clear()
            seletedpecuindex.addAll(previousSeletedpecuindex)
            dialog.dismiss();

        })

        val dialog = builder.create()
        dialog.show()
    }*/

    /*private fun IdFaceDialog() {
        var j = 0
        val list: MutableList<String> = ArrayList()
        val idlist: MutableList<String> = ArrayList()
        val namelist: MutableList<String> = ArrayList()
        for (i in inFaceHashMap) {
            val p_id = inFaceHashMap.get(j).get("IN_ID")
            val p_name = inFaceHashMap.get(j).get("IN_NAME")
//            list.add("\n" + p_id + "\n" + p_name + "\n")
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
        builder.setTitle("Select Face Type")
        if (!seletedIdFaceindex.isEmpty()) {
            checkedIdFace =
                BooleanArray(listItems.size) { false } //this will checked the items when user open the dialog
            var K: Int = 0
            for (i in seletedIdFaceindex) {
                Log.e("TAG", seletedIdFaceindex[K].toString())
                checkedIdFace!!.set(seletedIdFaceindex[K], true);
                K = K + 1
            }
            previousSeletedIdFaceindex.clear()
            previousSeletedIdFaceindex.addAll(seletedIdFaceindex)
        }

        builder.setMultiChoiceItems(listItems,
            checkedIdFace,
            DialogInterface.OnMultiChoiceClickListener { dialog, indexSelected, isChecked ->
                if (isChecked) {
                    seletedIdFaceItems.add(namelistItems[indexSelected].toString())
                    seletedIdFaceid.add(idlistItems[indexSelected].toString())
                    seletedIdFaceindex.add(indexSelected)

                } else if (!isChecked) {
                    seletedIdFaceItems.remove(namelistItems[indexSelected].toString())
                    seletedIdFaceid.remove(idlistItems[indexSelected].toString())
                    seletedIdFaceindex.remove(indexSelected)
                }
            }).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->


            if (seletedIdFaceItems.contains(resources.getString(R.string.others))) {
                //openOtherEnterValueDialog()
                showAlertDialogButtonClicked(
                    "Face Other Value",
                    "Description",
                    binding.llOtherFace,
                    binding.tvOtherFace
                )
            } else {
                binding.llOtherFace.visibility = View.GONE
                binding.tvOtherFace.text = ""
            }

            binding.cgFace.removeAllViews()
            var j: Int = 0
            for (i in seletedIdFaceItems) {
                addChipIcon(binding.cgFace, seletedIdFaceItems[j])
                j = j + 1
            }
        }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->

            seletedIdFaceindex.clear()
            seletedIdFaceindex.addAll(previousSeletedIdFaceindex)
            dialog.dismiss();
        })

        val dialog = builder.create()
        dialog.show()
    }*/

    private fun SpecialIdenTypeDialog() {
        var j = 0
        val list: MutableList<String> = ArrayList()
        val namelist: MutableList<String> = ArrayList()
        val typeofspcIdn: MutableList<String> = ArrayList()
        for (i in spcIdTypeHashMap) {
            val name = spcIdTypeHashMap.get(j).get("IN_NAME")
            val id = spcIdTypeHashMap.get(j).get("IN_ID")
            list.add("\n" + name)
            namelist.add(name.toString().trim())
            typeofspcIdn.add(id.toString().trim())
            j = j + 1
        }
        val listItems = list.toTypedArray()
        val namelistItems = namelist.toTypedArray()
        val idlistItems = typeofspcIdn.toTypedArray()

        val builder = AlertDialog.Builder(this@SubmitDeadBodyInformationActivity)
        builder.setTitle("Choose Type")

        val checkedItem = -1 //this will checked the item when user open the dialog
        valStr = ""

        builder.setSingleChoiceItems(
            listItems, checkedItem
        ) { dialog, which ->
            valStr = namelistItems[which]
            valTypeId = idlistItems[which]
            Log.e("VALUE", valStr)
            Log.e("VALUE", valTypeId)
        }

        builder.setPositiveButton(
            "Done"
        ) { dialog, which ->
            if (valStr.isEmpty() || valStr.equals("")) {
                dialog.dismiss()
            } else {
//                binding.tvSelectSpecType!!.setText(valStr)
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun SpecialIdenLocationDialog() {
        var j = 0
        val list: MutableList<String> = ArrayList()
        val namelist: MutableList<String> = ArrayList()
        val typeofspcIdn: MutableList<String> = ArrayList()
        for (i in spcIdLocHashMap) {
            val name = spcIdLocHashMap.get(j).get("IN_NAME")
            val id = spcIdLocHashMap.get(j).get("IN_ID")
            list.add("\n" + name)
            namelist.add(name.toString().trim())
            typeofspcIdn.add(id.toString().trim())
            j = j + 1
        }
        val listItems = list.toTypedArray()
        val namelistItems = namelist.toTypedArray()
        val idlistItems = typeofspcIdn.toTypedArray()

        val builder = AlertDialog.Builder(this@SubmitDeadBodyInformationActivity)
        builder.setTitle("Choose Location")

        val checkedItem = -1 //this will checked the item when user open the dialog
        valStrLocation = ""

        builder.setSingleChoiceItems(
            listItems, checkedItem
        ) { dialog, which ->
            valStrLocation = namelistItems[which]
            valTypeColor = idlistItems[which]
            Log.e("VALUE", valStrLocation)
            Log.e("VALUE", valTypeColor)
        }

        builder.setPositiveButton(
            "Done"
        ) { dialog, which ->
            if (valStrLocation.isEmpty() || valStrLocation.equals("")) {
                dialog.dismiss()
            } else {
//                binding.tvSelectSpecType!!.setText(valStr)
                dialog.dismiss()
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun HairTypeDialog() {
        var j = 0
        val list: MutableList<String> = ArrayList()
        val idlist: MutableList<String> = ArrayList()
        val namelist: MutableList<String> = ArrayList()
        for (i in hairHashMap) {
            val p_id = hairHashMap.get(j).get("IN_ID")
            val p_name = hairHashMap.get(j).get("IN_NAME")
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


    /*private fun IdNoseDialog() {
        var j = 0
        val list: MutableList<String> = ArrayList()
        val idlist: MutableList<String> = ArrayList()
        val namelist: MutableList<String> = ArrayList()
        for (i in inNoseHashMap) {
            val p_id = inNoseHashMap.get(j).get("IN_ID")
            val p_name = inNoseHashMap.get(j).get("IN_NAME")
//            list.add("\n" + p_id + "\n" + p_name + "\n")
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
        builder.setTitle("Select Nose Type")
        if (!seletedIdNoseindex.isEmpty()) {
            checkedIdNose =
                BooleanArray(listItems.size) { false } //this will checked the items when user open the dialog
            var K: Int = 0
            for (i in seletedIdNoseindex) {
                Log.e("TAG", seletedIdNoseindex[K].toString())
                checkedIdNose!!.set(seletedIdNoseindex[K], true);
                K = K + 1
            }
            previousSeletedIdNoseindex.clear()
            previousSeletedIdNoseindex.addAll(seletedIdNoseindex)
        }

        builder.setMultiChoiceItems(listItems,
            checkedIdNose,
            DialogInterface.OnMultiChoiceClickListener { dialog, indexSelected, isChecked ->
                if (isChecked) {
                    seletedIdNoseItems.add(namelistItems[indexSelected].toString())
                    seletedIdNoseid.add(idlistItems[indexSelected].toString())
                    seletedIdNoseindex.add(indexSelected)

                } else if (!isChecked) {
                    seletedIdNoseItems.remove(namelistItems[indexSelected].toString())
                    seletedIdNoseid.remove(idlistItems[indexSelected].toString())
                    seletedIdNoseindex.remove(indexSelected)
                }
            }).setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->

            binding.cgNose.removeAllViews()
            var j: Int = 0
            for (i in seletedIdNoseItems) {
                addChipIcon(binding.cgNose, seletedIdNoseItems[j])

                j = j + 1
            }

        }).setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
            seletedIdNoseindex.clear()
            seletedIdNoseindex.addAll(previousSeletedIdNoseindex)
            dialog.dismiss();
        })

        val dialog = builder.create()
        dialog.show()
    }*/


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
        }
        /*else if (binding.tietInquestOfficer.text.toString().isEmpty()) {
            binding.tietInquestOfficer.setFocusableInTouchMode(true)
            binding.tietInquestOfficer.requestFocus()
            showToastMessage("Please provide Inquest Officer's name")
            noError = false
        }*/
        else if (binding.tietUdCaseOfficerName.text.toString().isEmpty()) {
            binding.tietUdCaseOfficerName.isFocusableInTouchMode = true
            binding.tietUdCaseOfficerName.requestFocus()
            showToastMessage("Please provide Ud Case Officer's Name ")
            noError = false
        } else if (Wearingapp == 0) {
            binding.tvPhotowearappTxt.isFocusableInTouchMode = true
            binding.tvPhotowearappTxt.requestFocus()
            showToastMessage("Please select Wearing Apparels")
            noError = false
        } else if (Wearingapp == 1 && wApparelsImage.size == 0) {
            binding.tvPhotowearappTxt.isFocusableInTouchMode = true
            binding.tvPhotowearappTxt.requestFocus()
            showToastMessage("Please choose Wearing Apparels photo")
            noError = false
        } else if (FootwareApp == 0) {
            binding.tvFootwareTxt.isFocusableInTouchMode = true
            binding.tvFootwareTxt.requestFocus()
            showToastMessage("Please select Footwear")
            noError = false
        } else if (FootwareApp == 1 && footwareApparelsImage.size == 0) {
            binding.tvFootwareTxt.isFocusableInTouchMode = true
            binding.tvFootwareTxt.requestFocus()
            showToastMessage("Please choose Footwear photo")
            noError = false
        } else if (generalCondition == 1) {
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
            } else if (faceImage.size < 3) {
                binding.llPicFace.isFocusableInTouchMode = true
                binding.llPicFace.requestFocus()
                showToastMessage("Please choose Front, Left and right side of face photo ")
                noError = false
            } else if (bodyImage.size < 2) {
                binding.llPicBody.isFocusableInTouchMode = true
                binding.llPicBody.requestFocus()
                showToastMessage("Please choose Front and Back side photo")
                noError = false
            }
        }


        return noError

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

    private fun getMalePrivatePC(): String {
        var mppc = ""
        if (binding.rbYes.isSelected)
            mppc = "1"
        else if (binding.rbNo.isSelected)
            mppc = "2"

        return mppc
    }

    private fun getGenCondition(): String {
        var gcon = ""
        if (binding.rbCompleteBody.isSelected)
            gcon = "1"
        else if (binding.rbIncompleteBody.isSelected)
            gcon = "2"
        else if (binding.rbDecomposed.isSelected)
            gcon = "3"
        else if (binding.rbPartiallySkeletonized.isSelected)
            gcon = "4"
        else if (binding.rbFullySkeletonized.isSelected)
            gcon = "5"
        else if (binding.rbBurnt.isSelected)
            gcon = "6"
        return gcon
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


    private fun getPItemList(): String {
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

        return pitem

    }

    private fun getPecularitiesList(): String {
        var pl = ""
        val plidbuilder = StringBuilder()
        var m: Int = 0
        for (i in PecuModelArrayList) {
            plidbuilder.append(PecuModelArrayList[m].toString() + ",")
            m = m + 1
        }
        pl = plidbuilder.toString()

        return pl

    }

    private fun getBurnList(): String {
        var burn = ""
        val burnidbuilder = StringBuilder()
        var m: Int = 0
        for (i in seletedIdFirstid) {
            burnidbuilder.append(seletedIdFirstid[m].toString() + ",")
            m = m + 1
        }
        burn = burnidbuilder.toString()

        return burn
    }

    private fun getFaceList(): String {
        var face = ""
        val faceidbuilder = StringBuilder()
        var m: Int = 0
        for (i in seletedIdFaceid) {
            faceidbuilder.append(seletedIdFaceid[m].toString() + ",")
            m = m + 1
        }
        face = faceidbuilder.toString()

        return face

    }

    private fun getHairTypeList(): String {
        var hType = ""
        val hTypeidbuilder = StringBuilder()
        var m: Int = 0
        for (i in seletedIdHairid) {
            hTypeidbuilder.append(seletedIdHairid[m].toString() + ",")
            m = m + 1
        }
        hType = hTypeidbuilder.toString()

        return hType

    }

    private fun getHairColorList(): String {
        var hColor = ""
        val hColoridbuilder = StringBuilder()
        var m: Int = 0
        for (i in seletedIdHairColorid) {
            hColoridbuilder.append(seletedIdHairColorid[m].toString() + ",")
            m = m + 1
        }
        hColor = hColoridbuilder.toString()

        return hColor

    }

    private fun getNoseList(): String {
        var nose = ""
        val noseidbuilder = StringBuilder()
        var m: Int = 0
        for (i in seletedIdNoseid) {
            noseidbuilder.append(seletedIdNoseid[m].toString() + ",")
            m = m + 1
        }
        nose = noseidbuilder.toString()

        return nose

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

    fun SubmitUDData() {


        val vIdenticalMarksImagesPart = ArrayList<MultipartBody.Part>()
        val pItemImagesPart = ArrayList<MultipartBody.Part>()
        val faceImagesPart = ArrayList<MultipartBody.Part>()
        val dBodyImagesPart = ArrayList<MultipartBody.Part>()
        val WAImagesPart = ArrayList<MultipartBody.Part>()
        val footwareImagesPart = ArrayList<MultipartBody.Part>()
        val otherImagesPart = ArrayList<MultipartBody.Part>()
        val simImagesPart = ArrayList<MultipartBody.Part>()




        if (checkForInternet(this@SubmitDeadBodyInformationActivity)) {
            progressDialogCall(this@SubmitDeadBodyInformationActivity)
            for (i in 0..(visibleIdMarksImage.size - 1)) {

                val file: File = FileUtils.getFile(this, Uri.parse(visibleIdMarksImage.get(i)))

                //var file = File(visibleIdMarksImage.get(i))

                val filePart = MultipartBody.Part.createFormData(
                    "vIdmarkImage",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                System.out.println("JoyFILESPart" + filePart)
                vIdenticalMarksImagesPart.add(filePart)
            }

          /*  for (i in 0..(pItemImage.size - 1)) {
                //var file = File(pItemImage.get(i))
                var file: File = FileUtils.getFile(this, Uri.parse(pItemImage.get(i)))
                // calling from global scope
                GlobalScope.launch {
                    file =
                        Compressor.compress(applicationContext, file)
                    System.out.println("SubmitDeadBodyInformationActivity.SubmitUDData: file compress done")
                }
                val filePart = MultipartBody.Part.createFormData(
                    "pItemImage",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                pItemImagesPart.add(filePart)
                System.out.println("SubmitDeadBodyInformationActivity.SubmitUDData: FilePart attached ")
            }*/

            for (i in 0..(faceImage.size - 1)) {
               // var file = File(faceImage.get(i))
                val file: File = FileUtils.getFile(this, Uri.parse(faceImage.get(i)))

                val filePart = MultipartBody.Part.createFormData(
                    "faceImage",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                faceImagesPart.add(filePart)
            }

            for (i in 0..(bodyImage.size - 1)) {
               // val file = File(bodyImage.get(i))
                val file: File = FileUtils.getFile(this, Uri.parse(bodyImage.get(i)))

                val filePart = MultipartBody.Part.createFormData(
                    "dBodyImage",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                System.out.println("FILES" + filePart)
                dBodyImagesPart.add(filePart)
            }

            for (i in 0..(wApparelsImage.size - 1)) {
               // val file = File(wApparelsImage.get(i))
                val file: File = FileUtils.getFile(this, Uri.parse(wApparelsImage.get(i)))


                val filePart = MultipartBody.Part.createFormData(
                    "waImage",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                System.out.println("FILES" + filePart)
                WAImagesPart.add(filePart)
            }

            for (i in 0..(footwareApparelsImage.size - 1)) {
               // val file = File(footwareApparelsImage.get(i))
                val file: File = FileUtils.getFile(this, Uri.parse(footwareApparelsImage.get(i)))

                val filePart = MultipartBody.Part.createFormData(
                    "footwareImage",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                System.out.println("FILES" + filePart)
                footwareImagesPart.add(filePart)
            }
            for (i in 0..(othersImage.size - 1)) {
               // val file = File(othersImage.get(i))
                val file: File = FileUtils.getFile(this, Uri.parse(othersImage.get(i)))

                val filePart = MultipartBody.Part.createFormData(
                    "othersImage",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                System.out.println("FILES" + filePart)
                otherImagesPart.add(filePart)
            }

            for (i in 0..(SpecialMarksImage.size - 1)) {
               // val file = File(SpecialMarksImage.get(i))
                val file: File = FileUtils.getFile(this, Uri.parse(SpecialMarksImage.get(i)))

                val filePart = MultipartBody.Part.createFormData(
                    "simImage",
                    file.name,
                    file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                )
                System.out.println("FILES" + filePart)
                simImagesPart.add(filePart)
            }


            jwt_token = SharedPreferenceStorage.getValue(
                applicationContext, SharedPreferenceStorage.JWT_TOKEN, ""
            ).toString()

            val dist =
                districtArrayList.get(binding.spinnerOccurrenceDist.selectedItemPosition).district_id.toString()
            val ps =
                districtArrayList.get(binding.spinnerOccurrenceDist.selectedItemPosition).ps.get(
                    binding.spinnerOccurrencePs.selectedItemPosition
                ).ps_id.toString()
            val gen = getGender()
            val malepvp = getMalePrivatePC()
            val persoitem = getPItemList()
            val peculist = getPecuList()
            val specialidlist = getSpcIdnMarkList()
            val hairtype = getHairTypeList()
            val haircolor = getHairColorList()


            var mAPIService: APIService? = null
            mAPIService = ApiUtils.apiService
            Log.e("dist",dist);
            Log.e("ps",ps)
            Log.e("CaseNumber",binding.tietCaseNumber.text.toString().trim())

            mAPIService.UDDataSubmitApi(
                jwt_token,
                form_status,
                dist,
                ps,
                binding.tietCaseNumber.text.toString().trim(),
                binding.tvCaseDate.text.toString().trim(),
                binding.tietLatitude.text.toString(),
                binding.tietLongitude.text.toString().trim(),
                binding.tietPlaceWhereDeadBodyFound.text.toString().trim(),
                binding.tietUdCaseOfficerName.text.toString().trim(),
                binding.tietUdCaseOfficerContactNo.text.toString().trim(),
                generalCondition.toString(),
                binding.spnAgerange.selectedItem.toString().trim(),
                binding.tvHeight.text.toString().trim(),
                gen,
                malepvp,
                vIdenticalMarksImagesPart,
                binding.tietIdenticalMarks.text.toString().trim(),
                pItemImagesPart,
                persoitem,
                faceImagesPart,
                dBodyImagesPart,
                WAImagesPart,
                footwareImagesPart,
                binding.tietFootware.text.toString(),
                otherImagesPart,
                peculist,
                specialidlist,
                simImagesPart,
                hairtype,
                haircolor


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
                                        this@SubmitDeadBodyInformationActivity,
                                        "SERVER ERROR !!!"
                                    )
                                }

                            }
                        } catch (exception: java.lang.Exception) {
                            Toast.makeText(
                                this@SubmitDeadBodyInformationActivity,
                                "Some issue in server end",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        //  progressDialog!!.cancel()
                        Toast.makeText(
                            this@SubmitDeadBodyInformationActivity,
                            "SERVER ERROR!!! Please try after sometime...",
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

                override fun onFailure(call: Call<UDDataSubmitApiResponse>, t: Throwable) {
                    closeProgressDialogCall()
                    showAlertDialogMessage(
                        this@SubmitDeadBodyInformationActivity,
                        "SERVER ERROR on Failure !!!" + t.message
                    )
                }
            })
        }
        else {

            Log.d("CID WB","Saving data to local database")
            System.out.println("Saving data to local database")
            var db1 = DatabaseDb(this, null)
            db1.addSubmitData(
                form_status.toString(),
                districtArrayList.get(binding.spinnerOccurrenceDist.selectedItemPosition).district_id.toString(),
                districtArrayList.get(binding.spinnerOccurrenceDist.selectedItemPosition).ps.get(
                    binding.spinnerOccurrencePs.selectedItemPosition
                ).ps_id.toString(),
                binding.tietCaseNumber.text.toString().trim(),
                binding.tvCaseDate.text.toString().trim(),
                binding.tietLatitude.text.toString().trim(),
                binding.tietLongitude.text.toString().trim(),
                binding.tietPlaceWhereDeadBodyFound.text.toString().trim(),
                binding.tietUdCaseOfficerName.text.toString().trim(),
                binding.tietUdCaseOfficerContactNo.text.toString().trim(),
                generalCondition.toString(),
                binding.spnAgerange.selectedItem.toString().trim(),
                binding.tvHeight.text.toString().trim(),
                getGender(),
                getMalePrivatePC(),
                binding.tietIdenticalMarks.text.toString().trim(),
                getPItemList(),
                binding.tietFootware.text.toString(),
                getPecuList(),
                getSpcIdnMarkList(),
                getHairTypeList(),
                getHairColorList()
            )

            val cursor = db1.getData()
            var id: Int = 0
            if (cursor != null) {
                if (cursor.moveToLast()) {
                    //name = cursor.getString(column_index);//to get other values
                    if (cursor != null) {
                        id = cursor.getInt(0)
                    }//to get id, 0 is the column index
                }
            }

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
            for (i in 0..(visibleIdMarksImage.size - 1)) {
                Log.e("IMAGE_TEST", visibleIdMarksImage.get(i))

                saveImage(visibleIdMarksImage.get(i), "vid", id)
            }

            for (i in 0..(pItemImage.size - 1)) {
                saveImage(pItemImage.get(i), "pit", id)
            }

            for (i in 0..(faceImage.size - 1)) {
                saveImage(faceImage.get(i), "face", id)
            }

            for (i in 0..(bodyImage.size - 1)) {
                saveImage(bodyImage.get(i), "body", id)
            }
            for (i in 0..(wApparelsImage.size - 1)) {
                saveImage(wApparelsImage.get(i), "wa", id)
            }

            for (i in 0..(footwareApparelsImage.size - 1)) {

                saveImage(footwareApparelsImage.get(i), "foot", id)
            }
            for (i in 0..(othersImage.size - 1)) {

                saveImage(othersImage.get(i), "other", id)
            }
            for (i in 0..(SpecialMarksImage.size - 1)) {

                saveImage(SpecialMarksImage.get(i), "sim", id)
            }
            closeProgressDialogCall()
            showAlertDialogMessage(
                this@SubmitDeadBodyInformationActivity,
                "Data Saved "
            )
              finish();
//            startActivity(intent)
        }
    }



    private fun MessageDialog() {
        val alertDialog: AlertDialog.Builder =
            AlertDialog.Builder(this@SubmitDeadBodyInformationActivity)
        alertDialog.setTitle(" ")
        alertDialog.setIcon(R.drawable.ok_sign)
        alertDialog.setMessage("Successfully Submitted...")
        alertDialog.setPositiveButton(
            "OK"
        ) { dialog, id ->
            val accountsIntent =
                Intent(this@SubmitDeadBodyInformationActivity, MainActivity::class.java)
            startActivity(accountsIntent)
            finish()
        }
        val alert: AlertDialog = alertDialog.create()
        alert.setCanceledOnTouchOutside(false)
        alert.show()
    }


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
        progressDialogCall(this@SubmitDeadBodyInformationActivity)
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

                                        binding.spinnerOccurrenceDist.adapter = districtArrayAdapter


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
                                            this@SubmitDeadBodyInformationActivity,
                                            response.body()!!.status.toString(),
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                    closeProgressDialogCall()

                                }
                            } catch (exception: java.lang.Exception) {
                                closeProgressDialogCall()
                                Toast.makeText(
                                    this@SubmitDeadBodyInformationActivity,
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

    private val openPostActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagePath = result.data?.getStringExtra("path").toString()
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
                    wApparelsImage.add(imagePath)
                    binding.llPicWa.addView(inflater)
                    img_close.setOnClickListener {
                        wApparelsImage.remove(imagePath)

                        binding.llPicWa.removeView(inflater)
                        (binding.llPicWa.parent as ViewGroup).removeView(binding.llPicWa)
                    }
                } else if (imageCategory.equals("FOOTWARE")) {
                    footwareApparelsImage.add(imagePath)
                    binding.llFootwarePic.addView(inflater)
                    img_close.setOnClickListener {
                        footwareApparelsImage.remove(imagePath)

                        binding.llFootwarePic.removeView(inflater)
                        (binding.llFootwarePic.parent as ViewGroup).removeView(binding.llFootwarePic)
                    }
                } else if (imageCategory.equals("OTHERS")) {
                    othersImage.add(imagePath)
                    binding.llPicOthers.addView(inflater)
                    img_close.setOnClickListener {
                        othersImage.remove(imagePath)

                        binding.llPicOthers.removeView(inflater)
                        (binding.llPicOthers.parent as ViewGroup).removeView(binding.llPicOthers)
                    }
                }
                else if (imageCategory.equals("FACE")) {
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

                }
                else if (imageCategory.equals("BODY")) {
                    bodyImage.add(imagePath)
                    binding.llPicBody.addView(inflater)
                    img_close.setOnClickListener {
                        bodyImage.remove(imagePath)
                        binding.llPicBody.removeAllViews()
                        (binding.llPicBody.parent as ViewGroup).removeView(binding.llPicBody)
                    }
                }
                else if (imageCategory.equals("PERSONALITEM"))
                {
                    pItemImage.add(imagePath)
                    binding.llPersonalItemsOfTheBody.addView(
                        inflater,
                        binding.llPersonalItemsOfTheBody.childCount
                    )
                    img_close.setOnClickListener {
                        pItemImage.remove(imagePath)
                        binding.llPersonalItemsOfTheBody.removeAllViews()
                        (binding.llPersonalItemsOfTheBody.parent as ViewGroup).removeView(
                            binding.llPersonalItemsOfTheBody
                        )
                    }
                }
                else if (imageCategory.equals("SPECLMARKS")) {
                    SpecialMarksImage.add(imagePath)
                    binding.llSpcIdenMarks.addView(inflater)
                    img_close.setOnClickListener {
                        SpecialMarksImage.remove(imagePath)
                        binding.llSpcIdenMarks.removeAllViews()
                        (binding.llSpcIdenMarks.parent as ViewGroup).removeView(binding.llSpcIdenMarks)
                    }
                }
                else if (imageCategory.equals("IDENTIMARKS")) {
                    visibleIdMarksImage.add(imagePath)
                    Log.e("JOYIMAGE", imagePath)


                    binding.llIdenticalMarksPic.addView(inflater)
                    img_close.setOnClickListener {
                        visibleIdMarksImage.remove(imagePath)
                        binding.llIdenticalMarksPic.removeAllViews()
                        (binding.llIdenticalMarksPic.parent as ViewGroup).removeView(binding.llIdenticalMarksPic)
                    }
                }
                Log.e("SANKHA", faceImage.size.toString())
            }
        }


    /*fun openOtherEnterValueDialog() {
        val alert = AlertDialog.Builder(this@SubmitDeadBodyInformationActivity)

        val edittext = EditText(this@SubmitDeadBodyInformationActivity)
        edittext.hint = "Enter Name"
        edittext.maxLines = 1

        val layout = FrameLayout(this@SubmitDeadBodyInformationActivity)

//set padding in parent layout
        layout.setPaddingRelative(45, 15, 45, 0)

        alert.setTitle(title)

        layout.addView(edittext)

        alert.setView(layout)

        alert.setCancelable(false)

        alert.setPositiveButton(getString(R.string.submit), DialogInterface.OnClickListener {

                dialog, which ->

            run {

                val qName = edittext.text.toString()
                if (qName.isEmpty()) {
                    alert.setCancelable(false)
                    showToastMessage("PLease specify others")

                } else {
                    dialog.dismiss()
                }


                // Utility.hideKeyboard(context!!, dialogView!!)

            }

        })

        alert.show()
    }*/


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

    fun setTestValue() {

        binding.tietLatitude.setText("22.5689")
        binding.tietLongitude.setText("86.23598")
        binding.spinnerOccurrenceDist.setSelection(6)
        binding.spinnerOccurrencePs.setSelection(2)
        binding.tilCaseNumber.editText?.setText("215")



        binding.tietCaseNumber.setText("20")
//        binding.tietFirNo.setText("55")
//        binding.tietFirSection.setText("236/41 ACt A")
//        binding.tietGdReference.setText("288")
        binding.tietPlaceWhereDeadBodyFound.setText("Bhabani bhaban Top Floor")
//        binding.tietInquestOfficer.setText("Habildar Saikat SEN")
        binding.tietUdCaseOfficerName.setText("Inspector Arun Chakraborty")
        binding.tietUdCaseOfficerContactNo.setText("9850111222")

//        binding.tietAge.setText("32")
        binding.rbOther.isSelected = true
//        binding.tietDetailsOfInjuryMark.setText("Buke lal lal dag ache")
//        binding.tietWearingApparels.setText("Lungi with fata pant")
        binding.rbDecomposed.isSelected = true
    }


    /*    private fun getLocation() {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }
        override fun onLocationChanged(location: Location) {
            tvGpsLocation.text = "Latitude: " + location.latitude + " , Longitude: " + location.longitude
            binding.tietLatitude.setText(location.latitude.toString())
            binding.tietLongitude.setText(location.longitude.toString())
        }*/
    /*    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            if (requestCode == locationPermissionCode) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }*/


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


    fun showSeekAlertDialogButtonClicked(
        title: String,
        hintText: String?,
        parent_layout: LinearLayout?, textView: TextView?
    ) {
        // Create an alert builder
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        // set the custom layout
        val customLayout: View = layoutInflater.inflate(R.layout.custom_seek_alert_layout, null)
        builder.setView(customLayout)

        val skBarForm = customLayout.findViewById<SeekBar>(R.id.sk_bar_form)
        val skBarTo = customLayout.findViewById<SeekBar>(R.id.sk_bar_to)
        val til_other_description =
            customLayout.findViewById<TextInputLayout>(R.id.til_other_description)
        val tiet_other_description =
            customLayout.findViewById<TextInputEditText>(R.id.tiet_other_description)
        val tv_title = customLayout.findViewById<TextView>(R.id.tv_title)
        val tv_description = customLayout.findViewById<TextView>(R.id.tv_description)
        val tv_progress_to = customLayout.findViewById<TextView>(R.id.tv_progress_to)
        val tv_progress_form = customLayout.findViewById<TextView>(R.id.tv_progress_form)
        val submitButton = customLayout.findViewById<MaterialButton>(R.id.btn_submit)




        skBarForm?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                //  Toast.makeText(applicationContext, "seekbar progress: $progress", Toast.LENGTH_SHORT).show()
                tv_progress_form.text = progress.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //    Toast.makeText(applicationContext, "seekbar touch started!", Toast.LENGTH_SHORT).show()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                //   Toast.makeText(applicationContext, "seekbar touch stopped!", Toast.LENGTH_SHORT).show()
            }
        })

        skBarTo?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar, progress: Int,
                fromUser: Boolean
            ) {
                //  Toast.makeText(applicationContext, "seekbar progress: $progress", Toast.LENGTH_SHORT).show()
                tv_progress_to.text = progress.toString()
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

            if (tv_progress_form.text.isEmpty() || tv_progress_to.text.isEmpty()) {
                showToastMessage("Invalid")
            } else if (tv_progress_form.text.toString().toInt() > tv_progress_to.text.toString()
                    .toInt()
            ) {
                showToastMessage("From age can not be grater then  to age")
            } else {

//                binding.tvFormAndToDate.setText(tv_progress_form.text.toString() + " - " + tv_progress_to.text.toString())
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


    fun showHeightSelectionDialogButtonClicked(
        title: String,
        hintText: String?,
        parent_layout: LinearLayout?, textView: TextView?
    ) {
        // Create an alert builder
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
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

            if (tv_progress_feet.text.isEmpty() || tv_progress_inch.text.isEmpty()) {
                showToastMessage("Invalid")
            } else if (tv_progress_feet.text.toString().toInt() == 0) {
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


    fun showPersonalItemsDialogButtonClicked() {
        // Create an alert builder
        lateinit var dialog: AlertDialog
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
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


                getSelectedMarksInJSONString()
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
        for (i in spcIdTypeHashMap) {
            spinnerArrayType.set(z, spcIdTypeHashMap.get(z).get("IN_NAME"))
            spinnerArrayTypeId.set(z, spcIdTypeHashMap.get(z).get("IN_ID"))
            z = z + 1
        }
        var y = 0
        for (i in spcIdLocHashMap) {
            spinnerArrayLoca.set(y, spcIdLocHashMap.get(y).get("IN_NAME"))
            spinnerArrayLocaId.set(y, spcIdLocHashMap.get(y).get("IN_ID"))
            y = y + 1
        }

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_item, spinnerArrayType
        )
        spn_select_spec_type.adapter = adapter

        spn_select_spec_type.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                /*Toast.makeText(this@MainActivity,
                    getString(R.string.selected_item) + " " +
                            "" + languages[position], Toast.LENGTH_SHORT).show()*/
                valStr = spinnerArrayType.get(position)!!.toString()
                valStrID = spinnerArrayTypeId.get(position)!!.toInt().toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val adapterLoc = ArrayAdapter(
            this,
            R.layout.spinner_item, spinnerArrayLoca
        )
        spn_select_spec_type.prompt = "Select Type"
        spn_select_spec_location.adapter = adapterLoc

        spn_select_spec_location.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View, position: Int, id: Long
            ) {
                //  valStrLocation = spinnerArrayLocaId.get(spinnerArrayLoca[position]!!.toInt()).toString()
                valStrLocationID = spinnerArrayLocaId.get(position)!!.toInt().toString()
                valStrLocation = spinnerArrayLoca.get(position)!!.toString()

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

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

            if (spn_select_spec_type.selectedItemPosition == 0) {
                showToastMessage("PLease select Specific Type")
            } else if (spn_select_spec_location.selectedItemPosition == 0) {
                showToastMessage("PLease select Specific Location")
            } else if (SpecialMarksImage.size == 0) {
                showToastMessage("PLease Add Picture")
            } else {


                binding.cgSpcIden.removeAllViews()
                selectedLists.add(valStr + "-" + valStrLocation + "-" + tiet_Specify_mark.text)
                splMarksModelArrayList.add(
                    SpecialIDMarksModel(
                        valStrID.toInt(),
                        valStr,
                        valStrLocationID.toInt(),
                        valStrLocation,
                        tiet_Specify_mark.text.toString().trim()
                    )
                )


                getSelectedMarksInJSONString()
                var j: Int = 0
                for (i in selectedLists) {
                    addChipIcon(binding.cgSpcIden, selectedLists[j])
                    j = j + 1
                }

                //dialog.dismiss()
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

                    for (item: SpecialIDMarksModel in specialIDMarksModelArryList) {
                        put(JSONObject().apply {
                            put("type_id", item.type_id)
                            put("loc_body_id", item.loc_body_id)
                            put("specify", item.specify)
                        })
                    }
                })
        }
        println(json)
    }

    fun getPeculiartiesInJSONString() {
        val json = JSONObject().apply {
            put("marks",
                JSONArray().apply {

                    for (item: PecuMarksModel in PecuModelArrayList) {
                        put(JSONObject().apply {
                            put("valB", item.pecu_idTwo)
                            put("valC", item.pecu_idThree)
                            put("valA", item.pecu_idOne)
                            put("valD", item.pecu_idFour)
                        })
                    }

                })
        }

        println(json)
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


}
