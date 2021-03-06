package ru.yourok.torrserve.app

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import androidx.multidex.MultiDexApplication


class App : MultiDexApplication() {
    companion object {
        lateinit var context: Context

        private lateinit var wakeLock: PowerManager.WakeLock

        fun Toast(txt: String, long: Boolean = false) {
            Handler(Looper.getMainLooper()).post {
                val show = if (long)
                    android.widget.Toast.LENGTH_LONG
                else
                    android.widget.Toast.LENGTH_SHORT
                android.widget.Toast.makeText(context, txt, show).show()
            }
        }

        fun Toast(txt: Int, long: Boolean = false) {
            Handler(Looper.getMainLooper()).post {
                val show = if (long)
                    android.widget.Toast.LENGTH_LONG
                else
                    android.widget.Toast.LENGTH_SHORT
                android.widget.Toast.makeText(context, txt, show).show()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        //// DayNight Auto ON/OFF (useless?)
        //when (Settings.getTheme()) {
        //    "dark", "black" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        //    "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        //    else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        //}

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TorrServe:WakeLock")
    }
}
