package com.lihaotian.activity

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaPlayer.OnPreparedListener
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

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
    private var mMediaPlayer: MediaPlayer? = null
    private var context: Context? = null
    //当前状态
    private var state = IDLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.music_play)
        context = this

        var songName = intent.getStringExtra("songName")
        var songerName = intent.getStringExtra("songerName")

        var pageSongName: TextView = findViewById(R.id.song_title)
        var pageNowPlaying: TextView = findViewById(R.id.now_play)
        pageSongName.setText(songName)
        pageNowPlaying.setText(songName)
        var pageArtisName: TextView = findViewById(R.id.artist_name)
        pageArtisName.setText(songerName)

        var albumCover: View = findViewById(R.id.album_art)
        var musicCover = intent.getIntExtra("musicCover", R.mipmap.siben) // 注意后期改默认值
        albumCover.setBackgroundResource(musicCover)

        var albumColor: ConstraintLayout = findViewById(R.id.music_play_setting)
        var musicColor = intent.getIntExtra("musicColor", R.color.siben)
        albumColor.setBackgroundResource(musicColor)


        play = findViewById(R.id.btn_play)
        play!!.setOnClickListener {
            if (state == PLAYING) {
                pause()
            } else {
                start()
            }
        }

        // 初始化停止按钮
//        stop = findViewById(R.id.stop)
//        stop!!.setOnClickListener { stop() }
    }
    // 暂停
    private fun pause() {
        mMediaPlayer!!.pause()
        state = PAUSE
        play!!.setBackgroundResource(R.drawable.play_white)
    }

    // 开始
    private fun start() {
        if (state == IDLE || state == STOP) {
            play()
        } else if (state == PAUSE) {
            mMediaPlayer!!.start()
            state = PLAYING
        }
        play!!.setBackgroundResource(R.drawable.stop)
    }

    // 停止
    private fun stop() {
        mMediaPlayer!!.stop()
        state = STOP
        play!!.setBackgroundResource(R.drawable.play_white)
    }

    // 播放
    fun play() {
        Log.d("111111","调用play")
        try {
            if (mMediaPlayer == null || state == STOP) {
                // 创建MediaPlayer对象并设置Listener
//                var musicFile = intent.getStringExtra("musicFile")
                var musicFile = intent.getIntExtra("musicFile", R.raw.siben) // 注意后期改默认值
                mMediaPlayer = MediaPlayer.create(context, musicFile)
                mMediaPlayer!!.setOnPreparedListener(listener)
                mMediaPlayer!!.setOnCompletionListener {
                    Log.d("111111","播放一遍啦")
                    mMediaPlayer!!.start() // 逻辑应该是再播一遍
                    state = PLAYING
                }
            } else {
                // 复用MediaPlayer对象
                mMediaPlayer!!.reset()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // MediaPlayer进入prepared状态开始播放
    private val listener = OnPreparedListener {
        mMediaPlayer!!.start()
        Log.d("111111","开始播放啦")
        state = PLAYING
    }

    override fun onDestroy() {
        super.onDestroy()
        // Activity销毁后，释放播放器资源
        if (mMediaPlayer != null) {
            mMediaPlayer!!.release()
            mMediaPlayer = null
        }
    }
}