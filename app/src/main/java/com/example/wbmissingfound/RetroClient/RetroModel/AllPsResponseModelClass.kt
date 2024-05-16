package com.example.wbmissingfound.RetroClient.RetroModel

data class AllPsResponseModelClass (
    val status: String,
    val data: List<PSAllOnly>,
)

data class PSAllOnly(
    val ps_id: Int,
    val ps_name: String
) {
    override fun toString(): String {
        return ps_name
    }
}