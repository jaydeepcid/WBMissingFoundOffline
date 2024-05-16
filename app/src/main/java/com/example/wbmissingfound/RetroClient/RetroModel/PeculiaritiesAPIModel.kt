package com.example.wbmissingfound.RetroClient.RetroModel

data class PeculiaritiesAPIModel(
    val `data`: List<Peculiarities>,
    val error_code: Int,
    val status: String
) {
    data class Peculiarities(
        val id: Int,
        val name: String
    )
}