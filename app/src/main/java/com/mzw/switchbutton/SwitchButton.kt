package com.mzw.switchbutton

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller

class SwitchButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    val VIEW_HEIGHT = 20
    private val strokeLineWidth = 3
    private val circleStrokeWidth = 3

    private val StrokeLineColor = "#bebfc1"
    private val StrokeSolidColor = "#00ffffff"
    private val CircleStrokeColor = "#abacaf"
    private val CircleCheckedColor = "#ff5555"
    private val CircleNoCheckedColor = "#bebfc1"

    private var PADDING = 20
    private var MOVE_DISTANCE = 50

    private var circle_x: Float = 0.toFloat()

    private var isBigCircle = false

    private var strokeHeight: Int = 0
    private var strokeCircleRadius: Float = 0.toFloat()
    private var circleRadius: Float = 0.toFloat()
    private var mScroller: Scroller
    private var isChecked = false

    private var mWidth: Int = 0
    private var mHeight: Int = 0

    private var mPaint: Paint
    private var circleStartX: Float = 0.toFloat()
    private var circleEndX: Float = 0.toFloat()
    private var centerX: Int = 0
    private var centerY: Int = 0
    private var preX = 0f
    private var isMove: Boolean = false
    private var view_height_int: Int = 0
    private var strokeLineColor_int: Int = 0
    private var strokeCheckedSolidColor_int: Int = 0
    private var strokeNoCheckedSolidColor_int: Int = 0
    private var circleStrokeColor_int: Int = 0
    private var circleChecked_int: Int = 0
    private var circleNoCheckedColor_int: Int = 0

    init {
        isEnabled = true
        isClickable = true
        mPaint = Paint()
        mScroller = Scroller(context)
        view_height_int = dip2px(context, VIEW_HEIGHT.toFloat())
        strokeLineColor_int = Color.parseColor(StrokeLineColor)
        strokeNoCheckedSolidColor_int = Color.parseColor(StrokeSolidColor)
        circleStrokeColor_int = Color.parseColor(CircleStrokeColor)
        circleChecked_int = Color.parseColor(CircleCheckedColor)
        circleNoCheckedColor_int = Color.parseColor(CircleNoCheckedColor)
    }

    fun setSmallCircleModel(
        strokeLineColor: Int,
        strokeSolidColor: Int,
        circleCheckedColor: Int,
        circleNoCheckedColor: Int
    ) {
        isBigCircle = false
        strokeLineColor_int = strokeLineColor
        strokeNoCheckedSolidColor_int = strokeSolidColor
        circleChecked_int = circleCheckedColor
        circleNoCheckedColor_int = circleNoCheckedColor
        invalidate()
    }

    fun setBigCircleModel(
        strokeLineColor: Int, strokeCheckedSolidColor: Int,
        strokeNoCheckedSolidColor: Int, circleChecked: Int,
        circleNoCheckColor: Int
    ) {
        isBigCircle = true
        strokeLineColor_int = strokeLineColor
        strokeCheckedSolidColor_int = strokeCheckedSolidColor
        strokeNoCheckedSolidColor_int = strokeNoCheckedSolidColor
        circleChecked_int = circleChecked
        circleNoCheckedColor_int = circleNoCheckColor
        invalidate()
    }

    fun setOnCheckedListener(listener: SlideButtonOnCheckedListener) {
        this.mListener = listener
    }

    fun setChecked(checked: Boolean) {
        this.isChecked = checked
        if (isChecked) {
            circle_x = circleEndX
        } else {
            circle_x = circleStartX
        }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        var widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        var heightSize = MeasureSpec.getSize(heightMeasureSpec)

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = view_height_int
        }
        if (widthMode == MeasureSpec.AT_MOST) {
            widthSize = heightSize * 2
        }
        setMeasuredDimension(widthSize, heightSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        if (isBigCircle) {
            PADDING = h / 10
        } else {
            PADDING = h / 15
        }
        MOVE_DISTANCE = mWidth / 100
        strokeHeight = h - PADDING * 2
        strokeCircleRadius = (strokeHeight / 2).toFloat()
        centerY = mHeight / 2
        if (isBigCircle) {
            circleRadius = strokeCircleRadius + PADDING
        } else {
            circleRadius = strokeCircleRadius - PADDING * 2
        }
        Log.i("TAG", "mHeight:$mHeight   strokeCircleRadius: $strokeCircleRadius")
        circleStartX = PADDING + strokeCircleRadius
        circleEndX = mWidth - circleStartX
        if (isChecked) {
            circle_x = circleEndX
        } else {
            circle_x = circleStartX
        }

        centerX = mWidth / 2
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawRect(canvas)
        drawCircle(canvas)
    }

    private fun drawRect(canvas: Canvas) {
        mPaint.reset()
        mPaint.setAntiAlias(true)
        mPaint.setDither(true)

        if (isBigCircle && isChecked) {
            mPaint.setColor(strokeCheckedSolidColor_int)
        } else {
            mPaint.setColor(strokeNoCheckedSolidColor_int)
        }
        canvas.drawRoundRect(
            PADDING.toFloat(),
            PADDING.toFloat(),
            (mWidth - PADDING).toFloat(),
            (mHeight - PADDING).toFloat(),
            strokeCircleRadius,
            strokeCircleRadius,
            mPaint
        )

        mPaint.setStrokeWidth(strokeLineWidth.toFloat())
        mPaint.setColor(strokeLineColor_int)
        mPaint.setStyle(Paint.Style.STROKE)
        canvas.drawRoundRect(
            PADDING.toFloat(),
            PADDING.toFloat(),
            (mWidth - PADDING).toFloat(),
            (mHeight - PADDING).toFloat(),
            strokeCircleRadius,
            strokeCircleRadius,
            mPaint
        )
    }

    private fun drawCircle(canvas: Canvas) {
        mPaint.reset()
        mPaint.setAntiAlias(true)
        mPaint.setDither(true)
        var circleRadiusNew = circleRadius
        if (isBigCircle) {
            circleRadiusNew -= circleStrokeWidth.toFloat()
        }
        if (isChecked) {
            mPaint.setColor(circleChecked_int)
        } else {
            mPaint.setColor(circleNoCheckedColor_int)
        }
        canvas.drawCircle(circle_x, centerY.toFloat(), circleRadiusNew, mPaint)

        if (isBigCircle) {
            mPaint.setColor(circleStrokeColor_int)
            mPaint.setStyle(Paint.Style.STROKE)
            mPaint.setStrokeWidth(circleStrokeWidth.toFloat())
            canvas.drawCircle(circle_x, centerY.toFloat(), circleRadiusNew, mPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                preX = event.x
                isMove = false
                if (!isChecked) {
                    circle_x = PADDING + strokeCircleRadius
                } else {
                    circle_x = mWidth.toFloat() - PADDING.toFloat() - strokeCircleRadius
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val move_x = event.x
                if (Math.abs(move_x - preX) > MOVE_DISTANCE) {
                    isMove = true
                    if (move_x < circleStartX) {
                        circle_x = circleStartX
                        isChecked = false
                    } else if (move_x > circleEndX) {
                        circle_x = circleEndX
                        isChecked = true
                    } else {
                        circle_x = move_x
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                if (isMove) {
                    if (circle_x >= centerX) {
                        mScroller.startScroll(
                            circle_x.toInt(),
                            0,
                            (circleEndX - circle_x).toInt(),
                            0
                        )
                        isChecked = true
                    } else {
                        mScroller.startScroll(
                            circle_x.toInt(),
                            0,
                            (circleStartX - circle_x).toInt(),
                            0
                        )
                        isChecked = false
                    }
                } else {
                    if (!isChecked) {
                        mScroller.startScroll(
                            circle_x.toInt(),
                            0,
                            (circleEndX - circle_x).toInt(),
                            0
                        )
                        isChecked = true
                    } else {
                        mScroller.startScroll(
                            circle_x.toInt(),
                            0,
                            (circleStartX - circle_x).toInt(),
                            0
                        )
                        isChecked = false
                    }
                }
                if (mListener != null) {
                    mListener!!.onCheckedChangeListener(isChecked)
                }
                invalidate()
            }
        }
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            circle_x = mScroller.getCurrX().toFloat()
            invalidate()
        }
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    interface SlideButtonOnCheckedListener {
        fun onCheckedChangeListener(isChecked: Boolean)
    }

    private var mListener: SlideButtonOnCheckedListener? = null
}