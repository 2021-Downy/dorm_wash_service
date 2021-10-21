package com.example.myapplication

class Constants {

    companion object {
        const val BASE_URL = "https://fcm.googleapis.com"
        //나중에 가능하면 서버키도 db에서 가져오는 등의 방식으로 client에게서 숨기는 것이 바람직함
        const val SERVER_KEY = "AAAAzxbUmVo:APA91bGQ2qEWyrolvMmz2Iu7i-jKxa6Xe_bz_fKDMeJKxZEXZsZpcozj6Sn1eO5AT0lCj22WZTCHLLXKtvEF_b0QFQXxyW381eNRfHydtHqNR1cxTfm68FrsfjVUNUd8iTdPez7Lh9Ua"
        const val CONTENT_TYPE = "application/json"
    }
}