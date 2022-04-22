package com.bancempo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonProfile = view.findViewById<Button>(R.id.buttonProfile)
        val buttonList = view.findViewById<Button>(R.id.buttonList)


        buttonProfile.setOnClickListener{
            findNavController().navigate(R.id.action_timeSlotListFragment_to_showProfileFragment)
        }

        buttonList.setOnClickListener{
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment)
        }

        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(context)
        val adapter = SmallAdvAdapter(createAdvs(15))
        rv.adapter = adapter
    }

    private fun createAdvs(n: Int): List<SmallAdv>{
        val l = mutableListOf<SmallAdv>()
        val end = n%28
        for(i in 1..end){
            val k = SmallAdv("adv${i}", "${i}/01/12")
            l.add(k)
        }
        return l
    }
}