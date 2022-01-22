package info.guardianproject.panic

import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.util.Log

object PanicUtils {
    val TRIGGER_INTENT = Intent(Panic.ACTION_TRIGGER)
    val CONNECT_INTENT = Intent(Panic.ACTION_CONNECT)
    fun getCallingPackageName(activity: Activity): String? {
        // getCallingPackage() was unstable until android-18, use this
        val componentName = activity.callingActivity ?: return null
        val packageName = componentName.packageName
        if (TextUtils.isEmpty(packageName)) {
            Log.e(
                activity.packageName,
                "Received blank Panic Intent! The Intent must be sent using startActivityForResult() and received without launchMode singleTask or singleInstance!"
            )
        }
        return packageName
    }

    fun checkForIntentWithAction(activity: Activity, action: String?): Boolean {
        val intent = activity.intent ?: return false
        return TextUtils.equals(intent.action, action)
    }

    fun throwNotTriggerIntent() {
        throw IllegalArgumentException(
            "The provided Intent must have an action of "
                    + Panic.ACTION_TRIGGER
        )
    }
}