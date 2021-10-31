package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
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

var  errorMessage_mypage= "데이터를 불러올 수 없습니다."
var Day_of_all = arrayOf(0, 0, 0, 0, 0, 0, 0)    //전체 사용자 요일별 사용횟수(월,화,수,목,금,토,일)
var Day_of_my = arrayOf(0, 0, 0, 0, 0, 0, 0)
var Time_of_all = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)    //전체 사용자 24시간별 사용횟수(0시..23시)
var Time_of_my = arrayOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
var dormNum = ""
var get_using_num = "0"

class MypageActivity : AppCompatActivity() {

    //에뮬레이터로 실행시 ip주소
    private val IP_ADDRESS = "morned270.dothome.co.kr"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        //예지
        var remainTime = intent.getStringExtra("remainTime")
        val spinnerList = arrayOf<String>("개성재","계영원","양성재","양진재","양현재")
        editTextDorm.adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,spinnerList)

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
//        var lefttime = (0..50).random()   // 수정필요 : 남은시간이 lefttime분(0~50분 사이) 일단 랜덤으로 설정해둠
        var lefttime = remainTime!!.toInt()
        var progress = 100-lefttime*2
        waveLoadongView.setProgressValue(progress);
        print("----------현재 남은시간은 "+lefttime+"입니다-----------")

        if (get_using_num != "0") {
            if(lefttime>0) {
                if (progress < 90) {
                    waveLoadongView.setBottomTitle("");
                    waveLoadongView.setCenterTitle(String.format("%d분", lefttime));
                    waveLoadongView.setTopTitle("");
                    waveLoadongView.setWaveColor(Color.parseColor("#8ECAE6"));
                }
                else {
                    waveLoadongView.setCenterTitle(String.format("%d분", lefttime));
                    waveLoadongView.setTopTitle("");
                    waveLoadongView.setBottomTitle("");
                    waveLoadongView.setWaveColor(Color.parseColor("#FFB703"));
                }
            }
            else{
                waveLoadongView.setProgressValue(70);
                waveLoadongView.setCenterTitle(String.format("세탁 끝!"));
                waveLoadongView.setWaveColor(getResources().getColor(R.color.Reinbow_1));
            }

        }
        else{
            waveLoadongView.setProgressValue(70);
            waveLoadongView.setCenterTitle(String.format("사용대기중"));
            waveLoadongView.setWaveColor(getResources().getColor(R.color.Reinbow_4));
        }

        var user_num = intent.getStringExtra("user_num")

        val task2 = readData()
        task2.execute("http://$IP_ADDRESS/getjson_readUser.php", user_num)

        val task3 = readData2() //전체 요일별 그래프
        task3.execute("http://$IP_ADDRESS/getjson_readUser2.php", user_num)

        val task4 = readData3() //개인 요일별 그래프
        task4.execute("http://$IP_ADDRESS/getjson_readUser3.php", user_num)

        //data report 삽입
        var preferred_day_num = Day_of_my.indexOf(Day_of_my.maxOrNull())
        var Week = arrayOf("월요일","화요일","수요일","목요일","금요일","토요일","일요일")
        var preferred_day = Week[preferred_day_num]
        var preferred_time_num = Time_of_my.indexOf(Time_of_my.maxOrNull())
        var Time = arrayOf("0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23")
        var preferred_time = Time[preferred_time_num]
        val task5 = insertData() //개인 요일별 그래프
        task5.execute("http://$IP_ADDRESS/insertReport.php", preferred_day,preferred_time,user_num)


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
            UsageStatusActivity.putExtra("dorm_num", dormNum)
            startActivity(UsageStatusActivity)
            finish()
        }

        button_book.isEnabled = false
        button_book.setOnClickListener {
            //예약페이지 띄우기
            val MybookActivity = Intent(this, MybookActivity::class.java)
            MybookActivity.putExtra("user_num", user_num)
            MybookActivity.putExtra("dorm_num", dormNum)
            startActivity(MybookActivity)
            finish()
        }
    }

    /*Insert Data in mysql*/
    private class insertData : AsyncTask<String, Void, String>() {


        override fun doInBackground(vararg params: String?): String {

            val serverURL: String? = params[0]
            val preferred_day: String? = params[1]
            val preferred_time: String? = params[2]
            val user_num: String? = params[3]

            val postParameters: String = "preferred_day=$preferred_day&preferred_time=$preferred_time&user_num=$user_num"

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

    fun makedaycharts(data_all: Array<Int>, data_my: Array<Int>) :Unit{
        /* 요일 원형 그래프 */
        var Dayofweek = arrayOf("월","화","수","목","금","토","일")
        chart.setUsePercentValues(true)
        chart2.setUsePercentValues(true)

        // 데이터 set
        val chart1_entries = ArrayList<PieEntry>()
        val chart2_entries = ArrayList<PieEntry>()
        // 각 블록 색깔 지정 (꼭 리스트 값안에 담아야 함)
        val colorsItems = ArrayList<Int>()
        val colorsItems2 = ArrayList<Int>()

        for( i in 0..6) {
            print("-----------------------------------------"+i+"요일의 값은 : "+data_all[i]+"--------------------------------------------")
            if(data_all[i]!=0) {
                chart1_entries.add(PieEntry(data_all[i].toFloat(), Dayofweek[i]))
                if(i==0){
                    colorsItems.add(getResources().getColor(R.color.Reinbow_1))
                }
                else if(i==1){
                    colorsItems.add(getResources().getColor(R.color.Reinbow_2))
                }
                else if(i==2){
                    colorsItems.add(getResources().getColor(R.color.Reinbow_3))
                }
                else if(i==3){
                    colorsItems.add(getResources().getColor(R.color.Reinbow_4))
                }
                else if(i==4){
                    colorsItems.add(getResources().getColor(R.color.Reinbow_5))
                }
                else if(i==5){
                    colorsItems.add(getResources().getColor(R.color.Reinbow_6))
                }
                else if(i==6){
                    colorsItems.add(getResources().getColor(R.color.Reinbow_7))
                }
            }
            if(data_my[i]!=0) {
                chart2_entries.add(PieEntry(data_my[i].toFloat(), Dayofweek[i]))
                if(i==0){
                    colorsItems2.add(getResources().getColor(R.color.Reinbow_1))
                }
                else if(i==1){
                    colorsItems2.add(getResources().getColor(R.color.Reinbow_2))
                }
                else if(i==2){
                    colorsItems2.add(getResources().getColor(R.color.Reinbow_3))
                }
                else if(i==3){
                    colorsItems2.add(getResources().getColor(R.color.Reinbow_4))
                }
                else if(i==4){
                    colorsItems2.add(getResources().getColor(R.color.Reinbow_5))
                }
                else if(i==5){
                    colorsItems2.add(getResources().getColor(R.color.Reinbow_6))
                }
                else if(i==6){
                    colorsItems2.add(getResources().getColor(R.color.Reinbow_7))
                }
            }
        }

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
            colors = colorsItems2
            valueTextColor = Color.BLACK
            valueTextSize = 16f
        }
        chart1_pieDataSet.entryCount
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
            val dorm: String = editTextDorm.selectedItem.toString()
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
                        errorMessage_mypage,
                        Toast.LENGTH_LONG
                ).show()

            } else {
                mJsonString = result
                val TAG_JSON = "webnautes"
                val TAG_ID = "ID"
                val TAG_NAME = "name"
                val TAG_PHONE = "phone_num"
                val TAG_DORM = "dorm_num"
                val TAG_USINGNUM = "using_num"
                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                    for (i in 0 until jsonArray.length()) {
                        val item: JSONObject = jsonArray.getJSONObject(i)
                        val ID: String = item.getString(TAG_ID)
                        val name: String = item.getString(TAG_NAME)
                        val phone_num: String = item.getString(TAG_PHONE)
                        val using_num: String = item.getString(TAG_USINGNUM)
                        get_using_num = using_num
                        dormNum = item.getString(TAG_DORM)

                        /* 회원 정보 출력 */
                        editTextPersonName.setText(name);
                        editTextId.setText(ID);
                        editTextPhone.setText(phone_num);
                        if (dormNum=="개성재")editTextDorm.setSelection(0)
                        else if (dormNum=="계영원")editTextDorm.setSelection(1)
                        else if (dormNum=="양성재")editTextDorm.setSelection(2)
                        else if (dormNum=="양진재")editTextDorm.setSelection(3)
                        else if (dormNum=="양현재")editTextDorm.setSelection(4)

                        /* 예약상태일 경우 '예약' 버튼 활성화 */
                        if (using_num.toInt() < 0){
                            button_book.isEnabled = true
                        }
                    }
                } catch (e: JSONException) {
                    Toast.makeText(
                            applicationContext,
                            errorMessage_mypage,
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

    /*Read Data in mysql - to make usage Graph - 1*/
    private inner class readData2 : AsyncTask<String?, Void?, String?>() {

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == null) {
                Toast.makeText(
                        applicationContext,
                        errorMessage_mypage,
                        Toast.LENGTH_LONG
                ).show()

            } else {
                mJsonString = result
                val TAG_JSON = "webnautes"
                val TAG_MON = "mon"
                val TAG_TUE = "tue"
                val TAG_WED = "wed"
                val TAG_THU = "thu"
                val TAG_FRI = "fri"
                val TAG_SAT = "sat"
                val TAG_SUN = "sun"

                val TAG_T0 = "t0"

                val TAG_T1 = "t1"
                val TAG_T2 = "t2"
                val TAG_T3 = "t3"
                val TAG_T4 = "t4"
                val TAG_T5 = "t5"
                val TAG_T6 = "t6"
                val TAG_T7 = "t7"
                val TAG_T8 = "t8"
                val TAG_T9 = "t9"
                val TAG_T10 = "t10"

                val TAG_T11 = "t11"
                val TAG_T12 = "t12"
                val TAG_T13 = "t13"
                val TAG_T14 = "t14"
                val TAG_T15 = "t15"
                val TAG_T16 = "t16"
                val TAG_T17 = "t17"
                val TAG_T18 = "t18"
                val TAG_T19 = "t19"
                val TAG_T20 = "t20"

                val TAG_T21 = "t21"
                val TAG_T22 = "t22"
                val TAG_T23 = "t23"

                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                    for (i in 0 until jsonArray.length()) {
                        val item: JSONObject = jsonArray.getJSONObject(i)
                        val mon: Int = item.getInt(TAG_MON)
                        val tue: Int = item.getInt(TAG_TUE)
                        val wed: Int = item.getInt(TAG_WED)
                        val thu: Int = item.getInt(TAG_THU)
                        val fri: Int = item.getInt(TAG_FRI)
                        val sat: Int = item.getInt(TAG_SAT)
                        val sun: Int = item.getInt(TAG_SUN)

                        val t0: Int = item.getInt(TAG_T0)

                        val t1: Int = item.getInt(TAG_T1)
                        val t2: Int = item.getInt(TAG_T2)
                        val t3: Int = item.getInt(TAG_T3)
                        val t4: Int = item.getInt(TAG_T4)
                        val t5: Int = item.getInt(TAG_T5)
                        val t6: Int = item.getInt(TAG_T6)
                        val t7: Int = item.getInt(TAG_T7)
                        val t8: Int = item.getInt(TAG_T8)
                        val t9: Int = item.getInt(TAG_T9)
                        val t10: Int = item.getInt(TAG_T10)

                        val t11: Int = item.getInt(TAG_T11)
                        val t12: Int = item.getInt(TAG_T12)
                        val t13: Int = item.getInt(TAG_T13)
                        val t14: Int = item.getInt(TAG_T14)
                        val t15: Int = item.getInt(TAG_T15)
                        val t16: Int = item.getInt(TAG_T16)
                        val t17: Int = item.getInt(TAG_T17)
                        val t18: Int = item.getInt(TAG_T18)
                        val t19: Int = item.getInt(TAG_T19)
                        val t20: Int = item.getInt(TAG_T20)

                        val t21: Int = item.getInt(TAG_T21)
                        val t22: Int = item.getInt(TAG_T22)
                        val t23: Int = item.getInt(TAG_T23)


                        /* 여기서 출력 등 원하는 작업 수행 */

                        /*사용자 데이터 분석 그래프*/

                        //요일 원형 그래프
                        /* 수정 필요 : 데이터 data_all, data_my 배열에 INT형으로 넣으면 됨! */
                        Day_of_all = arrayOf(mon, tue, wed, thu, fri, sat, sun)    //전체 사용자 요일별 사용횟수(월,화,수,목,금,토,일)

                        //시간 꺾은 선 그래프
                        Time_of_all = arrayOf(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23)    //전체 사용자 24시간별 사용횟수(0시..23시)

                    }

                } catch (e: JSONException) {
                    Toast.makeText(
                            applicationContext,
                            errorMessage_mypage,
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

    /*Read Data in mysql - to make usage Graph - 2*/
    private inner class readData3 : AsyncTask<String?, Void?, String?>() {

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == null) {
                Toast.makeText(
                        applicationContext,
                        errorMessage_mypage,
                        Toast.LENGTH_LONG
                ).show()

            } else {
                mJsonString = result
                val TAG_JSON = "webnautes"
                val TAG_MON = "mon"
                val TAG_TUE = "tue"
                val TAG_WED = "wed"
                val TAG_THU = "thu"
                val TAG_FRI = "fri"
                val TAG_SAT = "sat"
                val TAG_SUN = "sun"

                val TAG_T0 = "t0"

                val TAG_T1 = "t1"
                val TAG_T2 = "t2"
                val TAG_T3 = "t3"
                val TAG_T4 = "t4"
                val TAG_T5 = "t5"
                val TAG_T6 = "t6"
                val TAG_T7 = "t7"
                val TAG_T8 = "t8"
                val TAG_T9 = "t9"
                val TAG_T10 = "t10"

                val TAG_T11 = "t11"
                val TAG_T12 = "t12"
                val TAG_T13 = "t13"
                val TAG_T14 = "t14"
                val TAG_T15 = "t15"
                val TAG_T16 = "t16"
                val TAG_T17 = "t17"
                val TAG_T18 = "t18"
                val TAG_T19 = "t19"
                val TAG_T20 = "t20"

                val TAG_T21 = "t21"
                val TAG_T22 = "t22"
                val TAG_T23 = "t23"

                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                    for (i in 0 until jsonArray.length()) {
                        val item: JSONObject = jsonArray.getJSONObject(i)
                        val mon: Int = item.getInt(TAG_MON)
                        val tue: Int = item.getInt(TAG_TUE)
                        val wed: Int = item.getInt(TAG_WED)
                        val thu: Int = item.getInt(TAG_THU)
                        val fri: Int = item.getInt(TAG_FRI)
                        val sat: Int = item.getInt(TAG_SAT)
                        val sun: Int = item.getInt(TAG_SUN)

                        val t0: Int = item.getInt(TAG_T0)

                        val t1: Int = item.getInt(TAG_T1)
                        val t2: Int = item.getInt(TAG_T2)
                        val t3: Int = item.getInt(TAG_T3)
                        val t4: Int = item.getInt(TAG_T4)
                        val t5: Int = item.getInt(TAG_T5)
                        val t6: Int = item.getInt(TAG_T6)
                        val t7: Int = item.getInt(TAG_T7)
                        val t8: Int = item.getInt(TAG_T8)
                        val t9: Int = item.getInt(TAG_T9)
                        val t10: Int = item.getInt(TAG_T10)

                        val t11: Int = item.getInt(TAG_T11)
                        val t12: Int = item.getInt(TAG_T12)
                        val t13: Int = item.getInt(TAG_T13)
                        val t14: Int = item.getInt(TAG_T14)
                        val t15: Int = item.getInt(TAG_T15)
                        val t16: Int = item.getInt(TAG_T16)
                        val t17: Int = item.getInt(TAG_T17)
                        val t18: Int = item.getInt(TAG_T18)
                        val t19: Int = item.getInt(TAG_T19)
                        val t20: Int = item.getInt(TAG_T20)

                        val t21: Int = item.getInt(TAG_T21)
                        val t22: Int = item.getInt(TAG_T22)
                        val t23: Int = item.getInt(TAG_T23)

                        /* 여기서 출력 등 원하는 작업 수행 */

                        /*사용자 데이터 분석 그래프*/

                        //요일 원형 그래프
                        Day_of_my = arrayOf(mon, tue, wed, thu, fri, sat, sun)       //나의 요일별 사용횟수(월,화,수,목,금,토,일)
                        makedaycharts(Day_of_my, Day_of_all)

                        //시간 꺾은 선 그래프
                        Time_of_my = arrayOf(t0, t1, t2, t3, t4, t5, t6, t7, t8, t9, t10, t11, t12, t13, t14, t15, t16, t17, t18, t19, t20, t21, t22, t23)  // 나의 시간별 사용횟수(0시..23시)
                        maketimecharts(Time_of_all, Time_of_my)

                    }

                } catch (e: JSONException) {
                    Toast.makeText(
                            applicationContext,
                            errorMessage_mypage,
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

    /*Update Data in mysql*/
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