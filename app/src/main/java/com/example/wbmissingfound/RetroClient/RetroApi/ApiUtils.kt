package com.example.wbmissingfound.RetroClient.RetroApi

import com.example.wbmissingfound.RetroClient.RetroApi.RetrofitClient
import com.example.wbmissingfound.Helper.Constants

object ApiUtils {

    val BASE_URL = Constants.BASE.BASE_URL

    val apiService: APIService
        get() = RetrofitClient.getClient(BASE_URL)!!.create(APIService::class.java)

}