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
        try {
            currentPosition = position
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(musicList!![position].musicUrl)
            mediaPlayer?.prepareAsync()
            mediaPlayer?.setOnPreparedListener {
                it.start()
                isPlaying = true
            }
            mediaPlayer?.setOnCompletionListener {
                playNext()
            }
            Log.d("MusicService", "开始播放音乐: ${musicList!![position].name}")
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("MusicService", "播放音乐失败: ${e.message}")
        }
    }

    fun pauseMusic() {
        try {
            mediaPlayer?.pause()
            isPlaying = false
            Log.d("MusicService", "暂停音乐")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun resumeMusic() {
        try {
            mediaPlayer?.start()
            isPlaying = true
            Log.d("MusicService", "恢复播放音乐")
        } catch (e: Exception) {
            e.printStackTrace()
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
        if (musicList != null && musicList!!.isNotEmpty()) {
            currentPosition = (currentPosition + 1) % musicList!!.size
            playMusic(currentPosition)
            Log.d("MusicService", "播放下一首: ${musicList!![currentPosition].name}")
        }
    }

    fun playPrevious() {
        if (musicList != null && musicList!!.isNotEmpty()) {
            currentPosition = if (currentPosition > 0) currentPosition - 1 else musicList!!.size - 1
            playMusic(currentPosition)
            Log.d("MusicService", "播放上一首: ${musicList!![currentPosition].name}")
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