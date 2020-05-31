package com.dfsly.android.logisticsupport.utils

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri


object MediaUtil {
    private var mMediaPlayer: MediaPlayer? = null
    fun playRing(context: Context) {
        if (mMediaPlayer != null) return//MediaPlayer只需存在一个实例
        try { //用于获取手机默认铃声的Uri
            mMediaPlayer = MediaPlayer().apply {
                val alert: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                setDataSource(context, alert)
                setAudioStreamType(AudioManager.STREAM_RING)
                isLooping = true
                prepare()
            }
            mMediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopRing() {
        mMediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
                it.release()
            }
        }
        mMediaPlayer = null
    }
}