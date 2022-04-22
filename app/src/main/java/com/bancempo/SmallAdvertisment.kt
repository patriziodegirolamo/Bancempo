package com.bancempo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

data class SmallAdv(val title:String, val date:String) {
}

class SmallAdvAdapter(val data: MutableList<SmallAdv>) : RecyclerView.Adapter<SmallAdvAdapter.SmallAdvHolder>(){
    class SmallAdvHolder(v:View) : RecyclerView.ViewHolder(v){
        val title: TextView = v.findViewById(R.id.tvSmallAdvTitle)
        val date: TextView = v.findViewById(R.id.tvsmallAdvDate)
        //val modify: ImageView = v.find...


        fun bind(adv: SmallAdv, action: (v:View)-> Unit){
            title.text = adv.title
            date.text = adv.date
            //modify.setOnClickListener(action)
        }

        fun unbind(){
            println("cosa succede su UNbind")
            //modify.setOnClickListener(null)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallAdvHolder {
        //vg significa view group
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.small_advertisment, parent, false)

        return SmallAdvHolder(vg)
    }

    //ti dice quale elemento della lista è correntemente visibile e la sua posizione il lista
    override fun onBindViewHolder(holder: SmallAdvHolder, position: Int) {
        holder.bind(data[position]){

        }


    }

    override fun getItemCount(): Int = data.size
}

