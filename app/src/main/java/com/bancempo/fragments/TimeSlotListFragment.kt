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
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bancempo.R
import com.bancempo.data.SmallAdv
import com.bancempo.models.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
import com.bancempo.data.SmallAdvAdapter as SmallAdvAdapter1
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

//TODO BARRA DI RICERCA IN COMBO CON ALTRI FILTRI

class TimeSlotListFragment : Fragment(R.layout.fragment_time_slot_list) {

    private var advGen: Boolean = false
    private val sharedVM: SharedViewModel by activityViewModels()
    private lateinit var spinnerSort: Spinner
    private lateinit var locationFilter: TextView
    private lateinit var searchLocation: EditText
    private lateinit var dateFilter: TextView
    private lateinit var reservedSpinner: Spinner

    private lateinit var rv: RecyclerView
    private lateinit var emptyListTV: TextView

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_filter_adv, menu)
        val sortButton = menu.findItem(R.id.sort)
        val filterButton = menu.findItem(R.id.filter)

        if (advGen) {
            sortButton.isVisible = true
            filterButton.isVisible = true
        }


    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        rv = view.findViewById(R.id.recyclerView)
        emptyListTV = view.findViewById(R.id.empty_list_tv)
        val sb = view.findViewById<SearchView>(R.id.search_bar)

        val skill = arguments?.getString("skill")

        spinnerSort = view.findViewById(R.id.sort_spinner)
        reservedSpinner = view.findViewById(R.id.reservedSpinner)
        locationFilter = view.findViewById(R.id.filterLocation)
        searchLocation = view.findViewById(R.id.searchLocation)
        dateFilter = view.findViewById(R.id.filterDate)
        val myInterests = arguments?.getBoolean("myInterests")
        val myReservations = arguments?.getBoolean("myReservations")

        reservedSpinner.isVisible = !(myReservations == null || !myReservations)

        if (skill != null) {
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

            var firstClickSearchlocation = true
            locationFilter.setOnClickListener {
                if (firstClickSearchlocation) {
                    searchLocation.isVisible = true
                    firstClickSearchlocation = false
                } else {
                    searchLocation.isVisible = false
                    firstClickSearchlocation = true
                }
            }

            dateFilter.setOnClickListener {
                if (dateFilter.text.toString() == getString(R.string.date))
                    showDialogOfDatePicker()
                else
                    dateFilter.text = getString(R.string.date)
            }
        } else {
            //TODO FEDERICO : TOGLIERE VISIBILITA' ICONE FILTRO E SORT DALLA BARRA DI NAVIGAZIONE
            searchLocation.isVisible = false
            locationFilter.isVisible = false
            dateFilter.isVisible = false
            spinnerSort.isVisible = false
        }

        fab.isVisible = false

        if (skill == null) {

            if (myInterests != null && myInterests) {
                //Page MY INTERESTS
                (activity as AppCompatActivity).supportActionBar?.title = "My Interests"
                var interests: MutableList<SmallAdv>

                rv.layoutManager = LinearLayoutManager(context)

                sharedVM.advs.observe(viewLifecycleOwner) { advs ->
                    val convs = sharedVM.conversations.value

                    interests = mutableListOf()
                    //Trovo gli annunci per cui ho una conversazione in stato CLOSED = FALSE e BOOKED = FALSE
                    if (advs != null && convs != null) {
                        val myOpenedConvs = convs.values.filter { conv ->
                            conv.idAsker == sharedVM.currentUser.value!!.email && !conv.closed
                        }

                        myOpenedConvs.forEach { conv ->
                            val filtered =
                                advs.values.filter { adv -> adv.id == conv.idAdv && !adv.booked }
                            if (filtered.isNotEmpty()) {
                                interests.add(interests.size, filtered.elementAt(0))
                            }
                        }
                        renderAdvList(interests.toList(), false, false)
                    }
                    //Filter by SearchBar
                    sb.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(newText: String): Boolean {

                            val newMyInterestsAdvs: List<SmallAdv> = interests.filter { x ->
                                x.title.lowercase()
                                    .contains(newText.lowercase())
                            }.toList()

                            renderAdvList(newMyInterestsAdvs, false, false)
                            return false
                        }

                        override fun onQueryTextSubmit(query: String): Boolean {
                            // task HERE
                            return false
                        }

                    })
                }
            } else if (myReservations != null && myReservations) {
                //Page MY RESERVATIONS
                (activity as AppCompatActivity).supportActionBar?.title = "My Reservations"
                var reservations: MutableList<SmallAdv> = mutableListOf()
                rv.layoutManager = LinearLayoutManager(context)

                reservedSpinner.onItemSelectedListener = object :
                    AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        if (pos == 0) {
                            //BY ME
                            //Sono gli interests che però sono booked
                            sharedVM.bookedAdvs.observe(viewLifecycleOwner) { advs ->
                                val convs = sharedVM.conversations.value

                                reservations = mutableListOf()
                                //Trovo gli annunci per cui ho una conversazione in stato CLOSED = FALSE e BOOKED = FALSE
                                if (advs != null && convs != null) {
                                    val myOpenedConvs = convs.values.filter { conv ->
                                        conv.idAsker == sharedVM.currentUser.value!!.email && !conv.closed
                                    }

                                    myOpenedConvs.forEach { conv ->
                                        val filtered =
                                            advs.values.filter { adv -> adv.id == conv.idAdv }
                                        if (filtered.isNotEmpty()) {
                                            reservations.add(
                                                reservations.size,
                                                filtered.elementAt(0)
                                            )
                                        }
                                    }
                                    renderAdvList(reservations.toList(), false, false)
                                }
                            }
                        } else if (pos == 1) {
                            //BY OTHERS
                            //Carico gli adv booked di cui sono il creatore
                            sharedVM.bookedAdvs.observe(viewLifecycleOwner) { advs ->
                                reservations = mutableListOf()
                                if (advs.isNotEmpty()) {
                                    reservations =
                                        advs.values.filter { adv -> adv.userId == sharedVM.currentUser.value!!.email }
                                            .toMutableList()
                                }
                                renderAdvList(reservations.toList(), false, true)
                            }
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>?) {}
                }

                //Filter by SearchBar
                sb.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String): Boolean {

                        val newMyInterestsAdvs: List<SmallAdv> = reservations.filter { x ->
                            x.title.lowercase()
                                .contains(newText.lowercase())
                        }.toList()

                        renderAdvList(newMyInterestsAdvs, false, true)
                        return false
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        // task HERE
                        return false
                    }

                })
            } else {
                fab.isVisible = true
                (activity as AppCompatActivity).supportActionBar?.title = "My Advertisements"

                fab.setOnClickListener {

                    if(sharedVM.currentUser.value!!.skills.isNotEmpty())
                    { val bundle = Bundle()
                        bundle.putBoolean("createNewAdv", true)
                        findNavController().navigate(
                            R.id.action_timeSlotListFragment_to_timeSlotEditFragment,
                            bundle
                        )}
                    else
                        Toast.makeText(
                            requireContext(),
                            "Please add skills in profile!",
                            Toast.LENGTH_SHORT
                        ).show()

                }

                sharedVM.myAdvs.observe(viewLifecycleOwner) { sadvs ->
                    rv.layoutManager = LinearLayoutManager(context)

                    renderAdvList(
                        sadvs.values.filter { adv -> !adv.booked }.toList()
                            .sortedBy { adv -> adv.title }, true, false
                    )

                    //FILTER BY SEARCHBAR
                    sb.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextChange(newText: String): Boolean {

                            val newMyAdvs: List<SmallAdv> =
                                sadvs.values.toList().sortedBy { adv -> adv.title }.filter { x ->
                                    x.title.lowercase()
                                        .contains(newText.lowercase())
                                }.toList()
                            renderAdvList(newMyAdvs, true, false)
                            return false
                        }

                        override fun onQueryTextSubmit(query: String): Boolean {
                            // task HERE
                            return false
                        }

                    })
                }
            }
        } else {
            fab.isVisible = false
            advGen = true
            sharedVM.advs.observe(viewLifecycleOwner) { sadvs ->

                var searchListOfAdvs: MutableList<SmallAdv> = sadvs.values.toMutableList()
                rv.layoutManager = LinearLayoutManager(context)

                skill.split(",").forEach {
                    searchListOfAdvs = sadvs.values.filter { adv ->
                        adv.userId != sharedVM.authUser.value!!.email &&
                                checkSkills(adv.skill, it)
                    }.toList().sortedBy { adv -> adv.title }.toMutableList()
                }
                renderAdvList(searchListOfAdvs, false, false)


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

                    @SuppressLint("SetTextI18n")
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        var newAdvs: List<SmallAdv> = listOf()

                        if (s != null && s.isNotEmpty() && s.isNotBlank()) {
                            if (sb.query.toString() == "") {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { x ->
                                        x.userId != sharedVM.authUser.value!!.email &&
                                                x.location.lowercase()
                                                    .contains(s.toString().lowercase()) &&
                                                checkSkills(x.skill, it)
                                    }.toList()
                                }
                            } else {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { x ->
                                        x.userId != sharedVM.authUser.value!!.email &&
                                                x.location.lowercase()
                                                    .contains(s.toString().lowercase()) &&
                                                checkSkills(x.skill, it) && x.title.lowercase()
                                            .contains(sb.query.toString().lowercase())
                                    }.toList()
                                }
                            }
                        } else {
                            if (sb.query.toString() != "") {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { x ->
                                        x.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(x.skill, it) &&
                                                x.title.lowercase()
                                                    .contains(sb.query.toString().lowercase())
                                    }.toList()
                                }
                            } else newAdvs = searchListOfAdvs
                        }

                        renderAdvList(newAdvs, false, false)

                        if (dateFilter.text.toString() != getString(R.string.date)) {
                            dateFilter.setText(dateFilter.text.toString().plus(" "))
                            dateFilter.setText(dateFilter.text.trim())
                            dateFilter.setText(dateFilter.text.toString().plus(" "))
                        }

                    }
                }
                searchLocation.addTextChangedListener(textWatcher)


                //FILTER BY DATE
                val textWatcherDate = object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                        if (dateFilter.text.toString() == getString(R.string.date)) {
                            dateFilter.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.ic_baseline_today_24,
                                0
                            )

                            var newAdvs: List<SmallAdv> = listOf()

                            if (searchLocation.text.isNotEmpty() || searchLocation.text.isNotBlank()) {
                                if (sb.query.toString() != "") {
                                    skill.split(",").forEach {
                                        newAdvs = searchListOfAdvs.filter { x ->
                                            x.userId != sharedVM.authUser.value!!.email &&
                                                    x.location.lowercase()
                                                        .contains(
                                                            searchLocation.text.toString()
                                                                .lowercase()
                                                        ) &&
                                                    checkSkills(x.skill, it)
                                                    && x.title.lowercase()
                                                .contains(sb.query.toString().lowercase())
                                        }.toList()
                                    }
                                } else {

                                    skill.split(",").forEach {
                                        newAdvs = searchListOfAdvs.filter { x ->
                                            x.userId != sharedVM.authUser.value!!.email &&
                                                    x.location.lowercase()
                                                        .contains(
                                                            searchLocation.text.toString()
                                                                .lowercase()
                                                        ) &&
                                                    checkSkills(x.skill, it)
                                        }.toList()
                                    }
                                }

                            } else {
                                if (sb.query.toString() != "") {
                                    skill.split(",").forEach {
                                        newAdvs = searchListOfAdvs.filter { x ->
                                            x.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(x.skill, it)
                                                    && x.title.lowercase()
                                                .contains(sb.query.toString().lowercase())
                                        }.toList()
                                    }
                                } else {
                                    skill.split(",").forEach {
                                        newAdvs = searchListOfAdvs.filter { x ->
                                            x.userId != sharedVM.authUser.value!!.email && checkSkills(
                                                x.skill,
                                                it
                                            )
                                        }.toList()
                                    }
                                }
                            }

                            renderAdvList(newAdvs, false, false)

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
                            }

                            if (searchLocation.text.isNotEmpty() || searchLocation.text.isNotBlank()) {
                                if (sb.query.toString() != "") {
                                    skill.split(",").forEach {
                                        newAdvs = searchListOfAdvs.filter { x ->
                                            x.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(x.skill, it) &&
                                                    x.location.lowercase()
                                                        .contains(
                                                            searchLocation.text.toString()
                                                                .lowercase()
                                                        )
                                                    && x.title.lowercase()
                                                .contains(
                                                    sb.query.toString().lowercase()
                                                ) && x.date == s.toString().trim()
                                        }.toList()
                                    }
                                } else {
                                    skill.split(",").forEach {
                                        newAdvs = searchListOfAdvs.filter { x ->
                                            x.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(x.skill, it) &&
                                                    x.location.lowercase()
                                                        .contains(
                                                            searchLocation.text.toString()
                                                                .lowercase()
                                                        ) && x.date == s.toString().trim()
                                        }.toList()
                                    }
                                }
                            } else {
                                if (sb.query.toString() != "") {
                                    skill.split(",").forEach {
                                        newAdvs = searchListOfAdvs.filter { x ->
                                            x.userId != sharedVM.authUser.value!!.email &&
                                                    checkSkills(x.skill, it) &&
                                                    x.title.lowercase()
                                                        .contains(
                                                            sb.query.toString().lowercase()
                                                        ) && x.date == s.toString().trim()
                                        }.toList()
                                    }

                                }
                            }

                            renderAdvList(newAdvs, false, false)
                        }
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
                                        x.title.lowercase()
                                            .contains(newText.lowercase()) && checkSkills(
                                    x.skill,
                                    it
                                )
                            }.toList()
                        }


                        if (dateFilter.text.toString() != getString(R.string.date)) {
                            if (searchLocation.text.isNotEmpty() || searchLocation.text.isNotBlank()) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { x ->
                                        x.userId != sharedVM.authUser.value!!.email &&
                                                x.title.lowercase()
                                                    .contains(newText.lowercase()) && checkSkills(
                                            x.skill,
                                            it
                                        )
                                                && x.date == dateFilter.text.toString().trim() &&
                                                x.location.lowercase()
                                                    .contains(
                                                        searchLocation.text.toString()
                                                            .lowercase()
                                                    )
                                    }.toList()
                                }
                            } else {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { x ->
                                        x.userId != sharedVM.authUser.value!!.email &&
                                                x.title.lowercase()
                                                    .contains(newText.lowercase()) && checkSkills(
                                            x.skill,
                                            it
                                        )
                                                && x.date == dateFilter.text.toString().trim()
                                    }.toList()
                                }
                            }
                        }

                        renderAdvList(newAdvs, false, false)
                        return false
                    }

                    override fun onQueryTextSubmit(query: String): Boolean {
                        // task HERE
                        return false
                    }

                })

                //SORT ADVS
                spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>,
                        view: View?,
                        pos: Int,
                        id: Long
                    ) {
                        var newAdvs: List<SmallAdv> = listOf()
                        if (pos == 0) {
                            if ((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty())
                                && (dateFilter.text.toString() != getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.location.lowercase()
                                            .contains(
                                                searchLocation.text.toString().lowercase()
                                            )
                                                && adv.date.lowercase()
                                            .contains(dateFilter.text.toString().lowercase())
                                    }.toList().sortedBy { adv -> adv.title }
                                }
                            } else if ((locationFilter.text.isBlank() || locationFilter.text.isEmpty()) &&
                                (dateFilter.text.toString() != getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.date.lowercase()
                                            .contains(dateFilter.text.toString().lowercase())
                                    }.toList().sortedBy { adv -> adv.title }
                                }
                            } else if ((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty()) &&
                                (dateFilter.text.toString() == getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.location.lowercase()
                                            .contains(
                                                searchLocation.text.toString().lowercase()
                                            )
                                    }.toList().sortedBy { adv -> adv.title }
                                }
                            } else {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                    }.toList().sortedBy { adv -> adv.title }
                                }
                            }
                            renderAdvList(newAdvs, false, false)

                        } else if (pos == 1) {
                            if ((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty())
                                && (dateFilter.text.toString() != getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.location.lowercase()
                                            .contains(
                                                searchLocation.text.toString().lowercase()
                                            )
                                                && adv.date.lowercase()
                                            .contains(dateFilter.text.toString().lowercase())
                                    }.toList().sortedBy { adv ->
                                        val arr = adv.date.split("/")
                                        val dd = arr[0]
                                        val mm = arr[1]
                                        val yyyy = arr[2]
                                        val new_date = yyyy + "/" + mm + "/" + dd
                                        new_date
                                    }
                                }
                            } else if ((locationFilter.text.isBlank() || locationFilter.text.isEmpty()) &&
                                (dateFilter.text.toString() != getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.date.lowercase()
                                            .contains(dateFilter.text.toString().lowercase())
                                    }.toList().sortedBy { adv ->
                                        val arr = adv.date.split("/")
                                        val dd = arr[0]
                                        val mm = arr[1]
                                        val yyyy = arr[2]
                                        val new_date = yyyy + "/" + mm + "/" + dd
                                        new_date
                                    }
                                }
                            } else if ((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty()) &&
                                (dateFilter.text.toString() == getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.location.lowercase()
                                            .contains(
                                                searchLocation.text.toString().lowercase()
                                            )
                                    }.toList().sortedBy { adv ->
                                        val arr = adv.date.split("/")
                                        val dd = arr[0]
                                        val mm = arr[1]
                                        val yyyy = arr[2]
                                        val new_date = yyyy + "/" + mm + "/" + dd
                                        new_date
                                    }
                                }
                            } else {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                    }.toList().sortedBy { adv ->
                                        val arr = adv.date.split("/")
                                        val dd = arr[0]
                                        val mm = arr[1]
                                        val yyyy = arr[2]
                                        val new_date = yyyy + "/" + mm + "/" + dd
                                        new_date
                                    }
                                }
                            }
                            renderAdvList(newAdvs, false, false)

                        } else if (pos == 2) {
                            if ((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty())
                                && (dateFilter.text.toString() != getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.location.lowercase()
                                            .contains(
                                                searchLocation.text.toString().lowercase()
                                            )
                                                && adv.date.lowercase()
                                            .contains(dateFilter.text.toString().lowercase())
                                    }.toList().sortedByDescending { adv ->
                                        val arr = adv.date.split("/")
                                        val dd = arr[0]
                                        val mm = arr[1]
                                        val yyyy = arr[2]
                                        val new_date = yyyy + "/" + mm + "/" + dd
                                        new_date
                                    }
                                }
                            } else if ((locationFilter.text.isBlank() || locationFilter.text.isEmpty()) &&
                                (dateFilter.text.toString() != getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.date.lowercase()
                                            .contains(dateFilter.text.toString().lowercase())
                                    }.toList().sortedByDescending { adv ->
                                        val arr = adv.date.split("/")
                                        val dd = arr[0]
                                        val mm = arr[1]
                                        val yyyy = arr[2]
                                        val new_date = yyyy + "/" + mm + "/" + dd
                                        new_date
                                    }
                                }
                            } else if ((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty()) &&
                                (dateFilter.text.toString() == getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.location.lowercase()
                                            .contains(
                                                searchLocation.text.toString().lowercase()
                                            )
                                    }.toList().sortedByDescending { adv ->
                                        val arr = adv.date.split("/")
                                        val dd = arr[0]
                                        val mm = arr[1]
                                        val yyyy = arr[2]
                                        val new_date = yyyy + "/" + mm + "/" + dd
                                        new_date
                                    }
                                }
                            } else {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                    }.toList().sortedByDescending { adv ->
                                        val arr = adv.date.split("/")
                                        val dd = arr[0]
                                        val mm = arr[1]
                                        val yyyy = arr[2]
                                        val new_date = yyyy + "/" + mm + "/" + dd
                                        new_date
                                    }
                                }
                            }
                            renderAdvList(newAdvs, false, false)


                        } else if (pos == 3) {
                            if ((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty())
                                && (dateFilter.text.toString() != getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.location.lowercase()
                                            .contains(
                                                searchLocation.text.toString().lowercase()
                                            )
                                                && adv.date.lowercase()
                                            .contains(dateFilter.text.toString().lowercase())
                                    }.toList().sortedBy { adv -> adv.title }
                                }
                            } else if ((locationFilter.text.isBlank() || locationFilter.text.isEmpty()) &&
                                (dateFilter.text.toString() != getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.date.lowercase()
                                            .contains(dateFilter.text.toString().lowercase())
                                    }.toList().sortedBy { adv -> adv.title }
                                }
                            } else if ((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty()) &&
                                (dateFilter.text.toString() == getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.location.lowercase()
                                            .contains(
                                                searchLocation.text.toString().lowercase()
                                            )
                                    }.toList().sortedBy { adv -> adv.title }
                                }
                            } else {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                    }.toList().sortedBy { adv -> adv.title }
                                }
                            }
                            renderAdvList(newAdvs, false, false)

                        } else if (pos == 4) {
                            if ((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty())
                                && (dateFilter.text.toString() != getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.location.lowercase()
                                            .contains(
                                                searchLocation.text.toString().lowercase()
                                            )
                                                && adv.date.lowercase()
                                            .contains(
                                                dateFilter.text.toString().lowercase()
                                            )
                                    }.toList().sortedByDescending { adv -> adv.title }
                                }
                            } else if ((locationFilter.text.isBlank() || locationFilter.text.isEmpty()) &&
                                (dateFilter.text.toString() != getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.date.lowercase()
                                            .contains(
                                                dateFilter.text.toString().lowercase()
                                            )
                                    }.toList().sortedByDescending { adv -> adv.title }
                                }
                            } else if ((locationFilter.text.isNotBlank() || locationFilter.text.isNotEmpty()) &&
                                (dateFilter.text.toString() == getString(R.string.date))
                            ) {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                                && adv.location.lowercase()
                                            .contains(
                                                searchLocation.text.toString().lowercase()
                                            )
                                    }.toList().sortedByDescending { adv -> adv.title }
                                }
                            } else {
                                skill.split(",").forEach {
                                    newAdvs = searchListOfAdvs.filter { adv ->
                                        adv.userId != sharedVM.authUser.value!!.email &&
                                                checkSkills(adv.skill, it)
                                    }.toList().sortedByDescending { adv -> adv.title }
                                }
                            }
                            renderAdvList(newAdvs, false, false)

                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>?) {}
                }

            }
        }
    }

    private fun checkSkills(advSkill: String, skill: String): Boolean {
        var valid = false
        advSkill.split(",").forEach {
            if (it == skill) {
                valid = true
            }
        }
        return valid
    }

    //Date
    private fun showDialogOfDatePicker() {
        val datePickerFragment = DatePickerFragment(dateFilter)
        datePickerFragment.show(requireActivity().supportFragmentManager, "datePicker")

    }

    private fun renderAdvList(advList: List<SmallAdv>, myAdv: Boolean, reservationPage: Boolean) {
        val adapter: com.bancempo.data.SmallAdvAdapter?

        if (!myAdv) {
            adapter =
                SmallAdvAdapter1(advList, false, reservationPage, sharedVM)
        } else {
            adapter =
                SmallAdvAdapter1(advList, true, reservationPage, sharedVM)
        }

        if (advList.isEmpty()) {
            rv.visibility = View.GONE
            emptyListTV.visibility = View.VISIBLE
            emptyListTV.text = getString(R.string.no_adv)
        } else {
            rv.visibility = View.VISIBLE
            emptyListTV.visibility = View.GONE
        }
        rv.adapter = adapter
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

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            return DatePickerDialog(requireContext(), this, year, month, day)
        }

        @SuppressLint("SetTextI18n")
        override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
            this.year = year
            this.month = month
            this.day = day
            val dateFormatter = LocalDate.of(year, month + 1, day)
            val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
            val dateFormatted = dateFormatter.format(formatter)

            date.text = dateFormatted.toString()
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.filter -> {
                val cv = view?.findViewById<CardView>(
                    R.id.cardView6
                )
                if (cv != null) {

                    cv.isVisible = !cv.isVisible

                }
                true
            }
            R.id.sort -> {
                val sp = view?.findViewById<Spinner>(
                    R.id.sort_spinner
                )
                if (sp != null) {

                    sp.isVisible = !sp.isVisible

                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
