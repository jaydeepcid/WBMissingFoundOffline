package com.example.wbmissingfound.Helper

class Constants {
    object BASE {
//        var BASE_URL = "https://9994-2001-4490-4051-6c23-10b1-c76a-ca88-e0f2.ngrok.io/"
        //var BASE_URL = "http://164.164.119.210/api/"
//       var BASE_URL = "http://172.25.150.211:3000/"
       // var BASE_URL = "https://demoud.nltr.org/api/"
        var BASE_URL = "https://udcase.wb.gov.in/api/"

    }

    object TEST {
        var TESTING = false
    }
    object USER_TYPE{
        var POLICESTATION="policeStation"
        var MORGUE = "morgue"
    }

    object RELEASE_TYPE {
        val Release = true

    }

    enum class ApplicationPlatform {
        app, web
    }

}