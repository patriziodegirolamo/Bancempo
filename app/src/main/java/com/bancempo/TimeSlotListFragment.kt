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



        if (sadvs.isEmpty()) {
            Toast.makeText(context, "NESSUN ADV", Toast.LENGTH_SHORT).show()
        }

        llm = LinearLayoutManager(context)
        llm.stackFromEnd = true
        llm.reverseLayout = true
        rv.layoutManager = llm
        rv.adapter = SmallAdvAdapter(sadvs)

        fab.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("createNewAdv", true)
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment, bundle)
        }

        setFragmentResultListener("confirmationOkCreate") { _, bundle ->
            val newAdv = createAdvFromBundle(bundle)
            advertisementVM.addNewAdv(newAdv)

            val adapter = SmallAdvAdapter(sadvs)
            adapter.notifyItemInserted(0)
            rv.adapter = adapter
            rv.smoothScrollToPosition(0)
        }


        setFragmentResultListener("confirmationOkModifyToList") { _, bundle ->
            val pos = bundle.getInt("position")
            val modAdv = createAdvFromBundle(bundle)

            advertisementVM.modifyAdv(modAdv, pos)
            val adapter = SmallAdvAdapter(sadvs)
            adapter.notifyItemInserted(pos)
            rv.adapter = adapter
        }


        setFragmentResultListener("confirmationOkModifyToDetails2") { _, bundle ->
            val pos = bundle.getInt("position")
            val modAdv = createAdvFromBundle(bundle)

            advertisementVM.modifyAdv(modAdv, pos)
            val adapter = SmallAdvAdapter(sadvs)
            adapter.notifyItemInserted(pos)
            rv.adapter = adapter
        }

    }

    private fun createAdvFromBundle(bundle: Bundle) : SmallAdv{
        val title = bundle.getString("title") ?: ""
        val date = bundle.getString("date") ?: ""
        val description = bundle.getString("description") ?: ""
        val timeslot = bundle.getString("timeslot") ?: ""
        val duration = bundle.getString("duration") ?: ""
        val location = bundle.getString("location") ?: ""
        val note = bundle.getString("note") ?: ""

        return SmallAdv(
            title,
            date,
            description,
            timeslot,
            duration,
            location,
            note
        )
    }
}