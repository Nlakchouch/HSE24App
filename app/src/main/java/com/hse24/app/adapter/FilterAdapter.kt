package com.hse24.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.RecyclerView

import com.hse24.app.R
import com.hse24.app.restApi.model.FilterGroup

class FilterAdapter(private val mContext: Context, private val filterGroups: List<FilterGroup>) : RecyclerView.Adapter<FilterAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        val filterItems = Array(filterGroups[position].filterItems.size) { i -> filterGroups[position].filterItems[i].displayName }

        // Creating adapter for spinner
        val dataAdapter: ArrayAdapter<CharSequence> = ArrayAdapter(
            mContext,
            android.R.layout.simple_spinner_item,
            filterItems
        )

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        holder.spinner.prompt = filterGroups[position].displayName
        holder.spinner.adapter = dataAdapter
      // holder.spinner.setOnItemSelectedListener(this)
    }

    override fun getItemCount(): Int {
        return filterGroups.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val spinner: Spinner = view.findViewById<View>(R.id.spinner) as Spinner
    }

}