package com.panic.ui.home.pages.view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.module.domain.models.InstalledAppEntity

internal class InstalledAppsAdapter constructor(
) : RecyclerView.Adapter<InstalledAppsAdapter.AppRowHolder>() {

    val appLabelList = mutableListOf<InstalledAppEntity>()
    var onClickListener: ((InstalledAppEntity)->Unit)? = null
    var onSwitchEnableListener: ((InstalledAppEntity, Boolean) -> Unit)? = null

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

    fun setList(it: List<InstalledAppEntity>) {
        appLabelList.clear()
        appLabelList.addAll(it)
        notifyDataSetChanged()
    }

    internal inner class AppRowHolder constructor(row: View) : RecyclerView.ViewHolder(row)

}