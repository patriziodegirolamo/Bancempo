package com.bancempo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list){
    var newPos = 0
    lateinit var adapter : SmallAdvAdapter
    lateinit var llm : LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonProfile = view.findViewById<Button>(R.id.buttonProfile)
        val buttonList = view.findViewById<Button>(R.id.buttonList)
        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)

        val advs:MutableList<SmallAdv> = createInitialListOfAdvs(15)

        buttonProfile.setOnClickListener{
            findNavController().navigate(R.id.action_timeSlotListFragment_to_showProfileFragment)
        }

        buttonList.setOnClickListener{
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment)
        }

        //se schiaccio il fab (a runtime!), setto llm e adapter
        fab.setOnClickListener{
            advs.add(newPos, SmallAdv("adv${newPos}", "${newPos}/01/12"))
            newPos++

            //ricarico adapter e llm
            adapter = SmallAdvAdapter(advs)
            llm = LinearLayoutManager(context)

            //setto adapter e llm
            llm.stackFromEnd = true
            adapter.notifyItemInserted(advs.size)

            //setto la view
            rv.layoutManager = llm
            rv.adapter = adapter

        }

        //di default setto llm e adapter
        llm = LinearLayoutManager(context)
        adapter = SmallAdvAdapter(advs)
        rv.layoutManager = llm
        rv.adapter = adapter
    }

    private fun createInitialListOfAdvs(n: Int): MutableList<SmallAdv>{
        val l = mutableListOf<SmallAdv>()
        newPos = n
        for(i in 0..n-1){
            val k = SmallAdv("adv${i}", "${i}/01/12")
            l.add(k)
        }
        return l
    }

}