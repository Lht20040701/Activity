package com.lihaotian.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MusicListViewActivity : AppCompatActivity() {
    private lateinit var listView: ListView

    private var musicNumbers = arrayOf(1, 2, 3, 4)
    private var musicNames = arrayOf("私奔", "思念是一种病", "Way Back Home", "I'll Be Back")
    private var musicAuthors = arrayOf("郑钧", "张震岳", "Sam Feldt", "Sam Feldt")
    private var musicFile = arrayOf(R.raw.siben, R.raw.sinianshiyizhongbing, R.raw.waybackhome, R.raw.iwillbeback)
    private var musicCover = arrayOf(R.mipmap.siben, R.mipmap.sinianshiyizhongbing, R.mipmap.waybackhome, R.mipmap.iwillbeback)
    private var musicColor = arrayOf(R.color.siben, R.color.sinianshiyizhongbing, R.color.waybackhome, R.color.iwillbeback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_show)
        listView = findViewById(R.id.list_music)
        listView.adapter = MyAdapter(this, R.layout.list_item_show, musicNames, musicNumbers, musicAuthors)
        listView.setOnItemClickListener {
            parent, view, position, id ->
                var musicPlayPage: Intent = Intent(this, MusicPlayPage::class.java)
                musicPlayPage.putExtra("songName", musicNames[position])
                musicPlayPage.putExtra("songerName", musicAuthors[position])
                musicPlayPage.putExtra("musicFile", musicFile[position])
                musicPlayPage.putExtra("musicCover", musicCover[position])
                musicPlayPage.putExtra("musicColor", musicColor[position])
                startActivity(musicPlayPage)
        }
    }

    class MyAdapter(_context: Context, _resource: Int, _musicNames: Array<String>, _musicNumbers: Array<Int>, _musicAuthors: Array<String>): BaseAdapter() {
        var context = _context
        var resource = _resource
        var musicNumbers = _musicNumbers
        var musicAuthors = _musicAuthors
        var musicNames = _musicNames

        override fun getCount(): Int {
            return musicNames.size
        }

        override fun getItem(position: Int): Any {
            return position
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            // 这里绑定的是list_view中条目的资源类型
            var view: View = View.inflate(context, resource, null)
            val musicAuthor = view.findViewById<TextView>(R.id.singer_name)
            val musicName = view.findViewById<TextView>(R.id.song_name)
            val number = view.findViewById<TextView>(R.id.number)

            musicName.text = musicNames[position]
            musicAuthor.text = musicAuthors[position]
            number.text = musicNumbers[position].toString()

            return view
        }
    }
}