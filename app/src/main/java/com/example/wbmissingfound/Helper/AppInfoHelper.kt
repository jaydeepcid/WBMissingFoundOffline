package com.example.wbmissingfound.Helper

import android.content.Context
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat


object AppInfoHelper {
    var VERSION_CODE = 0
    var VERSION_NAME = 1
    fun getInfo(context: Context, key: Int): String {
        var versionName = ""
        var versionCode = -1
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            versionName = packageInfo.versionName
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                versionCode = PackageInfoCompat.getLongVersionCode(packageInfo).toInt()
            else
                versionCode = packageInfo.versionCode

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (key == VERSION_CODE) "" + versionCode else "" + versionName
    }

    fun Context.getAppVersionCode(): Int {
        val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        return PackageInfoCompat.getLongVersionCode(packageInfo).toInt()
    }
}