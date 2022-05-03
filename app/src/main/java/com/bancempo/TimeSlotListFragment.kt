package com.bancempo

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list) {
    private val advertisementVM: AdvertismentsVM by activityViewModels()
    private lateinit var llm: LinearLayoutManager
    private lateinit var adapter: SmallAdvAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sadvs = advertisementVM.advs.value ?: mutableListOf()
        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)


        llm = LinearLayoutManager(context)
        adapter = SmallAdvAdapter(sadvs)

        if (sadvs.isEmpty()) {
            Toast.makeText(context, "NESSUN ADV", Toast.LENGTH_SHORT).show()
        }

        llm.stackFromEnd = true
        llm.reverseLayout = true
        rv.layoutManager = llm
        rv.adapter = SmallAdvAdapter(sadvs)




        fab.setOnClickListener {
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment)
        }

        setFragmentResultListener("confirmationOk"){ _, bundle ->
            println("--------------------- conf")
        }

        setFragmentResultListener("confirmationOkCreate") { _, bundle ->

            val fromDetails = bundle.getBoolean("fromDetails")
            println("---------------$fromDetails in oncreate")

            val title = bundle.getString("title") ?: ""
            val date = bundle.getString("date") ?: ""
            val description = bundle.getString("description") ?: ""
            val timeslot = bundle.getString("timeslot") ?: ""
            val duration = bundle.getString("duration") ?: ""
            val location = bundle.getString("location") ?: ""
            val note = bundle.getString("note") ?: ""

            val newAdv = SmallAdv(
                title,
                date,
                description,
                timeslot,
                duration,
                location,
                note
            )
            advertisementVM.addNewAdv(newAdv)

            adapter.notifyItemInserted(0)
            llm.stackFromEnd = true
            llm.reverseLayout = true
            rv.layoutManager = llm
            rv.adapter = SmallAdvAdapter(sadvs)


            val myGson = Gson()
            val jsonAdvList = myGson.toJson(sadvs)

            val mySharedPref =
                context?.getSharedPreferences("advs_list.bancempo.lab3", Context.MODE_PRIVATE)
            with(mySharedPref?.edit()) {
                this?.putString("json_advs_list", jsonAdvList)
            }?.apply()
        }



        setFragmentResultListener("confirmationOkModify") { _, bundle ->

            val fromDetails = bundle.getBoolean("fromDetails")
            println("---------------$fromDetails in modify")
            val pos = bundle.getInt("position") ?: -1
            val title = bundle.getString("title") ?: ""
            val date = bundle.getString("date") ?: ""
            val description = bundle.getString("description") ?: ""
            val timeslot = bundle.getString("timeslot") ?: ""
            val duration = bundle.getString("duration") ?: ""
            val location = bundle.getString("location") ?: ""
            val note = bundle.getString("note") ?: ""

            val newAdv = SmallAdv(
                title,
                date,
                description,
                timeslot,
                duration,
                location,
                note
            )

            sadvs[pos] = newAdv




            llm.stackFromEnd = true
            llm.reverseLayout = true
            rv.layoutManager = llm
            rv.adapter = SmallAdvAdapter(sadvs)


            val myGson = Gson()
            val jsonAdvList = myGson.toJson(sadvs)

            val mySharedPref =
                context?.getSharedPreferences("advs_list.bancempo.lab3", Context.MODE_PRIVATE)

            mySharedPref?.edit()?.clear()?.apply()
            with(mySharedPref?.edit()) {
                this?.putString("json_advs_list", jsonAdvList)
            }?.apply()
        }

    }


}