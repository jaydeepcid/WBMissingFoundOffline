package com.example.wbmissingfound.RetroClient.RetroModel

data class IdfirstAPIModel(
    val `data`: List<IdFirst>,
    val error_code: Int,
    val status: String
) {
    data class IdFirst(
        val id: Int,
        val name: String
    )
}