package xyz.yuanjihua.coolweather.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.preference.PreferenceManager
import okhttp3.Call
import okhttp3.Response
import xyz.yuanjihua.coolweather.util.HttpUtil
import xyz.yuanjihua.coolweather.util.Utility
import java.io.IOException

class AutoUpdateService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateWeather()
        updateBingPic()
        val manager=getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val span=8*60*60*1000
        val triggerAtTime=SystemClock.elapsedRealtime()+span
        val i=Intent(this,AutoUpdateService::class.java)
        val pi=PendingIntent.getActivity(this,0,i,0)
        manager.cancel(pi)
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi)
        return super.onStartCommand(intent, flags, startId)
    }
    //更新天气
    private fun updateWeather(){
        val prefs=PreferenceManager.getDefaultSharedPreferences(this)
        val weatherString=prefs.getString("weather",null)
        if(weatherString!=null){
            val weather=Utility.handleWeatherResponse(weatherString)
            val weatherId=weather?.basic?.weatherId
            val weatherUrl="http://guolin.tech/api/weather?cityid=${weatherId}&key=bc0418b57b2d4918819d3974ac1285d9"

            HttpUtil.sendOkHttpRequest(weatherUrl,object :okhttp3.Callback{

                override fun onResponse(call: Call?, response: Response?) {
                    if(weather!=null && weather.status=="ok"){
                        val responseText=response?.body()?.string()
                        val editor=PreferenceManager.getDefaultSharedPreferences(this@AutoUpdateService).edit()
                        editor.putString("weather",responseText)
                        editor.apply()
                    }
                }

                override fun onFailure(call: Call?, e: IOException?) {
                    e?.printStackTrace()
                }
            })
        }
    }

    //获取图片
    private fun updateBingPic(){
        val requestUrl="http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(requestUrl,object :okhttp3.Callback{
            override fun onResponse(call: Call?, response: Response?) {
                val bingPic=response?.body()?.string()
                val editor=PreferenceManager.getDefaultSharedPreferences(this@AutoUpdateService).edit()
                editor.putString("bing_pic",bingPic)
                editor.apply()

            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }
        })
    }
}
