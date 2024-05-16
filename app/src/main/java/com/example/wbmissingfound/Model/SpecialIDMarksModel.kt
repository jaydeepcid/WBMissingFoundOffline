package com.example.wbmissingfound.Model

data class SpecialIDMarksModel(
    val type_id: Int,
    val type_name: String,
    val loc_body_id: Int,
    val loc_body_name: String,
    val specify: String

) {
    override fun toString(): String {
        return "$type_id,$loc_body_id,$specify"
    }
}
