package com.example.wbmissingfound.RetroClient.RetroModel

import com.google.gson.annotations.SerializedName

data class LoginAPIModel(
@SerializedName("success")
val success:Boolean,
@SerializedName("userId")
    val userId: String,
@SerializedName("userType")
val userType: String,
@SerializedName("token")
val token: String,
@SerializedName("message")
    val message: String,
    @SerializedName("username")
 val username:String,
    @SerializedName("ds_id")
    val ds_id:Any,
    @SerializedName("ps_id")
    val ps_id:Any

)