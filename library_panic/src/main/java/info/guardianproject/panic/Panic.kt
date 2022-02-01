package info.guardianproject.panic

import android.content.Intent

object Panic {
    const val ACTION_CONNECT = "info.guardianproject.panic.action.CONNECT"
    const val ACTION_DISCONNECT = "info.guardianproject.panic.action.DISCONNECT"
    const val ACTION_TRIGGER = "info.guardianproject.panic.action.TRIGGER"
    const val PACKAGE_NAME_NONE = "NONE"
    const val PACKAGE_NAME_DEFAULT = "DEFAULT"

    /**
     * Check the specified [Intent] to see if it is a
     * [.ACTION_TRIGGER] `Intent`.
     *
     * @param intent the `Intent` to check
     * @return whether `intent` has an action of `Panic.ACTION_TRIGGER`
     */
    fun isTriggerIntent(intent: Intent?): Boolean {
        return intent != null && ACTION_TRIGGER == intent.action
    }

    fun isConnectIntent(intent: Intent?): Boolean {
        return intent != null && ACTION_CONNECT == intent.action
    }

    fun isDisconnectIntent(intent: Intent?): Boolean {
        return intent != null && ACTION_DISCONNECT == intent.action
    }
}