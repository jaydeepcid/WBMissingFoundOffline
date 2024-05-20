package com.example.wbmissingfound.DBHelper


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONObject
import java.util.ArrayList

class DatabaseDb(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    // below is the method for creating a database by a sqlite query
    override fun onCreate(db: SQLiteDatabase) {

        var query =
            ("CREATE TABLE district ( district_id INTEGER PRIMARY KEY,district_name  TEXT  )")
        db.execSQL(query)
        query = ("CREATE TABLE ps ( ps_id INTEGER PRIMARY KEY,ps_name  TEXT,ds_index INTEGER  )")
        db.execSQL(query)
        query = ("CREATE TABLE siloc ( id INTEGER PRIMARY KEY,name  TEXT  )")
        db.execSQL(query)
        query = ("CREATE TABLE siloctype ( id INTEGER PRIMARY KEY,name  TEXT  )")
        db.execSQL(query)
        query = ("CREATE TABLE haircolor ( id INTEGER PRIMARY KEY,name  TEXT  )")
        db.execSQL(query)
        query = ("CREATE TABLE hair ( id INTEGER PRIMARY KEY,name  TEXT  )")
        db.execSQL(query)
        query =
            ("CREATE TABLE submitdata ( id INTEGER PRIMARY KEY,formStatus  TEXT, dist_id TEXT, ps_id TEXT, case_no TEXT, case_date TEXT, lat TEXT, longi TEXT, place_dbf TEXT, udOffName TEXT, udOffPhone TEXT, gen_con TEXT, ageRange TEXT, height TEXT, gender TEXT, malePPC TEXT, idMarks TEXT, perItem TEXT, footwear TEXT, peculiarities TEXT, specialIdMarks TEXT, hairType TEXT, hairColor TEXT)")
        db.execSQL(query)
        query =
            ("CREATE TABLE submitpsdata ( id INTEGER PRIMARY KEY,ps_id TEXT, morgue_id_val TEXT,udNumber TEXT, udDate TEXT, udOfficer TEXT,udOfficerPhone TEXT, latitude TEXT, longitude TEXT,vic_gen_val TEXT, place TEXT, status TEXT, poPhoto TEXT , deadbodytype TEXT,vic_name_val TEXT,vic_age TEXT, vic_address_val TEXT)")
        db.execSQL(query)
        query =
            ("CREATE TABLE imagetable ( id INTEGER PRIMARY KEY AUTOINCREMENT,caseid INTEGER , path TEXT, type TEXT)")
        db.execSQL(query)
        query =
            ("CREATE TABLE imagetableMorgueOffline ( id INTEGER PRIMARY KEY AUTOINCREMENT,caseid TEXT , path TEXT, type TEXT)")
        db.execSQL(query)

        query=
            ("CREATE TABLE submitmorguedata( id TEXT PRIMARY KEY, age_min INTEGER, age_max INTEGER, height TEXT, gender TEXT, general_condition TEXT,foot_des TEXT, male_private TEXT, cloth TEXT, peculiarities TEXT, hair TEXT, haircolor TEXT)")
        db.execSQL(query)

        query=
            ("CREATE TABLE personalitemofflinedata( id TEXT PRIMARY KEY, personaldata TEXT, PersonalItem TEXT, case_id TEXT)")
        db.execSQL(query)

        query=
            ("CREATE TABLE burntypemarks( id TEXT PRIMARY KEY, burnmarkstype TEXT, Burnmarksimage TEXT, case_id TEXT)")
        db.execSQL(query)
        query =
            ("CREATE TABLE morgue ( morgue_id INTEGER PRIMARY KEY,morgue_name  TEXT  )")
        db.execSQL(query)

        query =
            ("CREATE TABLE morguelist ( morgue_id TEXT PRIMARY KEY,morgue_name  TEXT  )")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS district")
        db.execSQL("DROP TABLE IF EXISTS ps")
        db.execSQL("DROP TABLE IF EXISTS siloc")
        db.execSQL("DROP TABLE IF EXISTS siloctype")
        db.execSQL("DROP TABLE IF EXISTS haircolor")
        db.execSQL("DROP TABLE IF EXISTS hair")
        db.execSQL("DROP TABLE IF EXISTS submitdata")
        db.execSQL("DROP TABLE IF EXISTS submitpsdata")
        db.execSQL("DROP TABLE IF EXISTS imagetable")
        db.execSQL("DROP TABLE IF EXISTS imagetableMorgueOffline")
        db.execSQL("DROP TABLE IF EXISTS submitmorguedata")
        db.execSQL("DROP TABLE IF EXISTS personalitemofflinedata")
        db.execSQL("DROP TABLE IF EXISTS burntypemarks")
        db.execSQL("DROP TABLE IF EXISTS morgue")
        db.execSQL("DROP TABLE IF EXISTS morguelist")
        onCreate(db)
    }
    fun addPersonalItemDataOffline(id:String,personaldata:String,PersonalItem:ArrayList<String>,case_id:String){
        val values=ContentValues()
        values.put("id",id)
        values.put("personaldata",personaldata.toString())
        values.put("PersonalItem",PersonalItem.toString())
        values.put("case_id",case_id)
        val db = this.writableDatabase
        db.insert("personalitemofflinedata", null, values)
        db.close()
    }

    fun getPersonalItemfromLocalStroage(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM personalitemofflinedata", null)
    }
    fun addBurnMarksTypeData(id:String,burnmarkstype:String,Burnmarksimage:ArrayList<String>,case_id:String){
        val values=ContentValues()
        values.put("id",id)
        values.put("burnmarkstype",burnmarkstype)
        values.put("Burnmarksimage",Burnmarksimage.toString())
        values.put("case_id",case_id)
        val db = this.writableDatabase
        db.insert("burntypemarks", null, values)
        db.close()
    }

    fun getBurnDetailsfromOfflineDatabase(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM burntypemarks", null)
    }
    fun submitDataMorgueLevelOffline(case_id:String, agemin:Int, agemax:Int, height:String, gender:String,
                                     general_condition:String, foot_des:String, male_private:String, cloth:String,
                                     peculiarities: JSONObject, hair:String, haircolor:String){
        val values=ContentValues()
        values.put("id",case_id)
        values.put("age_min",agemin)
        values.put("age_max",agemax)
        values.put("height",height)
        values.put("gender",gender)
        values.put("general_condition",general_condition)
        values.put("foot_des",foot_des)
        values.put("male_private",male_private)
        values.put("cloth",cloth)
        values.put("peculiarities",peculiarities.toString())
        values.put("hair",hair)
        values.put("haircolor",haircolor)
        val db = this.writableDatabase
        db.insert("submitmorguedata", null, values)
        db.close()
    }

    fun getMorgueData(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM submitmorguedata", null)
    }
    fun addImageMorgueOffline(path: ArrayList<String>, type: String, caseid: String) {
        val values = ContentValues()
        values.put("path", path.toString())
        values.put("type", type)
        values.put("caseid", caseid)
        val db = this.writableDatabase
        db.insert("imagetableMorgueOffline", null, values)
        db.close()
    }

    fun getImagedetailsfromOfflineDatabase(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM imagetableMorgueOffline", null)
    }

    // This method is for adding data in our database
    fun addDistrict(name: String, id: String) {
        val values = ContentValues()
        values.put("district_name", name)
        values.put("district_id", id)
        val db = this.writableDatabase
        db.insert("district", null, values)
        db.close()
    }
    /*fun addMorgue(name: String, id: String) {
        val values = ContentValues()
        values.put("morgue_name", name)
        values.put("morgue_id", id)
        val db = this.writableDatabase
        db.insert("morgue", null, values)
        db.close()
    }*/
    fun addMorgue(id: String, name: String) {
        val values = ContentValues()
        values.put("morgue_id", id)
        values.put("morgue_name", name)
        val db = this.writableDatabase
        db.insert("morguelist", null, values)
        db.close()
    }

    fun addPs(name: String, id: String, index: String) {
        val values = ContentValues()
        values.put("ps_name", name)
        values.put("ps_id", id)
        values.put("ds_index", index)
        val db = this.writableDatabase
        db.insert("ps", null, values)
        db.close()
    }

    fun addSiLoc(name: String, id: String) {
        val values = ContentValues()
        values.put("name", name)
        values.put("id", id)
        val db = this.writableDatabase
        db.insert("siloc", null, values)
        db.close()
    }

    fun addSiLocType(name: String, id: String) {
        val values = ContentValues()
        values.put("name", name)
        values.put("id", id)
        val db = this.writableDatabase
        db.insert("siloctype", null, values)
        db.close()
    }

    fun addHair(name: String, id: String) {
        val values = ContentValues()
        values.put("name", name)
        values.put("id", id)
        val db = this.writableDatabase
        db.insert("hair", null, values)
        db.close()
    }

    fun addHairColor(name: String, id: String) {
        val values = ContentValues()
        values.put("name", name)
        values.put("id", id)
        val db = this.writableDatabase
        db.insert("haircolor", null, values)
        db.close()
    }

    fun addSubmitData(
        formStatus: String, dist_id: String, ps_id: String, case_no: String,
        case_date: String, lat: String, longi: String, place_dbf: String,
        udOffName: String, udOffPhone: String, gen_con: String, ageRange: String,
        height: String, gender: Int, malePPC: String, idMarks: String,
        perItem: String, footwear: String, peculiarities: String, specialIdMarks: String,
        hairType: String, hairColor: String
    ) {
        val values = ContentValues()
        values.put("formStatus", formStatus)
        values.put("dist_id", dist_id)
        values.put("ps_id", ps_id)
        values.put("case_no", case_no)
        values.put("case_date", case_date)
        values.put("lat", lat)
        values.put("longi", longi)
        values.put("place_dbf", place_dbf)
        values.put("udOffName", udOffName)
        values.put("udOffPhone", udOffPhone)
        values.put("gen_con", gen_con)
        values.put("ageRange", ageRange)
        values.put("height", height)
        values.put("gender", gender)
        values.put("malePPC", malePPC)
        values.put("idMarks", idMarks)
        values.put("perItem", perItem)
        values.put("footwear", footwear)
        values.put("peculiarities", peculiarities)
        values.put("specialIdMarks", specialIdMarks)
        values.put("hairType", hairType)
        values.put("hairColor", hairColor)
        val db = this.writableDatabase
        db.insert("submitdata", null, values)
        db.close()
    }
    fun addSubmitPSData(
        ps_id: Int, morgue_id_val: String,udNumber: String,
        udDate: String, udOfficer:String ,udOfficerPhone:String, latitude: String, longitude: String,
        vic_gen_val: String, place: String, status: String,poPhoto: ArrayList<String>, deadbodytype : Int ,vic_name_val:String,vic_age :String,
        vic_address_val:String

    ) {
        val values = ContentValues()
        values.put("ps_id", ps_id)
        values.put("morgue_id_val", morgue_id_val)
        values.put("udNumber", udNumber)
        values.put("udDate", udDate)
        values.put("udOfficer", udOfficer)
        values.put("udOfficerPhone", udOfficerPhone)
        values.put("latitude", latitude)
        values.put("longitude", longitude)
        values.put("vic_gen_val", vic_gen_val)
        values.put("place", place)
        values.put("status", status)
        values.put("poPhoto",poPhoto.toString())
        values.put("deadbodytype", deadbodytype)
        values.put("vic_name_val", vic_name_val)
        values.put("vic_age", vic_age)
        values.put("vic_address_val", vic_address_val)
        val db = this.writableDatabase
        db.insert("submitpsdata", null, values)
        db.close()
    }

    fun deletePsOfflineSubmitData(id: Int) {
        val db = this.writableDatabase
        db.delete("submitpsdata", "id=" + id, null)
        //  db.execSQL("Delete FROM imagetable where  caseid="+caseid, null)
        System.out.println("Delete FROM submitpsdata where  id=" + id)
        //  db.execSQL("Delete FROM submitdata where  id="+caseid, null)
        db.close()
    }
    fun deleteRowByIdPSData(Id: Int): Int {
        val db = this.writableDatabase
        return db.delete("submitpsdata", "id = ?", arrayOf(Id.toString()))
        db.close()
    }
    fun deleteRowByCaseIdBurncase(caseId: String): Int {
        val db = this.writableDatabase
        return db.delete("burntypemarks", "case_id = ?", arrayOf(caseId))
        db.close()
    }
    fun deleteRowByCaseIdPersonalItem(caseId: String): Int {
        val db = this.writableDatabase
        return db.delete("personalitemofflinedata", "case_id = ?", arrayOf(caseId))
        db.close()
    }
    fun deleteRowByCaseIdMorgeData(caseId: String): Int {
        val db = this.writableDatabase
        return db.delete("submitmorguedata", "id = ?", arrayOf(caseId))
        db.close()
    }
    fun deleteRowByCaseId(caseId: String,type:String): Int {
        val db = this.writableDatabase
        return db.delete("imagetableMorgueOffline", "caseid = ? AND  type= ?", arrayOf(caseId,type))
        db.close()
    }

    fun addImage(path: String, type: String, caseid: Int) {
        val values = ContentValues()
        values.put("path", path)
        values.put("type", type)
        values.put("caseid", caseid)
        val db = this.writableDatabase
        db.insert("imagetable", null, values)
        db.close()
    }



    fun getImagepath(type: String, caseid: Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery(
            "SELECT * FROM imagetable where type='" + type + "' and caseid=" + caseid,
            null
        )
    }

    fun getImagepathFace(caseid: Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM imagetable where type='face' and caseid=" + caseid, null)
    }

    fun deleteCase(caseid: Int) {
        val db = this.writableDatabase
        db.delete("imagetable", "caseid=" + caseid, null)
        //  db.execSQL("Delete FROM imagetable where  caseid="+caseid, null)
        System.out.println("Delete FROM submitdata where  id=" + caseid)
        db.delete("submitdata", "id=" + caseid, null)
        //  db.execSQL("Delete FROM submitdata where  id="+caseid, null)
        db.close()
    }

    fun getDistrict(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM district", null)
    }

    fun getData(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM submitdata", null)
    }
    fun getPSData(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM submitpsdata", null)
    }

    fun getSiLoc(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM siloc", null)
    }

    fun getSiLoctype(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM siloctype", null)
    }

    fun getHair(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM hair", null)
    }

    fun getHairColor(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM haircolor", null)
    }

    fun getPsdata(id: Int): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM ps where ds_index=" + id, null)
    }
    fun getmorgueList(): Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM morguelist", null)
    }
    fun getmorgueIdbyName(name:String) : Cursor? {
        val db = this.readableDatabase
        return db.rawQuery("SELECT morgue_id FROM morguelist where morgue_name='" + name+"'", null)
    }

    fun CheckIsDataAlreadyInDBorNot(): Boolean {
        val db = this.writableDatabase

        val Query = "Select * from district"
        val cursor = db.rawQuery(Query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }
    fun CheckIsDataAlreadyInDBorNotMorgueList(): Boolean {
        val db = this.writableDatabase

        val Query = "Select * from morguelist"
        val cursor = db.rawQuery(Query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }


    companion object {

        // below is variable for database name
        private val DATABASE_NAME = "OfflineDataUDCA.db"

        // below is the variable for database version
        private val DATABASE_VERSION = 15

    }
}