package com.example.wbmissingfound.RetroClient.RetroModel

import com.google.gson.annotations.SerializedName
import java.io.Serial

data class MorgueListApiModelClass(
    val status:String,
    val data:List<MorgueDetails>
   // val data:ArrayList<MorgueDetails>
)

data class  MorgueDetails(
    val id:String,
    val name:String,
){
   override fun toString(): String {
        return name
    }


}
