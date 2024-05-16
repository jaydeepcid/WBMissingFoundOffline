package com.example.wbmissingfound.RetroClient.RetroModel

data class HairTypeApiModel(
    val status:String,
    val error_code:Int,
    val data:List<HairType>
)

class HairType {
    val type:String
        get() {
            return type
        }

}
