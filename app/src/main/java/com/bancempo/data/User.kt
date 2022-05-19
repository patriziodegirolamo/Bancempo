package com.bancempo.data

import com.bancempo.Skill
import com.google.firebase.firestore.DocumentReference

data class User(
    val fullname: String,
    val nickname: String,
    val description: String,
    val location: String,
    val email: String,
    val skills: List<String>,
    val imageUser: String) {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        listOf(),
        "",
    )
}


