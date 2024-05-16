package com.example.wbmissingfound.Model

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.wbmissingfound.R
import java.io.File


class ListAdapter(private val context: Activity, private val genderdata: ArrayList<String>, private val uddatedata: ArrayList<String>,private val udnodata: ArrayList<String>,private val imagepath: ArrayList<String>)
    : ArrayAdapter<String>(context, R.layout.custom_list, genderdata) {

    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.custom_list, null, true)

        val genderText = rowView.findViewById(R.id.gender) as TextView
        val uddateText = rowView.findViewById(R.id.uddate) as TextView
        val udnoText = rowView.findViewById(R.id.udno) as TextView
        val imageView = rowView.findViewById(R.id.icon) as ImageView
       // val subtitleText = rowView.findViewById(R.id.description) as TextView

        genderText.text = genderdata[position]
        uddateText.text = uddatedata[position]
        udnoText.text = udnodata[position]
        Log.e("path",imagepath[position])
        if(!imagepath[position].isEmpty()){
            val imgFile = File(imagepath[position])
            Log.e("saikat",imgFile.path)
            if (imgFile.exists()) {
                val myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                imageView.setImageBitmap(myBitmap)
            }
        }
        else
        {
            imageView.setImageResource(R.drawable.user)
        }


       // subtitleText.text = description[position]

        return rowView
    }
}