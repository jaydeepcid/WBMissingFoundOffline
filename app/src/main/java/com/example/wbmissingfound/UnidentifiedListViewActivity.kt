package com.example.wbmissingfound

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wbmissingfound.Model.UDListRecycleViewCustomAdapter
import com.example.wbmissingfound.Model.UDListRecycleViewModel
import com.example.wbmissingfound.databinding.ActivityUnidentifiedListViewBinding


class UnidentifiedListViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUnidentifiedListViewBinding

    var CaseDataHashMap = ArrayList<HashMap<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnidentifiedListViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CaseDataHashMap.clear()

        val arl = intent.getSerializableExtra("CaseData") as ArrayList<HashMap<String, String>>?

        /*val CaseData = intent.getStringArrayListExtra("CaseData")
        Log.e("SANKHA", CaseData.toString())*/

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)

        binding.recyclerview.layoutManager = layoutManager

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)


        recyclerview.layoutManager = LinearLayoutManager(this)

        val data = ArrayList<UDListRecycleViewModel>()

        val CaseDataHashMap = arl


        if (CaseDataHashMap != null) {
            var j: Int = 0
            for (i in 1..CaseDataHashMap.size) {
                val par1 = CaseDataHashMap[j].get("c_id")
                val par2 = CaseDataHashMap[j].get("ud_number")
                val par3 = CaseDataHashMap[j].get("ud_date")
                val par4 = CaseDataHashMap[j].get("officer_name")
                val par5 = CaseDataHashMap[j].get("officer_phone")
                val par6 = CaseDataHashMap[j].get("latitude")
                val par7 = CaseDataHashMap[j].get("longitude")
                val par8 = CaseDataHashMap[j].get("place")
                val par9 = CaseDataHashMap[j].get("ud_officer")
                val par10 = CaseDataHashMap[j].get("udofficer_phone")
                val par11 = CaseDataHashMap[j].get("male_private")
                val par12 = CaseDataHashMap[j].get("status")
                val par13 = CaseDataHashMap[j].get("district_id")
                val par14 = CaseDataHashMap[j].get("ps_id")
                val par15 = CaseDataHashMap[j].get("submit_time")
                data.add(UDListRecycleViewModel("CASE NO: " + par1, "Submit Date & Time: " + par15))
                j = j + 1
            }
        }

        val adapter = UDListRecycleViewCustomAdapter(data)

        binding.recyclerview.adapter = adapter
    }
}