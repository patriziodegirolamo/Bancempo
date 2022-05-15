package com.bancempo.models

import android.app.Application
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.bancempo.R
import com.bancempo.Skill
import com.bancempo.SmallAdv
import com.bancempo.data.User
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SharedViewModel(private val app: Application) : AndroidViewModel(app) {
    val rootStorageDirectory = "gs://bancempo.appspot.com/"
    private val db = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance()

    val currentUser: MutableLiveData<User> by lazy {
        MutableLiveData<User>().also {
            loadUser("de96wgyM8s4GvwM6HFPr")
        }
    }

    val services: MutableLiveData<HashMap<String, Skill>> by lazy {
        MutableLiveData<HashMap<String, Skill>>().also {
            loadServices()
        }
    }

    val advs: MutableLiveData<HashMap<String, SmallAdv>> by lazy {
        MutableLiveData<HashMap<String, SmallAdv>>().also {
            loadAdvs()
        }
    }

    fun uploadBitmap(btm: Bitmap) {
        val creationTimeNewImage = System.currentTimeMillis()
        val imageName = "profile_".plus(creationTimeNewImage.toString()).plus(".jpg")
        val url = "$rootStorageDirectory/${currentUser.value?.id!!}/$imageName"
        val myOldRef = storageReference.getReferenceFromUrl(currentUser.value?.imageUser!!)
        val myNewRef = storageReference.getReferenceFromUrl(url)

        val baos = ByteArrayOutputStream()
        btm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        myNewRef.putBytes(data).addOnSuccessListener {
            myOldRef.delete().addOnSuccessListener {
                currentUser.value = User(
                    currentUser.value!!.id,
                    currentUser.value!!.fullname,
                    currentUser.value!!.nickname,
                    currentUser.value!!.description,
                    currentUser.value!!.location,
                    currentUser.value!!.email,
                    currentUser.value!!.skills,
                    myNewRef.toString()
                )
            }.addOnFailureListener {
                println("---------------------not ok: $it")
            }
        }.addOnFailureListener {
            Toast.makeText(app.applicationContext, "Error", Toast.LENGTH_SHORT).show()
        }

    }

    fun loadImageUser(view: ImageView) {
        val myRef = storageReference.getReferenceFromUrl(currentUser.value?.imageUser!!)

        Glide.with(app.applicationContext).load(myRef)
            .circleCrop()
            .placeholder(R.drawable.profile_edit).into(view)
    }

    fun updateUser(view: View, skillsString: String) {
        val fullname = view.findViewById<TextInputEditText>(R.id.editTextFullName).text.toString()
        val nickname = view.findViewById<TextInputEditText>(R.id.editTextNickname).text.toString()
        val email = view.findViewById<TextInputEditText>(R.id.editTextEmail).text.toString()
        val location = view.findViewById<TextInputEditText>(R.id.editTextLocation).text.toString()
        val description = view.findViewById<TextInputEditText>(R.id.editTextDescription).text.toString()

        val finalList = mutableListOf<String>()
        for (skill in skillsString.split(",")) {
            if (skill != "")
                finalList.add(skill)
        }
        val servicesDocRef = db.collection("services")
        val currentUserRef = db.collection("users").document(currentUser.value!!.id)

        var initialList = listOf<String>()
        servicesDocRef.get().addOnSuccessListener { snap ->
            initialList = snap.mapNotNull { x -> x.getString("title") }
        }.addOnSuccessListener {
            val toAdd = finalList.minus(initialList)
            val toDelete = initialList.minus(finalList)
            val finalListDocRefs = finalList.map { x -> servicesDocRef.document(x) }.toList()

            val user = User(
                currentUser.value!!.id,
                fullname,
                nickname,
                description,
                location,
                email,
                finalListDocRefs,
                currentUser.value!!.imageUser
            )

            val batch = db.batch()

            //1st: create new service for each element of addingList
            for (adding in toAdd) {
                batch.set(servicesDocRef.document(adding), createService(adding, currentUser.value!!.id))
            }

            //2nd: delete old services
            for (deleting in toDelete) {
                batch.delete(servicesDocRef.document(deleting))
            }

            //3rd: replace list of skills in user
            batch.set(currentUserRef, user)

            batch.commit()

        }
    }

    private fun loadUser(id: String) {
        println("-------------------------LOADING")
        db.collection("users")
            .whereEqualTo("id", id)
            .addSnapshotListener { r, e ->
                if (e != null)
                    currentUser.value = User()
                else {
                    for (doc in r!!) {
                        val nickname = doc.getString("nickname")
                        val fullname = doc.getString("fullname")
                        val email = doc.getString("email")
                        val location = doc.getString("location")
                        val description = doc.getString("description")
                        val listOfReferences = doc.data["skills"] as List<DocumentReference>
                        val imageUser = doc.getString("imageUser")
                        val user = User(
                            id, fullname!!, nickname!!, description!!, location!!,
                            email!!, listOfReferences, imageUser!!
                        )

                        currentUser.value = user
                    }
                }
            }
    }


    private fun loadServices() {
        db.collection("services")
            .addSnapshotListener { r, e ->
                if (e != null)
                    services.value = hashMapOf()
                else {
                    val serviceMap: HashMap<String, Skill> = hashMapOf()
                    for (doc in r!!) {
                        val title = doc.getString("title")
                        val creationTime = doc.getString("creationTime")
                        val createdBy = doc.getString("createdBy")
                        val service = Skill(title!!, creationTime!!, createdBy!!)
                        serviceMap[doc.id] = service
                    }
                    services.value = serviceMap
                }
            }
    }

    private fun createService(title: String, createdBy: String): Skill {
        return Skill(
            title,
            getCreationTime(),
            createdBy
        )
    }


    private fun loadAdvs() {
        db.collection("advertisements")
            .orderBy("creationTime", Query.Direction.DESCENDING)
            .addSnapshotListener { r, e ->
                if (e != null)
                    advs.value = hashMapOf()
                else {
                    val advMap: HashMap<String, SmallAdv> = hashMapOf()
                    for (doc in r!!) {
                        val date = doc.getString("date")
                        val description = doc.getString("description")
                        val duration = doc.getString("duration")
                        val location = doc.getString("location")
                        val note = doc.getString("note")
                        val time = doc.getString("time")
                        val title = doc.getString("title")
                        val creationTime = doc.getString("creationTime")
                        val adv = SmallAdv(
                            doc.id,
                            title!!,
                            date!!,
                            description!!,
                            time!!,
                            duration!!,
                            location!!,
                            note!!,
                            creationTime!!
                        )
                        advMap[doc.id] = adv
                    }
                    advs.value = advMap
                }
            }
    }

    private fun createAdvFromBundle(bundle: Bundle, id: String): SmallAdv {
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

    fun addNewAdv(newAdvBundle: Bundle) {
        val newId = db.collection("advertisements").document().id
        db.collection("advertisements").document(newId)
            .set(createAdvFromBundle(newAdvBundle, newId))
            .addOnSuccessListener {
                Toast.makeText(app.applicationContext, R.string.adv_create_succ, Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(app.applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }
    }

    fun modifyAdv(id: String, advBundle: Bundle) {
        db.collection("advertisements").document(id)
            .set(createAdvFromBundle(advBundle, id))
            .addOnSuccessListener {
                Toast.makeText(app.applicationContext, R.string.adv_create_succ, Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(app.applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }

    }


}

