
package com.lagos.moneyclicker

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import java.util.concurrent.atomic.AtomicBoolean

class TapAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var points = mutableListOf<Pair<Float, Float>>()
    private var pointIndex = 0
    private var intervalMs = 20L
    private var tapCount = 0L
    private val running = AtomicBoolean(false)

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {
        stopTapping()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
    }

    fun startTapping(targets: List<Pair<Float, Float>>, interval: Long) {
        if (targets.isEmpty()) return

        points = targets.toMutableList()
        pointIndex = 0
        intervalMs = interval.coerceAtLeast(1L)
        tapCount = 0
        running.set(true)

        handler.removeCallbacks(tapRunnable)
        handler.post(tapRunnable)
    }

    fun stopTapping() {
        running.set(false)
        handler.removeCallbacks(tapRunnable)
    }

    private val tapRunnable: Runnable = object : Runnable {
        override fun run() {
            if (!running.get() || points.isEmpty()) return

            val point = points[pointIndex % points.size]
            pointIndex++

            val path = Path().apply {
                moveTo(point.first, point.second)
            }

            val gesture = GestureDescription.Builder()
                .addStroke(
                    GestureDescription.StrokeDescription(path, 0, 35)
                )
                .build()

            dispatchGesture(
                gesture,
                object : GestureResultCallback() {
                    override fun onCompleted(
                        gestureDescription: GestureDescription?
                    ) {
                        tapCount++
                        FloatingControlsService.updateCounter(tapCount)

                        if (running.get()) {
                            handler.postDelayed(tapRunnable, intervalMs)
                        }
                    }

                    override fun onCancelled(
                        gestureDescription: GestureDescription?
                    ) {
                        if (running.get()) {
                            handler.postDelayed(tapRunnable, intervalMs)
                        }
                    }
                },
                handler
            )
        }
    }

    override fun onDestroy() {
        stopTapping()
        if (instance === this) {
            instance = null
        }
        super.onDestroy()
    }

    companion object {
        @Volatile
        var instance: TapAccessibilityService? = null
            private set
    }
}
