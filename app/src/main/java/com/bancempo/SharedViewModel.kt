package com.bancempo

import android.app.Application
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class SharedViewModel(private val app: Application): AndroidViewModel(app) {
    val db = FirebaseFirestore.getInstance()

    val services: MutableLiveData<HashMap<String, Service>> by lazy{
        MutableLiveData<HashMap<String, Service>>().also{
            loadServices()
        }
    }

    val advs: MutableLiveData<HashMap<String,SmallAdv>> by lazy{
        MutableLiveData<HashMap<String,SmallAdv>>().also{
            loadAdvs()
        }
    }

    private fun loadServices(){
        db.collection("services")
            .addSnapshotListener{r, e ->
                if( e!= null)
                    services.value = hashMapOf()
                else {
                    val serviceMap : HashMap<String, Service> = hashMapOf()
                    for (doc in r!!){
                        val title = doc.getString("title")
                        val creationTime = doc.getString("creationTime")
                        val service = Service(doc.id, title!!, creationTime!!)
                        serviceMap[doc.id] = service
                    }
                    services.value = serviceMap
                }
            }
    }

    fun addNewService(title: String){
        val newId = db.collection("services").document().id
        db.collection("services").document(newId)
            .set(createService(title, newId))
            .addOnSuccessListener {
                Toast.makeText(app.applicationContext, R.string.adv_create_succ, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(app.applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }
    }

    private fun createService(title: String, id: String) : Service{
        return Service(
            id,
            title,
            getCreationTime()
        )
    }


    private fun loadAdvs(){
        db.collection("advertisements")
            .orderBy("creationTime", Query.Direction.DESCENDING)
            .addSnapshotListener{r, e ->
                if( e!= null)
                    advs.value = hashMapOf()
                else {
                    val advMap : HashMap<String, SmallAdv> = hashMapOf()
                    for (doc in r!!){
                        val date = doc.getString("date")
                        val description = doc.getString("description")
                        val duration = doc.getString("duration")
                        val location = doc.getString("location")
                        val note = doc.getString("note")
                        val time = doc.getString("time")
                        val title = doc.getString("title")
                        val creationTime = doc.getString("creationTime")
                        val adv = SmallAdv(doc.id, title!!, date!!, description!!, time!!, duration!!, location!!, note!!, creationTime!!)
                        advMap[doc.id] = adv
                    }
                    advs.value = advMap
                }
            }
    }

    private fun createAdvFromBundle(bundle: Bundle, id: String) : SmallAdv{
        val title = bundle.getString("title") ?: ""
        val date = bundle.getString("date") ?: ""
        val description = bundle.getString("description") ?: ""
        val timeslot = bundle.getString("time") ?: ""
        val duration = bundle.getString("duration") ?: ""
        val location = bundle.getString("location") ?: ""
        val note = bundle.getString("note") ?: ""

        return SmallAdv(
            id,
            title,
            date,
            description,
            timeslot,
            duration,
            location,
            note,
            getCreationTime()

        )
    }

    private fun getCreationTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS")
        return current.format(formatter)
    }

    fun addNewAdv(newAdvBundle: Bundle){
        val newId = db.collection("advertisements").document().id
        db.collection("advertisements").document(newId)
            .set(createAdvFromBundle(newAdvBundle, newId))
            .addOnSuccessListener {
                Toast.makeText(app.applicationContext, R.string.adv_create_succ, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(app.applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }
    }

    fun modifyAdv(id: String, advBundle: Bundle){
        db.collection("advertisements").document(id)
            .set(createAdvFromBundle(advBundle, id))
            .addOnSuccessListener {
                Toast.makeText(app.applicationContext, R.string.adv_create_succ, Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(app.applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }

    }


}

