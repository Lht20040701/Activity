package com.lihaotian.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.lihaotian.network.MusicItem

class MusicService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private val binder = MusicBinder()
    private var currentPosition: Int = -1
    private var isPlaying: Boolean = false
    private var musicList: List<MusicItem>? = null
    private var isPreparing: Boolean = false

    // 添加观察者接口
    interface OnPlayStateChangeListener {
        fun onPlayStateChanged(isPlaying: Boolean)
        fun onTrackChanged(position: Int) // 添加新的回调方法
    }

    private val listeners = mutableListOf<OnPlayStateChangeListener>()

    fun addPlayStateChangeListener(listener: OnPlayStateChangeListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }

    fun removePlayStateChangeListener(listener: OnPlayStateChangeListener) {
        listeners.remove(listener)
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun setMusicData(list: List<MusicItem>) {
        musicList = list
    }

    fun playMusic(position: Int) {
        if (isPreparing || position == currentPosition && isPlaying) {
            return
        }

        try {
            isPreparing = true
            currentPosition = position
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(musicList!![position].musicUrl)
            mediaPlayer?.prepareAsync()
            mediaPlayer?.setOnPreparedListener {
                isPreparing = false
                it.start()
                isPlaying = true
                notifyPlayStateChanged()
                notifyTrackChanged()
            }
            mediaPlayer?.setOnCompletionListener {
                playNext()
            }
            Log.d("MusicService", "开始播放音乐: ${musicList!![position].name}")
        } catch (e: Exception) {
            isPreparing = false
            e.printStackTrace()
            Log.e("MusicService", "播放音乐失败: ${e.message}")
        }
    }

    private fun notifyPlayStateChanged() {
        listeners.forEach { listener ->
            listener.onPlayStateChanged(isPlaying)
        }
    }

    private fun notifyTrackChanged() {
        listeners.forEach { listener ->
            listener.onTrackChanged(currentPosition)
        }
    }

    fun pauseMusic() {
        if (!isPreparing && isPlaying) {
            try {
                mediaPlayer?.pause()
                isPlaying = false
                notifyPlayStateChanged()
                Log.d("MusicService", "暂停音乐")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun resumeMusic() {
        if (!isPreparing && !isPlaying) {
            try {
                mediaPlayer?.start()
                isPlaying = true
                notifyPlayStateChanged()
                Log.d("MusicService", "恢复播放音乐")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun isPlaying(): Boolean = isPlaying

    fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0

    fun getDuration(): Int = mediaPlayer?.duration ?: 0

    fun seekTo(position: Int) {
        try {
            mediaPlayer?.seekTo(position)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun playNext() {
        if (!isPreparing && musicList != null && musicList!!.isNotEmpty()) {
            val nextPosition = (currentPosition + 1) % musicList!!.size
            playMusic(nextPosition)
        }
    }

    fun playPrevious() {
        if (!isPreparing && musicList != null && musicList!!.isNotEmpty()) {
            val prevPosition = if (currentPosition > 0) currentPosition - 1 else musicList!!.size - 1
            playMusic(prevPosition)
        }
    }

    fun getCurrentMusicPosition(): Int = currentPosition

    fun getCurrentMusicName(): String = musicList?.get(currentPosition)?.name ?: ""
    fun getCurrentMusicAuthor(): String = musicList?.get(currentPosition)?.author ?: ""
    fun getCurrentMusicUrl(): String = musicList?.get(currentPosition)?.musicUrl ?: ""
    fun getCurrentMusicCover(): String = musicList?.get(currentPosition)?.coverUrl ?: ""
    fun getCurrentMusicColor(): String = musicList?.get(currentPosition)?.dominantColor ?: "#000000"

    override fun onDestroy() {
        super.onDestroy()
        try {
            mediaPlayer?.release()
            mediaPlayer = null
            Log.d("MusicService", "服务销毁，释放资源")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
} 