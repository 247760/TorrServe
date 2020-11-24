package ru.yourok.torrserve.ui.activities.play

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.runBlocking
import ru.yourok.torrserve.R
import ru.yourok.torrserve.services.TorrService
import ru.yourok.torrserve.ui.activities.play.Commands.processTorrentInfo
import ru.yourok.torrserve.ui.activities.play.Commands.processTorrentList
import ru.yourok.torrserve.ui.activities.play.Commands.processViewed
import ru.yourok.torrserve.ui.activities.play.Play.play


class PlayActivity : AppCompatActivity() {
    var command: String = ""
    var torrentLink: String = ""
    var torrentHash: String = ""
    var torrentTitle: String = ""
    var torrentPoster: String = ""
    var torrentSave: Boolean = false
    var torrentFileIndex: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.play_activity)
        setWindow()

        if (intent == null) {
            error(ErrIntentNull)
            return
        }

        TorrService.start()

        readArgs()
        runBlocking {
            processIntent()
        }
    }

    private fun setWindow() {
        setFinishOnTouchOutside(false)
        val attr = window.attributes
        if (resources.displayMetrics.widthPixels <= resources.displayMetrics.heightPixels)
            attr.width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        else if (resources.displayMetrics.widthPixels > resources.displayMetrics.heightPixels)
            attr.width = (resources.displayMetrics.widthPixels * 0.50).toInt()
        window.attributes = attr
    }

    private fun processIntent() {
        if (command.isNotEmpty()) {
            //// Commands
            when (command.toLowerCase()) {
                "viewed" -> processViewed()
                "torrentinfo" -> processTorrentInfo()
                "torrentlist" -> processTorrentList()
                else -> error(ErrUnknownCmd)
            }
            return
        } else {
            //// Play torrent
            processTorrent()
        }
    }


    private fun processTorrent() {
        if (intent.hasExtra("action") && intent.getStringExtra("action") == "play")
            play(false)
        else Chooser.show(this) {
            when (it) {
                1, 2 -> {
                    play(it == 2)
                }
                3 -> {
                    addAndExit()
                }
            }
        }
    }
}