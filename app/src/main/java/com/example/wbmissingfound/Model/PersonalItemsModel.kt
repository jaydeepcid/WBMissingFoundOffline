package com.example.wbmissingfound.Model

data class PersonalItemsModel(
    val pi_name: String,
    val specify: String

) {
    override fun toString(): String {
        return "$pi_name,$specify"
    }
}
