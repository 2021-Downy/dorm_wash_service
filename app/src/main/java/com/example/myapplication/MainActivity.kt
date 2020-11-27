package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myapplication.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // val button_signin = findViewById<Button>(R.id.button_signin)
        button_login.setOnClickListener{
            val LoginActivity = Intent(this, LoginActivity::class.java)
            startActivity(LoginActivity)
        }

        button_signup.setOnClickListener{
            val SignupActivity = Intent(this, SignupActivity::class.java)
            startActivity(SignupActivity)
        }
    }
}