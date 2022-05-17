package com.bancempo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bancempo.ItemAdapter
import com.bancempo.R
import com.bancempo.models.SharedViewModel


class ListSkillsFragment : Fragment(R.layout.fragment_list_skills) {

    private val sharedVM: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        sharedVM.services.observe(viewLifecycleOwner) { services ->

            val rv = view.findViewById<RecyclerView>(R.id.rvSkill)
            rv.layoutManager = LinearLayoutManager(findNavController().context)

            val adapter =
                ItemAdapter(services.values.sortedByDescending { x -> x.creationTime }.toList())
            rv.adapter = adapter
        }



    }


}