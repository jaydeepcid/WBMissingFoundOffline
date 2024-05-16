package com.example.wbmissingfound

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

open class BaseActivity : AppCompatActivity() {

    private var progressDialog: ProgressDialog? = null
    private val CAMERA_PERMISSION_CODE = 100
    private val STORAGE_PERMISSION_CODE = 101
    private var deviceId = ""
    fun showToastMessage(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
    }

    fun showAlertDialogMessage(activity: Activity, msg: String) {
        var alertDialog: AlertDialog? = activity.let {
            val appName = getString(R.string.app_name)
            var builder = AlertDialog.Builder(it)
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
        var alertDialog: AlertDialog? = activity.let {
            val appName = getString(R.string.app_name)
            var builder = AlertDialog.Builder(it)
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

    fun progressDialogCall(activity: Activity) {
        progressDialog = ProgressDialog(activity)
        progressDialog!!.setMessage("Please wait...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog!!.show()
       }

    open fun checkForInternet(context: Context): Boolean {

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

    fun closeProgressDialogCall() {
        if (progressDialog != null) {
            progressDialog!!.cancel()

        }

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

    public fun showUpdateAlertDialogMessage(activity: Activity, msg: String,link:String,isHardUpdate:Boolean) {
        var alertDialog: AlertDialog? = activity.let {
            val appName = activity.getString(R.string.app_name)
            var builder = AlertDialog.Builder(it)
            builder.setTitle(appName)
            builder.setIcon(R.drawable.error)
            builder.setMessage(msg)


            if(isHardUpdate) {
                builder!!.setCancelable(isHardUpdate)
                builder.apply {

                    setPositiveButton(R.string.update,
                        DialogInterface.OnClickListener { dialog, id ->
                            downloadApk(context,link,"UDCA.apk")
                        })
                }
            }else
            {
                builder.apply {

                    setNegativeButton(R.string.close,
                        DialogInterface.OnClickListener { dialog, id ->
                        })
                }
            }
            builder.create()
        }

        alertDialog?.show()
    }

   public  fun downloadApk(context: Context, apkUrl: String, apkFileName: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        println("apkUrl:"+apkUrl)
        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("Downloading $apkFileName")
            .setDescription("Downloading $apkFileName")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, apkFileName)

        downloadManager.enqueue(request)
//       val downloadId = downloadManager.enqueue(request)
//       val onComplete = object : BroadcastReceiver() {
//           override fun onReceive(context: Context?, intent: Intent?) {
//               val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
//               Log.e("ID_apk_Download",id.toString())
//               if (id == downloadId) {
//
//                   // Handle download completion or failure here
//                   // Check for errors in the intent
//               }
//           }
//       }
//       val downloadCompleteIntentFilter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
//       context.registerReceiver(onComplete, downloadCompleteIntentFilter)

    }

    open fun getDeviceID(): String? {
        return if (deviceId != null && !deviceId.isEmpty()) {
            deviceId
        } else {
            //return deviceId = /*InstanceID.getInstance(this).getId();*/UUID.randomUUID().toString();
            Settings.System.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
            ).also { deviceId = it }


        }
    }
}