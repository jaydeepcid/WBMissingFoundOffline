package com.example.wbmissingfound.Model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wbmissingfound.R

class UDListRecycleViewCustomAdapter(private val mList: List<UDListRecycleViewModel>) :
    RecyclerView.Adapter<UDListRecycleViewCustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycleview_card_view_design, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val ItemsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.textViewCaseNo.text = ItemsViewModel.caseno_text
        holder.textViewCaseDate.text = ItemsViewModel.casedate_text

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textViewCaseNo: TextView = itemView.findViewById(R.id.tv_case_no)
        val textViewCaseDate: TextView = itemView.findViewById(R.id.tv_case_date)
    }
}
