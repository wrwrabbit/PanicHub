package com.panic.handler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.panic.handler.databinding.ActivityMainBinding
import com.panic.handler.ext.formatedBy
import info.guardianproject.panic.Panic
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val panicDataStorage: PanicDataStorage by lazy {
        PanicDataStorage(this)
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        when {
            Panic.isTriggerIntent(intent) -> {
                handlePanic()
            }
            else -> {
                handleLaunch()
            }
        }
    }

    private fun handlePanic() {
        panicDataStorage.resetCounter()
        finish()
    }

    private fun handleLaunch() {
        binding.textView.text = panicDataStorage.getCounterValue().toString()
        binding.submitButton.setOnClickListener {
            panicDataStorage.increaseCounter()
            binding.textView.text = panicDataStorage.getCounterValue().toString()
        }
    }
}