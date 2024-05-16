package com.example.wbmissingfound.RetroClient.RetroModel

data class DistrictsAPIModel(
    val `districts`: List<District>,
    val error_code: Int,
    val status: String
)

data class District(
    val district_id: Int,
    val district_name: String,
    val ps: List<PS>


) {
    override fun toString(): String {
        return district_name
    }
}

data class PS(
    val ps_id: Int,
    val ps_name: String
) {
    override fun toString(): String {
        return ps_name
    }
}