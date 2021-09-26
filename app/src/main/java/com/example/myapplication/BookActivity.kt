package com.example.myapplication

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_book.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import android.util.Log

class BookActivity : AppCompatActivity() {

    private val IP_ADDRESS = "morned270.dothome.co.kr"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        var user_num = intent.getStringExtra("user_num").toString()
        var dorm_num = intent.getStringExtra("dorm_num").toString()
        var WM_num = intent.getStringExtra("WM_num").toString()
        textView_main.setText(WM_num + "번 세탁기는 사용 중입니다.\n사용을 예약하시겠습니까?")
        textView_wm.setText("세탁기 번호 : " + WM_num + "번")
        textView_dorm.setText("기숙사 : " + dorm_num)



        button_book.setOnClickListener{
            val task = InsertData()
            val date = LocalDateTime.now()
            /*사용등록이 아닌 예약일 경우 구별을 위해 using_num 을 음수로 삽입*/
            val WM_num_res = WM_num.toInt() * -1
            var msg = task.execute("http://$IP_ADDRESS/insertBook.php", user_num, WM_num_res.toString(), date.toString())
//            task.execute("http://$IP_ADDRESS/insertUses.php", user_num, WM_num, "2020-12-12","2020-12-12T20:30:00.00", "2020-12-12T21:20:00.00")

            val UsageStatusActivity = Intent(this, UsageStatusActivity::class.java)
            UsageStatusActivity.putExtra("user_num",user_num)
            UsageStatusActivity.putExtra("dorm_num",dorm_num)
            startActivity(UsageStatusActivity)

            Toast.makeText(applicationContext, "예약이 완료되었습니다.", Toast.LENGTH_LONG).show()
            finish()
        }
        button_cancel.setOnClickListener{
            val UsageStatusActivity = Intent(this, UsageStatusActivity::class.java)
            UsageStatusActivity.putExtra("user_num",user_num)
            UsageStatusActivity.putExtra("dorm_num",dorm_num)
            startActivity(UsageStatusActivity)
            finish()
        }
    }

    /*Insert Data in mysql*/
    private class InsertData : AsyncTask<String, Void, String>() {


        override fun doInBackground(vararg params: String?): String {

            val serverURL: String? = params[0]
            val user_num: String? = params[1]
            val WM_num: String? = params[2]
            val date: String? = params[3]
            val state: Int? = 1

            val postParameters: String = "user_num=$user_num&WM_num=$WM_num&date=$date&state=$state"
//            Log.d("book msg : ", user_num.toString() + WM_num.toString() + date.toString() + state.toString())
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