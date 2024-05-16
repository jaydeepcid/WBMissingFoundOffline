package com.example.wbmissingfound.RetroClient.RetroModel

import com.google.gson.annotations.SerializedName

data class BurnMarksModel(
    @SerializedName("success")
    var success: Boolean,
    @SerializedName("message")
    var message: String
)
