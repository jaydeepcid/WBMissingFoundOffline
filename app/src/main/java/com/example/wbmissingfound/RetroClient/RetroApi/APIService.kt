package com.example.wbmissingfound.RetroClient.RetroApi

import com.example.wbmissingfound.RetroClient.RetroModel.CaseDetails
import com.example.wbmissingfound.RetroClient.RetroModel.*
import com.example.wbmissingfound.RetroClient.RetroModel.UnIdentificationGetAllDataAPIModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface APIService {
    @POST("login")
    @FormUrlEncoded
    fun LoginApi(
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<LoginAPIModel>

    @POST("verifyToken")
    fun LogInValidityCheckAPI(
        @Header("authorization") jwt_token: String
    ): Call<LoginCheckModel>

    @POST("getCaseDetails-byUserWithDate")
    @FormUrlEncoded
    fun CaseDataApi(
        @Field("token") token: String,
        @Field("start_date") start_date: String,
        @Field("end_date") end_date: String
    ): Call<CaseDataModel>

    @POST("alldistrict")
    fun DistrictApi(
        @Header("authorization") jwt_token: String
    ): Call<DistrictsAPIModel>


    @POST("peculiarities")
    fun PeculiaritiesApi(
        @Header("authorization") jwt_token: String
    ): Call<PeculiaritiesAPIModel>

    @POST("idfirst")
    fun IdFirstApi(
        @Header("authorization") jwt_token: String
    ): Call<IdfirstAPIModel>

    @POST("idface")
    fun IdfaceApi(
        @Header("authorization") jwt_token: String
    ): Call<IdfaceAPIModel>

    @POST("idnose")
    fun IdnoseApi(
        @Header("authorization") jwt_token: String
    ): Call<IdnoseAPIModel>

    @POST("GetAllUnIdentifiedData")
    fun UnIdentificationGetAllDataAPI(
        @Header("authorization") jwt_token: String
    ): Call<UnIdentificationGetAllDataAPIModel>

    @POST("getDistrictAndPs")
    fun getDistrictAndPs(
        @Header("authorization") jwt_token: String
    ): Call<PSLevelDataEntryResponseModelClass>

    @POST("getPsByUserID")
    fun getPsByUserID(
        @Header("authorization") jwt_token: String
    ): Call<AllPsResponseModelClass>

    @POST("GetAllUnIdentifiedData")
    fun GetAllDataAPI(
        @Header("authorization") jwt_token: String
    ): Call<GetAllDataAPIModel>

    @Multipart
    @POST("CaseApiSubmit")
    fun UDDataSubmitApi(
        @Header("authorization") jwtToken: String,
        @Part("status") fstatus: Int,
        @Part("po_dist") po_dist: String,
        @Part("po_ps") po_ps: String,
        @Part("ud_caseno") ud_caseno: String,
        @Part("ud_casedate") ud_casedate: String,
        @Part("po_lat") po_lat: String,
        @Part("po_long") po_long: String,
        @Part("placeFound") placeFound: String,
        @Part("ud_offName") ud_offName: String,
        @Part("ud_offNo") ud_offDate: String,
        @Part("gen_condition") genCondition: String,
        @Part("dbody_age") dbodyAge: String,
        @Part("dbody_height") dbodyheight: String,
        @Part("dbody_Gender") dbodyGender: Int,
        @Part("malePrivatePC") malePrivatePC: String,
        @Part vIdmarkImage: ArrayList<MultipartBody.Part>,
        @Part("id_marks_des") id_marks_des: String,
        @Part pItemImage: ArrayList<MultipartBody.Part>,
        @Part("pers_item_des") pers_item_des: String,
        @Part faceImage: ArrayList<MultipartBody.Part>,
        @Part dBodyImage: ArrayList<MultipartBody.Part>,
        @Part waImage: ArrayList<MultipartBody.Part>,
        @Part footwareImage: ArrayList<MultipartBody.Part>,
        @Part("footware_desc") footware_desc: String,
        @Part othersImage: ArrayList<MultipartBody.Part>,
        @Part("pecuList") pecuList: String,
        @Part("simList") simList: String,
        @Part simImage: ArrayList<MultipartBody.Part>,
        @Part("hairTypeList") hairTypeList: String,
        @Part("hairColorList") hairColorList: String

    ): Call<UDDataSubmitApiResponse>//UDDataSubmitApiResponse

    @Multipart
    @POST("CaseApiSubmit")
    fun UDDataSubmitApiOffline(
        @Header("authorization") jwtToken: String,
        @Part("status") fstatus: Int,
        @Part("po_dist") po_dist: String,
        @Part("po_ps") po_ps: String,
        @Part("ud_caseno") ud_caseno: String,
        @Part("ud_casedate") ud_casedate: String,
        @Part("po_lat") po_lat: String,
        @Part("po_long") po_long: String,
        @Part("placeFound") placeFound: String,
        @Part("ud_offName") ud_offName: String,
        @Part("ud_offNo") ud_offDate: String,
        @Part("gen_condition") genCondition: String,
        @Part("dbody_age") dbodyAge: String,
        @Part("dbody_height") dbodyheight: String,
        @Part("dbody_Gender") dbodyGender: Int,
        @Part("malePrivatePC") malePrivatePC: String,
        @Part vIdmarkImage: ArrayList<MultipartBody.Part>,
        @Part("id_marks_des") id_marks_des: String,
        @Part pItemImage: ArrayList<MultipartBody.Part>,
        @Part("pers_item_des") pers_item_des: String,
        @Part faceImage: ArrayList<MultipartBody.Part>,
        @Part dBodyImage: ArrayList<MultipartBody.Part>,
        @Part waImage: ArrayList<MultipartBody.Part>,
        @Part footwareImage: ArrayList<MultipartBody.Part>,
        @Part("footware_desc") footware_desc: String,
        @Part othersImage: ArrayList<MultipartBody.Part>,
        @Part("pecuList") pecuList: String,
        @Part("simList") simList: String,
        @Part simImage: ArrayList<MultipartBody.Part>,
        @Part("hairTypeList") hairTypeList: String,
        @Part("hairColorList") hairColorList: String

    ): Call<String>//UDDataSubmitApiResponse





    @Multipart
    @POST("CaseApiSubmit")
    fun IDDataSubmitApi(
        @Header("authorization") jwtToken: String,
        @Part("status") fstatus: Int,
        @Part("po_dist") po_dist: String,
        @Part("po_ps") po_ps: String,
        @Part("ud_caseno") ud_caseno: String,
        @Part("ud_casedate") ud_casedate: String,
        @Part("po_lat") po_lat: String,
        @Part("po_long") po_long: String,
        @Part("placeFound") placeFound: String,
        @Part("ud_offName") ud_offName: String,
        @Part("ud_offNo") ud_offDate: String,
        @Part("vic_name") vicName: String,
        @Part("vic_age") vicAge: String,
        @Part("vic_gen") vicGender: Int

    ): Call<UDDataSubmitApiResponse>

    @Multipart
    @POST("createCasePs")

    fun caseDatailsPSLevel(
        @Header("authorization") authorization: String,
        @Part("ps_id") psId: Int,
        @Part("ud_number") udNumber: RequestBody,
        @Part("ud_date") udDate: RequestBody,
        @Part("ud_officer") udOfficer: RequestBody,
        @Part("udofficer_phone") udOfficerPhone: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("place") place: RequestBody,
        @Part("status") status: RequestBody,
        @Part poPhoto: ArrayList<MultipartBody.Part>,
        @Part("deadbodytype") deadbodytype: Int,
        @Part("vic_name") vicName: RequestBody,
        @Part("vic_age") vicAge: Int,
        @Part("vic_gen") vicGender: RequestBody,
        @Part("morgue_id") morgueId: RequestBody,
       // @Part("vic_address") address: RequestBody

    ): Call<CaseDetails>//UDDataSubmitApiResponse


    @Multipart
    @POST("updateCaseMorgue")

    fun updateCaseMorgue(
        @Header("authorization") authorization: String,
        @Part("case_id") psId: RequestBody,
        @Part("agemin") agemin: Int,
        @Part("agemax") agemax: Int,
        @Part("height") height: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("general_condition") general_condition: RequestBody,
        @Part("foot_des") foot_des: RequestBody,
        @Part("male_private") male_private: RequestBody,
        @Part("cloth") cloth: RequestBody,
        @Part("peculiarities") peculiarities: RequestBody,
        @Part("hair") hair: RequestBody,
        @Part("haircolor") haircolor: RequestBody,


    ): Call<CaseDetails>//UDDataSubmitApiResponse

    @POST("getCaseSubByPs")
    fun getCaseSubByPs(
        @Header("authorization") jwt_token: String
    ): Call<GetCaseSubByPsModelClass>

    @POST("getMorgueList")
    fun getMorgueList(
        @Header("authorization") jwt_token: String
    ): Call<MorgueListApiModelClass>

    @Multipart
    @POST("updatePersonaldata")
    fun updatePersonaldata(
        @Header("authorization") authorization: String,
        @Part("personaldata") personaldata: RequestBody,
        @Part PersonalItem: ArrayList<MultipartBody.Part>,
        @Part("case_id") case_id: RequestBody,
    ):Call<PersonalItemSaveResponseModel>

    @Multipart
    @POST("updatePhoto")
    fun updatePhoto(
        @Header("authorization") authorization: String,
        @Part ("FieldName") FieldName :RequestBody,
        @Part Picture:ArrayList<MultipartBody.Part>,
        @Part ("case_id") case_id:RequestBody

    ):Call<ImageUploadApiResponseModel>


    @POST("typemarks")
    fun typemarks(

    ):Call<TypeBurnmarksResponse>

    @POST("idburnmarks")
    fun burnmarks(

    ):Call<TypeBurnmarksResponse>

    @Multipart
    @POST("updateBurnMarks")
    fun updateBurningMarks(
        @Header("authorization") jwtToken: String,
        @Part("burnmarkstype") burnmarkstype: RequestBody,
        @Part Burnmarksimage: ArrayList<MultipartBody.Part>,
        @Part("case_id") case_id: RequestBody
    ): Call<BurnMarksModel>


    @Multipart
    @POST("updatePhoto")
    fun updatePhotoPictureOther(
        @Header("authorization") authorization: String,
        @Part ("FieldName") FieldName :RequestBody,
        @Part PictureOther:MultipartBody.Part,
        @Part ("case_id") case_id:RequestBody

    ):Call<ImageUploadApiResponseModel>

    @POST("idhair")
    fun idhair(
    ):Call<HairTypeApiModel>

    @POST("getLatestApk")
    @FormUrlEncoded
    fun getLatestApk(
        //@Header("Authorization") token: String,
        @Field("platform") platform: String,
        @Field("appVersion") appVersion: String,

    ): Call<GetLatestApkModel>

    /*@POST("login_sb")
    @FormUrlEncoded
    fun loginPost(
        @Field("cipher") cipher: String, @Field("mobileno") phno: String, @Field("password") password: String
    ): Call<LogInModel>

    @POST("get_oc_names")
    @FormUrlEncoded
    fun ocListPost(
        @Field("cipher") cipher: String
    ): Call<OC_List>

    @POST("get_do_names")
    @FormUrlEncoded
    fun doListPost(
        @Field("cipher") cipher: String
    ): Call<DO_List>

    @POST("get_ac_names")
    @FormUrlEncoded
    fun acListPost(
        @Field("cipher") cipher: String
    ): Call<AC_List>

    @POST("get_events")
    @FormUrlEncoded
    fun EventListPost(
        @Field("cipher") cipher: String
    ): Call<Events>

    @POST("get_dc_names")
    @FormUrlEncoded
    fun dcListPost(
        @Field("cipher") cipher: String
    ): Call<DC_List>

    @Multipart
    @POST("Report_insert")
    fun uploadReportPost(
        @Part("cipher") cipher: String, @Part("RP_TYPE") r_type: String, @Part("RP_PRIORITY") r_prio: String,
        @Part("RP_MODE") r_cat: String,
        @Part("EVID") program_type: Int, @Part("NOTE") note: String, @Part("OTHR_OFIDS") officers_id: String,
        @Part("RP_CREATED_BY") userid: Int, @Part("RP_GEOLOCATION") geolocation: String, @Part("IMEI") imei_no: String,
        @Part mediaFiles: Array<MultipartBody.Part?>

    ): Call<upload_report_response>

    @Multipart
    @POST("Report_insert")
    fun writeReport(
        @Part("cipher") cipher: String, @Part("RP_TYPE") r_type: String, @Part("RP_PRIORITY") r_prio: String,
        @Part("RP_MODE") r_cat: String, @Part("EVID") program_type: Int, @Part("RP_DATE") program_date: String,
        @Part("RP_TIME") program_time: String, @Part("VENUE") program_venue: String, @Part("ORGANIZATION") program_org: String,
        @Part("ISSUE") program_issue: String, @Part("STRENGTH") program_stren: Int, @Part("LEADERSHIP") program_lead: String,
        @Part("NOTE") note: String, @Part("OFID") conOc: Int, @Part("OTHR_OFIDS") officers_id: String,
        @Part("RP_CREATED_BY") userid: Int, @Part("RP_GEOLOCATION") geolocation: String, @Part("IMEI") imei_no: String,
        @Part("OTHEREVENT") other_event: String,@Part("RP_TIME_END") program_time_end: String,
        @Part mediaFiles: Array<MultipartBody.Part?>

    ): Call<upload_report_response>

    @Multipart
    @POST("Report_insert")
    fun writeReportothereventexception(
        @Part("cipher") cipher: String, @Part("RP_TYPE") r_type: String, @Part("RP_PRIORITY") r_prio: String,
        @Part("RP_MODE") r_cat: String, @Part("EVID") program_type: Int, @Part("RP_DATE") program_date: String,
        @Part("RP_TIME") program_time: String, @Part("VENUE") program_venue: String, @Part("ORGANIZATION") program_org: String,
        @Part("ISSUE") program_issue: String, @Part("STRENGTH") program_stren: Int, @Part("LEADERSHIP") program_lead: String,
        @Part("NOTE") note: String, @Part("OFID") conOc: Int, @Part("OTHR_OFIDS") officers_id: String,
        @Part("RP_CREATED_BY") userid: Int, @Part("RP_GEOLOCATION") geolocation: String, @Part("IMEI") imei_no: String,
        @Part("RP_TIME_END") program_time_end: String,
        @Part mediaFiles: Array<MultipartBody.Part?>

    ): Call<upload_report_response>

    @POST("Ongoing_report_fetch")
    @FormUrlEncoded
    fun onGoList(
        @Field("cipher") cipher: String, @Field("OFID") ofid: Int, @Field("IS_DO") isDO: Int, @Field("IS_OC") isOc: Int, @Field(
            "IS_AC"
        ) isAc: Int
    ): Call<ongo_list_response>

    @POST("reportdetails_by_id")
    @FormUrlEncoded
    fun ReportdetailsById(
        @Field("cipher") cipher: String, @Field("RPID") rpid: String): Call<Report_Details_Response>

    @GET
    fun downloadFileWithDynamicUrl(@Url fileUrl: String): Call<ResponseBody>

    @POST("mark_complete")
    @FormUrlEncoded
    fun MarkAsCompleted(
        @Field("cipher") cipher: String, @Field("RPID") rpid: String, @Field("OFID") ofId: String) : Call<Mark_Complete_Response>

    @POST("officerdetails_by_id")
    @FormUrlEncoded
    fun Directory(
        @Field("cipher") cipher: String, @Field("OFID") ofId: String) : Call<directory_response>

    @POST("complete_report_fetch")
    @FormUrlEncoded
    fun Completed(
        @Field("cipher") cipher: String, @Field("OFID") ofId: String) : Call<CompletedReportResponse>

    @POST("requests_fetch")
    @FormUrlEncoded
    fun Requests(
        @Field("cipher") cipher: String, @Field("OFID") ofId: String) : Call<requests_response>


    @POST("view_report_details")
    @FormUrlEncoded
    fun CompRepoDetail(
        @Field("cipher") cipher: String, @Field("RPID") ofId: String) : Call<CompleteDetailsResponse>

    @POST("complete_thread_fetch")
    @FormUrlEncoded
    fun CommunicationList(
        @Field("cipher") cipher: String, @Field("OFID") ofId: String) : Call<thread_list>*//*program_date,
            program_time,
            program_venue,
            program_org,
            program_issue,
            program_stren,
            program_lead,

    @POST("get_jtcp_names")
    @FormUrlEncoded
    fun doListPost(@Field("cipher") cipher: String
    ): Call<DO_List>*/

    /*@POST("get_adlcp_names")
    @FormUrlEncoded
    fun doListPost(@Field("cipher") cipher: String
    ): Call<DO_List>*/

    /*@POST("get_cp_names")
    @FormUrlEncoded
    fun doListPost(@Field("cipher") cipher: String
    ): Call<DO_List>*/

    /*@POST("get_cp_names")
    @FormUrlEncoded
    fun doUploadAReportPost(@Field("cipher") cipher: String
    ): Call<Upload_Report>*/

}
