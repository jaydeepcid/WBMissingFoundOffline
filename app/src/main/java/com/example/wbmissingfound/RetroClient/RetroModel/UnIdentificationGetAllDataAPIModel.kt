package com.example.wbmissingfound.RetroClient.RetroModel

data class UnIdentificationGetAllDataAPIModel(
    val status: String,
    val error_code: Int,
    val district: List<DistrictAll>,
    val face: List<Face>,
    val pecularities: List<Pecularity>,
    val si_type: List<siType>,
    val si_loc: List<siLocation>,
    val hair: List<HairAll>,
    val hair_color: List<HairColor>
)

data class PSAll(
    val ps_id: Int,
    val ps_name: String
) {
    override fun toString(): String {
        return ps_name
    }
}

data class Pecularity(
    val id: Int,
    val name: String
)

data class siLocation(
    val id: Int,
    val name: String
)

data class HairAll(
    val id: Int,
    val name: String
)

data class HairColor(
    val id: Int,
    val name: String
)

data class Face(
    val id: Int,
    val name: String
)

data class DistrictAll(
    val district_id: Int,
    val district_name: String,
    val ps: List<PSAll>
) {
    override fun toString(): String {
        return district_name
    }
}

data class siType(
    val id: Int,
    val name: String
)