package com.example.wbmissingfound.RetroClient.RetroModel

data class PSLevelDataEntryResponseModelClass(
    val success: Boolean,
    val data: List<DistrictAllPS>,

)

data class PSAllPS(
    val ps_id: Int,
    val ps_name: String
) {
    override fun toString(): String {
        return ps_name
    }
}




data class DistrictAllPS(
    val district_id: Int,
    val district_name: String,
    val ps: List<PSAll>
) {
    override fun toString(): String {
        return district_name
    }
}

