package com.panic.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.panic.R
import com.panic.databinding.ActivityMainBinding
import com.panic.ext.snackbar
import com.panic.ext.toastLong
import com.panic.ui.pages.Tabs
import com.panic.ui.pages.adapter.SectionsPagerAdapter
import info.guardianproject.panic.PanicTrigger

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (PanicTrigger.checkForConnectIntent(this)
            || PanicTrigger.checkForDisconnectIntent(this)
        ) {
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        sectionsPagerAdapter = SectionsPagerAdapter(this)
        binding.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = getString(Tabs.values()[position].tabTitle)
        }.attach()

        binding.fab.setOnClickListener { view ->
            PanicTrigger.sendTrigger(this)
            view.snackbar(getString(R.string.alarm_sent))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == CONNECT_RESULT) {
            sectionsPagerAdapter.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val TAG = "MainActivity"
        const val CONNECT_RESULT = 0x01
    }
}

/*
* AlertDialog.Builder(activity)
                    .setTitle(R.string.test_dialog_title)
                    .setMessage(R.string.panic_test_successful)
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        dialog.dismiss()
                        finish()
                    }.show()
* */