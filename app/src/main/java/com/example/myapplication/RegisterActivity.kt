package com.example.myapplication

import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_register.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime

class RegisterActivity : AppCompatActivity() {

    private val IP_ADDRESS = "morned270.dothome.co.kr"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var user_num = intent.getStringExtra("user_num").toString()
        var dorm_num = intent.getStringExtra("dorm_num").toString()
        var WM_num = intent.getStringExtra("WM_num").toString()
        textView_register.setText(WM_num+"번 세탁기를 사용하시겠습니까?")

        button_yes.setOnClickListener{
            val task = InsertData()
            val onlyDate: LocalDate = LocalDate.now()
            val start_time = LocalDateTime.now()
            val date = onlyDate.toString()
            task.execute("http://$IP_ADDRESS/insertUses.php", user_num, WM_num, date, start_time.toString(), start_time.plusMinutes(50).toString())
//            task.execute("http://$IP_ADDRESS/insertUses.php", user_num, WM_num, "2020-12-08", "2020-12-08T00:00:54.608", "2020-12-08T00:40:54.608")

            val UsageStatusActivity = Intent(this, UsageStatusActivity::class.java)
            UsageStatusActivity.putExtra("user_num",user_num)
            UsageStatusActivity.putExtra("dorm_num",dorm_num)
            startActivity(UsageStatusActivity)

            Toast.makeText(applicationContext, "등록되었습니다.", Toast.LENGTH_LONG).show()
            finish()
        }
        button_no.setOnClickListener{
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
            val start_time: String? = params[4]
            val end_time: String? = params[5]

            val postParameters: String = "user_num=$user_num&WM_num=$WM_num&date=$date&start_time=$start_time&end_time=$end_time"

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