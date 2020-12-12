package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import com.example.myapplication.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //애니메이션 추가
        val waveanim = AnimationUtils.loadAnimation(this,R.anim.wave)
        val alphaanim = AnimationUtils.loadAnimation(this,R.anim.alpha)
        img_logo.startAnimation(waveanim)
        text_intro.startAnimation(alphaanim)

            Handler().postDelayed({ startActivity(Intent(this, LoginActivity::class.java)) }, 3000L)
    }

}