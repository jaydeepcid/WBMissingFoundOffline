package com.example.wbmissingfound.Model

data class PecuMarksModel(
    val pecu_idOne: Int,
    val pecu_nameOne: String,
    val pecu_idTwo: Int,
    val pecu_nameTwo: String,
    val pecu_idThree: Int,
    val pecu_nameThree: String,
    val pecu_idFour: Int,
    val pecu_nameFour: String,

) {
    override fun toString(): String {
        return "$pecu_idOne,$pecu_idTwo,$pecu_idThree,$pecu_idFour"
    }
}