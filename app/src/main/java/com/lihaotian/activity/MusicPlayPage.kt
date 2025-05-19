package com.lihaotian.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.lihaotian.service.MusicService
import java.util.concurrent.TimeUnit

//播放状态
private const val PLAYING = 0

//暂停状态
private const val PAUSE = 1

//停止状态
private const val STOP = 2

//空闲状态
private const val IDLE = 3

class MusicPlayPage: AppCompatActivity() {
    //播放按钮
    private var play: View? = null
    //暂停按钮
//    private var stop: ImageButton? = null
    //播放器对象
    private var musicService: MusicService? = null
    private var context: Context? = null
    //当前状态
    private var state = IDLE
    private var bound = false

    // ========================================================================
    // 进度条相关
    private var seekBar: SeekBar? = null
    private var currentTimeText: TextView? = null
    private var totalTimeText: TextView? = null
    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBar = object : Runnable {
        override fun run() {
            if (musicService != null && musicService!!.isPlaying()) {
                // 更新进度条最大值
                seekBar?.max = musicService!!.getDuration()
                seekBar?.progress = musicService!!.getCurrentPosition()
                updateTimeText(currentTimeText, musicService!!.getCurrentPosition())
                updateTimeText(totalTimeText, musicService!!.getDuration())
                handler.postDelayed(this, 1000)
            }
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            bound = true
            
            // 设置进度条最大值
            seekBar?.max = musicService!!.getDuration()
            // 更新总时长显示
            updateTimeText(totalTimeText, musicService!!.getDuration())
            // 开始更新进度
            handler.post(updateSeekBar)
            
            // 更新播放按钮状态
            play?.setBackgroundResource(
                if (musicService!!.isPlaying()) R.drawable.stop else R.drawable.play_white
            )
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

    // ========================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_play)
        context = this

        // 绑定服务
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        var songName = intent.getStringExtra("songName")
        var songerName = intent.getStringExtra("songerName")

        var pageSongName: TextView = findViewById(R.id.song_title)
        var pageNowPlaying: TextView = findViewById(R.id.now_play)
        pageSongName.setText(songName)
        pageNowPlaying.setText(songName)
        var pageArtisName: TextView = findViewById(R.id.artist_name)
        pageArtisName.setText(songerName)

        // ========================================================================
        // 初始化进度条相关控件
        seekBar = findViewById(R.id.song_progress)
        currentTimeText = findViewById(R.id.current_time)
        totalTimeText = findViewById(R.id.total_time)
        
        // 设置SeekBar监听器
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && musicService != null) {
                    musicService?.seekTo(progress)
                    updateTimeText(currentTimeText, progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                handler.removeCallbacks(updateSeekBar)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (musicService != null && musicService!!.isPlaying()) {
                    handler.post(updateSeekBar)
                }
            }
        })

        // ========================================================================

        var albumCover: View = findViewById(R.id.album_art)
        var musicCover = intent.getIntExtra("musicCover", R.mipmap.siben) // 注意后期改默认值
        albumCover.setBackgroundResource(musicCover)

        var albumColor: ConstraintLayout = findViewById(R.id.music_play_setting)
        var musicColor = intent.getIntExtra("musicColor", R.color.siben)
        albumColor.setBackgroundResource(musicColor)

        var btnBack: View = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 设置播放/暂停按钮
        play = findViewById(R.id.btn_play)
        // 初始化播放按钮状态
        play?.setBackgroundResource(
            if (musicService?.isPlaying() == true) R.drawable.stop else R.drawable.play_white
        )
        play!!.setOnClickListener {
            if (musicService != null) {
                if (musicService!!.isPlaying()) {
                    musicService!!.pauseMusic()
                    play!!.setBackgroundResource(R.drawable.play_white)
                } else {
                    musicService!!.resumeMusic()
                    play!!.setBackgroundResource(R.drawable.stop)
                }
            }
        }

        // 设置上一首按钮
        findViewById<View>(R.id.btn_prev).setOnClickListener {
            musicService?.playPrevious()
            updateUI()
        }

        // 设置下一首按钮
        findViewById<View>(R.id.btn_next).setOnClickListener {
            musicService?.playNext()
            updateUI()
        }
    }

    private fun updateUI() {
        if (musicService != null) {
            val position = musicService!!.getCurrentMusicPosition()
            if (position != -1) {
                var pageSongName: TextView = findViewById(R.id.song_title)
                var pageNowPlaying: TextView = findViewById(R.id.now_play)
                var pageArtisName: TextView = findViewById(R.id.artist_name)
                var albumCover: View = findViewById(R.id.album_art)
                var albumColor: ConstraintLayout = findViewById(R.id.music_play_setting)

                pageSongName.text = musicService!!.getCurrentMusicName()
                pageNowPlaying.text = musicService!!.getCurrentMusicName()
                pageArtisName.text = musicService!!.getCurrentMusicAuthor()
                albumCover.setBackgroundResource(musicService!!.getCurrentMusicCover())
                albumColor.setBackgroundResource(musicService!!.getCurrentMusicColor())

                // 更新进度条最大值和当前进度
                seekBar?.max = musicService!!.getDuration()
                seekBar?.progress = musicService!!.getCurrentPosition()
                updateTimeText(totalTimeText, musicService!!.getDuration())
                updateTimeText(currentTimeText, musicService!!.getCurrentPosition())

                play!!.setBackgroundResource(
                    if (musicService!!.isPlaying()) R.drawable.stop else R.drawable.play_white
                )
            }
        }
    }

    // ========================================================================
    // 格式化时间显示
    private fun formatTime(timeMs: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMs.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMs.toLong()) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    // 更新时间文本显示
    private fun updateTimeText(textView: TextView?, timeMs: Int) {
        textView?.text = formatTime(timeMs)
    }
    // ========================================================================

    override fun onDestroy() {
        super.onDestroy()

        // ========================================================================
        // 移除进度更新回调
        handler.removeCallbacks(updateSeekBar)
        // ========================================================================
        // Activity销毁后，释放播放器资源
        if (musicService != null) {
            musicService = null
        }
        // 解绑服务
        if (bound) {
            unbindService(connection)
            bound = false
        }
    }
}