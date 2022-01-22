package info.guardianproject.panic

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.util.Log
import info.guardianproject.panic.Panic.isTriggerIntent
import java.util.ArrayList
import java.util.HashSet

object PanicTrigger {
    const val TAG = "PanicTrigger"
    private const val SHARED_PREFS_MODE = Context.MODE_PRIVATE
    private const val CONNECTED_SHARED_PREFS = "info.guardianproject.panic.PanicTrigger.CONNECTED"
    private const val ENABLED_SHARED_PREFS = "info.guardianproject.panic.PanicTrigger.ENABLED"

    /**
     * Checks whether the provided [Activity] was started with the action
     * [Panic.ACTION_CONNECT], and if so, processes that [Intent] ,
     * adding the sending app as the panic trigger.
     *
     * @param activity the `Activity` to check for the `Intent`
     * @return whether an `ACTION_DISCONNECT Intent` was received
     */
    fun checkForConnectIntent(activity: Activity): Boolean {
        val result = PanicUtils.checkForIntentWithAction(activity, Panic.ACTION_CONNECT)
        val packageName = PanicUtils.getCallingPackageName(activity)
        addConnectedResponder(activity, packageName)
        return result
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
        val result = PanicUtils.checkForIntentWithAction(activity, Panic.ACTION_DISCONNECT)
        val packageName = PanicUtils.getCallingPackageName(activity)
        removeConnectedResponder(activity, packageName)
        return result
    }

    /**
     * Add a `packageName` to the list of connected responders.
     *
     * @param context     the app's [Context]
     * @param packageName the responder to add
     * @return whether it was successfully completed
     * @see .removeConnectedResponder
     */
    fun addConnectedResponder(context: Context, packageName: String?): Boolean {
        val prefs = context.getSharedPreferences(CONNECTED_SHARED_PREFS, SHARED_PREFS_MODE)
        // present in the prefs means connected
        return prefs.edit().putBoolean(packageName, true).commit()
    }

    /**
     * Remove a `packageName` from the list of connected responders.
     *
     * @param context     the app's [Context]
     * @param packageName the responder to remove
     * @return whether it was successfully removed
     * @see .addConnectedResponder
     */
    fun removeConnectedResponder(context: Context, packageName: String?): Boolean {
        val prefs = context.getSharedPreferences(CONNECTED_SHARED_PREFS, SHARED_PREFS_MODE)
        // absent from the prefs means not connected
        return prefs.contains(packageName) && prefs.edit().remove(packageName).commit()
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
        val activitiesList = pm.queryIntentActivities(PanicUtils.TRIGGER_INTENT, 0)
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
        val receiversList = pm.queryBroadcastReceivers(PanicUtils.TRIGGER_INTENT, 0)
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
        val servicesList = pm.queryIntentServices(PanicUtils.TRIGGER_INTENT, 0)
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
     * @return the set of `packageNames` of responder apps
     * @see .getResponderActivities
     * @see .getResponderBroadcastReceivers
     * @see .getResponderServices
     * @see .getEnabledResponders
     */
    fun getAllResponders(context: Context): Set<String> {
        val packageNames: MutableList<String> = ArrayList(getResponderActivities(context))
        packageNames.addAll(getResponderBroadcastReceivers(context))
        packageNames.addAll(getResponderServices(context))
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
        val prefs = context.getSharedPreferences(CONNECTED_SHARED_PREFS, SHARED_PREFS_MODE)
        val connectedAndInstalled: MutableSet<String> = HashSet()
        val all = getAllResponders(context)
        // present in the connected prefs means it has been connected
        for ((packageName) in prefs.all) {
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
     */
    fun getRespondersThatCanConnect(context: Context): Set<String> {
        val connectInfos = context.packageManager.queryIntentActivities(
            PanicUtils.CONNECT_INTENT, 0
        )
        val connectPackageNameList: MutableSet<String> = HashSet(connectInfos.size)
        for (resolveInfo in connectInfos) {
            if (resolveInfo.activityInfo == null) continue
            connectPackageNameList.add(resolveInfo.activityInfo.packageName)
        }
        return connectPackageNameList
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
    /**
     * Send a basic [Panic.ACTION_TRIGGER] [Intent] to all
     * configured panic receivers.  See [.sendTrigger]
     * if you want to use a custom `Intent` that can include things
     * like a text message, email addresses, phone numbers, etc.
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
     * @throws IllegalArgumentException if not a [Panic.ACTION_TRIGGER]
     * `Intent`
     */
    fun sendTrigger(context: Context, intent: Intent = PanicUtils.TRIGGER_INTENT) {
        if (!isTriggerIntent(intent)) {
            PanicUtils.throwNotTriggerIntent()
        }
        val enabled = getEnabledResponders(context)
        for (s in enabled) try {
            //region start Activities
            for (packageName in getResponderActivities(context)) {
                if (enabled.contains(packageName)) {
                    intent.setPackage(packageName)
                    try {
                        val activity = context as Activity
                        activity.startActivityForResult(intent, 0)
                    } catch (e: ClassCastException) {
                        Log.w(TAG, "sending trigger from Context, receivers cannot see sender packageName!")
                        // startActivityForResult() comes from Activity, so use an
                        // alternate method of sending that Context supports. This
                        // currently will send an Intent which the receiver will
                        // not be able to verify which app sent it. That requires
                        // including an IntentSender or some other hack like that
                        // https://dev.guardianproject.info/issues/6260
                        context.startActivity(intent)
                    }
                }
            }
            //endregion
            //region start BroadcastReceivers
            for (packageName in getResponderBroadcastReceivers(context)) {
                if (enabled.contains(packageName)) {
                    intent.setPackage(packageName)
                    context.sendBroadcast(intent)
                }
            }
            //endregion
            //region start Services
            for (packageName in getResponderServices(context)) {
                if (enabled.contains(packageName)) {
                    intent.setPackage(packageName)
                    context.startForegroundService(intent)
                }
            }
            //endregion
        } catch (e: ActivityNotFoundException) {
            // intent-filter without DEFAULT category makes the Activity be detected but not found
            e.printStackTrace()
        } catch (e: SecurityException) {
            // if we don't have permission to start the Service
            e.printStackTrace()
        }
    }
}