package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.example.myapplication.ui.login.mJsonString
import kotlinx.android.synthetic.main.content_usage_status2.*
//import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.format.DateTimeFormatter

var errorMessage_wm = "세탁기 현황 정보를 불러올 수 없습니다."
//var arrWMlist = mutableListOf<Array<Int>>()    //사용자 정보에 해당하는 기숙사의 세탁기들 리스트
var runningWM = mutableListOf<Int>()
var emptyWM = mutableListOf<Int>()
var reservedWM = mutableListOf<Int>()
var buttonList_R = mutableListOf<Button>()
var usingList_R = mutableListOf<Button>()
var reservedList_R = mutableListOf<Button>()
var using_num=""
var user_num=""
var dorm_num=""
var remainTime="0"
//var WMList = mutableListOf<Button>()

class UsageStatusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usage_status2)
        setSupportActionBar(findViewById(R.id.toolbar))

        user_num = intent.getStringExtra("user_num").toString()
        dorm_num = intent.getStringExtra("dorm_num").toString()
        using_num = intent.getStringExtra("using_num").toString()

        remainTime="0"//사용종료->마이페이지 보면 0분으로 나오기 위해 초기화

        val task2 = readData2()
        task2.execute("http://morned270.dothome.co.kr/getjson_readUN.php",user_num)
        val task = readData()
        task.execute("http://morned270.dothome.co.kr/getjson_readWM.php",dorm_num)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            val MypageActivity = Intent(this, MypageActivity::class.java)
            MypageActivity.putExtra("user_num",user_num)
            MypageActivity.putExtra("remainTime",remainTime)
            startActivity(MypageActivity)
            finish()
        }
        findViewById<FloatingActionButton>(R.id.scannerButton).setOnClickListener {
            val QRscanActivity = Intent(this, QRscanActivity::class.java)
            startActivity(QRscanActivity)
//            finish()  // 스캔 액티비티에서 '뒤로가기' 했을때 돌아오기 위함
        }
    }

    fun setButton(buttonList: MutableList<Button>, usingList: MutableList<Button>, reservedList: MutableList<Button>){
        var WM_num = ""
        for (i in buttonList){
            //빈 세탁기 이미지 삽입
            i.setBackgroundResource(R.drawable.img_wm_empty)

            // 사용가능한 세탁기는 'QR 스캔' 기능을 통해서만 사용 등록 가능
//            i.setOnClickListener{
//                WM_num = i.text.toString()
//                val RegisterActivity = Intent(this, RegisterActivity::class.java)
//                RegisterActivity.putExtra("user_num",user_num)
//                RegisterActivity.putExtra("dorm_num",dorm_num)
//                RegisterActivity.putExtra("WM_num",WM_num)
//                startActivity(RegisterActivity)
//                finish()
//            }
        }
        for (i in usingList){
            var s = i.text.toString()
            //사용중인 세탁기 이미지 삽입
            i.setBackgroundResource(R.drawable.img_wm_otheruse)

            i.setOnClickListener{
                WM_num = i.text.toString()
                val BookActivity = Intent(this, BookActivity::class.java)
                BookActivity.putExtra("user_num",user_num)
                BookActivity.putExtra("dorm_num",dorm_num)
                BookActivity.putExtra("WM_num",WM_num)
                startActivity(BookActivity)
                finish()
            }
        }
        for (i in reservedList){
            var s = i.text.toString()

            //예약된 세탁기 이미지 삽입
            i.setBackgroundResource(R.drawable.img_wm_otheruse)

            i.setOnClickListener{
                Toast.makeText(applicationContext, "예약된 세탁기 입니다.", Toast.LENGTH_LONG).show()
            }
        }
        if(using_num!="" && using_num.toInt()>0){

            for (i in usingList){

                var WM_num = i.text.toString()
                if(WM_num == using_num){
                    i.setBackgroundResource(R.drawable.img_wm_myuse)

                    i.setOnClickListener{
                        val TerminateActivity = Intent(this, TerminateActivity::class.java)
                        TerminateActivity.putExtra("user_num",user_num)
                        TerminateActivity.putExtra("dorm_num",dorm_num)
                        TerminateActivity.putExtra("WM_num",WM_num)
                        startActivity(TerminateActivity)
                        finish()
                    }

                    val task3 = readData3()
                    task3.execute("http://morned270.dothome.co.kr/getjson_readTime.php",using_num)
                }
            }
        }
        else if(using_num!="" && using_num.toInt()<0){

            for (i in usingList){

                var WM_num = i.text.toString().toInt()
                if(WM_num == -using_num.toInt()){
                    i.setBackgroundResource(R.drawable.img_wm_reserved)

                    i.setOnClickListener{
                        Toast.makeText(applicationContext, "내가 예약한 세탁기 입니다.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun setWM_state(WM: Button, WM_num: Int, running: Int){
        var usingList = mutableListOf<Button>()
        var buttonList = mutableListOf<Button>()
        var reservedList = mutableListOf<Button>()

        WM.visibility = View.VISIBLE
        WM.text = WM_num.toString()
        if(running==0){
            emptyWM.add(1)
            buttonList.add(WM)
        }
        else if (running==1) {
            runningWM.add(1)
            usingList.add(WM)
        }
        else if (running==2 || running==3) {
            reservedWM.add(1)
            usingList.add(WM)
            reservedList.add(WM)
        }

        buttonList_R = buttonList
        usingList_R = usingList
        reservedList_R = reservedList
        setButton(buttonList,usingList,reservedList)
    }

    fun setWM(arrWMlist: MutableList<Array<Int>>){

        var WMList = mutableListOf<Button>()
        WMList.add(WM1)
        WMList.add(WM2)
        WMList.add(WM3)
        WMList.add(WM4)
        WMList.add(WM5)
        WMList.add(WM6)
        WMList.add(WM7)
        WMList.add(WM8)
        WMList.add(WM9)
        WMList.add(WM10)
        WMList.add(WM11)
        WMList.add(WM12)
        for (i in WMList){
            i.visibility = View.GONE
        }


        for(i in arrWMlist){
            var row = i[1]
            var column = i[2]

            if(row==1 && column==1){
                setWM_state(WM1, i[0], i[3])
            }
            if(row==1 && column==2){
                setWM_state(WM2, i[0], i[3])
            }
            if(row==1 && column==3){
                setWM_state(WM3, i[0], i[3])
            }
            if(row==2 && column==1){
                setWM_state(WM4, i[0], i[3])
            }
            if(row==2 && column==2){
                setWM_state(WM5, i[0], i[3])
            }
            if(row==2 && column==3){
                setWM_state(WM6, i[0], i[3])
            }
            if(row==3 && column==1){
                setWM_state(WM7, i[0], i[3])
            }
            if(row==3 && column==2){
                setWM_state(WM8, i[0], i[3])
            }
            if(row==3 && column==3){
                setWM_state(WM9, i[0], i[3])
            }
            if(row==4 && column==1){
                setWM_state(WM10, i[0], i[3])
            }
            if(row==4 && column==2){
                setWM_state(WM11, i[0], i[3])
            }
            if(row==4 && column==3){
                setWM_state(WM12, i[0], i[3])
            }
        }
    }

    private inner class readData : AsyncTask<String?, Void?, String?>() {

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == null) {
                Toast.makeText(
                    applicationContext,
                    errorMessage_wm,
                    Toast.LENGTH_LONG
                ).show()

            } else {
                mJsonString = result
                val TAG_JSON = "webnautes"
                val TAG_WM_num = "WM_num"
                val TAG_row = "position_row"
                val TAG_column = "position_column"
                val TAG_running = "running"
                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)

                    var arrWMlist = mutableListOf<Array<Int>>()
                    for (i in 0 until jsonArray.length()) {
                        val item: JSONObject = jsonArray.getJSONObject(i)
                        val WM_num: String = item.getString(TAG_WM_num)
                        val row: String = item.getString(TAG_row)
                        val column: String = item.getString(TAG_column)
                        val running: String = item.getString(TAG_running)
                        var arrWM = arrayOf(WM_num.toInt(),row.toInt(),column.toInt(),running.toInt())    //한개의 세탁기에 대한 정보 저장하는 리스트
                        arrWMlist.add(arrWM)
                    }
                    setWM(arrWMlist)
                } catch (e: JSONException) {
                    Toast.makeText(
                        applicationContext,
                        errorMessage_wm,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun doInBackground(vararg params: String?): String? {
            val serverURL = params[0]
            val dorm = params[1]
            var dorm_num = 0
            if(dorm == "계영원")
                dorm_num=1
            else if(dorm=="개성재"){
                dorm_num=2
            }
            else if(dorm=="양성재"){
                dorm_num=3
            }
            else if(dorm=="양진재"){
                dorm_num=4
            }
            else if(dorm=="양현재"){
                dorm_num=5
            }
            val postParameters: String = "dorm_num=$dorm_num"

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

            }catch (e: Exception) {
                //errorString = e.toString()
                null
            }
        }
    }

    private inner class readData3 : AsyncTask<String?, Void?, String?>() {

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == null) {
                Toast.makeText(
                    applicationContext,
                    errorMessage_wm,
                    Toast.LENGTH_LONG
                ).show()

            } else {
                mJsonString = result
                val TAG_JSON = "webnautes"
                val TAG_left_time = "left_time"
                val TAG_start_time = "start_time"
                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                    for (i in 0 until jsonArray.length()) {
                        val item: JSONObject = jsonArray.getJSONObject(i)
                        var left_time = item.getString(TAG_left_time)
                        // 남은시간계산
                        var start_time = item.getString(TAG_start_time)
                        val startTime = LocalDateTime.parse(start_time, DateTimeFormatter.ISO_DATE_TIME)
                        remainTime = (50-Duration.between(startTime,LocalDateTime.now()).toMinutes()).toString()
//                        remainTime = left_time.toString();
                    }

                } catch (e: JSONException) {
                    Toast.makeText(
                        applicationContext,
                        errorMessage_wm,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        override fun doInBackground(vararg params: String?): String? {
            val serverURL = params[0]
            val using_num = params[1]
            val postParameters: String = "using_num=$using_num"

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

            }catch (e: Exception) {
                //errorString = e.toString()
                null
            }
        }
    }

    private inner class readData2 : AsyncTask<String?, Void?, String?>() {

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == null) {
                Toast.makeText(
                    applicationContext,
                    errorMessage_wm,
                    Toast.LENGTH_LONG
                ).show()

            } else {
                mJsonString = result
                val TAG_JSON = "webnautes"
                val TAG_using_num = "using_num"
                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                    for (i in 0 until jsonArray.length()) {
                        val item: JSONObject = jsonArray.getJSONObject(i)
                        using_num = item.getString(TAG_using_num)
                    }
                } catch (e: JSONException) {
                    Toast.makeText(
                        applicationContext,
                        errorMessage_wm,
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

            }catch (e: Exception) {
                //errorString = e.toString()
                null
            }
        }
    }
}