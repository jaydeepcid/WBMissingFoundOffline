package com.example.wbmissingfound.custom

import android.app.DatePickerDialog
import android.content.Context
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import java.util.Calendar

/**
 * Created by SANKHA PATTANAYAK on 16-01-2024.
 */

class DatePicker(private val context: Context, private val view: View, private val pre_date: String) {
    private var datePickerDialog: DatePickerDialog? = null
    private var setvalueCallBack: SetvalueCallBack? = null

    interface SetvalueCallBack {
        fun onSelection(date: String)
    }

    constructor(context: Context, view: View, pre_date: String, setvalueCallBack: SetvalueCallBack) : this(context, view, pre_date) {
        this.setvalueCallBack = setvalueCallBack
    }

    fun setFutureDateEnable(isEnable: Boolean) {
        if (!isEnable) {
            datePickerDialog?.datePicker?.maxDate = System.currentTimeMillis()
        }
    }

    fun selectDate() {
        val c = Calendar.getInstance()
        var mYear = c.get(Calendar.YEAR)
        var mMonth = c.get(Calendar.MONTH)
        var mDay = c.get(Calendar.DAY_OF_MONTH)
        if (pre_date.length > 0) {
            val dt = pre_date.split("/")
            mDay = dt[0].toInt()
            mMonth = dt[1].toInt() - 1
            mYear = dt[2].toInt()
        }
        datePickerDialog = DatePickerDialog(context, DatePickerDialog.OnDateSetListener { datePicker: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            if (view is TextView) {
                val formattedMonth = String.format("%02d", monthOfYear + 1)
                val date = "$formattedMonth/$dayOfMonth/$year"
                view.text = date
                view.error = null
                setvalueCallBack?.onSelection(date)
            }
            if (view is EditText) {
                val formattedMonth = String.format("%02d", monthOfYear + 1)
                view.setText("$formattedMonth/$dayOfMonth/$year")
            }
        }, mYear, mMonth, mDay)
        datePickerDialog?.show()
    }
}


