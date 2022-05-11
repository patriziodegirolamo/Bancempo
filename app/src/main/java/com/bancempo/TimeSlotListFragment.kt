package com.bancempo

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list) {
    private val sharedVM: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        val emptyListTV = view.findViewById<TextView>(R.id.empty_list_tv)

        fab.setOnClickListener {
            val bundle = Bundle()
            bundle.putBoolean("createNewAdv", true)
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment, bundle)
        }

        sharedVM.advs.observe(viewLifecycleOwner) { sadvs ->
            if (sadvs.isEmpty()) {
                rv.visibility = View.GONE
                emptyListTV.visibility = View.VISIBLE
            } else {
                rv.visibility = View.VISIBLE
                emptyListTV.visibility = View.GONE
            }

            rv.layoutManager = LinearLayoutManager(context)
            rv.adapter = SmallAdvAdapter(sadvs.values.sortedByDescending { x -> x.creationTime }.toList())

            setFragmentResultListener("confirmationOkCreate") { _, _ ->
                val adapter = SmallAdvAdapter(sadvs.values.sortedByDescending { x -> x.creationTime }.toList())
                adapter.notifyItemInserted(0)
                rv.adapter = adapter
            }

        }

    }

}