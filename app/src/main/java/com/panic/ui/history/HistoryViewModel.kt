package com.panic.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.module.domain.usecases.GetAlarmsUseCase
import com.module.domain.models.AlarmEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryViewModel(
    application: Application,
    private val getAlarmsUseCase: GetAlarmsUseCase
) : AndroidViewModel(application) {

    private val _listLiveData = MutableLiveData<List<AlarmEntity>>()
    val listLiveData: LiveData<List<AlarmEntity>> = _listLiveData

    init {
        viewModelScope.launch {
            val alarmList = getAlarmsUseCase()
            withContext(Dispatchers.Main){
                _listLiveData.value = alarmList
            }
        }

    }
}