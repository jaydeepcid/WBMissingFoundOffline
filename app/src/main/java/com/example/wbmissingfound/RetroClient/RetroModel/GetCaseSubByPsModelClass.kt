package com.example.wbmissingfound.RetroClient.RetroModel

import com.google.gson.annotations.SerializedName

data class GetCaseSubByPsModelClass (
    @SerializedName("status")
    val status: String,
     @SerializedName("data")
     val data: List<Information>,

 ){
    data class Information(
        @SerializedName("id")
        val id:String,
        @SerializedName("ps_id")
        val ps_id:String,
        @SerializedName("ps_name")
        val ps_name:String,
        @SerializedName("ud_number")
        val ud_number:String,
        @SerializedName("ud_date")
        val ud_date:String,
        @SerializedName("ud_officer")
        val ud_officer:String,
        @SerializedName("place")
        val place:String,
        @SerializedName("udofficer_phone")
        val udofficer_phone:String,
        @SerializedName("status")
        val status:Int
    )
 }


