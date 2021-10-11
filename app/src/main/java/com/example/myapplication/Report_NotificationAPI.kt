package com.example.myapplication

import com.example.myapplication.Report_Constants.Companion.CONTENT_TYPE
import com.example.myapplication.Report_Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST



interface Report_NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: Report_PushNotification
    ): Response<ResponseBody>
}