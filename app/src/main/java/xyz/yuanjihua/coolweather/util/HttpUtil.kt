package xyz.yuanjihua.coolweather.util

import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Created by jianli on 2017/11/23
 */
class HttpUtil {
    companion object {
        fun sendOkHttpRequest(address:String,callback:okhttp3.Callback){
            val client=OkHttpClient()
            val request=Request.Builder()
                    .url(address)
                    .build()
            client.newCall(request).enqueue(callback)
        }
    }
}