package com.bancempo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class SmallAdv(val title:String, val date:String) {
}

class SmallAdvAdapter(val data: List<SmallAdv>) : RecyclerView.Adapter<SmallAdvAdapter.SmallAdvHolder>(){
    class SmallAdvHolder(v:View) : RecyclerView.ViewHolder(v){
        val title: TextView = v.findViewById(R.id.tvSmallAdvTitle)
        val date: TextView = v.findViewById(R.id.tvsmallAdvDate)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallAdvHolder {
        //vg significa view group
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.small_advertisment, parent, false)

        return SmallAdvHolder(vg)
    }

    override fun onBindViewHolder(holder: SmallAdvHolder, position: Int) {
        holder.title.text = data[position].title
        holder.date.text = data[position].date
    }

    override fun getItemCount(): Int = data.size
}

