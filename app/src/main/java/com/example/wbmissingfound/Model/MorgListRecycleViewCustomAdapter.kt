package com.example.wbmissingfound.Model

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.wbmissingfound.MorgueLevelSubmitInfoActivity
import com.example.wbmissingfound.R
import com.example.wbmissingfound.RetroClient.RetroModel.GetCaseSubByPsModelClass
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class MorgListRecycleViewCustomAdapter(var  context: Context,private val mList: List<GetCaseSubByPsModelClass.Information>) :
    RecyclerView.Adapter<MorgListRecycleViewCustomAdapter.ViewHolder>() {


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleview_card_view_design_for_morg_list, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]
        Log.e("count",mList.size.toString())
//        val inputFormatter =
//            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
//        val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyy ", Locale.ENGLISH)
//        val date = LocalDate.parse(ItemsViewModel.ud_date, inputFormatter)
//        val formattedDate = outputFormatter.format(date)

        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        val outputFormat = SimpleDateFormat("dd/MM/yyyy ")
        val date: Date = inputFormat.parse(ItemsViewModel.ud_date)
        val formattedDate = outputFormat.format(date)
        Log.e("date",formattedDate)


        // sets the text to the textview from our itemHolder class
        holder.textViewCaseNo.text = ItemsViewModel.ud_number
        holder.textViewPsname.text=ItemsViewModel.ps_name
        holder.textViewCaseDate.text=formattedDate
        holder.textViewOfficerName.text=ItemsViewModel.ud_officer
        holder.textViewOfficerContact.text=ItemsViewModel.udofficer_phone
        holder.textViewPlace.text=ItemsViewModel.place
        try {
            if(ItemsViewModel.status==1){
                holder.textViewIdentify.setTextColor(Color.parseColor("#006400"))
                holder.textViewIdentify.text="IDENTIFIED"
            }
            else if(ItemsViewModel.status==0){
                holder.textViewIdentify.setTextColor(Color.parseColor("#FF0000"))
                holder.textViewIdentify.text="UN-IDENTIFIED"
            }else{
                holder.textViewIdentify.text=" "
            }
        }catch (exception:NullPointerException){
            holder.textViewIdentify.setTextColor(Color.parseColor("#0000FF"))
            holder.textViewIdentify.text="No value"
        }


        //holder.textViewCaseDate.text = ItemsViewModel.casedate_text

        holder.ll_info_morge.setOnClickListener {
            Log.e("position",position.toString())
            loadDataFromList(ItemsViewModel.id.toString())
        }



    }


    private fun loadDataFromList(caseID:String) {
    val intent=Intent(context,MorgueLevelSubmitInfoActivity::class.java)
       intent.putExtra("CaseId",caseID)
        context.startActivity(intent)

    }


    // return the number of the items in the list
    override fun getItemCount(): Int {
        Log.e("count",mList.size.toString())
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textViewCaseNo: TextView = itemView.findViewById(R.id.tv_case_no)
        val textViewCaseDate: TextView = itemView.findViewById(R.id.tv_case_date)
        val  ll_info_morge:LinearLayout=itemView.findViewById(R.id.ll_info_morge)
        val textViewPsname:TextView=itemView.findViewById(R.id.tv_psname)
        val textViewOfficerName:TextView=itemView.findViewById(R.id.tv_ud_officer_name)
        val textViewOfficerContact:TextView=itemView.findViewById(R.id.tv_ud_officer_phn_number)
        val textViewPlace:TextView=itemView.findViewById(R.id.tv_pplace)
        val textViewIdentify:TextView=itemView.findViewById(R.id.tv_indentify_status)




    }
}
