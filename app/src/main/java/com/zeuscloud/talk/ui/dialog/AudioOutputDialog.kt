/*
 * Nextcloud Talk application
 *
 * @author Marcel Hibbe
 * Copyright (C) 2022 Marcel Hibbe <dev@mhibbe.de>
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

package com.zeuscloud.talk.ui.dialog

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import autodagger.AutoInjector
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.nextcloud.android.common.ui.theme.utils.ColorRole
import com.zeuscloud.talk.R
import com.zeuscloud.talk.activities.CallActivity
import com.zeuscloud.talk.application.NextcloudTalkApplication
import com.zeuscloud.talk.databinding.DialogAudioOutputBinding
import com.zeuscloud.talk.ui.theme.ViewThemeUtils
import com.zeuscloud.talk.webrtc.WebRtcAudioManager
import javax.inject.Inject

@AutoInjector(NextcloudTalkApplication::class)
class AudioOutputDialog(val callActivity: CallActivity) : BottomSheetDialog(callActivity) {

    @Inject
    lateinit var viewThemeUtils: ViewThemeUtils

    private lateinit var dialogAudioOutputBinding: DialogAudioOutputBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NextcloudTalkApplication.sharedApplication?.componentApplication?.inject(this)

        dialogAudioOutputBinding = DialogAudioOutputBinding.inflate(layoutInflater)
        setContentView(dialogAudioOutputBinding.root)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        viewThemeUtils.platform.themeDialogDark(dialogAudioOutputBinding.root)
        updateOutputDeviceList()
        initClickListeners()
    }

    fun updateOutputDeviceList() {
        if (callActivity.audioManager?.audioDevices?.contains(WebRtcAudioManager.AudioDevice.BLUETOOTH) == false) {
            dialogAudioOutputBinding.audioOutputBluetooth.visibility = View.GONE
        } else {
            dialogAudioOutputBinding.audioOutputBluetooth.visibility = View.VISIBLE
        }

        if (callActivity.audioManager?.audioDevices?.contains(WebRtcAudioManager.AudioDevice.EARPIECE) == false) {
            dialogAudioOutputBinding.audioOutputEarspeaker.visibility = View.GONE
        } else {
            dialogAudioOutputBinding.audioOutputEarspeaker.visibility = View.VISIBLE
        }

        if (callActivity.audioManager?.audioDevices?.contains(WebRtcAudioManager.AudioDevice.SPEAKER_PHONE) == false) {
            dialogAudioOutputBinding.audioOutputSpeaker.visibility = View.GONE
        } else {
            dialogAudioOutputBinding.audioOutputSpeaker.visibility = View.VISIBLE
        }

        if (callActivity.audioManager?.currentAudioDevice?.equals(
                WebRtcAudioManager.AudioDevice.WIRED_HEADSET
            ) == true
        ) {
            dialogAudioOutputBinding.audioOutputEarspeaker.visibility = View.GONE
            dialogAudioOutputBinding.audioOutputSpeaker.visibility = View.GONE
            dialogAudioOutputBinding.audioOutputWiredHeadset.visibility = View.VISIBLE
        } else {
            dialogAudioOutputBinding.audioOutputWiredHeadset.visibility = View.GONE
        }

        highlightActiveOutputChannel()
    }

    private fun highlightActiveOutputChannel() {
        when (callActivity.audioManager?.currentAudioDevice) {
            WebRtcAudioManager.AudioDevice.BLUETOOTH -> {
                viewThemeUtils.platform.colorImageView(
                    dialogAudioOutputBinding.audioOutputBluetoothIcon,
                    ColorRole.PRIMARY
                )
                viewThemeUtils.platform
                    .colorPrimaryTextViewElementDarkMode(dialogAudioOutputBinding.audioOutputBluetoothText)
            }

            WebRtcAudioManager.AudioDevice.SPEAKER_PHONE -> {
                viewThemeUtils.platform.colorImageView(
                    dialogAudioOutputBinding.audioOutputSpeakerIcon,
                    ColorRole.PRIMARY
                )
                viewThemeUtils.platform
                    .colorPrimaryTextViewElementDarkMode(dialogAudioOutputBinding.audioOutputSpeakerText)
            }

            WebRtcAudioManager.AudioDevice.EARPIECE -> {
                viewThemeUtils.platform.colorImageView(
                    dialogAudioOutputBinding.audioOutputEarspeakerIcon,
                    ColorRole.PRIMARY
                )
                viewThemeUtils.platform
                    .colorPrimaryTextViewElementDarkMode(dialogAudioOutputBinding.audioOutputEarspeakerText)
            }

            WebRtcAudioManager.AudioDevice.WIRED_HEADSET -> {
                viewThemeUtils.platform
                    .colorImageView(dialogAudioOutputBinding.audioOutputWiredHeadsetIcon, ColorRole.PRIMARY)
                viewThemeUtils.platform
                    .colorPrimaryTextViewElementDarkMode(dialogAudioOutputBinding.audioOutputWiredHeadsetText)
            }

            else -> Log.d(TAG, "AudioOutputDialog doesn't know this AudioDevice")
        }
    }

    private fun initClickListeners() {
        dialogAudioOutputBinding.audioOutputBluetooth.setOnClickListener {
            callActivity.setAudioOutputChannel(WebRtcAudioManager.AudioDevice.BLUETOOTH)
            dismiss()
        }

        dialogAudioOutputBinding.audioOutputSpeaker.setOnClickListener {
            callActivity.setAudioOutputChannel(WebRtcAudioManager.AudioDevice.SPEAKER_PHONE)
            dismiss()
        }

        dialogAudioOutputBinding.audioOutputEarspeaker.setOnClickListener {
            callActivity.setAudioOutputChannel(WebRtcAudioManager.AudioDevice.EARPIECE)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = findViewById<View>(R.id.design_bottom_sheet)
        val behavior = BottomSheetBehavior.from(bottomSheet as View)
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    companion object {
        private const val TAG = "AudioOutputDialog"
    }
}
