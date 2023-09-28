/*
 * Nextcloud Talk application
 *
 * @author Marcel Hibbe
 * @author Andy Scherzinger
 * @author Ezhil Shanmugham
 * @author Parneet Singh
 * Copyright (C) 2021 Andy Scherzinger <info@andy-scherzinger.de>
 * Copyright (C) 2021 Marcel Hibbe <dev@mhibbe.de>
 * Copyright (C) 2023 Ezhil Shanmugham <ezhil56x.contact@gmail.com>
 * Copyright (c) 2023 Parneet Singh <gurayaparneet@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.zeuscloud.talk.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import autodagger.AutoInjector
import com.zeuscloud.talk.BuildConfig
import com.zeuscloud.talk.R
import com.zeuscloud.talk.application.NextcloudTalkApplication
import com.zeuscloud.talk.databinding.ActivityFullScreenMediaBinding
import com.zeuscloud.talk.utils.Mimetype.VIDEO_PREFIX_GENERIC
import java.io.File

@AutoInjector(NextcloudTalkApplication::class)
class FullScreenMediaActivity : AppCompatActivity() {
    lateinit var binding: ActivityFullScreenMediaBinding

    private lateinit var path: String
    private var player: ExoPlayer? = null

    private var playWhenReadyState: Boolean = true
    private var playBackPosition: Long = 0L

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_preview, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
            true
        } else if (item.itemId == R.id.share) {
            val shareUri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID,
                File(path)
            )

            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, shareUri)
                type = VIDEO_PREFIX_GENERIC
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.send_to)))

            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileName = intent.getStringExtra("FILE_NAME")
        val isAudioOnly = intent.getBooleanExtra("AUDIO_ONLY", false)

        path = applicationContext.cacheDir.absolutePath + "/" + fileName

        binding = ActivityFullScreenMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.mediaviewToolbar)
        supportActionBar?.title = fileName
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding.playerView.showController()
        if (isAudioOnly) {
            binding.playerView.controllerShowTimeoutMs = 0
        }

        binding.playerView.setControllerVisibilityListener(
            PlayerView.ControllerVisibilityListener { v ->
                if (v != 0) {
                    hideSystemUI()
                    supportActionBar?.hide()
                } else {
                    showSystemUI()
                    supportActionBar?.show()
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
        preparePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(applicationContext).build()
        binding.playerView.player = player
    }

    private fun preparePlayer() {
        val mediaItem: MediaItem = MediaItem.fromUri(path)
        player?.let { exoPlayer ->
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.playWhenReady = playWhenReadyState
            exoPlayer.seekTo(playBackPosition)
            exoPlayer.prepare()
        }
    }

    private fun releasePlayer() {
        player?.let { exoPlayer ->
            playBackPosition = exoPlayer.currentPosition
            playWhenReadyState = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        player = null
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
            )
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
    }
}
