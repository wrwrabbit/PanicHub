package com.panic.ui.pages.buttons

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.module.domain.InstalledApp
import com.panic.ui.base.BaseViewModel
import info.guardianproject.panic.PanicResponder
import info.guardianproject.panic.PanicTrigger

class TriggersViewModel(application:Application) : BaseViewModel(application) {

    private val _index = MutableLiveData<Int>()
    private val _list = MutableLiveData<List<InstalledApp>>()
    val text: LiveData<String> = Transformations.map(_index) {
        "Hello world from section: $it"
    }
    val list: LiveData<List<InstalledApp>> get() = _list

    fun setIndex(index: Int) {
        _index.value = index
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        getAllRespondersList()
    }

    private fun getAllRespondersList() {
        val enabledResponders = PanicResponder.getTriggerPackageNameList(getApplication())
        val respondersThatCanConnect = PanicTrigger.getRespondersThatCanConnect(getApplication())

        val pm = getApplication<Application>().packageManager
        val apps = respondersThatCanConnect
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