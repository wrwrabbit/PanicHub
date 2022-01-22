package info.guardianproject.ripple

import android.annotation.TargetApi
import android.app.Activity
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import info.guardianproject.panic.PanicTrigger

class CountDownActivity : Activity() {
    private var mCountDownAsyncTask: CountDownAsyncTask? = null
    private var mCountDownNumber: TextView? = null
    private var mTouchToCancel: TextView? = null
    private var mCancelButton: ImageView? = null
    private var mCountDown = 0xff
    private var mTestRun = false

    // lint is failing to see that setOnSystemUiVisibilityChangeListener is wrapped in
    // if (Build.VERSION.SDK_INT >= 11).
    @TargetApi(11)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mTestRun = intent.getBooleanExtra(PanicActivity.EXTRA_TEST_RUN, false)
        val window = window
        window.setBackgroundDrawable(null)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_count_down)
        val displayMetrics = resources.displayMetrics
        val scale: Int
        scale = if (displayMetrics.heightPixels > displayMetrics.widthPixels) {
            displayMetrics.heightPixels
        } else {
            displayMetrics.widthPixels
        }
        mCountDownNumber = findViewById<View>(R.id.countDownNumber) as TextView
        mCountDownNumber!!.textSize = scale.toFloat() * 0.45f / resources.displayMetrics.scaledDensity
        mTouchToCancel = findViewById<View>(R.id.tap_anywhere_to_cancel) as TextView
        mCancelButton = findViewById<View>(R.id.cancelButton) as ImageView
        mCountDownAsyncTask = CountDownAsyncTask()
        if (savedInstanceState != null && savedInstanceState.getBoolean(KEY_COUNT_DOWN_DONE, false)) {
            showDoneScreen()
        } else {
            mCountDownAsyncTask!!.execute()
        }
        val frameRoot: ConstraintLayout = findViewById(R.id.frameRoot)
        frameRoot.setOnTouchListener { view, motionEvent ->
            cancel()
            true
        }
        if (Build.VERSION.SDK_INT >= 16) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        }
        frameRoot.setOnSystemUiVisibilityChangeListener { visibility -> /* If the nav bar comes back while the countdown is active,
                   that means the user clicked on the screen. Showing the
                   test dialog also triggers this, so filter on countdown */
            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0 && mCountDown > 0) {
                cancel()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_COUNT_DOWN_DONE, mCountDown == 0)
        if (mCountDown > 0) {
            // cancel the countdown, it'll get restarted when the Activity comes back
            mCountDownAsyncTask!!.cancel(true)
        }
    }

    private fun cancel() {
        mCountDownAsyncTask!!.cancel(true)
        finish()
    }

    private fun showDoneScreen() {
        mTouchToCancel!!.setText(R.string.done)
        mTouchToCancel!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, 64f)
        mCancelButton!!.visibility = View.GONE
        mCountDownNumber!!.visibility = View.GONE
    }

    private inner class CountDownAsyncTask : AsyncTask<Void?, Int?, Void?>() {
        override fun onProgressUpdate(vararg values: Int?) {
            mCountDown = values[0]!!
            if (values[0]!! > 0) {
                mCountDownNumber!!.text = values[0].toString()
            } else {
                showDoneScreen()
            }
        }

        override fun doInBackground(vararg voids: Void?): Void? {
            try {
                var countdown = 2
                while (countdown >= 0) {
                    publishProgress(countdown)
                    countdown--
                    Thread.sleep(1000)
                    if (isCancelled) {
                        break
                    }
                }
            } catch (e: InterruptedException) {
                // ignored
            }
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            val activity: Activity = this@CountDownActivity
            if (mTestRun) {
                AlertDialog.Builder(activity)
                    .setTitle(R.string.test_dialog_title)
                    .setMessage(R.string.panic_test_successful)
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        dialog.dismiss()
                        finish()
                    }.show()
            } else {
                runOnUiThread {
                    PanicTrigger.sendTrigger(activity)
                    Toast.makeText(activity, R.string.done, Toast.LENGTH_LONG).show()
                    activity.finish()
                }
                //                /* This app needs to stay running for a while to make sure that it sends
//                 * all of the Intents to Activities, Services, and BroadcastReceivers. If
//                 * it exits too soon, they will not get sent. */
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        ExitActivity.exitAndRemoveFromRecentApps(activity);
//                    }
//                }, 10000); // 10 second delay
            }
        }
    }

    companion object {
        private const val TAG = "CountDownActivity"
        private const val KEY_COUNT_DOWN_DONE = "keyCountDownDone"
    }
}