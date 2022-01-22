package info.guardianproject.ripple

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import info.guardianproject.ripple.CountDownActivity

class PanicActivity : Activity(), OnTouchListener {
    private var yMaxTranslation = 0
    private var yCurrentTranslation = 0
    private var yDelta = 0
    private var mReleaseWillTrigger = false
    private var mFrameRoot: RelativeLayout? = null
    private lateinit var mPanicSwipeButton: ImageView
    private var mTextHint: TextView? = null
    private var mSwipeArrows: ImageView? = null
    private var mRipples: RippleDrawingView? = null
    private var mColorWhite = 0
    private var mColorRipple = 0
    private var mColorTriggeredText = 0
    private var mRedStart = 0
    private var mGreenStart = 0
    private var mBlueStart = 0
    private var mRedDelta = 0
    private var mGreenDelta = 0
    private var mBlueDelta = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window.setBackgroundDrawable(null)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_panic)
        mFrameRoot = findViewById<View>(R.id.frameRoot) as RelativeLayout
        mTextHint = findViewById<View>(R.id.textHint) as TextView
        mSwipeArrows = findViewById<View>(R.id.swipe_arrows) as ImageView
        mRipples = findViewById<View>(R.id.ripples) as RippleDrawingView
        mPanicSwipeButton = findViewById<View>(R.id.panic_swipe_button) as ImageView
        mPanicSwipeButton.setOnTouchListener(this)
        val btnCancel = findViewById<View>(R.id.btnCancel)
        btnCancel.setOnClickListener { finish() }
        val r = resources
        mColorWhite = r.getColor(android.R.color.white)
        mColorRipple = r.getColor(R.color.ripple)
        val colorTriggered = r.getColor(R.color.triggered)
        mColorTriggeredText = r.getColor(R.color.triggered_text)
        mRedStart = mColorRipple and 0x00ff0000 shr 16
        mGreenStart = mColorRipple and 0x0000ff00 shr 8
        mBlueStart = mColorRipple and 0x000000ff
        val redEnd = colorTriggered and 0x00ff0000 shr 16
        val greenEnd = colorTriggered and 0x0000ff00 shr 8
        val blueEnd = colorTriggered and 0x000000ff
        mRedDelta = redEnd - mRedStart
        mGreenDelta = greenEnd - mGreenStart
        mBlueDelta = blueEnd - mBlueStart
    }

    override fun onPause() {
        super.onPause()
        // if the user navigates away, reset the trigger process
        finish()
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (view === mPanicSwipeButton) {
            val Y = event.rawY.toInt()
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    mPanicSwipeButton.isPressed = true
                    val lParams = view.getLayoutParams() as RelativeLayout.LayoutParams
                    yDelta = Y - lParams.topMargin
                    mReleaseWillTrigger = false
                    var mArrowRect: Rect? = Rect()
                    if (!mSwipeArrows!!.getGlobalVisibleRect(mArrowRect)) {
                        mArrowRect = null
                    } else {
                        val symbolRect = Rect()
                        if (mPanicSwipeButton.getGlobalVisibleRect(symbolRect)) {
                            yMaxTranslation = mArrowRect!!.bottom - symbolRect.bottom
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    mPanicSwipeButton.isPressed = false
                    mRipples!!.setSize(0f)
                    mRipples!!.invalidate()
                    if (mReleaseWillTrigger) {
                        AnimationHelpers.scale(mPanicSwipeButton, 1.0f, 0f, 200) {
                            val intent = Intent(baseContext, CountDownActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            intent.putExtra(
                                EXTRA_TEST_RUN,
                                getIntent().getBooleanExtra(EXTRA_TEST_RUN, false)
                            )
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        AnimationHelpers.translateY(mPanicSwipeButton, yCurrentTranslation.toFloat(), 0f, 200)
                        mFrameRoot!!.setBackgroundColor(mColorRipple)
                    }
                    mReleaseWillTrigger = false
                }
                MotionEvent.ACTION_POINTER_DOWN -> {}
                MotionEvent.ACTION_POINTER_UP -> {}
                MotionEvent.ACTION_MOVE -> {
                    yCurrentTranslation = Math.max(0, Math.min(Y - yDelta, yMaxTranslation))
                    AnimationHelpers.translateY(mPanicSwipeButton, yCurrentTranslation.toFloat(), yCurrentTranslation.toFloat(), 0)
                    val v = yCurrentTranslation.toFloat() / yMaxTranslation
                    mFrameRoot!!.setBackgroundColor(
                        (-0x1000000 + (mRedStart + (mRedDelta * v).toInt() shl 16)
                                + (mGreenStart + (mGreenDelta * v).toInt() shl 8)
                                + (mBlueStart + mBlueDelta * v)).toInt()
                    )
                    val rippleSize = yMaxTranslation / 2
                    if (yCurrentTranslation > rippleSize) {
                        val k = (rippleSize / (yMaxTranslation - rippleSize)).toFloat()
                        mRipples!!.setSize((yCurrentTranslation - rippleSize) * k)
                        mRipples!!.invalidate()
                    }
                    if (yCurrentTranslation == yMaxTranslation) {
                        mReleaseWillTrigger = true
                        mTextHint!!.setText(R.string.release_to_confirm)
                        mTextHint!!.setTextColor(mColorTriggeredText)
                    } else {
                        mReleaseWillTrigger = false
                        mTextHint!!.setText(R.string.swipe_down_to_trigger)
                        mTextHint!!.setTextColor(mColorWhite)
                    }
                }
            }
            view.invalidate()
            return true
        }
        return false
    }

    companion object {
        const val TAG = "PanicActivity"
        const val EXTRA_TEST_RUN = "info.guardianproject.ripple.extra.TEST_RUN"
    }
}