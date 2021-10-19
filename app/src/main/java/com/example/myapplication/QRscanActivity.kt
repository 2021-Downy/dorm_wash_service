package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

open class QRscanActivity : AppCompatActivity() {
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
    // qr코드 주소 반환 시에
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

                    // 데이터를 전달하면서 사용 등록 액티비티를 실행
                    val RegisterActivity = Intent(this, RegisterActivity::class.java)
                    RegisterActivity.putExtra("user_num", user_num)
                    RegisterActivity.putExtra("dorm_num", dorm_num)
                    RegisterActivity.putExtra("WM_num", WM_num)
                    startActivity(RegisterActivity)
                    finish()
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
}