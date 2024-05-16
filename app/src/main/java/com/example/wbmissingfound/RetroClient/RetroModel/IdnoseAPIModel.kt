package com.example.wbmissingfound.RetroClient.RetroModel

data class IdnoseAPIModel(
    val `data`: List<IdNose>,
    val error_code: Int,
    val status: String
) {
    data class IdNose(
        val id: Int,
        val name: String
    )
}