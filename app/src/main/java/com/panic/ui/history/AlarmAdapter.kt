package com.panic.ui.history

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.module.domain.models.AlarmEntity


class DiffAlarmItemCallback : DiffUtil.ItemCallback<AlarmEntity>() {
    override fun areItemsTheSame(oldItem: AlarmEntity, newItem: AlarmEntity): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AlarmEntity, newItem: AlarmEntity): Boolean {
        return oldItem == newItem
    }
}

class AlarmAdapter : ListAdapter<AlarmEntity, AlarmAdapter.ItemViewHolder>(DiffAlarmItemCallback()) {

    var onItemClickListener: OnComplaintReasonClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(AlarmItemView(parent.context))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        val itemView = holder.view
        itemView.renderView(position, item)
        itemView.setOnClickListener {
            onItemClickListener?.onItemClicked(item)
        }
    }

    class ItemViewHolder(val view: AlarmItemView) : RecyclerView.ViewHolder(view)

    interface OnComplaintReasonClickListener {
        fun onItemClicked(item: AlarmEntity)
    }
}