package ru.yourok.torrserve.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import ru.yourok.torrserve.ad.ADManager
import ru.yourok.torrserve.app.App
import ru.yourok.torrserve.atv.channels.UpdaterCards
import ru.yourok.torrserve.server.api.Api
import ru.yourok.torrserve.server.local.ServerFile
import ru.yourok.torrserve.settings.Settings
import kotlin.concurrent.thread

class TorrService : Service() {
    private var notification = NotificationHelper()
    private val serverFile = ServerFile()

    override fun onBind(p0: Intent?): IBinder? = null
    override fun onCreate() {
        super.onCreate()
        thread {
            ADManager.get()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            if (it.action != null) {
                when (it.action) {
                    ActionStart -> {
                        startServer()
                        return START_STICKY
                    }
                    ActionStop -> {
                        stopServer(intent.hasExtra("forceclose"))
                        return START_NOT_STICKY
                    }
                }
            }
        }
        return START_NOT_STICKY
    }

    private fun startServer() {
        thread {
            if (serverFile.exists() && isLocal() && Api.echo().isEmpty()) {
                Log.d("TorrService", "startServer()")
                serverFile.run()
                notification.doBindService(this)
            }
        }
        UpdaterCards.updateCards()
    }

    private fun stopServer(forceClose: Boolean) {
        thread {
            Log.d("TorrService", "stopServer(forceClose:$forceClose)")
            if (isLocal() && Api.echo().isNotEmpty())
                Api.shutdown()
            serverFile.stop()
            notification.doUnbindService(this)
            if (forceClose) {
                thread {
                    Thread.sleep(200)
                    Runtime.getRuntime().exit(0)
                }
            }
            stopSelf()
        }
    }

    companion object {
        const val ActionStart = "ru.yourok.torrserve.server.action_start"
        const val ActionStop = "ru.yourok.torrserve.server.action_stop"

        fun start() {
            try {
                val intent = Intent(App.context, TorrService::class.java)
                intent.action = ActionStart
                App.context.startService(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun stop() {
            try {
                val intent = Intent(App.context, TorrService::class.java)
                intent.action = ActionStop
                App.context.startService(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun wait(timeout: Int = -1): Boolean {
            var count = 0
            if (timeout < 0)
                count = -20
            while (Api.echo() == "") {
                Thread.sleep(1000)
                count++
                if (count > timeout)
                    return false
            }
            return true
        }

        fun isLocal(): Boolean {
            val host = Settings.getHost()
            return host.contains("://127.0.0.1") || host.contains("://localhost")
        }
    }
}