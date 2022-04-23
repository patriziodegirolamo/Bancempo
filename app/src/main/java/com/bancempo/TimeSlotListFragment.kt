package com.bancempo

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list) {
    var sadvs = mutableListOf<SmallAdv>()

    lateinit var llm: LinearLayoutManager
    lateinit var adapter: SmallAdvAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonProfile = view.findViewById<Button>(R.id.buttonProfile)
        val buttonList = view.findViewById<Button>(R.id.buttonList)
        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)


        llm = LinearLayoutManager(context)
        adapter = SmallAdvAdapter(sadvs)

        val gson = Gson()
        val sharedPref = context?.getSharedPreferences("advs_list.bancempo.lab3", Context.MODE_PRIVATE)
        if( sharedPref == null ){
            with(sharedPref?.edit()){
                this?.putString("json_advs_list", "")
            }?.apply()
        }

        val stringJSON:String? = sharedPref?.getString("json_advs_list", "")
        if(stringJSON != null && stringJSON != ""){
            val myType = object : TypeToken<MutableList<SmallAdv>>() {}.type
            sadvs = gson.fromJson<MutableList<SmallAdv>>(stringJSON, myType)
        }

        if (sadvs.isEmpty()) {
            Toast.makeText(context, "NESSUN ADV", Toast.LENGTH_SHORT).show()
        }

        llm.stackFromEnd = true
        llm.reverseLayout = true
        rv.layoutManager = llm
        rv.adapter = SmallAdvAdapter(sadvs)

        buttonProfile.setOnClickListener {
            findNavController().navigate(R.id.action_timeSlotListFragment_to_showProfileFragment)
        }

        buttonList.setOnClickListener {
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment)
        }


        fab.setOnClickListener {
            val adv = SmallAdv("advertisment${sadvs.size}", "${sadvs.size}/11/2022")
            sadvs.add(adv)


            adapter.notifyItemInserted(0)
            llm.stackFromEnd = true
            llm.reverseLayout = true
            rv.layoutManager = llm
            rv.adapter = SmallAdvAdapter(sadvs)


            val gson = Gson()
            val jsonAdvList = gson.toJson(sadvs)

            val sharedPref = context?.getSharedPreferences("advs_list.bancempo.lab3", Context.MODE_PRIVATE)
            with(sharedPref?.edit()){
                this?.putString("json_advs_list", jsonAdvList)
            }?.apply()

        }


    }



}