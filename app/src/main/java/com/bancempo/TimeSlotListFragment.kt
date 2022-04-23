package com.bancempo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list) {
    var newPos = 0

    var sadvs = listOf<SmallAdv>()
    val vm by viewModels<SimpleVM>()

    //serve solo per fare il clear della lista!
    var firstTime = true

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
        rv.layoutManager = llm
        rv.adapter = adapter

        vm.totAdv.observe(viewLifecycleOwner) {

        }
        vm.advs.observe(viewLifecycleOwner) { list ->
            if (firstTime) {
                //vm.clear()
            }
            newPos = list.size
            //list.forEach{println("----------------------------------${it.toString()}")}
            sadvs = list.map { it.toSmallAdv() }
            if (list.isEmpty()) {
                Toast.makeText(context, "NESSUN ADV", Toast.LENGTH_SHORT).show()
            }

            println("---------------initialize list inside")
            rv.layoutManager = llm
            rv.adapter = SmallAdvAdapter(sadvs)
            firstTime = false

        }


        buttonProfile.setOnClickListener {
            findNavController().navigate(R.id.action_timeSlotListFragment_to_showProfileFragment)
        }

        buttonList.setOnClickListener {
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment)
        }

        fab.setOnClickListener {
            vm.add(newPos)

            println("-----------------add elem to list")

            llm.stackFromEnd = true
            adapter.notifyItemInserted(newPos)
        }

    }


}