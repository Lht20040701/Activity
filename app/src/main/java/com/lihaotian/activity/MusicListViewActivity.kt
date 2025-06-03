package com.lihaotian.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.lihaotian.network.MusicItem
import com.lihaotian.network.RetrofitClient
import com.lihaotian.service.MusicService
import kotlinx.coroutines.launch

class MusicListViewActivity : AppCompatActivity() {
    private var musicService: MusicService? = null
    private var bound = false
    private var currentPlayingPosition = -1
    private var musicList: List<MusicItem> = listOf()

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            bound = true
            
            // 设置音乐数据
            musicService?.setMusicData(musicList)
            updateBottomBar()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_show)

        // 绑定服务
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        val listView: ListView = findViewById(R.id.list_music)
        val adapter = MyAdapter(this, R.layout.list_item_show)
        listView.adapter = adapter
        
        // 从后端获取音乐列表
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getMusicList()
                if (response.success) {
                    musicList = response.data
                    musicService?.setMusicData(musicList)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@MusicListViewActivity, response.message, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MusicListViewActivity, "获取音乐列表失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        
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
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun startMusicPlayActivity(position: Int) {
        val musicPlayPage = Intent(this, MusicPlayPage::class.java)
        musicPlayPage.putExtra("songName", musicList[position].name)
        musicPlayPage.putExtra("songerName", musicList[position].author)
        musicPlayPage.putExtra("musicUrl", musicList[position].musicUrl)
        musicPlayPage.putExtra("coverUrl", musicList[position].coverUrl)
        musicPlayPage.putExtra("dominantColor", musicList[position].dominantColor)
        startActivity(musicPlayPage)
    }

    private fun updateBottomBar() {
        if (musicService != null) {
            val position = musicService!!.getCurrentMusicPosition()
            if (position != -1 && position < musicList.size) {
                findViewById<TextView>(R.id.bottom_music_name).text = musicService!!.getCurrentMusicName()
                findViewById<View>(R.id.bottom_play_button).setBackgroundResource(
                    if (musicService!!.isPlaying()) R.drawable.stop else R.drawable.play_white
                )
                
                // 更新底部播放栏封面图片
                val coverImageView = findViewById<ImageView>(R.id.bottom_music_cover)
                val currentCoverUrl = musicService!!.getCurrentMusicCover()
                if (!currentCoverUrl.isNullOrEmpty()) {
                    Glide.with(this)
                        .load(currentCoverUrl)
                        .placeholder(R.drawable.temp)
                        .error(R.drawable.temp)
                        .into(coverImageView)
                } else {
                    coverImageView.setImageResource(R.drawable.temp)
                }
                
                // 更新底部播放栏背景颜色
                findViewById<View>(R.id.player_controls).setBackgroundColor(
                    Color.parseColor(musicService!!.getCurrentMusicColor())
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

    inner class MyAdapter(_context: Context, _resource: Int) : BaseAdapter() {
        var context = _context
        var resource = _resource

        override fun getCount(): Int = musicList.size
        override fun getItem(position: Int): Any = position
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = View.inflate(context, resource, null)
            
            val musicAuthor = view.findViewById<TextView>(R.id.singer_name)
            val musicName = view.findViewById<TextView>(R.id.song_name)
            val number = view.findViewById<TextView>(R.id.number)
            val playButton = view.findViewById<ImageView>(R.id.list_item_play)

            val music = musicList[position]
            musicName.text = music.name
            musicAuthor.text = music.author
            number.text = (position + 1).toString()

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