package com.lihaotian.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var regis: Button
    private lateinit var login: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        login.setOnClickListener {
            //SharedPreference 读
            val sp = getSharedPreferences("user", MODE_PRIVATE)
            var nameKey: String = "username:" + username.text.toString()
//            println("=================================================" + nameKey)
            var passwd: String = sp.getString(nameKey,"").toString()
//            println("=================================================" + passwd)
            if ("".equals(passwd)) {
                Toast.makeText(this, "此用户还未注册,请注册!!!", Toast.LENGTH_SHORT).show()
            } else if (password.text.toString().equals(passwd)) {
                Toast.makeText(this, "登录成功!!!", Toast.LENGTH_SHORT).show()
//                var sucessPage: Intent = Intent(this, sucess::class.java)
//                sucessPage.putExtra("username", username.text.toString())
//                startActivity(sucessPage)

                // =================================================================================
                var testPage: Intent = Intent(this, MusicListViewActivity::class.java)
                startActivity(testPage)
            } else {
                Toast.makeText(this, "密码错误!!!", Toast.LENGTH_SHORT).show()
                password.setText("")
            }
        }

        regis.setOnClickListener {
            var regisPage: Intent = Intent(this, regisPage::class.java)
            startActivity(regisPage)
        }

    }

    fun init() {
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        regis = findViewById(R.id.regis_but)
        login = findViewById(R.id.login_but)
    }
}