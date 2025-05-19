package com.lihaotian.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lihaotian.service.MusicService

class MusicListViewActivity : AppCompatActivity() {
    private var musicService: MusicService? = null
    private var bound = false
    private var currentPlayingPosition = -1

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            bound = true
            
            // 设置音乐数据
            musicService?.setMusicData(
                musicFile,
                musicNames,
                musicAuthors,
                musicCover,
                musicColor
            )
            updateBottomBar()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

    private var musicNumbers = arrayOf(1, 2, 3, 4)
    private var musicNames = arrayOf("私奔", "思念是一种病", "Way Back Home", "I'll Be Back")
    private var musicAuthors = arrayOf("郑钧", "张震岳", "Sam Feldt", "Sam Feldt")
    private var musicFile = arrayOf(R.raw.siben, R.raw.sinianshiyizhongbing, R.raw.waybackhome, R.raw.iwillbeback)
    private var musicCover = arrayOf(R.mipmap.siben, R.mipmap.sinianshiyizhongbing, R.mipmap.waybackhome, R.mipmap.iwillbeback)
    private var musicColor = arrayOf(R.color.siben, R.color.sinianshiyizhongbing, R.color.waybackhome, R.color.iwillbeback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_show)

        // 绑定服务
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        val listView: ListView = findViewById(R.id.list_music)
        
        // 设置列表适配器
        val adapter = MyAdapter(this, R.layout.list_item_show, musicNames, musicNumbers, musicAuthors)
        listView.adapter = adapter
        
        // 设置列表项点击事件
        listView.setOnItemClickListener { _, _, position, _ ->
            if (musicService != null) {
                musicService!!.playMusic(position)
                currentPlayingPosition = position
                updateBottomBar()
                startMusicPlayActivity(position)
            }
        }

        // 设置底部播放栏点击事件
        findViewById<View>(R.id.player_controls).setOnClickListener {
            if (currentPlayingPosition != -1) {
                startMusicPlayActivity(currentPlayingPosition)
            }
        }
        
        // 设置底部播放/暂停按钮点击事件
        findViewById<View>(R.id.bottom_play_button).setBackgroundResource(R.drawable.play_white)
        findViewById<View>(R.id.bottom_play_button).setOnClickListener {
            if (musicService != null) {
                if (musicService!!.isPlaying()) {
                    musicService!!.pauseMusic()
                } else {
                    musicService!!.resumeMusic()
                }
                updateBottomBar()
            }
        }
        
        // 设置底部下一首按钮点击事件
        findViewById<View>(R.id.bottom_next_button).setOnClickListener {
            if (musicService != null) {
                musicService!!.playNext()
                currentPlayingPosition = musicService!!.getCurrentMusicPosition()
                updateBottomBar()
                (listView.adapter as MyAdapter).notifyDataSetChanged()
            }
        }
    }

    private fun startMusicPlayActivity(position: Int) {
        val musicPlayPage = Intent(this, MusicPlayPage::class.java)
        musicPlayPage.putExtra("songName", musicNames[position])
        musicPlayPage.putExtra("songerName", musicAuthors[position])
        musicPlayPage.putExtra("musicFile", musicFile[position])
        musicPlayPage.putExtra("musicCover", musicCover[position])
        musicPlayPage.putExtra("musicColor", musicColor[position])
        startActivity(musicPlayPage)
    }

    private fun updateBottomBar() {
        if (musicService != null) {
            val position = musicService!!.getCurrentMusicPosition()
            if (position != -1) {
                findViewById<TextView>(R.id.bottom_music_name).text = musicService!!.getCurrentMusicName()
                findViewById<View>(R.id.bottom_play_button).setBackgroundResource(
                    if (musicService!!.isPlaying()) R.drawable.stop else R.drawable.play_white
                )
                
                // 更新底部播放栏封面图片
                findViewById<ImageView>(R.id.bottom_music_cover).setImageResource(
                    musicService!!.getCurrentMusicCover()
                )
                
                // 更新底部播放栏背景颜色
                findViewById<View>(R.id.player_controls).setBackgroundResource(
                    musicService!!.getCurrentMusicColor()
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateBottomBar()
        (findViewById<ListView>(R.id.list_music).adapter as MyAdapter).notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (bound) {
            unbindService(connection)
            bound = false
        }
    }

    inner class MyAdapter(_context: Context, _resource: Int, _musicNames: Array<String>, 
                         _musicNumbers: Array<Int>, _musicAuthors: Array<String>) : BaseAdapter() {
        var context = _context
        var resource = _resource
        var musicNumbers = _musicNumbers
        var musicAuthors = _musicAuthors
        var musicNames = _musicNames

        override fun getCount(): Int = musicNames.size
        override fun getItem(position: Int): Any = position
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = View.inflate(context, resource, null)
            
            val musicAuthor = view.findViewById<TextView>(R.id.singer_name)
            val musicName = view.findViewById<TextView>(R.id.song_name)
            val number = view.findViewById<TextView>(R.id.number)
            val playButton = view.findViewById<ImageView>(R.id.list_item_play)

            musicName.text = musicNames[position]
            musicAuthor.text = musicAuthors[position]
            number.text = musicNumbers[position].toString()

            // 更新播放状态图标
            if (position == musicService?.getCurrentMusicPosition()) {
                playButton.setImageResource(
                    if (musicService?.isPlaying() == true) R.drawable.stop_black else R.drawable.play
                )
            } else {
                playButton.setImageResource(R.drawable.play)
            }

            // 设置播放按钮点击事件
            playButton.setOnClickListener {
                if (musicService != null) {
                    if (position == musicService!!.getCurrentMusicPosition()) {
                        if (musicService!!.isPlaying()) {
                            musicService!!.pauseMusic()
                        } else {
                            musicService!!.resumeMusic()
                        }
                    } else {
                        musicService!!.playMusic(position)
                        currentPlayingPosition = position
                    }
                    updateBottomBar()
                    notifyDataSetChanged()
                }
            }

            return view
        }
    }
}