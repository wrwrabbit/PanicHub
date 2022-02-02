package com.panic.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.domain.models.AlarmEntity
import com.module.domain.usecases.AddAlarmUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

class MainViewModel constructor(
    application: Application,
    private val addAlarmUseCase: AddAlarmUseCase
) : AndroidViewModel(application) {
    val onPanicHandledLiveEvent = MutableLiveData<Unit>()

    fun onPanicEvent(callingPackageName: String) {
        viewModelScope.launch {
            addAlarmUseCase(
                AlarmEntity(
                    id = UUID.randomUUID().toString(),
                    packageName = callingPackageName,
                    createdAt = Date()
                )
            )

            withContext(Dispatchers.Main){
                onPanicHandledLiveEvent.value = Unit
            }
        }
    }
}