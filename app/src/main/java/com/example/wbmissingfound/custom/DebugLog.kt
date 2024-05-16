package com.example.wbmissingfound.custom

import android.util.Log
import com.example.wbmissingfound.Helper.Constants


object DebugLog {
    @JvmStatic
    fun print(str: String?) {
        if (Constants.RELEASE_TYPE.Release) println(str)
    }

    @JvmStatic
    fun printD(TAG: String?, str: String?) {
        if (!Constants.RELEASE_TYPE.Release) Log.d(TAG, str!!)
    }

    @JvmStatic
    fun printI(TAG: String?, str: String?) {
        if (!Constants.RELEASE_TYPE.Release) Log.i(TAG, str!!)
    }
}