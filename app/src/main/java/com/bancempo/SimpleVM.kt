package com.bancempo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bancempo.databinding.SmallAdvertismentBinding
import com.bancempo.model.AdvRepository
import com.bancempo.model.Advertisment
import kotlin.concurrent.thread

class SimpleVM(application: Application): AndroidViewModel(application) {

    val repo = AdvRepository(application)

    val advs : LiveData<List<Advertisment>> = repo.findAll()
    val totAdv : LiveData<Int> = repo.count()

    fun add(num : Int){
        thread {
        repo.add("advertisment${totAdv.value}", "$num/11/2022")
        }
    }

    fun clear(){
        thread{
            repo.clear()
        }
    }

}