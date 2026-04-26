package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0
) : View(context, attr, defStyleAttr, defStyleRes) {

    private var bitmap: Bitmap? = null
    private val imagePlayBitmap: Bitmap?
    private val imagePauseBitmap: Bitmap?
    private var isPlaying = false
    private var imageRect = RectF(0f, 0f, 0f, 0f)

    init {
        context.theme.obtainStyledAttributes(
            attr,
            R.styleable.CustomPlayback,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {

                imagePlayBitmap = getDrawable(R.styleable.CustomPlayback_imageResPlayId)?.toBitmap()
                imagePauseBitmap = getDrawable(R.styleable.CustomPlayback_imageResPauseId)?.toBitmap()

            } finally {
                recycle()
            }
        }
    }

    fun switchPlayButton(playing: Boolean) {
        if (isPlaying == playing) return
        isPlaying = playing
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val defaultBitmap = imagePlayBitmap ?: imagePauseBitmap

        val desiredWidth = defaultBitmap?.width ?: 0
        val desiredHeight = defaultBitmap?.height ?: 0

        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)


        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> minOf(desiredWidth, widthSize)
            else -> desiredWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(desiredHeight, heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, w.toFloat(), h.toFloat())
    }

    override fun onDraw(canvas: Canvas) {

        bitmap = if (isPlaying) imagePauseBitmap else imagePlayBitmap
        bitmap ?: return

        canvas.drawBitmap(bitmap!!, null, imageRect, null)

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return true
            }
            MotionEvent.ACTION_UP -> {
                switchPlayButton(!isPlaying)
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }


}