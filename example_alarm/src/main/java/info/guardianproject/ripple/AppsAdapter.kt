package info.guardianproject.ripple

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import info.guardianproject.panic.PanicTrigger
import java.util.ArrayList

internal class AppsAdapter constructor(
    private val responders: Array<String>,
    private val enabledResponders: Set<String>,
    private val respondersThatCanConnect: Set<String>,
    private val iconList: ArrayList<Drawable>,
    private val appLabelList: ArrayList<CharSequence>,
    private val onClickListener: (String)->Unit
) : RecyclerView.Adapter<AppsAdapter.AppRowHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppRowHolder {
        return AppRowHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.row, parent, false),
            enabledResponders,
            onClickListener
        )
    }

    override fun onBindViewHolder(holder: AppRowHolder, position: Int) {
        val packageName = responders[position]
        val canConnect = respondersThatCanConnect.contains(packageName)
        holder.setupForApp(
            packageName,
            iconList[position],
            appLabelList[position],
            canConnect
        )
    }

    override fun getItemCount(): Int {
        return appLabelList.size
    }

    internal inner class AppRowHolder constructor(
        row: View,
        private val enabledResponders: Set<String>,
        private val onClickListener: (String)->Unit
    ) : RecyclerView.ViewHolder(row) {
        private val onSwitch: SwitchCompat = row.findViewById<View>(R.id.on_switch) as SwitchCompat
        private val editableLabel: TextView = row.findViewById<View>(R.id.editableLabel) as TextView
        private val iconView: ImageView = row.findViewById<View>(R.id.iconView) as ImageView
        private val appLabelView: TextView = row.findViewById<View>(R.id.appLabel) as TextView
        private var rowPackageName: String? = null
        fun setEnabled(enabled: Boolean) {
            if (enabled) {
                editableLabel.visibility = View.VISIBLE
                appLabelView.isEnabled = true
                iconView.isEnabled = true
                iconView.colorFilter = null
            } else {
                editableLabel.visibility = View.GONE
                appLabelView.isEnabled = false
                iconView.isEnabled = false
                // grey out app icon when disabled
                val matrix = ColorMatrix()
                matrix.setSaturation(0f)
                val filter = ColorMatrixColorFilter(matrix)
                iconView.colorFilter = filter
            }
        }

        fun setupForApp(packageName: String, icon: Drawable?, appLabel: CharSequence?, editable: Boolean) {
            rowPackageName = packageName
            iconView.setImageDrawable(icon)
            appLabelView.text = appLabel
            if (editable) {
                iconView.setOnClickListener {
                    onClickListener.invoke(packageName)
                }
                appLabelView.setOnClickListener{
                    onClickListener.invoke(packageName)
                }
                editableLabel.setOnClickListener{
                    onClickListener.invoke(packageName)
                }
                editableLabel.setText(R.string.edit)
                editableLabel.setTypeface(null, Typeface.BOLD)
                editableLabel.isAllCaps = true
            } else {
                iconView.setOnClickListener(null)
                appLabelView.setOnClickListener(null)
                editableLabel.setOnClickListener(null)
                editableLabel.setText(R.string.app_hides)
                editableLabel.setTypeface(null, Typeface.NORMAL)
                editableLabel.isAllCaps = false
            }
            val enabled = enabledResponders.contains(packageName)
            onSwitch.isChecked = enabled
            setEnabled(enabled)
        }

        init {
            onSwitch.setOnCheckedChangeListener { compoundButton, enabled ->
                setEnabled(enabled)
                if (enabled) {
                    PanicTrigger.enableResponder(compoundButton.context.applicationContext, rowPackageName)
                } else {
                    PanicTrigger.disableResponder(compoundButton.context.applicationContext, rowPackageName)
                }
            }
        }
    }
}