package com.example.wbmissingfound.RetroClient.RetroModel

data class BurnTypesMarksApiModel(
    val status:String,
    val error_code:Int,
    val data:List<BurnMarks>
)
class BurnMarks {
    val type:String
        get() {
            return type
        }

}

