package com.example.myapplication

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_register.*
import java.time.LocalDate
import java.time.LocalDateTime

class RegisterActivity : AppCompatActivity() {

    private val IP_ADDRESS = "192.168.0.17"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var user_num = intent.getStringExtra("user_num")

        var WM_num = intent.getStringExtra("WM_num").toString()
        textView_register.setText(WM_num+"번 세탁기를 사용하시겠습니까?")

        button_yes.setOnClickListener{
//            val task = InsertData()
//            task.execute("http://$IP_ADDRESS/insertTest.php", user_num, WM_num, LocalDate.now(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(40))

            Toast.makeText(applicationContext, "등록되었습니다.", Toast.LENGTH_LONG).show()
        }
        button_no.setOnClickListener{
            super.onBackPressed();
        }
    }
}