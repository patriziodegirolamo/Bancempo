package com.bancempo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

data class SmallAdv(val title:String, val date:String, val description:String, val time:String, val duration:String, val location:String, val note:String)

class SmallAdvAdapter(private val data: List<SmallAdv>) : RecyclerView.Adapter<SmallAdvAdapter.SmallAdvHolder>(){
    class SmallAdvHolder(v:View) : RecyclerView.ViewHolder(v){
        private val title: TextView = v.findViewById(R.id.tvSmallAdvTitle)
        private val date: TextView = v.findViewById(R.id.tvsmallAdvDate)
        private val edit: FloatingActionButton = v.findViewById((R.id.edit_adv))
        //val modify: ImageView = v.find...


        fun bind(adv: SmallAdv){
            title.text = adv.title
            date.text = "Date: ${adv.date}"


            edit.setOnClickListener{
                val bundle = Bundle()
                bundle.putString("title", adv.title)
                bundle.putString("date", adv.date)
                bundle.putString("description", adv.description)
                bundle.putString("time", adv.time)
                bundle.putString("duration", adv.duration)
                bundle.putString("location", adv.location)
                bundle.putString("note", adv.note)

                findNavController(it).navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment, bundle)
            }
        }

        fun unbind(){
            //modify.setOnClickListener(null)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmallAdvHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.small_advertisment, parent, false)

        return SmallAdvHolder(vg)
    }

    //ti dice quale elemento della lista è correntemente visibile e la sua posizione il lista
    override fun onBindViewHolder(holder: SmallAdvHolder, position: Int) {
        holder.bind(data[position])

        holder.itemView.setOnClickListener{
            //TODO passare come argomenti: title, data, ecc
            val bundle = Bundle()
            bundle.putString("title", data[position].title)
            bundle.putString("date", data[position].date)
            bundle.putString("description", data[position].description)
            bundle.putString("time", data[position].time)
            bundle.putString("duration", data[position].duration)
            bundle.putString("location", data[position].location)
            bundle.putString("note", data[position].note)


            findNavController(it).navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment, bundle)
        }
    }

    override fun getItemCount(): Int = data.size


}