package com.example.wbmissingfound

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.wbmissingfound.databinding.ActivityCameraXactivityBinding
import com.example.wbmissingfound.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import com.example.wbmissingfound.GraphicOverlay
class CameraXActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCameraXactivityBinding

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var graphicOverlay: GraphicOverlay<*>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraXactivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
        graphicOverlay=viewBinding.viewGraphicOverlay
        viewBinding.textView.text= bannerText
        viewBinding.textView.setTextColor(Color.parseColor("#FFFFFF"));
        // Set up the listeners for take photo and video capture buttons
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }

        cameraExecutor = Executors.newSingleThreadExecutor()


    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/UDCases-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    val path = "${output.savedUri}"
//                    perItem.add(path)
                    if (param.equals("IDENTIMARKS")){
                        val intent= Intent()
                        intent.putExtra(MorgueLevelSubmitInfoActivity.PATH,path)
                        intent.putExtra(MorgueLevelSubmitInfoActivity.TYPE, param)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }else if (param.equals("WEARINGAPP")){
                        val intent= Intent()
                        intent.putExtra(MorgueLevelSubmitInfoActivity.PATH,path)
                        intent.putExtra(MorgueLevelSubmitInfoActivity.TYPE, param)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }else if (param.equals("FOOTWARE")){
                        val intent= Intent()
                        intent.putExtra(MorgueLevelSubmitInfoActivity.PATH,path)
                        intent.putExtra(MorgueLevelSubmitInfoActivity.TYPE, param)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }else if (param.equals("OTHERS")){
                        val intent= Intent()
                        intent.putExtra(MorgueLevelSubmitInfoActivity.PATH,path)
                        intent.putExtra(MorgueLevelSubmitInfoActivity.TYPE, param)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }else if (param.equals("FACE")){
                        val intent= Intent()
                        intent.putExtra(MorgueLevelSubmitInfoActivity.PATH,path)
                        intent.putExtra(MorgueLevelSubmitInfoActivity.TYPE, param)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }else if (param.equals("BODY")){
                        val intent= Intent()
                        intent.putExtra(MorgueLevelSubmitInfoActivity.PATH,path)
                        intent.putExtra(MorgueLevelSubmitInfoActivity.TYPE, param)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }else if (param.equals("PERSONALITEM")){
                        val intent= Intent()
                        intent.putExtra(MorgueLevelSubmitInfoActivity.PATH,path)
                        intent.putExtra(MorgueLevelSubmitInfoActivity.TYPE, param)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }else if (param.equals("SPECLMARKS")){
                        val intent= Intent()
                        intent.putExtra(MorgueLevelSubmitInfoActivity.PATH,path)
                        intent.putExtra(MorgueLevelSubmitInfoActivity.TYPE, param)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }
                    else if (param.equals("PLACEOFOCCURENCE")){
                        val intent= Intent()
                        intent.putExtra(PSLevelSubmitDeadBodyInfoActivity.PATH,path)
                        intent.putExtra(PSLevelSubmitDeadBodyInfoActivity.TYPE, param)
                        setResult(Activity.RESULT_OK,intent)
                        finish()
                    }
                    Log.d(TAG, msg)
                }
            }
        )
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()
            imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, CameraAnalyzer(graphicOverlay))
                }
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,imageAnalysis, imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
                ,
                //Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

        const val PATH = "path"
        const val ID = "id"
        lateinit var bannerText:String
        lateinit var param: String

        fun getIntent(context: Context, postId: Int, type: String, bannertext: String): Intent {
            param = type
            bannerText=bannertext

            return Intent(context, CameraXActivity::class.java).apply {
                putExtra(ID, postId)
            }
        }

    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            // Handle Permission granted/rejected
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && it.value == false)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext,
                    "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }
}