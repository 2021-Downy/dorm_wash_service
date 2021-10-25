package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.ui.login.mJsonString
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
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

open class QRscanActivity : AppCompatActivity() {


    //에뮬레이터로 실행시 ip주소
    private val IP_ADDRESS = "morned270.dothome.co.kr"
    private var runningState = -1;
    private var WMnumber = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initQRcodeScanner()
    }
    private fun initQRcodeScanner() {
        val integrator  = IntentIntegrator(this)
        integrator.setBeepEnabled(true) // QR 스캔 시 소리 출력 여부
        integrator.setOrientationLocked(false)   // 가로, 세로 모드를 고정
        integrator.setPrompt("사용하려는 세탁기의 QR 코드를 스캔해주세요.")
        integrator.initiateScan()   // Zxing 라이브러리의 스캐너가 보여짐
    }
    // QR 코드 주소 반환 시에
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result : IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {

            if (result.contents == null) {   // QR 코드에 데이터가 없거나, 뒤로가기를 클릭한 경우
                Toast.makeText(this, "QR코드 인증이 취소되었습니다.", Toast.LENGTH_SHORT)
                finish()
            }

            else {                          // QR 코드에 데이터가 있는 경우

                var str = result.contents;      // QR 코드의 데이터를 str에 저장
                var token = str.split('-');     // '-'를 기준으로 문자열을 쪼갬
                var dormNumber = token[0];      // 앞 숫자는 기숙사 번호를 의미하고
                var wmNumber = token[1];        // 뒷 숫자는 세탁기 번호를 의미

                val dormName = when(dormNumber){    // when문을 통해

                    // 기숙사 번호와 기숙사 이름을 매칭
                    "1" -> "계영원"
                    "2" -> "개성재"
                    "3" -> "양성재"
                    "4" -> "양진재"
                    "5" -> "양현재"

                    // 예외처리를 위한 else branch
                    else -> ""
                }

                if (dorm_num == dormName){  // QR 코드의 기숙사 정보가 사용자 정보와 일치할 경우

                    // 스캔한 QR 코드의 세탁기 번호를 지정해준 다음
                    val WM_num = wmNumber

                    // 해당 세탁기의 사용가능 여부를 확인하고, 이에 따라 올바른 동작을 수행하는 메소드 호출
                    val task = readData()
                    task.execute("http://morned270.dothome.co.kr/getjson_readAvailable.php", WM_num)
                }
                else {                      // QR 코드의 기숙사 정보가 사용자 정보와 일치하지 않는 경우

                    // 안내 메시지를 띄운 다음
                    Toast.makeText(
                            applicationContext,
                            "거주중인 기숙사의 세탁기만 사용할 수 있습니다.",
                            Toast.LENGTH_LONG
                    ).show()

                    // 사용현황 액티비티를 실행
                    val UsageStatusActivity = Intent(this, com.example.myapplication.UsageStatusActivity::class.java)
                    UsageStatusActivity.putExtra("user_num",user_num)
                    UsageStatusActivity.putExtra("dorm_num",dorm_num)
                    startActivity(UsageStatusActivity)
                    finish()
                }

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
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
                        "세탁기 상태 정보를 불러오지 못했습니다.",
                        Toast.LENGTH_LONG
                ).show()

            } else {
                mJsonString = result
                val TAG_JSON = "webnautes"
                val TAG_WMNUM = "WM_num"
                val TAG_RUNNING = "running"

                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                    for (i in 0 until jsonArray.length()) {
                        val item: JSONObject = jsonArray.getJSONObject(i)
                        val WM_num: String = item.getString(TAG_WMNUM)
                        val running: Int = item.getInt(TAG_RUNNING)

                        WMnumber = WM_num;
                        /* 읽어들인 세탁기의 상태를 runningState 변수에 대입 */
                        runningState = running;

                        // 세탁기의 상태가 '사용 대기'일 경우에만
                        if (runningState == 0) {

                            // 데이터를 전달하면서 사용 등록 액티비티를 실행
                            val RegisterActivity = Intent(this@QRscanActivity, RegisterActivity::class.java)
                            RegisterActivity.putExtra("user_num", user_num)
                            RegisterActivity.putExtra("dorm_num", dorm_num)
                            RegisterActivity.putExtra("WM_num", WM_num)
                            startActivity(RegisterActivity)
                            finish()
                        }

                        // 그 외의 상태일 경우
                        else {
                            // 안내 메시지를 띄운 다음
                            Toast.makeText(
                                    applicationContext,
                                    WM_num + "번 세탁기는 이미 사용중입니다.",
                                    Toast.LENGTH_LONG
                            ).show()

                            // 사용현황 액티비티를 실행
                            val UsageStatusActivity = Intent(this@QRscanActivity, com.example.myapplication.UsageStatusActivity::class.java)
                            UsageStatusActivity.putExtra("user_num",user_num)
                            UsageStatusActivity.putExtra("dorm_num",dorm_num)
                            startActivity(UsageStatusActivity)
                            finish()
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

            } catch (e: Exception) {
                //errorString = e.toString()
                null
            }
        }
    }
}