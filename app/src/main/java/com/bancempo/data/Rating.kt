package com.bancempo.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bancempo.R
import com.bancempo.models.GlideApp
import com.bancempo.models.SharedViewModel
import com.google.firebase.storage.FirebaseStorage

data class Rating(
    val idAuthor: String,
    val idReceiver: String,
    val idAdv: String,
    val rating: Double,
    val ratingText: String
)

class ReviewsAdapter(
    private val data: List<Rating>, private val sharedVM: SharedViewModel
) :

    RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder>() {

    class ReviewsViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        //val imageUser: ImageView = v.findViewById(R.id.iv_profileImage)
        val author: TextView = v.findViewById(R.id.tvAuthor)
        val ratingBar: RatingBar = v.findViewById(R.id.ratingBar)
        val description: TextView = v.findViewById(R.id.ratingDescription)

        val view: View = v

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewsViewHolder {

        val vg = LayoutInflater.from(parent.context).inflate(R.layout.small_rating, parent, false)
        return ReviewsViewHolder(vg)

    }

    override fun onBindViewHolder(holder: ReviewsViewHolder, position: Int) {
        val author = sharedVM.users.value!![data[position].idAuthor]!!
        holder.author.text = author.nickname
        holder.ratingBar.rating = data[position].rating.toFloat()
        if(data[position].ratingText.isNullOrEmpty() || data[position].ratingText.isNullOrBlank()){
            holder.description.visibility = View.GONE
        }
        else
        holder.description.text = data[position].ratingText
    }

    override fun getItemCount(): Int = data.size


}