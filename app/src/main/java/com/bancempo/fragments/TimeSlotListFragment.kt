package com.bancempo.fragments

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bancempo.R
import com.bancempo.models.SharedViewModel
import com.bancempo.SmallAdvAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list) {
    private val sharedVM: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        val emptyListTV = view.findViewById<TextView>(R.id.empty_list_tv)

        val skill = arguments?.getString("skill")

        if (skill == null) {
            fab.isVisible = true

            fab.setOnClickListener {
                val bundle = Bundle()
                bundle.putBoolean("createNewAdv", true)
                findNavController().navigate(
                    R.id.action_timeSlotListFragment_to_timeSlotEditFragment,
                    bundle
                )
            }

            sharedVM.myAdvs.observe(viewLifecycleOwner) { sadvs ->
                if (sadvs.isEmpty()) {
                    rv.visibility = View.GONE
                    emptyListTV.visibility = View.VISIBLE
                    emptyListTV.text = "No advertisements yet available. Create one now!"
                } else {
                    rv.visibility = View.VISIBLE
                    emptyListTV.visibility = View.GONE
                }

                rv.layoutManager = LinearLayoutManager(context)
                rv.adapter =
                    SmallAdvAdapter(sadvs.values.sortedByDescending { x -> x.creationTime }
                        .toList(), true)

                setFragmentResultListener("confirmationOkCreate") { _, _ ->
                    val adapter =
                        SmallAdvAdapter(sadvs.values.sortedByDescending { x -> x.creationTime }
                            .toList(), true)
                    adapter.notifyItemInserted(0)
                    rv.adapter = adapter
                }

            }
        }
        else{
            fab.isVisible = false
            sharedVM.advs.observe(viewLifecycleOwner) { sadvs ->
                if (sadvs.values.filter { adv -> adv.userId != sharedVM.authUser.value!!.email && adv.skill == skill}
                        .toList().isEmpty()) {
                    rv.visibility = View.GONE
                    emptyListTV.visibility = View.VISIBLE
                    emptyListTV.text = "Sorry, no available advertisements for that category!"
                } else {
                    rv.visibility = View.VISIBLE
                    emptyListTV.visibility = View.GONE
                }

                rv.layoutManager = LinearLayoutManager(context)
                println("---ACTUAL SKILL $skill")
                rv.adapter =
                    SmallAdvAdapter(sadvs.values.filter { adv -> adv.skill.contains(skill)}
                        .toList(), false)

            }
        }
    }

}