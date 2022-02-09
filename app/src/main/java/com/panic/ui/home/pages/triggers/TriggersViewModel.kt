package com.panic.ui.home.pages.triggers

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.module.domain.models.InstalledAppEntity
import com.panic.ui.base.BaseViewModel
import info.guardianproject.panic.PanicResponder
import info.guardianproject.panic.PanicTrigger

class TriggersViewModel(application:Application) : BaseViewModel(application) {

    private val _index = MutableLiveData<Int>()
    private val _list = MutableLiveData<List<InstalledAppEntity>>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }
    val list: LiveData<List<InstalledAppEntity>> get() = _list

    fun setIndex(index: Int) {
        _index.value = index
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        getAllTriggersList()
    }

    private fun getAllTriggersList() {
        val allTriggers = PanicTrigger.getTriggerApps(getApplication())
        val enabledResponders = PanicResponder.getEnabledTriggers(getApplication())
        val respondersThatCanConnect = PanicTrigger.getRespondersThatCanConnect(getApplication())

        val pm = getApplication<Application>().packageManager
        val apps = allTriggers
            .filter { packageName ->
                packageName != getApplication<Application>().packageName
            }
            .map { packageName ->
                val applicationLabel = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0))
                val applicationIcon = pm.getApplicationIcon(packageName)
                InstalledAppEntity(
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