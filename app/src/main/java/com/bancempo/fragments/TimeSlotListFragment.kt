package com.bancempo.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.text.toLowerCase
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bancempo.R
import com.bancempo.SmallAdv
import com.bancempo.models.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import com.bancempo.SmallAdvAdapter as SmallAdvAdapter1


class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list) {
    private val sharedVM: SharedViewModel by activityViewModels()
    private lateinit var spinnerSort: Spinner
    private lateinit var locationFilter: TextView
    private lateinit var searchLocation: EditText
    private lateinit var dateFilter: TextView

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        val emptyListTV = view.findViewById<TextView>(R.id.empty_list_tv)
        val sb = view.findViewById<SearchView>(R.id.search_bar)

        val skill = arguments?.getString("skill")

        spinnerSort = view.findViewById<Spinner>(R.id.sort_spinner)
        locationFilter = view.findViewById<TextView>(R.id.filterLocation)
        searchLocation = view.findViewById<EditText>(R.id.searchLocation)
        dateFilter = view.findViewById<TextView>(R.id.filterDate)


        searchLocation.isVisible = false

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

        var first_click_searchLocation = true
        locationFilter.setOnClickListener {
            if (first_click_searchLocation == true) {
                searchLocation.isVisible = true
                first_click_searchLocation = false
            } else {
                searchLocation.isVisible = false
                first_click_searchLocation = true
            }
        }

        dateFilter.setOnClickListener {
            if (dateFilter.text.toString() == "Filter by date ")
                showDialogOfDatePicker()
            else
                dateFilter.text = "Filter by date "
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


                var searchListOfAdvs: MutableList<SmallAdv> = sadvs.values.toMutableList()
                var newAdapter: com.bancempo.SmallAdvAdapter? = null

                //FILTER BY LOCATION
                val textWatcher = object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                        var newAdvs: List<SmallAdv> = listOf()

                        skill.split(",").forEach {
                            newAdvs = searchListOfAdvs.filter { x ->
                                x.userId != sharedVM.authUser.value!!.email &&
                                        x.location.toLowerCase()
                                            .contains(s.toString().toLowerCase()) &&
                                        checkSkills(x.skill, it)
                            }.toList()
                            newAdapter =
                                SmallAdvAdapter1(newAdvs, false, sharedVM)
                        }

                        if (newAdvs.isEmpty()) {
                            println("----Empty advssss")
                            rv.visibility = View.GONE
                            emptyListTV.visibility = View.VISIBLE
                            emptyListTV.text = "Sorry, no available advertisements for that search!"
                        } else {
                            rv.visibility = View.VISIBLE
                            emptyListTV.visibility = View.GONE
                        }

                        rv.adapter = newAdapter


                        if(dateFilter.text.toString() != "Filter by date "){
                            dateFilter.setText(dateFilter.text.toString() + " ")
                            dateFilter.setText(dateFilter.text.trim())
                            dateFilter.setText(dateFilter.text.toString() + " ")
                        }

                    }
                }
                searchLocation.addTextChangedListener(textWatcher)


                //FILTER BY DATE
                val textWatcherDate = object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                        if (dateFilter.text.toString() == "Filter by date ") {
                            println("------${dateFilter.text}")
                            dateFilter.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_icons8_modifica_il_calendario_24,
                                0
                            )

                            var newAdvs: List<SmallAdv> = listOf()

                            if(searchLocation.text.isNotEmpty() || searchLocation.text.isNotBlank()) {
                                var newAdvs: List<SmallAdv> = listOf()

                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { x ->
                                        x.userId != sharedVM.authUser.value!!.email &&
                                                x.location.toLowerCase()
                                                    .contains(searchLocation.text.toString().toLowerCase()) &&
                                                checkSkills(x.skill, it)
                                    }.toList()
                                    newAdapter =
                                        SmallAdvAdapter1(newAdvs, false, sharedVM)
                                }

                                if (newAdvs.isEmpty()) {
                                    println("----Empty advssss")
                                    rv.visibility = View.GONE
                                    emptyListTV.visibility = View.VISIBLE
                                    emptyListTV.text = "Sorry, no available advertisements for that search!"
                                } else {
                                    rv.visibility = View.VISIBLE
                                    emptyListTV.visibility = View.GONE
                                }
                            }
                            else {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { x ->
                                        x.userId != sharedVM.authUser.value!!.email && checkSkills(
                                            x.skill,
                                            it
                                        )
                                    }.toList()
                                    newAdapter =
                                        SmallAdvAdapter1(newAdvs, false, sharedVM)
                                }

                                if (newAdvs.isEmpty()) {
                                    println("----Empty advssss")
                                    rv.visibility = View.GONE
                                    emptyListTV.visibility = View.VISIBLE
                                    emptyListTV.text =
                                        "Sorry, no available advertisements for that search!"
                                } else {
                                    rv.visibility = View.VISIBLE
                                    emptyListTV.visibility = View.GONE
                                }
                            }

                            rv.adapter = newAdapter


                        } else {
                            dateFilter.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_icons8_xbox_x_48,
                                0
                            )

                            var newAdvs: List<SmallAdv> = listOf()

                            skill.split(",").forEach {
                                newAdvs = searchListOfAdvs.filter { x ->
                                    x.userId != sharedVM.authUser.value!!.email &&
                                            x.date == s.toString().trim() && checkSkills(
                                        x.skill,
                                        it
                                    )
                                }.toList()
                                newAdapter =
                                    SmallAdvAdapter1(newAdvs, false, sharedVM)
                            }

                            if (newAdvs.isEmpty()) {
                                println("----Empty advssss")
                                rv.visibility = View.GONE
                                emptyListTV.visibility = View.VISIBLE
                                emptyListTV.text = "Sorry, no available advertisements for that search!"
                            } else {
                                rv.visibility = View.VISIBLE
                                emptyListTV.visibility = View.GONE
                            }
                            if(searchLocation.text.isNotEmpty() || searchLocation.text.isNotBlank()) {
                                var newAdvs: List<SmallAdv> = listOf()

                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { x ->
                                        x.userId != sharedVM.authUser.value!!.email &&
                                                x.location.toLowerCase()
                                                    .contains(searchLocation.text.toString().toLowerCase()) &&
                                                checkSkills(x.skill, it) && x.date == s.toString().trim()
                                    }.toList()
                                    newAdapter =
                                        SmallAdvAdapter1(newAdvs, false, sharedVM)
                                }

                                if (newAdvs.isEmpty()) {
                                    println("----Empty advssss")
                                    rv.visibility = View.GONE
                                    emptyListTV.visibility = View.VISIBLE
                                    emptyListTV.text = "Sorry, no available advertisements for that search!"
                                } else {
                                    rv.visibility = View.VISIBLE
                                    emptyListTV.visibility = View.GONE
                                }
                            }
                        }

                        rv.adapter = newAdapter

                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {

                    }
                }
                dateFilter.addTextChangedListener(textWatcherDate)

                //FILTER BY SEARCHBAR
                sb.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {

                        var newAdvs: List<SmallAdv> = listOf()

                        skill.split(",").forEach {
                            newAdvs = searchListOfAdvs.filter { x ->
                                x.userId != sharedVM.authUser.value!!.email &&
                                        x.title.toLowerCase()
                                            .contains(newText.toLowerCase()) && checkSkills(
                                    x.skill,
                                    it
                                )
                            }.toList()
                            newAdapter =
                                SmallAdvAdapter1(newAdvs, false, sharedVM)
                        }

                        if (newAdvs.isEmpty()) {
                            println("----Empty advssss")
                            rv.visibility = View.GONE
                            emptyListTV.visibility = View.VISIBLE
                            emptyListTV.text = "Sorry, no available advertisements for that search!"
                        } else {
                            rv.visibility = View.VISIBLE
                            emptyListTV.visibility = View.GONE
                        }
                        rv.adapter = newAdapter
                        return false
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        // task HERE
                        return false
                    }

                })

                println("----advssss list $searchListOfAdvs")

                //GENERALLY
                if (searchListOfAdvs.isEmpty()) {
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
                    newAdapter =
                        SmallAdvAdapter1(sadvs.values.filter { adv ->
                            adv.userId != sharedVM.authUser.value!!.email &&
                                    checkSkills(adv.skill, it)
                        }.toList().sortedBy { adv -> adv.title }, false, sharedVM)
                }
                searchListOfAdvs = sadvs.values.toMutableList()
                rv.adapter = newAdapter

                //SORT ADVS
                spinnerSort.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if(pos == 0){
                            if((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty())
                                && (dateFilter.text.toString() != "Filter by date ")) {
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.location.toLowerCase()
                                                .contains(searchLocation.text.toString().toLowerCase())
                                                    && adv.date.toLowerCase()
                                                .contains(dateFilter.text.toString().toLowerCase())
                                        }.toList().sortedBy { adv -> adv.title }, false, sharedVM)
                                }
                            }
                            else if((locationFilter.text.isBlank() || locationFilter.text.isEmpty()) &&
                                (dateFilter.text.toString() != "Filter by date ")){
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.date.toLowerCase()
                                                .contains(dateFilter.text.toString().toLowerCase())
                                        }.toList().sortedBy { adv -> adv.title }, false, sharedVM)
                                }
                            }
                            else if((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty()) &&
                                (dateFilter.text.toString() == "Filter by date ")){
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.location.toLowerCase()
                                                .contains(searchLocation.text.toString().toLowerCase())
                                        }.toList().sortedBy { adv -> adv.title }, false, sharedVM)
                                }
                            }
                            else{
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                        }.toList().sortedBy { adv -> adv.title }, false, sharedVM)
                                }
                            }

                            rv.adapter = newAdapter
                        }
                       else if(pos == 1){
                            if((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty())
                                && (dateFilter.text.toString() != "Filter by date ")) {
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.location.toLowerCase()
                                                .contains(searchLocation.text.toString().toLowerCase())
                                                    && adv.date.toLowerCase()
                                                .contains(dateFilter.text.toString().toLowerCase())
                                        }.toList().sortedBy { adv ->
                                            val arr = adv.date.split("/")
                                            val dd = arr[0]
                                            val mm = arr[1]
                                            val yyyy = arr[2]
                                            val new_date = yyyy+"/"+mm+"/"+dd
                                            new_date
                                        }, false, sharedVM)
                                }
                            }
                            else if((locationFilter.text.isBlank() || locationFilter.text.isEmpty()) &&
                                (dateFilter.text.toString() != "Filter by date ")){
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.date.toLowerCase()
                                                .contains(dateFilter.text.toString().toLowerCase())
                                        }.toList().sortedBy { adv ->
                                            val arr = adv.date.split("/")
                                            val dd = arr[0]
                                            val mm = arr[1]
                                            val yyyy = arr[2]
                                            val new_date = yyyy+"/"+mm+"/"+dd
                                            new_date
                                        }, false, sharedVM)
                                }
                            }
                            else if((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty()) &&
                                (dateFilter.text.toString() == "Filter by date ")){
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.location.toLowerCase()
                                                .contains(searchLocation.text.toString().toLowerCase())
                                        }.toList().sortedBy { adv ->
                                            val arr = adv.date.split("/")
                                            val dd = arr[0]
                                            val mm = arr[1]
                                            val yyyy = arr[2]
                                            val new_date = yyyy+"/"+mm+"/"+dd
                                            new_date
                                        }, false, sharedVM)
                                }
                            }
                            else{
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                        }.toList().sortedBy { adv ->
                                            val arr = adv.date.split("/")
                                            val dd = arr[0]
                                            val mm = arr[1]
                                            val yyyy = arr[2]
                                            val new_date = yyyy+"/"+mm+"/"+dd
                                            new_date
                                        }, false, sharedVM)
                                }
                            }

                            rv.adapter = newAdapter

                        }
                        else if (pos == 2) {
                            println("-----SORTED DESCENDING DATE ")

                            if((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty())
                                && (dateFilter.text.toString() != "Filter by date ")) {
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.location.toLowerCase()
                                                .contains(searchLocation.text.toString().toLowerCase())
                                                    && adv.date.toLowerCase()
                                                .contains(dateFilter.text.toString().toLowerCase())
                                        }.toList().sortedByDescending { adv ->
                                            val arr = adv.date.split("/")
                                            val dd = arr[0]
                                            val mm = arr[1]
                                            val yyyy = arr[2]
                                            val new_date = yyyy+"/"+mm+"/"+dd
                                            new_date
                                        }, false, sharedVM)
                                }
                            }
                            else if((locationFilter.text.isBlank() || locationFilter.text.isEmpty()) &&
                                (dateFilter.text.toString() != "Filter by date ")){
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.date.toLowerCase()
                                                .contains(dateFilter.text.toString().toLowerCase())
                                        }.toList().sortedByDescending { adv ->
                                            val arr = adv.date.split("/")
                                            val dd = arr[0]
                                            val mm = arr[1]
                                            val yyyy = arr[2]
                                            val new_date = yyyy+"/"+mm+"/"+dd
                                            new_date
                                        }, false, sharedVM)
                                }
                            }
                            else if((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty()) &&
                                (dateFilter.text.toString() == "Filter by date ")){
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.location.toLowerCase()
                                                .contains(searchLocation.text.toString().toLowerCase())
                                        }.toList().sortedByDescending { adv ->
                                            val arr = adv.date.split("/")
                                            val dd = arr[0]
                                            val mm = arr[1]
                                            val yyyy = arr[2]
                                            val new_date = yyyy+"/"+mm+"/"+dd
                                            new_date
                                        }, false, sharedVM)
                                }
                            }
                            else{
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                        }.toList().sortedByDescending { adv ->
                                            val arr = adv.date.split("/")
                                            val dd = arr[0]
                                            val mm = arr[1]
                                            val yyyy = arr[2]
                                            val new_date = yyyy+"/"+mm+"/"+dd
                                            new_date
                                        }, false, sharedVM)
                                }
                            }

                            rv.adapter = newAdapter


                        } else if (pos == 3) {
                            println("-----SORTED ASCENDING TITLE ")

                            if((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty())
                                && (dateFilter.text.toString() != "Filter by date ")) {
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.location.toLowerCase()
                                                .contains(searchLocation.text.toString().toLowerCase())
                                                    && adv.date.toLowerCase()
                                                .contains(dateFilter.text.toString().toLowerCase())
                                        }.toList().sortedBy { adv -> adv.title }, false, sharedVM)
                                }
                            }
                            else if((locationFilter.text.isBlank() || locationFilter.text.isEmpty()) &&
                                (dateFilter.text.toString() != "Filter by date ")){
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.date.toLowerCase()
                                                .contains(dateFilter.text.toString().toLowerCase())
                                        }.toList().sortedBy { adv -> adv.title }, false, sharedVM)
                                }
                            }
                            else if((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty()) &&
                                (dateFilter.text.toString() == "Filter by date ")){
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.location.toLowerCase()
                                                .contains(searchLocation.text.toString().toLowerCase())
                                        }.toList().sortedBy { adv -> adv.title }, false, sharedVM)
                                }
                            }
                            else{
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                        }.toList().sortedBy { adv -> adv.title }, false, sharedVM)
                                }
                            }


                            rv.adapter = newAdapter



                        } else if (pos == 4) {
                            println("-----SORTED DESCENDING TITLE ")

                            if((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty())
                                && (dateFilter.text.toString() != "Filter by date ")) {
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.location.toLowerCase()
                                                .contains(searchLocation.text.toString().toLowerCase())
                                                    && adv.date.toLowerCase()
                                                .contains(dateFilter.text.toString().toLowerCase())
                                        }.toList().sortedByDescending { adv -> adv.title }, false, sharedVM)
                                }
                            }
                            else if((locationFilter.text.isBlank() || locationFilter.text.isEmpty()) &&
                                (dateFilter.text.toString() != "Filter by date ")){
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.date.toLowerCase()
                                                .contains(dateFilter.text.toString().toLowerCase())
                                        }.toList().sortedByDescending { adv -> adv.title }, false, sharedVM)
                                }
                            }
                            else if((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty()) &&
                                (dateFilter.text.toString() == "Filter by date ")){
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                                    && adv.location.toLowerCase()
                                                .contains(searchLocation.text.toString().toLowerCase())
                                        }.toList().sortedByDescending { adv -> adv.title }, false, sharedVM)
                                }
                            }
                            else{
                                skill.split(",").forEach {
                                    newAdapter =
                                        SmallAdvAdapter1(searchListOfAdvs.filter { adv ->
                                            adv.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(adv.skill, it)
                                        }.toList().sortedByDescending { adv -> adv.title }, false, sharedVM)
                                }
                            }


                            rv.adapter = newAdapter

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

    //Date
    private fun showDialogOfDatePicker() {
        val datePickerFragment = TimeSlotListFragment.DatePickerFragment(dateFilter)
        datePickerFragment.show(requireActivity().supportFragmentManager, "datePicker")

    }


    class DatePickerFragment(private val date: TextView) :
        DialogFragment(), DatePickerDialog.OnDateSetListener {

        private var c = Calendar.getInstance()
        private var year = c.get(Calendar.YEAR)
        private var month = c.get(Calendar.MONTH)
        private var day = c.get(Calendar.DAY_OF_MONTH)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            c.set(year, month, day)

            retainInstance = true
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)

        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            return DatePickerDialog(requireContext(), this, year, month, day)
        }

        @SuppressLint("SetTextI18n")
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            this.year = year
            this.month = month
            this.day = day
            date.text = ("${day}/${(month + 1)}/${year} ")
        }

    }
}