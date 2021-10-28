package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.etToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import android.annotation.SuppressLint
import org.json.JSONObject
import org.json.JSONArray
import org.json.JSONException

var mJsonString =""
const val TOPIC = "/topics/myTopic2"

class RegisterActivity : AppCompatActivity() {

    private val IP_ADDRESS = "morned270.dothome.co.kr"
    val TAG = "RegisterActivity"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
            etToken.setText(it.token)
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        var user_num = intent.getStringExtra("user_num").toString()
        var dorm_num = intent.getStringExtra("dorm_num").toString()
        var WM_num = intent.getStringExtra("WM_num").toString()
        textView_register.setText(WM_num + "번 세탁기를 사용하시겠습니까?")

        button_yes.setOnClickListener{
            val task = InsertData()
            val onlyDate: LocalDate = LocalDate.now()
            val start_time = LocalDateTime.now()
            val left_time = "50";
            val date = onlyDate.toString()
            task.execute(
                "http://$IP_ADDRESS/insertUses.php",
                user_num,
                WM_num,
                date,
                start_time.toString(),
                start_time.plusMinutes(
                    50
                ).toString(),
                left_time
            )
//            task.execute("http://$IP_ADDRESS/insertUses.php", user_num, WM_num, "2020-12-12","2020-12-12T20:30:00.00", "2020-12-12T21:20:00.00")

            val UsageStatusActivity = Intent(this, UsageStatusActivity::class.java)
            UsageStatusActivity.putExtra("user_num", user_num)
            UsageStatusActivity.putExtra("dorm_num", dorm_num)
            startActivity(UsageStatusActivity)

            Toast.makeText(applicationContext, "등록되었습니다.", Toast.LENGTH_LONG).show()
            finish()
        }
        button_no.setOnClickListener{
            val UsageStatusActivity = Intent(this, UsageStatusActivity::class.java)
            UsageStatusActivity.putExtra("user_num", user_num)
            UsageStatusActivity.putExtra("dorm_num", dorm_num)
            startActivity(UsageStatusActivity)
            finish()
        }
        button_report.setOnClickListener {
            val task = readData()
            task.execute("http://$IP_ADDRESS/getjson_uncollect.php", WM_num)

            //시간이 되면 변경 : 한번 신고하기 누르면 버튼 비활성화 되게(그치만 db를 이용해야되서 조금 번거로울 수도 있음)
            button_report.setEnabled(false)
        }
    }

    //받은 fcm요청을 처리
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    fun reportUncollect(user_token: String) {
        val title = "세탁물 미수거 알림"
        val message = "사용하신 세탁기에 세탁물이 남아있습니다. 빠른 수거 부탁드립니다"

        val recipientToken = user_token
        Log.d(TAG, "미수거 사용자 토큰 :"+recipientToken)
        if(recipientToken.isNotEmpty()) {
            PushNotification(
                NotificationData(title, message),   //메세지와 제목을 json형태로 만듬
                recipientToken  //이 토큰으로 보냄
            ).also {
                sendNotification(it)
            }
        }
    }

    /*Read Data in mysql*/
    private inner class readData : AsyncTask<String?, Void?, String?>() {

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == null) {
//                loading.visibility = View.INVISIBLE
                Toast.makeText(
                    applicationContext,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()

            } else {
                mJsonString = result
                val TAG_JSON = "webnautes"
                val TAG_TOKEN = "user_token"
                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                    for (i in 0 until jsonArray.length()) {
                        val item: JSONObject = jsonArray.getJSONObject(i)
                        val user_token: String = item.getString(TAG_TOKEN)
                        reportUncollect(user_token)
                    }
                } catch (e: JSONException) {
                    //Log.d(LoginActivity.TAG, "showResult : ", e)
//                    loading.visibility = View.INVISIBLE
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
            val WM_num = params[1]
            val postParameters: String = "WM_num=$WM_num"

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

    /*Insert Data in mysql*/
    private class InsertData : AsyncTask<String, Void, String>() {


        override fun doInBackground(vararg params: String?): String {

            val serverURL: String? = params[0]
            val user_num: String? = params[1]
            val WM_num: String? = params[2]
            val date: String? = params[3]
            val start_time: String? = params[4]
            val end_time: String? = params[5]
            val left_time: String? = params[6]

            val postParameters: String = "user_num=$user_num&WM_num=$WM_num&date=$date&start_time=$start_time&end_time=$end_time&left_time=$left_time"

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