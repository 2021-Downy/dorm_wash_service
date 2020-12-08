package com.example.myapplication

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.example.myapplication.ui.login.LoginActivity
import kotlinx.android.synthetic.main.activity_mypage.*
import me.itangqi.waveloadingview.WaveLoadingView

class MypageActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        //test를 위한 seekbar
        var seekbar : SeekBar = findViewById(R.id.seekbar)  //java 에서는 SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar)

        //남은 시간을 나타내는 원 웨이브 이펙트
        var waveLoadongView : WaveLoadingView = findViewById(R.id.waveLoadongView)
        waveLoadongView.setProgressValue(0);

        //seekbar의 동작에 따라 달라지는 웨이브 이팩트 progress가 시간(현재까지 소요된 시간)이고 50분이 max로 설정해둠 남은시간(50-progress)을 표시하도록 설정해둠
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress : Int, fromUser : Boolean) {
                waveLoadongView.setProgressValue(progress);
                if(progress < 90)
                {
                    waveLoadongView.setBottomTitle("");
                    waveLoadongView.setCenterTitle(String.format("%d%%",progress));
                    waveLoadongView.setTopTitle("");
                    waveLoadongView.setWaveColor(Color.parseColor("#8ECAE6"));
                }
                else
                {
                    waveLoadongView.setBottomTitle("");
                    waveLoadongView.setCenterTitle(String.format("%d%%",progress));
                    waveLoadongView.setTopTitle("");
                    waveLoadongView.setWaveColor(Color.parseColor("#FFB703"));
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        var user_num = intent.getStringExtra("user_num")
        var id = intent.getStringExtra("id")
        var pw = intent.getStringExtra("pw")
        var name = intent.getStringExtra("name")
        var dorm_num = intent.getStringExtra("dorm_num")
        var phone_num = intent.getStringExtra("phone_num")
        var using_num = intent.getStringExtra("using_num")

        mypage_name.text = "이름 : " + name
        mypage_id.text = "ID : " + id
        mypage_phone.text = "연락처 : "+phone_num
        mypage_dorm.text = "기숙사 : " + dorm_num
    }

}