package com.example.wbmissingfound.RetroClient.RetroModel

data class CaseDataModel(
    val message: String,
    val result: List<CaseDataResult>
) {
    data class CaseDataResult(
        val case_id: Int,
        val district_id: String,
        val latitude: String,
        val longitude: String,
        val male_private: Int,
        val officer_name: String,
        val officer_phone: String,
        val place: String,
        val ps_id: String,
        val status: Int,
        val submit_time: String,
        val ud_date: String,
        val ud_number: String,
        val ud_officer: String,
        val udofficer_phone: String,
        val user_id: Int
    )
}