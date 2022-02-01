package com.panic.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainViewModel (application: Application) :AndroidViewModel(application){
    val onPanicHandledLiveEvent = MutableLiveData<Unit>()

    fun onPanicEvent(callingPackageName: String) {
        TODO("Not yet implemented")
    }
}