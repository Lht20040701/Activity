package com.lihaotian.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.lihaotian.network.LoginRequest
import com.lihaotian.network.RetrofitClient
import kotlinx.coroutines.launch

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
            val usernameStr = username.text.toString()
            val passwordStr = password.text.toString()
            
            if (usernameStr.isEmpty() || passwordStr.isEmpty()) {
                Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.login(LoginRequest(usernameStr, passwordStr))
                    if (response.success) {
                        // 保存token和用户信息
                        getSharedPreferences("user", MODE_PRIVATE).edit().apply {
                            putString("token", response.token)
                            putString("username", response.user?.username)
                            putString("email", response.user?.email)
                            putString("gender", response.user?.gender)
                            apply()
                        }
                        
                        Toast.makeText(this@MainActivity, "登录成功", Toast.LENGTH_SHORT).show()
                        val musicListPage = Intent(this@MainActivity, MusicListViewActivity::class.java)
                        startActivity(musicListPage)
                    } else {
                        Toast.makeText(this@MainActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@MainActivity, "网络请求失败: ${e.message}", Toast.LENGTH_SHORT).show()
                }
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