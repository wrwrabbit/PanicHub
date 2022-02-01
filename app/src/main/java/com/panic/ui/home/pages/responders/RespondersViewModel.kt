package com.panic.ui.home.pages.responders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.module.domain.InstalledApp
import info.guardianproject.panic.PanicTrigger

class RespondersViewModel(application: Application) : AndroidViewModel(application) {

    private val _index = MutableLiveData<Int>()
    private val _list = MutableLiveData<List<InstalledApp>>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }
    val list: LiveData<List<InstalledApp>> get() = _list

    fun setIndex(index: Int) {
        _index.value = index
    }

    init {
        getAllRespondersList()
    }

    private fun getAllRespondersList() {
        val allResponders = PanicTrigger.getAllResponders(getApplication())
        val enabledResponders = PanicTrigger.getEnabledResponders(getApplication())
        val respondersThatCanConnect = PanicTrigger.getRespondersThatCanConnect(getApplication())

        val pm = getApplication<Application>().packageManager
        val apps = allResponders
            .filter { packageName ->
                packageName != getApplication<Application>().packageName
            }
            .map { packageName ->
                val applicationLabel = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0))
                val applicationIcon = pm.getApplicationIcon(packageName)
                InstalledApp(
                    packageName = packageName,
                    label = applicationLabel.toString(),
                    icon = applicationIcon,
                    enabled = enabledResponders.contains(packageName),
                    canConnect = respondersThatCanConnect.contains(packageName)
                )
            }

        _list.value = apps
    }

}