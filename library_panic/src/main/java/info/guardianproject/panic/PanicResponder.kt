package info.guardianproject.panic

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.preference.ListPreference
import android.preference.PreferenceManager
import info.guardianproject.panic.Panic.isTriggerIntent
import java.io.File
import java.util.ArrayList
import java.util.HashSet


object PanicResponder {
    private const val PREF_TRIGGER_PACKAGE_NAME_SIZE = "panicResponderTriggerPackageNameSize"
    private const val PREF_TRIGGER_PACKAGE_NAME_ITEM_ = "panicResponderTriggerPackageNameItem_"

    /**
     * Checks the provided [Activity] to see whether it has received a
     * [Panic.ACTION_CONNECT] `Intent`. If it has, it returns the
     * package name of the app that sent it. Otherwise, it returns `null`.
     * The sender is the only information used from the
     * `ACTION_CONNECT Intent`.
     *
     *
     * The responder app should always respond to every
     * `ACTION_CONNECT Intent`, even it is from the currently connected
     * trigger app. That trigger app could have been uninstalled and
     * reinstalled, so it needs to receive the confirmation again.
     *
     *
     * **WARNING**: If the `Activity` has
     * `android:launchMode="singleInstance"` or `"singleTask"`, then
     * this method will always return `false` because it is not possible
     * to get the calling `Activity`, as set by
     * [Activity.startActivityForResult]
     *
     * @param activity the `Activity` that received the `Intent`
     * @return the package of the sending app or `null` if it was not a
     * `ACTION_CONNECT Intent` or the `Intent` was not sent
     * with [Activity.startActivityForResult]
     * @see .checkForDisconnectIntent
     */
    fun getConnectIntentSenderPackageName(activity: Activity): String? {
        return if (PanicUtils.checkForIntentWithAction(activity, Panic.ACTION_CONNECT)) {
            PanicUtils.getCallingPackageName(activity)
        } else null
    }

    /**
     * Checks whether the provided [Activity] was started with the action
     * [Panic.ACTION_DISCONNECT], and if so, processes that [Intent]
     * , removing the sending app as the panic trigger if it is currently
     * configured to be so.
     *
     *
     * **WARNING**: If the `Activity` has
     * `android:launchMode="singleInstance"` or `"singleTask"`, then
     * this method will not disconnect because it is not possible to get the
     * calling `Activity`, as set by
     * [Activity.startActivityForResult]
     *
     * @param activity the `Activity` to check for the `Intent`
     * @return whether an `ACTION_DISCONNECT Intent` was received
     * @see PanicResponder.getConnectIntentSender
     */
    fun checkForDisconnectIntent(activity: Activity): Boolean {
        var result = false
        if (PanicUtils.checkForIntentWithAction(activity, Panic.ACTION_DISCONNECT)) {
            result = true
            val callingPackageName = PanicUtils.getCallingPackageName(activity)
            val contains = isTriggerPackageNameListContains(activity, callingPackageName)
            if (contains) {
                removeTriggerPackageName(activity, callingPackageName)
            }
        }
        return result
    }

    fun isTriggerPackageNameListContains(activity: Activity, packageName: String?): Boolean {
        packageName ?: return false
        val triggerPackageNameList = getTriggerPackageNameList(activity)
        return triggerPackageNameList.contains(packageName)
    }

    fun removeTriggerPackageName(activity: Activity, packageName: String?) {
        if (packageName == null) {
            return
        }

        val triggerPackageNameList = getTriggerPackageNameList(activity)
        clearTriggerPackageNameList(activity)
        val mutableList = triggerPackageNameList.toMutableList()
        mutableList.remove(packageName)
        saveTriggerPackageNameList(activity, mutableList)

        val pm = activity.packageManager
        val intent = PanicUtils.buildDisconnectIntent()
        intent.setPackage(packageName)
        val resInfos = pm.queryIntentActivities(intent, 0)
        if (resInfos.size > 0) {
            activity.startActivityForResult(intent, 0)
        }
    }

    /**
     * Set the `packageName` as the currently configured panic trigger
     * app. Set to `null` to have no panic trigger app active.
     *
     *
     * When the user changes the panic app config, then the current app needs to
     * send [Intent]s to the previous app, and the currently configured
     * app to let them know about the changes. This is done by sending an
     * `ACTION_DISCONNECT Intent` to the previous app, and an
     * `ACTION_CONNECT Intent` to the newly configured app.
     *
     * @param activity    the current [Activity]
     * @param packageName the app to set as the panic trigger
     */
    fun addTriggerPackageName(activity: Activity, packageName: String) {
        val triggerPackageNameList = getTriggerPackageNameList(activity)
        clearTriggerPackageNameList(activity)
        val mutableList = triggerPackageNameList.toMutableList()
        mutableList.add(packageName)
        saveTriggerPackageNameList(activity, mutableList)

        val pm = activity.packageManager
        val intent = PanicUtils.buildConnectIntent()
        intent.setPackage(packageName)
        val resInfos = pm.queryIntentActivities(intent, 0)
        if (resInfos.size > 0) {
            activity.startActivityForResult(intent, 0)
        }
    }

    fun saveTriggerPackageNameList(context: Context, list: List<String>) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putInt(PREF_TRIGGER_PACKAGE_NAME_SIZE, list.size);

        for (i in list.indices) {
            editor.putString("$PREF_TRIGGER_PACKAGE_NAME_ITEM_$i", list[i])
        }

        editor.apply()
    }

    fun clearTriggerPackageNameList(activity: Activity) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        val size = prefs.getInt(PREF_TRIGGER_PACKAGE_NAME_SIZE, 0)

        val editor: Editor = prefs.edit()
        for (i in 0 until size) {
            editor.remove("$PREF_TRIGGER_PACKAGE_NAME_ITEM_$i")
        }

        editor.apply()
    }

    /**
     * Get the `packageName` of the currently configured panic trigger
     * app, or `null` if none.
     *
     * @param context the app's [Context]
     * @return the `packageName` or null
     */
    fun getTriggerPackageNameList(context: Context): List<String> {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val list = mutableListOf<String>()
        val size = prefs.getInt(PREF_TRIGGER_PACKAGE_NAME_SIZE, 0)

        for (i in 0 until size) {
            val element = prefs.getString("$PREF_TRIGGER_PACKAGE_NAME_ITEM_$i", null)
            if (element != null) {
                list.add(element)
            }
        }
        return list
    }

    /**
     * Set the currently configured panic trigger app using the [Activity]
     * that received a [Panic.ACTION_CONNECT] [Intent]. If that
     * `Intent` was not set with either
     * [Activity.startActivityForResult] or
     * [Intent.setPackage], then this will result in no panic
     * trigger app being active.
     *
     *
     * When the user changes the panic app config, then the current app needs to
     * send [Intent]s to the previous app, and the currently configured
     * app to let them know about the changes. This is done by sending an
     * `ACTION_DISCONNECT Intent` to the previous app, and an
     * `ACTION_CONNECT Intent` to the newly configured app.
     *
     *
     * When this is called with an `Activity` in the same responder app
     * that called the [Activity.startActivityForResult],
     * then it will **not** change the existing trigger app setting! For
     * example, if the responder app launches its Panic Config `Activity`.
     *
     * @param activity the [Activity] that received an
     * [Panic.ACTION_CONNECT] [Intent]
     */
    fun addCallingPackageNameAsTrigger(activity: Activity) {
        val intentPackageName = activity.intent.getPackage()
        val callingPackageName = PanicUtils.getCallingPackageName(activity)?:return
        if (intentPackageName == null) {
            // ignored
        } else {
            addTriggerPackageName(activity, callingPackageName)
        }
    }

    /**
     * Get a list of resolved [Activity]s that can send panic trigger
     * [Intent]s.
     *
     * @param pm a [PackageManager] instance from the app's [Context]
     * @return [List] of [ResolveInfo] instances for each app that
     * responds to [Panic.ACTION_CONNECT] but not [Panic.ACTION_TRIGGER]
     */
    fun resolveTriggerApps(pm: PackageManager): List<ResolveInfo> {
        /*
         * panic trigger apps respond to ACTION_CONNECT, but only send
         * ACTION_TRIGGER, so they won't be resolved for ACTION_TRIGGER
         */
        val connects = pm.queryIntentActivities(PanicUtils.buildConnectIntent(), 0)
        if (connects.size == 0) {
            return connects
        }
        val triggerApps = ArrayList<ResolveInfo>(connects.size)
        val triggers = pm.queryIntentActivities(PanicUtils.buildTriggerIntent(), 0)
        val haveTriggers = HashSet<String>(triggers.size)
        for (resInfo in triggers) {
            haveTriggers.add(resInfo.activityInfo.packageName)
        }
        for (connect in connects) {
            if (!haveTriggers.contains(connect.activityInfo.packageName)) {
                triggerApps.add(connect)
            }
        }
        return triggerApps
    }

    /**
     * Check whether the provided [Activity] has received an [Intent]
     * that has an action of [Panic.ACTION_TRIGGER] and is from the
     * panic trigger app that is currently connected to this app.
     *
     *
     * **WARNING**: If the `Activity` has
     * `android:launchMode="singleInstance"` or `"singleTask"`, then
     * this method will always return `false` because it is not possible
     * to get the calling `Activity`, as set by
     * [Activity.startActivityForResult]
     *
     * @param activity the `Activity` to get for an `Intent`
     * @return boolean
     */
    fun receivedTriggerFromConnectedApp(activity: Activity): Boolean {
        if (!isTriggerIntent(activity.intent)) {
            return false
        }
        val packageName = PanicUtils.getCallingPackageName(activity)
        return !packageName.isNullOrEmpty()
                && isTriggerPackageNameListContains(activity, packageName)
    }

    /**
     * Check whether the provided [Activity] has received an [Intent]
     * that has an action of [Panic.ACTION_TRIGGER] and is not from the
     * currently configured panic trigger app, or, there is no panic trigger app
     * configured.
     *
     * @param activity the `Activity` to get for an `Intent`
     * @return boolean
     */
    fun shouldUseDefaultResponseToTrigger(activity: Activity): Boolean {
        if (!isTriggerIntent(activity.intent)) {
            return false
        }
        val packageName = PanicUtils.getCallingPackageName(activity)
        return (packageName.isNullOrEmpty()
                || "DEFAULT" == packageName || !isTriggerPackageNameListContains(activity, packageName))
    }

    /**
     * Given a [ListPreference] widget, this method sets up that widget to
     * display the current state of the trigger app. If a trigger app is
     * connected, then the icon and name of that app will be shown.
     *
     * @param listPreference the UI widget to display the current state
     * @param defaultSummaryResid the string resource for the default summary
     * @param noneSummaryResid the string resource for the summary when no app is allowed
     */
    fun configTriggerAppListPreference(
        listPreference: ListPreference,
        defaultSummaryResid: Int,
        noneSummaryResid: Int
    ) {
        val context = listPreference.context
        val triggerPackageNameList = getTriggerPackageNameList(context).toMutableList()
        if (triggerPackageNameList.isEmpty()) {
            triggerPackageNameList.add(Panic.PACKAGE_NAME_DEFAULT)
        }
        for (triggerPackageName in triggerPackageNameList) {
            if (triggerPackageName.isEmpty()
                || triggerPackageName == Panic.PACKAGE_NAME_DEFAULT
            ) {
                listPreference.value = Panic.PACKAGE_NAME_DEFAULT
                listPreference.setDefaultValue(Panic.PACKAGE_NAME_DEFAULT)
                listPreference.setSummary(defaultSummaryResid)
                listPreference.icon = null
            } else {
                listPreference.value = triggerPackageName
                listPreference.setDefaultValue(triggerPackageName)
                if (triggerPackageName == Panic.PACKAGE_NAME_NONE) {
                    listPreference.setSummary(noneSummaryResid)
                    listPreference.setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                } else {
                    try {
                        val pm = context.packageManager
                        listPreference.summary = pm.getApplicationLabel(
                            pm.getApplicationInfo(triggerPackageName!!, 0)
                        )
                        listPreference.icon = pm.getApplicationIcon(triggerPackageName)
                    } catch (e: PackageManager.NameNotFoundException) {
                        listPreference.setSummary(defaultSummaryResid)
                        listPreference.icon = null
                    }
                }
            }
        }
    }

    fun deleteAllAppData(context: Context) {
        // SharedPreferences can hold onto values and write them out later
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().clear().apply()
        val dirs = HashSet<File?>(3)
        dirs.add(context.filesDir.parentFile) // root of the app's /data/data
        dirs.add(context.cacheDir)
        dirs.add(context.externalCacheDir)
        for (f in context.externalCacheDirs) {
            dirs.add(f)
        }
        for (f in context.externalMediaDirs) {
            dirs.add(f)
        }
        for (dir in dirs) {
            try {
                if (dir != null && dir.exists()) {
                    deleteRecursive(dir)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            // this will force close this app, so run last
            (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .clearApplicationUserData()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteRecursive(f: File?) {
        if (f == null) {
            return
        }
        if (f.isDirectory) {
            val list = f.list()
            if (list != null) {
                for (child in list) {
                    deleteRecursive(File(f, child))
                }
            }
        }
        f.delete()
    }
}