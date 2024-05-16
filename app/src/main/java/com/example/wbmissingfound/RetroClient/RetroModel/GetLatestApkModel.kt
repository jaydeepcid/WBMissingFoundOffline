package com.example.wbmissingfound.RetroClient.RetroModel

import com.google.gson.annotations.SerializedName

data class GetLatestApkModel(
    @SerializedName("appVersion")
    val appVersion: String,
    @SerializedName("isHardUpdate")
    val isHardUpdate: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("updateLink")
    val updateLink: Any
)
