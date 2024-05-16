package com.example.wbmissingfound.RetroClient.RetroModel

data class TypeBurnmarksResponse(
    val status: String,
    val error_code: Int,
    val data: List<typemarksTypeItem>
)
class typemarksTypeItem {
    val type: String = ""
}
