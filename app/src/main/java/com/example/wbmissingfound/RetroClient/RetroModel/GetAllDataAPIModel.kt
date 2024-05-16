package com.example.wbmissingfound.RetroClient.RetroModel

data class GetAllDataAPIModel(
    val status: String,
    val error_code: Int,
    val district: List<GetAllDataDistrictAll>,
    val face: List<GetAllDataFace>,
    val pecularities: List<GetAllDataPecularity>,
    val si_type: List<GetAllDatasiType>,
    val si_loc: List<GetAllDatasiLocation>,
    val hair: List<GetAllDataHairAll>,
    val hair_color: List<GetAllDataHairColor>
)

data class GetAllDataPSAll(
    val ps_id: Int,
    val ps_name: String
) {
    override fun toString(): String {
        return ps_name
    }
}

data class GetAllDataPecularity(
    val id: Int,
    val name: String
)

data class GetAllDatasiLocation(
    val id: Int,
    val name: String
)

data class GetAllDataHairAll(
    val id: Int,
    val name: String
)

data class GetAllDataHairColor(
    val id: Int,
    val name: String
)

data class GetAllDataFace(
    val id: Int,
    val name: String
)

data class GetAllDataDistrictAll(
    val district_id: Int,
    val district_name: String,
    val ps: List<GetAllDataPSAll>
) {
    override fun toString(): String {
        return district_name
    }
}

data class GetAllDatasiType(
    val id: Int,
    val name: String
)