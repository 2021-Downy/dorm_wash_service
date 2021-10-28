package com.example.myapplication

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_signup.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import com.google.firebase.iid.FirebaseInstanceId
import android.util.Log

var errorMessage = "해당 ID는 사용하실 수 없습니다."
var duplicated=0

class SignupActivity : AppCompatActivity() {

    //에뮬레이터로 실행시 ip주소
    private val IP_ADDRESS = "morned270.dothome.co.kr"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        //내 토큰 불러오기
        val newToken = FirebaseInstanceId.getInstance().getToken()
        Log.d("새 토큰", "나의 토큰 :"+ newToken)

        val spinnerList = arrayOf<String>("개성재","계영원","양성재","양진재","양현재")
        spinner.adapter = ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,spinnerList)

        btn_register.setOnClickListener {
            val name : String = signup_name.text.toString()
            val email: String = editTextTextEmailAddress.text.toString()
            val password : String = editTextTextPassword.text.toString()
            val phone: String = editTextPhone.text.toString()
            val dorm: String = spinner.selectedItem.toString()
            val token: String = newToken.toString()

            val task = InsertData()
            task.execute("http://$IP_ADDRESS/insertTest.php", name, email, password, phone, dorm, token)

            Toast.makeText(applicationContext, "가입되었습니다.", Toast.LENGTH_LONG).show()
            super.onBackPressed();
        }
    }


    /*Insert Data in mysql*/
    private class InsertData : AsyncTask<String, Void, String>() {


        override fun doInBackground(vararg params: String?): String {

            val serverURL: String? = params[0]
            val name: String? = params[1]
            val email: String? = params[2]
            val password: String? = params[3]
            val phone: String? = params[4]
            val dorm: String? = params[5]
            val token: String? = params[6]

            val postParameters: String = "name=$name&ID=$email&pw=$password&phone_num=$phone&dorm_num=$dorm&user_token=$token"

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