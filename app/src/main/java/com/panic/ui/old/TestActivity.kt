package info.guardianproject.ripple

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        val panicButton = findViewById<View>(R.id.panic_button) as ImageButton
        panicButton.setOnClickListener {
            val prefs = PreferenceManager.getDefaultSharedPreferences(this@TestActivity)
            val intent = Intent(this@TestActivity, PanicActivity::class.java)
            intent.putExtra(PanicActivity.EXTRA_TEST_RUN, true)
            startActivity(intent)
            finish()
        }
    }
}