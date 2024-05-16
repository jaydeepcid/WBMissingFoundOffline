package com.example.wbmissingfound.RetroClient.RetroModel

data class IdfaceAPIModel(
    val `data`: List<IdFace>,
    val error_code: Int,
    val status: String
) {
    data class IdFace(
        val id: Int,
        val name: String
    )
}