package info.guardianproject.ripple

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import info.guardianproject.panic.Panic
import info.guardianproject.panic.PanicTrigger
import info.guardianproject.panic.PanicTrigger.addConnectedResponder
import info.guardianproject.panic.PanicTrigger.checkForConnectIntent
import info.guardianproject.panic.PanicTrigger.checkForDisconnectIntent
import info.guardianproject.panic.PanicTrigger.getAllResponders
import info.guardianproject.panic.PanicTrigger.getEnabledResponders
import info.guardianproject.panic.PanicTrigger.getRespondersThatCanConnect
import java.util.ArrayList
import java.util.LinkedHashSet

class MainActivity : AppCompatActivity() {
    private lateinit var responders: Array<String>
    private lateinit var enabledResponders: Set<String>
    private lateinit var respondersThatCanConnect: Set<String>
    private lateinit var appLabelList: ArrayList<CharSequence>
    private lateinit var iconList: ArrayList<Drawable>
    private lateinit var prefs: SharedPreferences
    private var requestPackageName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkForConnectIntent(this)
            || checkForDisconnectIntent(this)
        ) {
            finish()
            return
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.title = ""
        setSupportActionBar(toolbar)
        val panicButton = findViewById<View>(R.id.panic_button)
        panicButton.setOnClickListener {
//            startActivity(Intent(this@MainActivity, PanicActivity::class.java))
            PanicTrigger.sendTrigger(this)
            Toast.makeText(this, R.string.done, Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        enabledResponders = getEnabledResponders(this)
        respondersThatCanConnect = getRespondersThatCanConnect(this)

        // sort enabled first, then disabled
        responders = LinkedHashSet(getAllResponders(this)).toTypedArray()
        appLabelList = ArrayList(responders.size)
        iconList = ArrayList(responders.size)

        val pm = packageManager
        for (packageName in responders) {
            try {
                appLabelList.add(pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)))
                iconList.add(pm.getApplicationIcon(packageName))
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }

        val recyclerView = findViewById<View>(R.id.recycler_view) as RecyclerView
        recyclerView.addItemDecoration(SimpleDividerItemDecoration(applicationContext))
        recyclerView.setHasFixedSize(true) // does not change, except in onResume()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AppsAdapter(
            responders,
            enabledResponders,
            respondersThatCanConnect,
            iconList,
            appLabelList,
            onClickListener =
            { rowPackageName ->
                requestPackageName = rowPackageName
                val intent = Intent(Panic.ACTION_CONNECT)
                intent.setPackage(requestPackageName)
                // TODO add TrustedIntents here
                ActivityCompat.startActivityForResult(this, intent, CONNECT_RESULT, null)
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_test_run -> {
                val intent = Intent(this, TestActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == CONNECT_RESULT) {
            addConnectedResponder(this, requestPackageName)
        }
    }

    companion object {
        const val TAG = "MainActivity"
        private const val CONNECT_RESULT = 0x01
    }
}