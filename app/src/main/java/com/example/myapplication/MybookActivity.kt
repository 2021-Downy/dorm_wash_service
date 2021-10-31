package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ui.login.mJsonString
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ColorTemplate.COLORFUL_COLORS
import kotlinx.android.synthetic.main.activity_book.*
import kotlinx.android.synthetic.main.activity_mybook.*
import kotlinx.android.synthetic.main.activity_mybook.button_back
import kotlinx.android.synthetic.main.activity_mybook.button_cancel
import kotlinx.android.synthetic.main.activity_mypage.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL



class MybookActivity : AppCompatActivity() {

    //에뮬레이터로 실행시 ip주소
    private val IP_ADDRESS = "morned270.dothome.co.kr"
    var read_WM_num = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mybook)

        val task = readData()
        task.execute("http://$IP_ADDRESS/getjson_readBook.php", user_num)


        button_back.setOnClickListener {
            //mypage 띄우기
            val MypageActivity = Intent(this, MypageActivity::class.java)
            MypageActivity.putExtra("user_num",user_num)
            MypageActivity.putExtra("dorm_num",dorm_num)
            MypageActivity.putExtra("using_num", using_num)
            MypageActivity.putExtra("remainTime",remainTime)
            startActivity(MypageActivity)
            finish()
        }

        button_register.isEnabled = false

        // 사용완료 상태면(2) 버튼 활성화 (0: 사용대기, 1: 사용중, 2: 사용완+예약대기, 3: 사용중+예약완)
        button_register.setOnClickListener{
            //insert use
//            val RegisterActivity = Intent(this, RegisterActivity::class.java)
//            RegisterActivity.putExtra("user_num",user_num)
//            RegisterActivity.putExtra("dorm_num",dorm_num)
//            RegisterActivity.putExtra("WM_num",read_WM_num)
//            startActivity(RegisterActivity)
//            finish()

            val QRscanActivity = Intent(this, QRscanActivity::class.java)
            startActivity(QRscanActivity)
            finish()
        }

        button_cancel.setOnClickListener{
            // delete reservation
            val task2 = UpdateData()
            task2.execute("http://$IP_ADDRESS/cancelBook.php", user_num)
            Toast.makeText(applicationContext, "예약이 취소되었습니다.", Toast.LENGTH_LONG).show()

            //mypage 띄우기
            val MypageActivity = Intent(this, MypageActivity::class.java)
            MypageActivity.putExtra("user_num",user_num)
            MypageActivity.putExtra("dorm_num",dorm_num)
            MypageActivity.putExtra("using_num", using_num)
            MypageActivity.putExtra("remainTime",remainTime)
            startActivity(MypageActivity)
            finish()
        }

    }

    /*Update Data in mysql*/
    private class UpdateData : AsyncTask<String, Void, String>() {


        override fun doInBackground(vararg params: String?): String {

            val serverURL: String? = params[0]
            val user_num: String? = params[1]

            val postParameters: String = "user_num=$user_num"

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
                val TAG_WMNUM = "WM_num"
                val TAG_RMTIME = "left_time"
                val TAG_RUNNING = "running"
                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                    for (i in 0 until jsonArray.length()) {
                        val item: JSONObject = jsonArray.getJSONObject(i)
                        val WM_num: String = item.getString(TAG_WMNUM)
                        val left_time: String = item.getString(TAG_RMTIME)
                        val running: String = item.getString(TAG_RUNNING)

                        textView.setText(left_time + "분 후 " + WM_num + "번 세탁기를 사용할 수 있습니다.")
                        Log.d("running!!!!!!!", running)
                        if (running.toInt() == 2){
                            textView.setText(WM_num + "번 세탁기를 사용할 수 있습니다.")
                            button_register.isEnabled = true
                            read_WM_num = WM_num
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
}