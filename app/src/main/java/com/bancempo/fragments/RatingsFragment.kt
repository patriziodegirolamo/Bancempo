package com.bancempo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bancempo.R
import com.bancempo.data.ReviewsAdapter
import com.bancempo.models.SharedViewModel

class RatingsFragment : Fragment(R.layout.reviews) {

    private lateinit var rv: RecyclerView
    private val sharedVM: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.recycler_reviews)
        rv.layoutManager = LinearLayoutManager(findNavController().context)

        val userId = arguments?.getString("userId")

        sharedVM.ratings.observe(viewLifecycleOwner){ ratings ->
            val user = sharedVM.users.value!![userId]!!
            val nickname = user.nickname
            val url = user.imageUser

            rv.adapter = ReviewsAdapter(
                ratings.values.filter { x -> x.idReceiver == userId }
                    .sortedBy { x -> x.rating}.toList()
                , nickname, url)
        }

    }


}