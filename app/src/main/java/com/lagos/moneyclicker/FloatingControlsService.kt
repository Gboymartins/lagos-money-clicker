package com.lagos.moneyclicker

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.provider.Settings
import android.view.*
import android.widget.*
import kotlin.math.max

class FloatingControlsService : Service() {
    private lateinit var wm: WindowManager
    private var panel: View? = null
    private var targetViews = mutableListOf<View>()
    private val targets = mutableListOf<Pair<Float, Float>>()
    private var counter: TextView? = null
    private var interval = 20L
    private val gold = Color.rgb(242, 190, 75)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return }
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
        showPanel()
        addTarget()
    }

    private fun params(w: Int, h: Int) = WindowManager.LayoutParams(
        w, h, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    )

    private fun showPanel() {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 10, 12, 10)
            setBackgroundColor(Color.rgb(25, 31, 43))
        }
        val heading = TextView(this).apply {
            text = "LAGOS CLICKER"
            setTextColor(gold); textSize = 13f
        }
        box.addView(heading)
        counter = TextView(this).apply { text = "Taps: 0"; setTextColor(Color.WHITE); textSize = 12f }
        counterView = counter
        box.addView(counter)
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        fun button(label: String, action: () -> Unit) {
            val b = Button(this).apply {
                text = label; textSize = 10f; isAllCaps = false
                setPadding(4, 0, 4, 0); setOnClickListener { action() }
            }
            row.addView(b, LinearLayout.LayoutParams(0, 48, 1f))
        }
        button("ADD") { addTarget() }
        button("START") { startTaps() }
        button("STOP") { TapAccessibilityService.instance?.stopTapping() }
        button("−") { interval = (interval - 5).coerceAtLeast(1); Toast.makeText(this, "Interval: ${interval}ms", Toast.LENGTH_SHORT).show() }
        button("+") { interval = (interval + 5).coerceAtMost(2000); Toast.makeText(this, "Interval: ${interval}ms", Toast.LENGTH_SHORT).show() }
        box.addView(row)
        val p = params(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.TOP or Gravity.START; x = 20; y = 100
        }
        panel = box
        wm.addView(box, p)
        box.setOnTouchListener(object : View.OnTouchListener {
            var sx = 0f; var sy = 0f; var ox = 0; var oy = 0
            override fun onTouch(v: View, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN) {
                    sx = e.rawX; sy = e.rawY
                    val lp = box.layoutParams as WindowManager.LayoutParams
                    ox = lp.x; oy = lp.y; return true
                }
                if (e.action == MotionEvent.ACTION_MOVE) {
                    val lp = box.layoutParams as WindowManager.LayoutParams
                    lp.x = ox + (e.rawX - sx).toInt()
                    lp.y = oy + (e.rawY - sy).toInt()
                    wm.updateViewLayout(box, lp); return true
                }
                return false
            }
        })
    }

    private fun addTarget() {
        if (targets.size >= 5) {
            Toast.makeText(this, "Maximum 5 targets", Toast.LENGTH_SHORT).show(); return
        }
        val marker = TextView(this).apply {
            text = "✥"; textSize = 30f; setTextColor(gold)
            setPadding(12, 8, 12, 8); setBackgroundColor(0x55333333)
        }
        val p = params(100, 90).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 120 + targets.size * 35; y = 420 + targets.size * 35
        }
        wm.addView(marker, p)
        targetViews.add(marker)
        targets.add(Pair((p.x + 50).toFloat(), (p.y + 45).toFloat()))
        marker.setOnTouchListener(object : View.OnTouchListener {
            var sx = 0f; var sy = 0f; var ox = 0; var oy = 0
            override fun onTouch(v: View, e: MotionEvent): Boolean {
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        sx = e.rawX; sy = e.rawY
                        val lp = marker.layoutParams as WindowManager.LayoutParams
                        ox = lp.x; oy = lp.y; return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val lp = marker.layoutParams as WindowManager.LayoutParams
                        lp.x = ox + (e.rawX - sx).toInt()
                        lp.y = oy + (e.rawY - sy).toInt()
                        wm.updateViewLayout(marker, lp)
                        val i = targetViews.indexOf(marker)
                        if (i >= 0) targets[i] = Pair((lp.x + 50).toFloat(), (lp.y + 45).toFloat())
                        return true
                    }
                }
                return false
            }
        })
        Toast.makeText(this, "Drag ✥ to a tap location. Add up to 5.", Toast.LENGTH_SHORT).show()
    }

    private fun startTaps() {
        val service = TapAccessibilityService.instance
        if (service == null) {
            Toast.makeText(this, "Enable Lagos Money Clicker in Accessibility settings first.", Toast.LENGTH_LONG).show()
            return
        }
        service.startTapping(targets.toList(), interval)
    }

    override fun onDestroy() {
        TapAccessibilityService.instance?.stopTapping()
        counterView = null
        targetViews.forEach { try { wm.removeView(it) } catch (_: Exception) {} }
        panel?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        super.onDestroy()
    }

    companion object {
        @Volatile private var counterView: TextView? = null
        fun updateCounter(value: Long) { counterView?.post { counterView?.text = "Taps: $value" } }
    }
}
