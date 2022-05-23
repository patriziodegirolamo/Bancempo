package com.bancempo.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bancempo.ItemAdapter
import com.bancempo.R
import com.bancempo.SmallAdv
import com.bancempo.models.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.bancempo.SmallAdvAdapter as SmallAdvAdapter1


class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list) {
    private val sharedVM: SharedViewModel by activityViewModels()
    private lateinit var spinnerSort: Spinner

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        val emptyListTV = view.findViewById<TextView>(R.id.empty_list_tv)
        //   val spinnerFilter = view.findViewById<TextView>(R.id.filter_spinner)
        val sb = view.findViewById<SearchView>(R.id.search_bar)

        val skill = arguments?.getString("skill")

        val spinnerSort = view.findViewById<Spinner>(R.id.sort_spinner)

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this.requireContext(),
            R.array.sort,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinnerSort.adapter = adapter
        }



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
                    SmallAdvAdapter1(sadvs.values.sortedByDescending { x -> x.creationTime }
                        .toList(), true, sharedVM)

                setFragmentResultListener("confirmationOkCreate") { _, _ ->
                    val adapter =
                        SmallAdvAdapter1(sadvs.values.sortedByDescending { x -> x.creationTime }
                            .toList(), true, sharedVM)
                    adapter.notifyItemInserted(0)
                    rv.adapter = adapter
                }

            }
        } else {


            fab.isVisible = false
            sharedVM.advs.observe(viewLifecycleOwner) { sadvs ->
                println("---- ${sadvs}")
                println("----- ${sharedVM.authUser.value!!.email}")

                var searchListOfAdvs: MutableList<SmallAdv> = sadvs.values.toMutableList()

                sb.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {

                        searchListOfAdvs = sadvs.values.filter { x -> x.title.toLowerCase().contains(newText.toLowerCase()) && x.skill == skill}.toMutableList()
                        if (searchListOfAdvs.isEmpty() ) {
                            println("----Empty advssss")
                            rv.visibility = View.GONE
                            emptyListTV.visibility = View.VISIBLE
                            emptyListTV.text = "Sorry, no available advertisements for that search!"
                        } else {
                            rv.visibility = View.VISIBLE
                            emptyListTV.visibility = View.GONE
                        }

                        val newAdapter = SmallAdvAdapter1(searchListOfAdvs.toList(), false, sharedVM)
                        rv.adapter = newAdapter
                        return false
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        // task HERE
                        return false
                    }

                })

                println("----advssss list $searchListOfAdvs")

                if (searchListOfAdvs.filter { adv -> adv.userId != sharedVM.authUser.value!!.email && adv.skill == skill }
                        .toList().isEmpty() ) {
                    println("----Empty advssss")
                    rv.visibility = View.GONE
                    emptyListTV.visibility = View.VISIBLE
                    emptyListTV.text = "Sorry, no available advertisements for that category!"
                } else {
                    rv.visibility = View.VISIBLE
                    emptyListTV.visibility = View.GONE
                }

                rv.layoutManager = LinearLayoutManager(context)
                println("---ACTUAL SKILL $skill")



                skill.split(",").forEach {
                    rv.adapter =
                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                            checkSkills(adv.skill, it)
                        }.toList().sortedBy { adv -> adv.date }, false, sharedVM)
                }

                spinnerSort.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if (pos == 1) {
                            skill.split(",").forEach {
                                rv.adapter =
                                    SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                        checkSkills(adv.skill, it)
                                    }.toList().sortedByDescending { adv -> adv.date }, false, sharedVM)
                            }
                        } else if (pos == 2){
                            skill.split(",").forEach {
                                rv.adapter =
                                    SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                        checkSkills(adv.skill, it)
                                    }.toList().sortedBy { adv -> adv.title }, false, sharedVM)
                            }
                        } else if (pos == 3){
                            skill.split(",").forEach {
                                rv.adapter =
                                    SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                        checkSkills(adv.skill, it)
                                    }.toList().sortedByDescending { adv -> adv.title }, false, sharedVM)
                            }
                        } else {
                            skill.split(",").forEach {
                                rv.adapter =
                                    SmallAdvAdapter1(
                                        searchListOfAdvs.filter { adv ->
                                            checkSkills(adv.skill, it)
                                        }.toList().sortedBy { adv -> adv.date },
                                        false,
                                        sharedVM
                                    )
                            }
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>?) {}
                })

            }
        }
    }

    private fun checkSkills(advSkill: String, skill: String): Boolean {
        var valid = false;
        advSkill.split(",").forEach {
            if (it == skill) {
                valid = true;
            }
        }
        return valid;
    }

}

