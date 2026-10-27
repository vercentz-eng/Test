package com.example.ramcleaner

import android.app.ActivityManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var memoryText: TextView
    private lateinit var usageBar: ProgressBar
    private lateinit var cleanButton: Button
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        memoryText = findViewById(R.id.memoryText)
        usageBar = findViewById(R.id.usageBar)
        cleanButton = findViewById(R.id.cleanButton)
        statusText = findViewById(R.id.statusText)

        cleanButton.setOnClickListener {
            cleanAppMemory()
            refreshMemoryStats()
        }

        refreshMemoryStats()
    }

    override fun onResume() {
        super.onResume()
        refreshMemoryStats()
    }

    private fun refreshMemoryStats() {
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalMb = memoryInfo.totalMem / (1024.0 * 1024.0)
        val availableMb = memoryInfo.availMem / (1024.0 * 1024.0)
        val usedMb = totalMb - availableMb
        val usedPercent = ((usedMb / totalMb) * 100).toInt().coerceIn(0, 100)

        memoryText.text = buildString {
            appendLine("Total RAM: %.0f MB".format(totalMb))
            appendLine("Available RAM: %.0f MB".format(availableMb))
            append("Used RAM: %.0f MB (%d%%)".format(usedMb, usedPercent))
        }

        usageBar.progress = usedPercent
    }

    private fun cleanAppMemory() {
        statusText.visibility = View.VISIBLE
        statusText.text = "Cleaning this app's caches…"

        try {
            applicationContext.cacheDir.deleteRecursively()
            codeCacheDir.deleteRecursively()
            System.gc()
            Runtime.getRuntime().gc()

            statusText.text =
                "Cleaned this app's reclaimable memory.

" +
                "Android does not allow normal apps to force-close other apps."
        } catch (e: Exception) {
            statusText.text = "Cleaning failed: ${e.message}"
        }
    }
}
