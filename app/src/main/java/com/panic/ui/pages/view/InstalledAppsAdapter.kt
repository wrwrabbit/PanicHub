package com.panic.ui.pages.view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.module.domain.InstalledApp

internal class InstalledAppsAdapter constructor(
) : RecyclerView.Adapter<InstalledAppsAdapter.AppRowHolder>() {

    val appLabelList = mutableListOf<InstalledApp>()
    var onClickListener: ((InstalledApp)->Unit)? = null
    var onSwitchEnableListener: ((InstalledApp, Boolean) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppRowHolder {
        return AppRowHolder(InstalledAppView(parent.context))
    }

    override fun onBindViewHolder(holder: AppRowHolder, position: Int) {
        val installedAppView = holder.itemView as InstalledAppView
        installedAppView.onClickListener = onClickListener
        installedAppView.onSwitchEnableListener = onSwitchEnableListener
        installedAppView.setupForApp(
            appLabelList[position]
        )
    }

    override fun getItemCount(): Int {
        return appLabelList.size
    }

    fun setList(it: List<InstalledApp>) {
        appLabelList.clear()
        appLabelList.addAll(it)
        notifyDataSetChanged()
    }

    internal inner class AppRowHolder constructor(row: View) : RecyclerView.ViewHolder(row)

}