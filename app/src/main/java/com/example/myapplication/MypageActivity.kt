package com.example.myapplication

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS
import kotlinx.android.synthetic.main.activity_mypage.*
import kotlinx.android.synthetic.main.activity_mypage.editTextPhone
import kotlinx.android.synthetic.main.activity_signup.*
import me.itangqi.waveloadingview.WaveLoadingView
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL


class MypageActivity : AppCompatActivity() {

    //에뮬레이터로 실행시 ip주소
    private val IP_ADDRESS = "morned270.dothome.co.kr"

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
                    waveLoadongView.setCenterTitle(String.format("%d분",(100-progress)/2));
                    waveLoadongView.setTopTitle("");
                    waveLoadongView.setWaveColor(Color.parseColor("#8ECAE6"));
                }
                else
                {
                    waveLoadongView.setBottomTitle("");
                    waveLoadongView.setCenterTitle(String.format("%d분",(100-progress)/2));
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

        /* 회원 정보 출력 */
        editTextPersonName.setText(name);
        editTextId.setText(id);
        editTextPhone.setText(phone_num);
        editTextDorm.setText(dorm_num);

        /*사용자 데이터 분석 그래프*/


        /* 요일 원형 그래프 */
        chart.setUsePercentValues(true)

        // 데이터 set
        val chart1_entries = ArrayList<PieEntry>()
        chart1_entries.add(PieEntry(10f, "월"))
        chart1_entries.add(PieEntry(20f, "화"))
        chart1_entries.add(PieEntry(30f, "수"))
        chart1_entries.add(PieEntry(40f, "목"))
        chart1_entries.add(PieEntry(50f, "금"))

        val chart2_entries = ArrayList<PieEntry>()
        chart2_entries.add(PieEntry(508f, "월"))
        chart2_entries.add(PieEntry(600f, "화"))
        chart2_entries.add(PieEntry(750f, "수"))
        chart2_entries.add(PieEntry(508f, "목"))
        chart2_entries.add(PieEntry(670f, "금"))

        // 각 블록 색깔 지정 (꼭 리스트 값안에 담아야 함)
        val colorsItems = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colorsItems.add(c)
        for (c in COLORFUL_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.LIBERTY_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.PASTEL_COLORS) colorsItems.add(c)
        colorsItems.add(ColorTemplate.getHoloBlue())

        //PieDataSet 변수를 만들어 위에서 셋팅한 색상과 그래프에 들어갈 퍼센테이지 수치 색상과 사이즈를 지정할 수 있다.
        //생성할 때, entries는 위에서 데이터셋한 리스트의 이름이고 오른쪽은 value값인데 빈값으로 셋팅해도 된다
       val chart1_pieDataSet = PieDataSet(chart1_entries, "")
        chart1_pieDataSet.apply {
            colors = colorsItems
            valueTextColor = Color.BLACK
            valueTextSize = 16f
        }
        val chart2_pieDataSet = PieDataSet(chart2_entries, "")
        chart2_pieDataSet.apply {
            colors = colorsItems
            valueTextColor = Color.BLACK
            valueTextSize = 7f
        }

        //PieData변수에 위에서 만든 PieDataSet을 넘겨주고, xml에서 만든 chart에 데이터를 셋팅해주는 부분
        val pieData = PieData(chart1_pieDataSet)
        chart.apply {
            data = pieData
            //그래프 이름...보기싫어서 안보이게 해둠..^^
            description.isEnabled = false
            //그래프 만지면 회전하는 애니메이션.. 안쓸꺼임^^
            isRotationEnabled = false
            centerText = "MY"
            setEntryLabelColor(Color.BLACK)
            //그래프 최초 시작시 12시방향으로 휘리릭.. 이뿌다....^^
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }

        chart2.apply {
            data = pieData
            description.isEnabled = false
            isRotationEnabled = false
            centerText = "ALL"
            setEntryLabelColor(Color.BLACK)
            animateY(1400, Easing.EaseInOutQuad)
            animate()

        }

        //아래 라벨 숨기기
        val l: Legend = chart.getLegend()
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.isEnabled = false

        val l2: Legend = chart2.getLegend()
        l2.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l2.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l2.orientation = Legend.LegendOrientation.VERTICAL
        l2.setDrawInside(false)
        l2.isEnabled = false

        var btn_event = findViewById<Button>(R.id.button_modify)

        /* 수정하기 버튼 클릭시 */
        btn_event.setOnClickListener{
            val name : String = editTextPersonName.text.toString()
            val phone: String = editTextPhone.text.toString()
            val dorm: String = editTextDorm.text.toString()
            val email: String = editTextId.text.toString()


            val task = MypageActivity.UpdateData()
            task.execute("http://$IP_ADDRESS/updateTest.php", name, phone, dorm, email)

            Toast.makeText(applicationContext, "정보 수정이 완료되었습니다.", Toast.LENGTH_LONG).show()
            super.onBackPressed();
        }

    }

    /*Insert Data in mysql*/
    private class UpdateData : AsyncTask<String, Void, String>() {


        override fun doInBackground(vararg params: String?): String {

            val serverURL: String? = params[0]
            val name: String? = params[1]
            val phone: String? = params[2]
            val dorm: String? = params[3]
            val email: String? = params[4]

            val postParameters: String = "name=$name&phone_num=$phone&dorm_num=$dorm&ID=$email"

            try {
                val url = URL(serverURL)
                val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection


                httpURLConnection.readTimeout = 5000
                httpURLConnection.connectTimeout = 5000
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.connect()


                val outputStream: OutputStream = httpURLConnection.outputStream
                outputStream.write(postParameters.toByteArray(charset("UTF-8")))
                outputStream.flush()
                outputStream.close()

                val responseStatusCode: Int = httpURLConnection.responseCode


                val inputStream: InputStream
                inputStream = if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    httpURLConnection.inputStream
                } else {
                    httpURLConnection.errorStream
                }


                val inputStreamReader = InputStreamReader(inputStream, "UTF-8")
                val bufferedReader = BufferedReader(inputStreamReader)

                val sb = StringBuilder()
                var line: String? = null

                while (bufferedReader.readLine().also({ line = it }) != null) {
                    sb.append(line)
                }

                bufferedReader.close();

                return sb.toString();

            } catch (e: Exception) {
                return "Error" + e.message
            }

        }


    }

}