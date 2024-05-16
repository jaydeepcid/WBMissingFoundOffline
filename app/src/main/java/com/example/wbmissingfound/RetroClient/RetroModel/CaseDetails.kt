package com.example.wbmissingfound.RetroClient.RetroModel

import com.google.gson.annotations.SerializedName

class CaseDetails (
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)