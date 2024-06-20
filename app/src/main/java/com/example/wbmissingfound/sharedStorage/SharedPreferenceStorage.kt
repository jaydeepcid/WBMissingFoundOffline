package com.example.wbmissingfound.sharedStorage

import android.content.Context
import android.content.SharedPreferences
import com.example.wbmissingfound.RetroClient.RetroModel.MorgueDetails

/**
 * Created by Joy Mondal on 24/02/2023.
 */
object SharedPreferenceStorage {
    private const val PREF_NAME = "com.example.wbmissingfound"
    private const val MODE = Context.MODE_PRIVATE
    const val DIST_PS = "dist_ps"
    const val BDDS_UNITS = "bdds_units"
    const val CALL_TYPE = "call_type"
    const val DISPOSAL_TYPE = "disposal_type"
    const val NOTIFICATION_COUNT = "notification_count"
    const val IS_APP_UPDATE = "is_app_update"
    const val JWT_TOKEN = "jwt_token"
    const val USERID = "userid"
    const val USERTYPE = "usertype"
    const val USERNAME = "username"
    const val PS_NAME = "ps_name"
    const val MORGUE_NAME = "morgue_name"
    /**
     * GetSharedPreferences
     *
     * @param context
     * @return
     */
    fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, MODE)
    }

    /**
     * GetSharedPreferences with Preference Name
     *
     * @param context
     * @param PREF_NAME
     * @return
     */
    fun getPreferences(context: Context, PREF_NAME: String?): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, MODE)
    }

    /**
     * GetSharedPreferences.Editor
     *
     * @param context
     * @return
     */
    fun getEditor(context: Context): SharedPreferences.Editor {
        return getPreferences(context).edit()
    }

    /**
     * GetSharedPreferences.Editor with Preference Name
     *
     * @param context
     * @param PREF_NAME
     * @return
     */
    fun getEditor(context: Context, PREF_NAME: String?): SharedPreferences.Editor {
        return getPreferences(context, PREF_NAME).edit()
    }

    /**
     * GetValues
     *
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    fun getValue(context: Context, key: String?, defaultValue: Int): Int {
        return getPreferences(context).getInt(key, defaultValue)
    }

    fun getValue(context: Context, key: String?, value: Long): Long {
        return getPreferences(context).getLong(key, value)
    }

    fun getValue(context: Context, key: String?, defaultValue: Boolean): Boolean {
        return getPreferences(context).getBoolean(key, defaultValue)
    }

    fun getValue(context: Context, key: String?, default_value: String?): String? {
        return getPreferences(context).getString(key, default_value)
    }

    /**
     * GetValues with Preference Name
     *
     * @param context
     * @param PREF_NAME
     * @param key
     * @param default_value
     * @return
     */
    fun getValue(context: Context, PREF_NAME: String?, key: String?, default_value: Int): Int {
        return getPreferences(context, PREF_NAME).getInt(key, default_value)
    }

    fun getValue(context: Context, PREF_NAME: String?, key: String?, default_value: Long): Long {
        return getPreferences(context, PREF_NAME).getLong(key, default_value)
    }

    fun getValue(
        context: Context,
        PREF_NAME: String?,
        key: String?,
        default_value: Boolean
    ): Boolean {
        return getPreferences(context, PREF_NAME).getBoolean(key, default_value)
    }

    fun getValue(
        context: Context,
        PREF_NAME: String?,
        key: String?,
        default_value: String?
    ): String? {
        return getPreferences(context, PREF_NAME).getString(key, default_value)
    }

    /**
     * SetValues
     *
     * @param context
     * @param key
     * @param value
     */
    fun setValue(context: Context, key: String?, value: Int) {
        getEditor(context).putInt(key, value).commit()
    }

    fun setValue(context: Context, key: String?, value: Long) {
        getEditor(context).putLong(key, value).commit()
    }

    @JvmStatic
    fun setValue(context: Context, key: String?, value: Boolean) {
        getEditor(context).putBoolean(key, value).commit()
    }
@JvmStatic
    fun setValue(context: Context, key: String?, value: String?) {
        getEditor(context).putString(key, value).commit()
    }


    /**
     * SetValues with Preference Name
     *
     * @param context
     * @param PREF_NAME
     * @param key
     * @param value
     */
    fun setValue(context: Context, PREF_NAME: String?, key: String?, value: Int) {
        getEditor(context, PREF_NAME).putInt(key, value).commit()
    }

    fun setValue(context: Context, PREF_NAME: String?, key: String?, value: Long) {
        getEditor(context, PREF_NAME).putLong(key, value).commit()
    }

    fun setValue(context: Context, PREF_NAME: String?, key: String?, value: Boolean) {
        getEditor(context, PREF_NAME).putBoolean(key, value).commit()
    }

    fun setValue(context: Context, PREF_NAME: String?, key: String?, value: String?) {
        getEditor(context, PREF_NAME).putString(key, value).commit()
    }

    /**
     * ClearSharedPreferences
     *
     * @param context
     */
    fun clearSharedPreferences(context: Context) {
        getEditor(context).clear().apply()
    }

    /**
     * ClearSharedPreferences
     *
     * @param context
     */
    fun clearSharedPreferences(context: Context, str: Array<String?>) {
        for (i in str.indices) {
            getPreferences(context, str[i]).edit().clear().apply()
        }
    }

    object Login {
        const val ID = "loginID"
        const val FCM_TOKEN = "fcmToken"
        const val FCM_TOKEN_SEND_TO_SERVER =""
        const val IS_SEND_FCM_TOKEN = "is_send_fcmToken"
        const val LOGOUT = "logout_user"
        const val DETAILS = "details"
        const val USER_TYPE = "user_type"
        const val DISTRICT_ID = "district_id"
        const val PS_ID = "ps_id"
        const val IS_ACTION_PERMISSION = "is_action_permission"
        const val UNIT_ID = "unit_id"
    }

    enum class ShowDialogs {
        HARD_UPDATE, SHOW_DIALOG_CONTROL_ID, SHOW_DIALOG_CONTROL_ID_TO_SHOW, SHOW_DIALOG_WEBVIEW, SHOW_DIALOG_LINK, SHOW_DIALOG_TITLE, SHOW_DIALOG_MESSAGE, SHOW_DIALOG_BUTTON_NAME
    }
}