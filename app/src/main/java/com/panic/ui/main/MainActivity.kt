package com.panic.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.panic.R
import com.panic.databinding.ActivityMainBinding
import com.panic.ext.currentNavFragment
import com.panic.ui.history.HistoryViewModel
import info.guardianproject.panic.Panic
import info.guardianproject.panic.PanicResponder
import info.guardianproject.panic.PanicTrigger
import info.guardianproject.panic.PanicUtils


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        when {
            Panic.isTriggerIntent(intent) -> {
                handlePanic()
            }
            Panic.isConnectIntent(intent) -> {
                handleConnect()
            }
            Panic.isDisconnectIntent(intent) -> {
                handleDisconnect()
            }
            else -> {
                handleLaunch()
            }
        }
    }

    private fun handleLaunch() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_nav)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_history
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun handlePanic() {
        Log.d(TAG, "isTriggerIntent: true")

        val callingPackageName = PanicUtils.getCallingPackageName(this)
        Log.d(TAG, "callingPackageName: $callingPackageName")
        if (callingPackageName == null){
            finish()
            return
        }

        /*check that caller is at allowed trigger list*/
        if (!PanicResponder.isTriggerPackageNameListContains(this, callingPackageName)) {
            Log.e(TAG, "isTriggerPackageNameListContains: $callingPackageName - false")
            finish()
            return
        }
        Log.d(TAG, "isTriggerPackageNameListContains: $callingPackageName - true")
        viewModel.onPanicHandledLiveEvent.observe(this){ s->
            finish()
        }
        viewModel.onPanicEvent(callingPackageName)
    }

    private fun handleConnect() {
        PanicTrigger.checkForConnectIntent(this)
        finish()
    }

    private fun handleDisconnect() {
        PanicTrigger.checkForDisconnectIntent(this)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        currentNavFragment()?.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val TAG = "MainActivity"
        const val CONNECT_RESULT = 0x01
    }
}