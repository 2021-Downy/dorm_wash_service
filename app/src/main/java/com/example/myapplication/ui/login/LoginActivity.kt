package com.example.myapplication.ui.login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import com.example.myapplication.R
import com.example.myapplication.SignupActivity
import com.example.myapplication.UsageStatusActivity
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
var mJsonString =""
var errorMessage = "ID 또는 패스워드가 잘못됐습니다."
var get_using_num = ""

class LoginActivity : AppCompatActivity() {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        //내 토큰 불러오기
        val newToken = FirebaseInstanceId.getInstance().getToken()
        Log.d("새 토큰", "나의 토큰 :"+ newToken)

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val login = findViewById<Button>(R.id.btn_login)
        val loading = findViewById<ProgressBar>(R.id.loading)

        btn_signup.setOnClickListener{
            val SignupActivity = Intent(this, SignupActivity::class.java)
            startActivity(SignupActivity)
        }

        loginViewModel = ViewModelProvider(this, LoginViewModelFactory())
            .get(LoginViewModel::class.java)

        loginViewModel.loginFormState.observe(this@LoginActivity, Observer {
            val loginState = it ?: return@Observer

            // disable login button unless both username / password is valid
            login.isEnabled = loginState.isDataValid

            if (loginState.usernameError != null) {
                username.error = getString(loginState.usernameError)
            }
            if (loginState.passwordError != null) {
                password.error = getString(loginState.passwordError)
            }
        })

//        loginViewModel.loginResult.observe(this@LoginActivity, Observer {
//            val loginResult = it ?: return@Observer
//
//            loading.visibility = View.GONE
//            if (loginResult.error != null) {
//                showLoginFailed(loginResult.error)
//            }
//            if (loginResult.success != null) {
//                updateUiWithUser(loginResult.success)
//            }
//            setResult(Activity.RESULT_OK)
//
//            //Complete and destroy login activity once successful
//            finish()
//        })

        username.afterTextChanged {
            loginViewModel.loginDataChanged(
                username.text.toString(),
                password.text.toString()
            )
        }

        password.apply {
            afterTextChanged {
                loginViewModel.loginDataChanged(
                    username.text.toString(),
                    password.text.toString()
                )
            }

            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        loginViewModel.login(
                            username.text.toString(),
                            password.text.toString()
                        )
                }
                false
            }



            login.setOnClickListener {
                loading.visibility = View.VISIBLE

                val ID : String = username.text.toString()
                val pw : String = password.text.toString()
                //토큰
                val token : String = newToken.toString()

                val task = readData()
                task.execute("http://morned270.dothome.co.kr/getjson.php",ID,pw)

//                loginViewModel.login(username.text.toString(), password.text.toString())
            }
        }
    }

    /*Read Data in mysql*/
    private inner class readData : AsyncTask<String?, Void?, String?>() {

        @SuppressLint("SetTextI18n")
        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            if (result == null) {
                loading.visibility = View.INVISIBLE
                Toast.makeText(
                    applicationContext,
                    errorMessage,
                    Toast.LENGTH_LONG
                ).show()

            } else {
                mJsonString = result
                val TAG_JSON = "webnautes"
                val TAG_NUM = "user_num"
                val TAG_NAME = "name"
                val TAG_DORM = "dorm_num"
                val TAG_USINGNUM = "using_num"
                try {
                    val jsonObject = JSONObject(result)
                    val jsonArray: JSONArray = jsonObject.getJSONArray(TAG_JSON)
                    for (i in 0 until jsonArray.length()) {
                        val item: JSONObject = jsonArray.getJSONObject(i)
                        val user_num: String = item.getString(TAG_NUM)
                        val name: String = item.getString(TAG_NAME)
                        val dorm_num: String = item.getString(TAG_DORM)
                        val using_num: String = item.getString(TAG_USINGNUM)
                        get_using_num = using_num
                        updateUiWithUser(user_num, name, dorm_num)
                    }
                } catch (e: JSONException) {
                    //Log.d(LoginActivity.TAG, "showResult : ", e)
                    loading.visibility = View.INVISIBLE
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
            val ID = params[1]
            val pw = params[2]
            val postParameters: String = "ID=$ID&pw=$pw"

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

    fun updateUiWithUser(user_num: String, displayName: String, dorm_num: String) {

        val welcome = getString(R.string.welcome)
        //val displayName = model.displayName
        // TODO : initiate successful logged in experience
        Toast.makeText(
            applicationContext,
            "$welcome $displayName",
            Toast.LENGTH_LONG
        ).show()

        //사용현황 띄우기
        val UsageStatusActivity = Intent(this, UsageStatusActivity::class.java)
        UsageStatusActivity.putExtra("user_num", user_num)
        UsageStatusActivity.putExtra("dorm_num", dorm_num)
        UsageStatusActivity.putExtra("using_num", get_using_num)
        startActivity(UsageStatusActivity)
        finish()
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}