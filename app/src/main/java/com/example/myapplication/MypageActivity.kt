package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ui.login.mJsonString
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS
import kotlinx.android.synthetic.main.activity_mypage.*
import kotlinx.android.synthetic.main.activity_mypage.editTextPhone
import kotlinx.android.synthetic.main.activity_signup.*
import me.itangqi.waveloadingview.WaveLoadingView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
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

        //예지
        var remainTime = intent.getStringExtra("remainTime")
        var dorm_num = intent.getStringExtra("dorm_num")

        //test를 위한 seekbar
        var seekbar: SeekBar = findViewById(R.id.seekbar)  //java 에서는 SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar)

        //남은 시간을 나타내는 원 웨이브 이펙트
        var waveLoadongView: WaveLoadingView = findViewById(R.id.waveLoadongView)
        waveLoadongView.setProgressValue(0);

        /*추후에도 필요할지 모르니 지우지 말것!!!*/
//        //seekbar의 동작에 따라 달라지는 웨이브 이팩트 progress가 시간(현재까지 소요된 시간)이고 50분이 max로 설정해둠 남은시간(50-progress)을 표시하도록 설정해둠
//        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
//            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//                waveLoadongView.setProgressValue(progress);
//
//                if (progress < 90) {
//                    waveLoadongView.setBottomTitle("");
//                    waveLoadongView.setCenterTitle(String.format("%d분", (100 - progress) / 2));
//                    waveLoadongView.setTopTitle("");
//                    waveLoadongView.setWaveColor(Color.parseColor("#8ECAE6"));
//                } else {
//                    waveLoadongView.setBottomTitle("");
//                    waveLoadongView.setCenterTitle(String.format("%d분", (100 - progress) / 2));
//                    waveLoadongView.setTopTitle("");
//                    waveLoadongView.setWaveColor(Color.parseColor("#FFB703"));
//                }
//            }
//
//            override fun onStartTrackingTouch(p0: SeekBar?) {
//
//            }
//
//            override fun onStopTrackingTouch(p0: SeekBar?) {
//
//            }
//
//        })
        //TEST용 SEEKBAR 비활성화
        seekbar.visibility = View.INVISIBLE

        //세탁기 전체 사용 시간은 50분이라고 할 때
        //예지
        var lefttime = remainTime!!.toInt()   //(0..50).random()   // 수정필요 : 남은시간이 lefttime분(0~50분 사이) 일단 랜덤으로 설정해둠
        var progress = lefttime*2
        waveLoadongView.setProgressValue(progress);

        if (progress < 90) {
                    waveLoadongView.setBottomTitle("");
                    waveLoadongView.setCenterTitle(String.format("%d분", lefttime));
                    waveLoadongView.setTopTitle("");
                    waveLoadongView.setWaveColor(Color.parseColor("#8ECAE6"));
                } else {
                    waveLoadongView.setCenterTitle(String.format("%d분", lefttime));
                    waveLoadongView.setTopTitle("");
                    waveLoadongView.setBottomTitle("");
                    waveLoadongView.setWaveColor(Color.parseColor("#FFB703"));
        }


        var user_num = intent.getStringExtra("user_num")
        var using_num = intent.getStringExtra("using_num")

        val task2 = readData()
        task2.execute("http://$IP_ADDRESS/getjson_readUser.php", user_num)


//        /* 회원 정보 출력 */
//        editTextPersonName.setText(name);
//        editTextId.setText(id);
//        editTextPhone.setText(phone_num);
//        editTextDorm.setText(dorm_num);

        /*사용자 데이터 분석 그래프*/

        //요일 원형 그래프
        /*수정 필요 : 데이터 data_all, data_my 배열에 INT형으로 넣으면 됨! */
        var Day_of_all = Array(7,{i ->(10..100).random()})    //전체 사용자 요일별 사용횟수(월,화,수,목,금,토,일)
        var Day_of_my = Array(7,{i ->(0..10).random()})       //나의 요일별 사용횟수(월,화,수,목,금,토,일)
        makedaycharts(Day_of_all, Day_of_my)


        //시간 꺾은 선 그래프
        /*int 형으로 넣어야함 */
        var Time_of_all = Array(24,{i ->(0..50).random()})    //전체 사용자 24시간별 사용횟수(0시..23시)
        var Time_of_my = Array(24, {i->(0..10).random()})  // 나의 시간별 사용횟수(0시..23시)
        maketimecharts(Time_of_all, Time_of_my)

        /*스위치 버튼 이벤트*/
        switchreport.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener
        { buttonView, isChecked ->
            if (isChecked == true) {
                //Toast.makeText(this, "스위치-ON", Toast.LENGTH_SHORT).show()
                chart.setEnabled(true)
                chart.visibility = View.INVISIBLE
                chart2.setEnabled(true)
                chart2.visibility = View.INVISIBLE
                lineChart.visibility = View.VISIBLE
                lineChart.animateX(1800, Easing.EaseInExpo)
            } else {
                //Toast.makeText(this, "스위치-OFF", Toast.LENGTH_SHORT).show()
                chart.setEnabled(false)
                chart.visibility = View.VISIBLE
                chart.animateY(1400, Easing.EaseInOutQuad)
                chart.setEnabled(false)
                chart2.visibility = View.VISIBLE
                chart2.animateY(1400, Easing.EaseInOutQuad)
                lineChart.visibility = View.INVISIBLE
            }
        })

        //예지
        button_back.setOnClickListener {
            //사용현황 띄우기
            val UsageStatusActivity = Intent(this, UsageStatusActivity::class.java)
            UsageStatusActivity.putExtra("user_num", user_num)
            UsageStatusActivity.putExtra("dorm_num", dorm_num)
            startActivity(UsageStatusActivity)
            finish()
        }
    }

    fun makedaycharts(data_all: Array<Int>, data_my: Array<Int>) :Unit{
        /* 요일 원형 그래프 */
        var Dayofweek = arrayOf("월","화","수","목","금","토","일")
        chart.setUsePercentValues(true)
        chart2.setUsePercentValues(true)

        // 데이터 set
        val chart1_entries = ArrayList<PieEntry>()
        val chart2_entries = ArrayList<PieEntry>()
        for( i in 0..6) {
            chart1_entries.add(PieEntry(data_all[i].toFloat(), Dayofweek[i]))
            chart2_entries.add(PieEntry(data_my[i].toFloat(), Dayofweek[i]))

        }

        // 각 블록 색깔 지정 (꼭 리스트 값안에 담아야 함)
        val colorsItems = ArrayList<Int>()
        colorsItems.add(getResources().getColor(R.color.Reinbow_1))
        colorsItems.add(getResources().getColor(R.color.Reinbow_2))
        colorsItems.add(getResources().getColor(R.color.Reinbow_3))
        colorsItems.add(getResources().getColor(R.color.Reinbow_4))
        colorsItems.add(getResources().getColor(R.color.Reinbow_5))
        colorsItems.add(getResources().getColor(R.color.Reinbow_6))
        colorsItems.add(getResources().getColor(R.color.Reinbow_7))

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
            valueTextSize = 16f
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
            setCenterTextSize(20f)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(10f)
            //그래프 최초 시작시 12시방향으로 휘리릭.. 이뿌다....^^
            animateY(1400, Easing.EaseInOutQuad)
            animate()
        }
        val pieData2 = PieData(chart2_pieDataSet)
        chart2.apply {
            data = pieData2
            description.isEnabled = false
            isRotationEnabled = false
            centerText = "ALL"
            setCenterTextSize(20f)
            setEntryLabelColor(Color.BLACK)
            setEntryLabelTextSize(10f)
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

            //마이페이지 새로띄우기
            val MypageActivity = Intent(this, MypageActivity::class.java)
            MypageActivity.putExtra("user_num",user_num)
            MypageActivity.putExtra("using_num", using_num)
            startActivity(MypageActivity)
            finish()

            Toast.makeText(applicationContext, "정보 수정이 완료되었습니다.", Toast.LENGTH_LONG).show()
        }

    }

    /*Read Data in mysql*/
    private inner class readData : AsyncTask<String?, Void?, String?>() {

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == null) {
                Toast.makeText(
                    applicationContext,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()

            } else {
                mJsonString = result
                val TAG_JSON = "webnautes"
                val TAG_ID = "ID"
                val TAG_NAME = "name"
                val TAG_PHONE = "phone_num"
                val TAG_DORM = "dorm_num"
                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                    for (i in 0 until jsonArray.length()) {
                        val item: JSONObject = jsonArray.getJSONObject(i)
                        val ID: String = item.getString(TAG_ID)
                        val name: String = item.getString(TAG_NAME)
                        val phone_num: String = item.getString(TAG_PHONE)
                        val dorm_num: String = item.getString(TAG_DORM)

                        /* 회원 정보 출력 */
                        editTextPersonName.setText(name);
                        editTextId.setText(ID);
                        editTextPhone.setText(phone_num);
                        editTextDorm.setText(dorm_num);
                    }
                } catch (e: JSONException) {
                    Toast.makeText(
                        applicationContext,
                        errorMessage,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun doInBackground(vararg params: String?): String? {
            val serverURL = params[0]
            val user_num = params[1]
            val postParameters: String = "user_num=$user_num"

            return try {
                val url = URL(serverURL)
                val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

                httpURLConnection.readTimeout = 5000
                httpURLConnection.connectTimeout = 5000
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.connect()

                val outputStream: OutputStream = httpURLConnection.outputStream
                if (postParameters != null) {
                    outputStream.write(postParameters.toByteArray(charset("UTF-8")))
                }
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
                //errorString = e.toString()
                null
            }
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

    fun maketimecharts(val1List : Array<Int>, val2List : Array<Int> ) :Unit{
        //Part1
        var timeList = Array(24,{i ->i})    //시간(0~23시)
        val entries = ArrayList<Entry>()
        val entries2 = ArrayList<Entry>()

        //Part2
        for(i in 0..23) {
            entries.add(Entry(timeList[i].toFloat(), val1List[i].toFloat()))
            entries2.add(Entry(timeList[i].toFloat(), val2List[i].toFloat()))
        }

        //Part3
        val vl = LineDataSet(entries, "ALL")
        val v2 = LineDataSet(entries2, "MY")

        //Part4
        vl.setDrawValues(false) //각 점의 수 표시하기
        vl.setDrawFilled(true)  //선그래프 아래 색 채우기
        vl.setColor(getResources().getColor(R.color.Reinbow_7))
        vl.setCircleColor(getResources().getColor(R.color.Reinbow_7))
        vl.setFillColor(getResources().getColor(R.color.Reinbow_6))
        vl.lineWidth = 3f

        v2.setDrawValues(false)
        v2.setDrawFilled(true)
        v2.lineWidth = 3f
        v2.setColor(getResources().getColor(R.color.Reinbow_1))
        v2.setCircleColor(getResources().getColor(R.color.Reinbow_1))
        v2.setFillColor(getResources().getColor(R.color.Reinbow_2))


        //Part5
        lineChart.xAxis.labelRotationAngle = 0f

        //Part6
        //두 그래프 합치기
        val chartData = LineData()
        chartData.addDataSet(vl)
        chartData.addDataSet(v2)
        lineChart.data = chartData
        lineChart.invalidate()

        //Part7
        lineChart.axisRight.isEnabled = false


        //Part8
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        //Part9
        lineChart.description.text = "Usage/Hour"
        lineChart.setNoDataText("아직 데이터가 부족합니다!")



        lineChart.visibility=View.INVISIBLE
    }
}

