package com.bancempo

import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class AdvertismentsVM(private val app: Application): AndroidViewModel(app) {
    val advs = MutableLiveData<MutableList<SmallAdv>>()

    fun addNewAdv(newAdv: SmallAdv){
        if(advs.value == null){
            println("------------error")
        }
        else{
            advs.value?.add(newAdv)
        }
    }

    init {
        val gson = Gson()
        val sharedPref = app.getSharedPreferences("advs_list.bancempo.lab3", Context.MODE_PRIVATE)
        if( sharedPref == null ){
            println("-----------create shared pref")
            with(sharedPref?.edit()){
                this?.putString("json_advs_list", "")
            }?.apply()
        }

        val stringJSON:String? = sharedPref?.getString("json_advs_list", "")
        if(stringJSON != null && stringJSON != ""){
            val myType = object : TypeToken<MutableList<SmallAdv>>() {}.type
            advs.value = gson.fromJson(stringJSON, myType)
        }
        else{
            advs.value = mutableListOf()
        }



    }

}