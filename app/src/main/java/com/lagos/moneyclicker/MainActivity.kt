package com.lagos.moneyclicker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import android.graphics.Color

class MainActivity : Activity() {
    private val gold = Color.rgb(242, 190, 75)
    private val dark = Color.rgb(17, 22, 32)
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        buildUi()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 32, 28, 24)
            setBackgroundColor(dark)
        }
        val title = TextView(this).apply {
            text = "LAGOS MONEY CLICKER"
            textSize = 24f
            setTextColor(gold)
            gravity = Gravity.CENTER
        }
        root.addView(title, matchWrap())
        val subtitle = TextView(this).apply {
            text = "AUTO CLICKER  •  ANDROID"
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 24)
        }
        root.addView(subtitle, matchWrap())

        status = TextView(this).apply {
            text = "Setup required: enable Accessibility and overlay permissions."
            textSize = 14f
            setTextColor(Color.WHITE)
            setPadding(16, 18, 16, 18)
            setBackgroundColor(Color.rgb(35, 44, 60))
        }
        root.addView(status, matchWrap())

        addButton(root, "1. ENABLE ACCESSIBILITY SERVICE") {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        addButton(root, "2. ALLOW DISPLAY OVER OTHER APPS") {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")))
            } else Toast.makeText(this, "Overlay permission already allowed", Toast.LENGTH_SHORT).show()
        }
        addButton(root, "3. OPEN FLOATING CONTROLS") {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Allow overlay permission first", Toast.LENGTH_LONG).show()
            } else {
                startService(Intent(this, FloatingControlsService::class.java))
                Toast.makeText(this, "Floating controls started", Toast.LENGTH_SHORT).show()
            }
        }

        val note = TextView(this).apply {
            text = "HOW TO USE\n• Enable the service in Android Accessibility settings.\n• Allow display-over-other-apps permission.\n• Open floating controls.\n• Drag the gold target marker to the position you want tapped.\n• Add up to 5 points, then press START.\n• Use STOP at any time. Keep automation visible and use it only where permitted."
            textSize = 13f
            setTextColor(Color.LTGRAY)
            setPadding(4, 20, 4, 0)
        }
        root.addView(note, matchWrap())
        val scroll = ScrollView(this)
        scroll.addView(root)
        setContentView(scroll)
    }

    private fun addButton(parent: LinearLayout, label: String, action: () -> Unit) {
        val b = Button(this).apply {
            text = label
            setTextColor(Color.BLACK)
            setBackgroundColor(gold)
            isAllCaps = false
            setOnClickListener { action() }
        }
        val p = LinearLayout.LayoutParams(-1, -2)
        p.topMargin = 14
        parent.addView(b, p)
    }

    private fun matchWrap() = LinearLayout.LayoutParams(-1, -2)
}
