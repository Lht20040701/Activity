package com.lihaotian.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.FileOutputStream

class regisPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis_page)
        var confirmBut: Button = findViewById(R.id.btn_register)
        var usernameEdit: EditText = findViewById(R.id.et_username)
        var emailEdit: EditText = findViewById(R.id.et_email)
        var maleRb: RadioButton = findViewById(R.id.rb_male)
        var femaleRb: RadioButton = findViewById(R.id.rb_female)
        var passwordEdit: EditText = findViewById(R.id.et_password)
        confirmBut.setOnClickListener {
            val sp = getSharedPreferences("user", MODE_PRIVATE)
            var nameKey: String = "username:" + usernameEdit.text.toString()
            var isRegis: String = sp.getString(nameKey,"").toString()
            if (!"".equals(isRegis)) {
                // 是否已经注册过了
                Toast.makeText(this, "已经注册过了!!!", Toast.LENGTH_SHORT).show()
            } else {
                val spEdit = sp.edit()
                //
                spEdit.putString(nameKey, passwordEdit.text.toString())
                //
                var emailKey: String = "email:" + usernameEdit.text.toString()
                spEdit.putString(emailKey, emailEdit.text.toString())
                //
                var sexKey: String = "sex:" + usernameEdit.text.toString()
                var sexValue: String = ""
                if (maleRb.isChecked) sexValue = maleRb.text.toString()
                if (femaleRb.isChecked) sexValue = femaleRb.text.toString()
                spEdit.putString(sexKey, sexValue)
                //
                spEdit.apply()
                Toast.makeText(this, "注册成功!!!",Toast.LENGTH_SHORT).show()
                // 跳转一下
                var iitent: Intent = Intent(this, MainActivity::class.java)
                startActivity(iitent)
            }
        }
    }
}