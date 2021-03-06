package info.guardianproject.panic

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.core.app.ActivityCompat
import info.guardianproject.panic.Panic.isTriggerIntent

object PanicTrigger {
    const val TAG = "PanicTrigger"
    private const val SHARED_PREFS_MODE = Context.MODE_PRIVATE
    private const val ENABLED_SHARED_PREFS = "info.guardianproject.panic.PanicTrigger.ENABLED"
    private const val PREF_CONNECTED_PACKAGE_NAME_SIZE = "panicResponderConnectedPackageNameSize"
    private const val PREF_CONNECTED_PACKAGE_NAME_ITEM_ = "panicResponderConnectedPackageNameItem_"

    /**
     * Checks whether the provided [Activity] was started with the action
     * [Panic.ACTION_CONNECT], and if so, processes that [Intent] ,
     * adding the sending app as the panic trigger.
     *
     * @param activity the `Activity` to check for the `Intent`
     * @return whether an `ACTION_DISCONNECT Intent` was received
     */
    fun checkForConnectIntent(activity: Activity): Boolean {
        val isConnectAction = Panic.isConnectIntent(activity.intent)
        if (isConnectAction) {
            val packageName = PanicUtils.getCallingPackageName(activity)
            addConnectedResponder(activity, packageName)
        }
        return isConnectAction
    }

    /**
     * Checks whether the provided [Activity] was started with the action
     * [Panic.ACTION_DISCONNECT], and if so, processes that [Intent]
     * , removing the sending app as the panic trigger if it is currently
     * configured to be so.
     *
     * @param activity the `Activity` to check for the `Intent`
     * @return whether an `ACTION_DISCONNECT Intent` was received
     */
    fun checkForDisconnectIntent(activity: Activity): Boolean {
        val isDisconnectAction = Panic.isDisconnectIntent(activity.intent)
        if (isDisconnectAction) {
            val packageName = PanicUtils.getCallingPackageName(activity)
            removeConnectedResponder(activity, packageName)
        }
        return isDisconnectAction
    }

    /**
     * Add a `packageName` to the list of connected responders.
     *
     * @param context     the app's [Context]
     * @param packageName the responder to add
     * @return whether it was successfully completed
     * @see .removeConnectedResponder
     */
    fun addConnectedResponder(context: Context, packageName: String?) {
        if (packageName == null) {
            return
        }
        val list = getConnectedPackageNameList(context)
        clearConnectedPackageNameList(context)
        val mutableList = list.toMutableList()
        mutableList.add(packageName)
        saveConnectedPackageNameList(context, mutableList)
    }

    fun saveConnectedPackageNameList(context: Context, list: List<String>) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putInt(PREF_CONNECTED_PACKAGE_NAME_SIZE, list.size);

        for (i in list.indices) {
            editor.putString("${PREF_CONNECTED_PACKAGE_NAME_ITEM_}$i", list[i])
        }

        editor.apply()
    }

    fun clearConnectedPackageNameList(activity: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val size = prefs.getInt(PREF_CONNECTED_PACKAGE_NAME_SIZE, 0)

        val editor: SharedPreferences.Editor = prefs.edit()
        for (i in 0 until size) {
            editor.remove("${PREF_CONNECTED_PACKAGE_NAME_ITEM_}$i")
        }

        editor.apply()
    }

    fun getConnectedPackageNameList(context: Context): List<String> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val list = mutableListOf<String>()
        val size = prefs.getInt(PREF_CONNECTED_PACKAGE_NAME_SIZE, 0)

        for (i in 0 until size) {
            val element = prefs.getString("${PREF_CONNECTED_PACKAGE_NAME_ITEM_}$i", null)
            if (element != null) {
                list.add(element)
            }
        }
        return list
    }

    /**
     * Remove a `packageName` from the list of connected responders.
     *
     * @param context     the app's [Context]
     * @param packageName the responder to remove
     * @return whether it was successfully removed
     * @see .addConnectedResponder
     */
    fun removeConnectedResponder(context: Context, packageName: String?) {
        if (packageName == null) {
            return
        }

        val list = getConnectedPackageNameList(context)
        clearConnectedPackageNameList(context)
        val mutableList = list.toMutableList()
        mutableList.remove(packageName)
        saveConnectedPackageNameList(context, mutableList)
    }

    /**
     * Add a `packageName` to the list of responders that will receive a trigger from this app.
     *
     * @param context     the app's [Context]
     * @param packageName the responder to add
     * @return whether it was successfully completed
     * @see .disableResponder
     */
    fun enableResponder(context: Context, packageName: String?): Boolean {
        val prefs = context.getSharedPreferences(ENABLED_SHARED_PREFS, SHARED_PREFS_MODE)
        return prefs.edit().putBoolean(packageName, true).commit()
    }

    /**
     * Remove a `packageName` to the list of responders that will receive a trigger from this app.
     *
     * @param context     the app's [Context]
     * @param packageName the responder to add
     * @return whether it was successfully completed
     * @see .enableResponder
     */
    fun disableResponder(context: Context, packageName: String?): Boolean {
        val prefs = context.getSharedPreferences(ENABLED_SHARED_PREFS, SHARED_PREFS_MODE)
        return prefs.contains(packageName) && prefs.edit().remove(packageName).commit()
    }

    /**
     * Get the [Set] of `packageNames` of all [Activity]s that respond to
     * [Panic.ACTION_TRIGGER].
     *
     * @param context the app's [Context]
     * @return the set of `packageNames` of responder `Activity`s
     * @see .getResponderServices
     * @see .getResponderBroadcastReceivers
     */
    fun getResponderActivities(context: Context): Set<String> {
        val pm = context.packageManager
        val activitiesList = pm.queryIntentActivities(PanicUtils.buildTriggerIntent(), 0)
        val activities: MutableSet<String> = HashSet()
        for (resInfo in activitiesList) {
            activities.add(resInfo.activityInfo.packageName)
        }
        return activities
    }

    /**
     * Get the [Set] of `packageNames` of all
     * [android.content.BroadcastReceiver]s that respond to
     * [Panic.ACTION_TRIGGER].  Unlike with [android.app.Service]s and
     * [Activity]s, a `BroadcastReceiver` cannot verify which app
     * sent this [Intent] to it.
     *
     *
     * [android.content.BroadcastReceiver]s are not able to verify which app sent this.
     *
     * @param context the app's [Context]
     * @return the set of `packageNames` of responder `BroadcastReceiver`s
     * @see .getResponderActivities
     * @see .getResponderServices
     */
    fun getResponderBroadcastReceivers(context: Context): Set<String> {
        val pm = context.packageManager
        val receiversList = pm.queryBroadcastReceivers(PanicUtils.buildTriggerIntent(), 0)
        val broadcastReceivers: MutableSet<String> = HashSet()
        for (resInfo in receiversList) {
            broadcastReceivers.add(resInfo.activityInfo.packageName)
        }
        return broadcastReceivers
    }

    /**
     * Get the [Set] of `packageNames` of all [android.app.Service]s
     * that respond to [Panic.ACTION_TRIGGER].
     *
     *
     * [android.app.Service]s are not able to verify which app sent this.
     *
     * @param context the app's [Context]
     * @return the set of `packageNames` of responder `Service`s
     * @see .getResponderActivities
     * @see .getResponderBroadcastReceivers
     */
    fun getResponderServices(context: Context): Set<String> {
        val pm = context.packageManager
        val servicesList = pm.queryIntentServices(PanicUtils.buildTriggerIntent(), 0)
        val services: MutableSet<String> = HashSet()
        for (resInfo in servicesList) {
            services.add(resInfo.serviceInfo.packageName)
        }
        return services
    }

    /**
     * Get the [Set] of all `packageNames` of installed apps that include
     * any [Activity], [android.content.BroadcastReceiver], or
     * [android.app.Service] that responds to [Panic.ACTION_TRIGGER]
     * [Intent]s.
     *
     * @param context the app's [Context]
     * @return the set of `packageNames` of responder apps that include [Panic.ACTION_TRIGGER]
     * @see .getResponderActivities
     * @see .getResponderBroadcastReceivers
     * @see .getResponderServices
     * @see .getEnabledResponders
     *
     * TRIGGER - ?????? handler.
     */
    fun getAllResponders(context: Context): Set<String> {
        val packageNames = mutableListOf<String>()
        packageNames.addAll(getResponderActivities(context))
        packageNames.addAll(getResponderBroadcastReceivers(context))
        packageNames.addAll(getResponderServices(context))
        return HashSet(packageNames)
    }

    /**
     * TriggerApp  = CONNECT/DISCONNECT without TRIGGER
     * */
    fun getTriggerApps(context: Context): Set<String> {
        val packageNames = mutableListOf<String>()
        packageNames.addAll(getRespondersThatCanConnect(context))
        packageNames.removeAll(getAllResponders(context))
        return HashSet(packageNames)
    }

    /**
     * Get the [Set] of `packageNames` of any [Activity]s or
     * [android.app.Service]s that respond to [Panic.ACTION_TRIGGER]
     * and have been manually connected by the user to this app.
     *
     * @param context the app's [Context]
     * @return the set of `packageNames` of responder apps that are
     * currently to this trigger app
     * @see .checkForConnectIntent
     * @see .checkForDisconnectIntent
     * @see .getAllResponders
     * @see .getEnabledResponders
     */
    fun getConnectedResponders(context: Context): Set<String> {
        val list = getConnectedPackageNameList(context)
        val connectedAndInstalled: MutableSet<String> = HashSet()
        val all = getAllResponders(context)
        // present in the connected prefs means it has been connected
        for (packageName in list) {
            if (all.contains(packageName)) {
                connectedAndInstalled.add(packageName)
            }
        }
        return connectedAndInstalled
    }

    /**
     * Get the [Set] of `packageNames` of any [Activity]s or
     * [android.app.Service]s that respond to [Panic.ACTION_TRIGGER]
     * and have been enabled by the user
     *
     * @param context the app's [Context]
     * @return the set of `packageNames` of responder apps that are
     * currently to this trigger app
     * @see .checkForConnectIntent
     * @see .checkForDisconnectIntent
     * @see .getAllResponders
     * @see .getConnectedResponders
     */
    fun getEnabledResponders(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(ENABLED_SHARED_PREFS, SHARED_PREFS_MODE)
        val allPrefs = prefs.all
        val enabledAndInstalled: MutableSet<String> = HashSet()
        val all = getAllResponders(context)
        return if (allPrefs.isEmpty()) {
            // make sure allPrefs is not empty if the user disables all apps
            prefs.edit().putBoolean("hasBeenInited", true).apply()
            // the default is enabled, so write this this out
            for ((key) in allPrefs) {
                enableResponder(context, key)
            }
            all
        } else {
            // present in the enabled prefs means it is currently enabled
            for ((packageName) in allPrefs) {
                if (all.contains(packageName)) {
                    enabledAndInstalled.add(packageName)
                }
            }
            enabledAndInstalled
        }
    }

    /**
     * Get all of the responders that are able to make a full connection using
     * [Panic.ACTION_CONNECT], which is used to configure the response an
     * app makes.  For destructive responses, it is essential that the trigger
     * and responder are connected in order to prevent random apps from making
     * responders destroy things.  Apps can also only respond to
     * [Panic.ACTION_TRIGGER] with a non-destructive response which does
     * not require the apps to connect or the user to configure anything.
     *
     * @param context the app's [Context]
     * @return the set of `packageNames` of responder apps that can connect
     * to a trigger app
     *
     * TRIGGER + CONNECT/DISCONNECT - ?????? handler, ?? ???????????????? ???????? ???????????? edit.
     * ???? ???? ?????????? ???????????????????????? ??????????????, ???????? ?????????????? ?? ???????? ???? ??????????????????.
     */
    fun getRespondersThatCanConnect(context: Context): Set<String> {
        val pm = context.packageManager
        val activitiesList = pm.queryIntentActivities(PanicUtils.buildConnectIntent(), 0)
        val activities: MutableSet<String> = HashSet(activitiesList.size)
        for (resolveInfo in activitiesList) {
            if (resolveInfo.activityInfo == null) {
                continue
            }
            activities.add(resolveInfo.activityInfo.packageName)
        }
        return activities
    }

    fun sendTriggerWithExtras(context: Context, intent: Intent = PanicUtils.buildTriggerIntent(), extras: Bundle?) {
        extras?.let { intent.putExtras(it) }
        sendTrigger(context, intent)
    }

    /**
     * Send the [Intent] to all configured panic receivers.  It must have
     * an `action` of [Panic.ACTION_TRIGGER] or a
     * [IllegalArgumentException] will be thrown.  The `Intent` can
     * include things like a text message, email addresses, phone numbers, etc.
     * which a panic receiver app can use to send the message.
     *
     *
     * Only the receiving `Activity`s will be able to verify which app sent this,
     * [android.app.Service]s and [android.content.BroadcastReceiver]s
     * will not.
     *
     *
     * **WARNING**: If the receiving apps must be able to verify
     * which app sent this `Intent`, then `context` **must** be
     * an instance of [Activity]. Also, that `Activity` cannot have
     * `android:launchMode="singleInstance"` (because it is not possible
     * to get the calling `Activity`, as set by
     * [Activity.startActivityForResult])
     *
     * @param context the `Context` that will send the trigger `Intent`,
     * If this is an instance of `Activity`, then the receiving
     * apps will be able to verify which app sent the `Intent`
     * @param intent  the `Intent` to send to panic responders
     * @throws IllegalArgumentException if not a [Panic.ACTION_TRIGGER]
     * `Intent`
     */
    fun sendTrigger(context: Context, intent: Intent = PanicUtils.buildTriggerIntent()) {
        if (!isTriggerIntent(intent)) {
            PanicUtils.throwNotTriggerIntent()
        }
        val enabled = getEnabledResponders(context)

        val responderActivities = getResponderActivities(context)
            .filter { item ->
                enabled.contains(item)
            }
        val responderBroadcastReceivers = getResponderBroadcastReceivers(context)
            .filter { item ->
                enabled.contains(item)
            }
        val responderServices = getResponderServices(context)
            .filter { item ->
                enabled.contains(item)
            }

        try {
            //region start Activities
            for (packageName in responderActivities) {
                intent.setPackage(packageName)
                try {
                    ActivityCompat.startActivityForResult(context as Activity, intent, 0, null)
                } catch (e: ClassCastException) {
                    Log.w(TAG, "sending trigger from Context, receivers cannot see sender packageName!", e)
                    // startActivityForResult() comes from Activity, so use an
                    // alternate method of sending that Context supports. This
                    // currently will send an Intent which the receiver will
                    // not be able to verify which app sent it. That requires
                    // including an IntentSender or some other hack like that
                    // https://dev.guardianproject.info/issues/6260
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    ActivityCompat.startActivity(context, intent, null)
                } catch (e: ActivityNotFoundException) {
                    Log.w(TAG, "startActivityForResult packageName = $packageName!", e)
                    // startActivityForResult() comes from Activity, so use an
                    // alternate method of sending that Context supports. This
                    // currently will send an Intent which the receiver will
                    // not be able to verify which app sent it. That requires
                    // including an IntentSender or some other hack like that
                    // https://dev.guardianproject.info/issues/6260
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        ActivityCompat.startActivity(context, intent, null)
                    } catch (e: Exception) {
                        Log.w(TAG, "startActivity packageName = $packageName!", e)
                    }
                }
            }
            //endregion
            //region start BroadcastReceivers
            for (packageName in responderBroadcastReceivers) {
                intent.setPackage(packageName)
                context.sendBroadcast(intent)
            }
            //endregion
            //region start Services
            for (packageName in responderServices) {
                try {
                    intent.setPackage(packageName)
                    context.startForegroundService(intent)
                } catch (e: SecurityException) {
                    // if we don't have permission to start the Service
                    e.printStackTrace()
                }
            }
            //endregion
        } catch (e: ActivityNotFoundException) {
            // intent-filter without DEFAULT category makes the Activity be detected but not found
            e.printStackTrace()
        }
    }
}