package com.bancempo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore

class SharedViewModel(private val app: Application): AndroidViewModel(app) {

    val services: MutableLiveData<HashMap<String, Service>> by lazy {
        MutableLiveData<HashMap<String, Service>>().also {
            loadServices()
        }
    }

    fun loadServices(){

        val db = FirebaseFirestore.getInstance()

        db.collection("services")
            .get()
            .addOnSuccessListener { res ->
                println("-----------------RES: $res")
            }
            .addOnFailureListener{
                Log.d("Firebase services", it.message?:"error")
            }

    }
}

