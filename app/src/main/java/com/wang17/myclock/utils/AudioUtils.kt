package com.wang17.myclock.utils

import android.content.Context
import android.media.AudioManager


class AudioUtils private constructor(context: Context) {
    private val mAudioManager: AudioManager

    //获取多媒体最大音量
    val mediaMaxVolume: Int
        get() = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)//音量类型

    //多媒体音量
    var mediaVolume: Int
        get() = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        set(value) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,  //音量类型
                    value, AudioManager.FLAG_PLAY_SOUND
                    or AudioManager.FLAG_SHOW_UI)
        }

    //获取通话最大音量
    val callMaxVolume: Int
        get() = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)

    var callVolume: Int
        get() = mAudioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        set(value) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,  //音量类型
                    value, AudioManager.FLAG_PLAY_SOUND
                    or AudioManager.FLAG_SHOW_UI)
        }

    //获取系统音量最大值
    val systemMaxVolume: Int
        get() = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)

    //获取系统音量
    var systemVolume: Int
        get() = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM)
        set(value) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,  //音量类型
                    value, AudioManager.FLAG_PLAY_SOUND
                    or AudioManager.FLAG_SHOW_UI)
        }

    //获取提示音量最大值
    val alermMaxVolume: Int
        get() = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)

    var alermVolume: Int
        get() = mAudioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        set(value) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM,  //音量类型
                    value, AudioManager.FLAG_PLAY_SOUND
                    or AudioManager.FLAG_SHOW_UI)
        }

    // 关闭/打开扬声器播放
    fun setSpeakerStatus(on: Boolean) {
        if (on) { //扬声器
            mAudioManager.isSpeakerphoneOn = true
            mAudioManager.mode = AudioManager.MODE_NORMAL
        } else {
            // 设置最大音量
            val max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
            mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, max, AudioManager.STREAM_VOICE_CALL)
            // 设置成听筒模式
            mAudioManager.mode = AudioManager.MODE_IN_COMMUNICATION
            mAudioManager.isSpeakerphoneOn = false // 关闭扬声器
            mAudioManager.setRouting(AudioManager.MODE_NORMAL, AudioManager.ROUTE_EARPIECE, AudioManager.ROUTE_ALL)
        }
    }

    companion object {
        private lateinit var mInstance: AudioUtils

        @Synchronized
        fun getInstance(context: Context): AudioUtils {
            mInstance = AudioUtils(context)
            return mInstance
        }
    }

    init {
        mAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
}