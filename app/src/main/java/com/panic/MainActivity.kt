package com.panic

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import info.guardianproject.panic.PanicTrigger
import java.util.ArrayList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (PanicTrigger.checkForConnectIntent(this)
            || PanicTrigger.checkForDisconnectIntent(this)
        ) {
            finish()
            return
        }

        setContentView(R.layout.activity_main)
        val btnSend = findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {
            printAll()
            printActivities()
            printBroadcastReceivers()
            printServices()
            printEnabled()

            Log.e(TAG, "sendTrigger")
            PanicTrigger.sendTrigger(this)

            printAll()
            printActivities()
            printBroadcastReceivers()
            printServices()
            printEnabled()
        }
    }

    override fun onResume() {
        super.onResume()
        printAll()
        printActivities()
        printBroadcastReceivers()
        printServices()
        printEnabled()

        val enabledResponders = PanicTrigger.getEnabledResponders(this)
        val panic = listOf("org.fdroid.fdroid","com.panic")
        for (item in panic){
            if (!enabledResponders.contains(item)) {
                Log.e(TAG, "onCreate: enableResponder $item")
                PanicTrigger.enableResponder(this, item)
            }
        }

        printAll()
        printActivities()
        printBroadcastReceivers()
        printServices()
        printEnabled()
    }


    private fun printActivities() {
        val list = PanicTrigger.getResponderActivities(this)
        Log.e(TAG, "onCreate: getResponderActivities = $list")
    }
    private fun printBroadcastReceivers() {
        val list = PanicTrigger.getResponderBroadcastReceivers(this)
        Log.e(TAG, "onCreate: getResponderBroadcastReceivers = $list")
    }
    private fun printServices() {
        val list = PanicTrigger.getResponderServices(this)
        Log.e(TAG, "onCreate: getResponderServices = $list")
    }
    private fun printAll() {
        val allResponders = PanicTrigger.getAllResponders(this)
        Log.e(TAG, "onCreate: allResponders = $allResponders")
    }

    private fun printEnabled() {
        val enabledResponders = PanicTrigger.getEnabledResponders(this)
        Log.e(TAG, "onCreate: enabledResponders = $enabledResponders")
    }

    private fun printConnected() {
        val connectedResponders = PanicTrigger.getConnectedResponders(this)
        Log.e(TAG, "onCreate: connectedResponders = $connectedResponders")
    }

    private fun printCanConnect() {
        val respondersThatCanConnect = PanicTrigger.getRespondersThatCanConnect(this)
        Log.e(TAG, "onCreate: respondersThatCanConnect = $respondersThatCanConnect")
    }

    companion object {
        const val TAG: String = "MainActivity"
    }
}