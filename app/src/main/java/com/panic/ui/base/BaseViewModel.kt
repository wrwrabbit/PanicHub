package com.panic.ui.base

import android.app.Application
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver


open class BaseViewModel(
    application: Application,
) : AndroidViewModel(application), DefaultLifecycleObserver {

    protected fun getString(@StringRes stringResId: Int): String = getApplication<Application>().getString(stringResId)

    protected val context get() = getApplication<Application>()

    override fun onCleared() {
        super.onCleared()
        disposeAll()
    }

    private fun disposeAll() {
        Log.i(TAG, "${javaClass.simpleName} disposeAll")
    }

    companion object {
        const val TAG: String = "BaseViewModel"
    }
}
