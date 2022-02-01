package com.panic.ui.home.pages.view

import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import com.module.domain.InstalledApp
import com.panic.R
import com.panic.databinding.InstalledAppViewBinding

class InstalledAppView constructor(context: Context) : RelativeLayout(context) {

    private val binding = InstalledAppViewBinding.inflate(LayoutInflater.from(context), this)

    var onClickListener: ((InstalledApp) -> Unit)? = null
    var onSwitchEnableListener: ((InstalledApp, Boolean) -> Unit)? = null
    private var rowPackageName: String? = null
    override fun setEnabled(enabled: Boolean) {
        if (enabled) {
            binding.editableLabel.visibility = View.VISIBLE
            binding.appLabel.isEnabled = true
            binding.iconView.isEnabled = true
            binding.iconView.colorFilter = null
        } else {
            binding.editableLabel.visibility = View.GONE
            binding.appLabel.isEnabled = false
            binding.iconView.isEnabled = false
            // grey out app icon when disabled
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(matrix)
            binding.iconView.colorFilter = filter
        }
    }

    fun setupForApp(item: InstalledApp) {
        rowPackageName = item.packageName
        binding.iconView.setImageDrawable(item.icon)
        binding.appLabel.text = item.label

        if (item.canConnect) {
            binding.iconView.setOnClickListener {
                onClickListener?.invoke(item)
            }
            binding.appLabel.setOnClickListener {
                onClickListener?.invoke(item)
            }
            binding.editableLabel.setOnClickListener {
                onClickListener?.invoke(item)
            }
            binding.editableLabel.setText(R.string.edit)
            binding.editableLabel.setTypeface(null, Typeface.BOLD)
            binding.editableLabel.isAllCaps = true
        } else {
            binding.iconView.setOnClickListener(null)
            binding.appLabel.setOnClickListener(null)
            binding.editableLabel.setOnClickListener(null)
            binding.editableLabel.setText(R.string.app_hides)
            binding.editableLabel.setTypeface(null, Typeface.NORMAL)
            binding.editableLabel.isAllCaps = false
        }
        isEnabled = item.enabled
        binding.onSwitch.setOnCheckedChangeListener(null)
        binding.onSwitch.isChecked = item.enabled
        binding.onSwitch.setOnCheckedChangeListener { compoundButton, enabled ->
            isEnabled = enabled
            onSwitchEnableListener?.invoke(item, enabled)
        }
    }

}
