package info.guardianproject.ripple

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import info.guardianproject.ripple.R

class RippleDrawingView : View {
    private var mRippleOutsidePaint: Paint? = null
    private var mRippleInsidePaint: Paint? = null
    private var mRippleCenterPaint: Paint? = null
    private var mSize = 0f

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        init(context)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (Build.VERSION.SDK_INT >= 11) {
            val width = width.toFloat()
            val x = width / 2
            val y = height * 0.92f
            canvas.drawCircle(x, y, mSize, mRippleOutsidePaint!!)
            canvas.drawCircle(x, y, mSize - width * 0.166666f, mRippleInsidePaint!!)
            canvas.drawCircle(x, y, mSize - width * 0.333333f, mRippleCenterPaint!!)
        }
    }

    fun setSize(size: Float) {
        mSize = size
    }

    private fun init(context: Context) {
        val r = context.resources
        mRippleOutsidePaint = Paint()
        mRippleOutsidePaint!!.style = Paint.Style.FILL
        mRippleOutsidePaint!!.color = r.getColor(R.color.ripple_outside)
        mRippleInsidePaint = Paint()
        mRippleInsidePaint!!.style = Paint.Style.FILL
        mRippleInsidePaint!!.color = r.getColor(R.color.ripple)
        mRippleCenterPaint = Paint()
        mRippleCenterPaint!!.style = Paint.Style.FILL
        mRippleCenterPaint!!.color = r.getColor(R.color.triggered)
    }
}