package com.example.wbmissingfound.Model

data class PsSubmitDataModel (
    val id:Int,
    val psid: String,
    val morgueID: String,
    val ud_number: String,
    val ud_date: String,
    val officer_name: String,
    val officer_contact: String,
    val lat: String,
    val long: String,
    val vic_gen_val: String,
    val placeDescription: String,
    val status: String,
    val poPhoto: String,
    val deadbodyType: String,
    val vic_name: String,
    val vic_age: String,
    val vic_address: String

    )