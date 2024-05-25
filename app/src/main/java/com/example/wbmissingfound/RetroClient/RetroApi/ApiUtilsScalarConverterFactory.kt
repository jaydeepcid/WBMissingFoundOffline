package com.example.wbmissingfound.RetroClient.RetroApi

import com.example.wbmissingfound.RetroClient.RetroApi.RetrofitClient
import com.example.wbmissingfound.Helper.Constants

object ApiUtilsScalarConverterFactory {

    val BASE_URL = Constants.BASE.BASE_URL

    val apiService: APIService
        get() = RetrofitClientScalarConverterFactory.getClient(BASE_URL)!!.create(APIService::class.java)

}