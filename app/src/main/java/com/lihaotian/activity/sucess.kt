package com.lihaotian.activity

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class sucess : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sucess)
        var username: String = intent.getStringExtra("username").toString()
        var sp = getSharedPreferences("user", MODE_PRIVATE)
        var emailKey: String = "email:" + username
        var sexKey: String = "sex:" + username
        var email: String =  sp.getString(emailKey, "").toString()
        var sex: String =  sp.getString(sexKey, "").toString()

        val tv_username: TextView = findViewById(R.id.tv_username)
        val tv_email: TextView = findViewById(R.id.tv_email)
        val tv_gender: TextView = findViewById(R.id.tv_gender)

        tv_username.setText(username)
        tv_email.setText(email)
        tv_gender.setText(sex)
    }
}