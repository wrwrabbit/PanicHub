package com.panic.ui.history

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.updatePadding
import com.module.domain.models.AlarmEntity
import com.panic.R
import com.panic.databinding.ViewAlarmItemBinding
import com.panic.ext.DISPLAY_DATE_FORMAT
import com.panic.ext.dimen
import com.panic.ext.formatedBy

class AlarmItemView : LinearLayoutCompat {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val binding = ViewAlarmItemBinding.inflate(LayoutInflater.from(context), this)

    init {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = HORIZONTAL
        updatePadding(
            left = dimen(R.dimen.app_default_16dp),
            top = dimen(R.dimen.app_default_8dp),
            bottom = dimen(R.dimen.app_default_8dp),
            right = dimen(R.dimen.app_default_16dp),
        )
    }

    fun renderView(position: Int, item: AlarmEntity) {
        val text = "${position + 1}. ${item.packageName} : ${item.createdAt.formatedBy(DISPLAY_DATE_FORMAT)}"
        binding.titleView.text = text
    }
}
