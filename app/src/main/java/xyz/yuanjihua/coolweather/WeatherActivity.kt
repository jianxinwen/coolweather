package xyz.yuanjihua.coolweather

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.aqi.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.forecast_item.*
import kotlinx.android.synthetic.main.now.*
import kotlinx.android.synthetic.main.suggestion.*
import kotlinx.android.synthetic.main.title.*
import okhttp3.Call
import okhttp3.Response
import xyz.yuanjihua.coolweather.gson.Weather
import xyz.yuanjihua.coolweather.service.AutoUpdateService
import xyz.yuanjihua.coolweather.util.HttpUtil
import xyz.yuanjihua.coolweather.util.Utility
import java.io.IOException


class WeatherActivity : AppCompatActivity() {



    lateinit var bingPicImg:ImageView
    lateinit var mWeatherId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(Build.VERSION.SDK_INT>=21){
            val decroView=window.decorView
            decroView.systemUiVisibility=(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.statusBarColor= Color.TRANSPARENT
        }
        setContentView(R.layout.activity_weather)

        val prefs=PreferenceManager.getDefaultSharedPreferences(this)
        val weatherString=prefs.getString("weather",null)
        if(weatherString!=null){
            val weather=Utility.handleWeatherResponse(weatherString)
            mWeatherId=weather?.basic?.weatherId!!
            showWeatherInfo(weather)
        }else{
            mWeatherId=intent.getStringExtra("weather_id")
            weather_layout.visibility=View.INVISIBLE
            requestWeather(mWeatherId)
        }
        bingPicImg=findViewById(R.id.bing_pic_img)
        val bingPic=prefs.getString("bing_pic",null)
        if(bingPic!=null){
            Glide.with(this).load(bingPic).into(bingPicImg)
        }else{
            loadBingPic()
        }
        //下拉刷新
        swipe_refresh.setColorSchemeResources(R.color.colorPrimary)
        swipe_refresh.setOnRefreshListener {
            requestWeather(mWeatherId)
        }
        nav_button.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }

    }
    //请求天气数据
    fun requestWeather(weatherId:String){
        val weatherUrl="http://guolin.tech/api/weather?cityid=${weatherId}&key=bc0418b57b2d4918819d3974ac1285d9"
        HttpUtil.sendOkHttpRequest(weatherUrl,object :okhttp3.Callback{
            override fun onResponse(call: Call?, response: Response?) {
                val responseText=response?.body()?.string()
                val weather=Utility.handleWeatherResponse(responseText!!)
                runOnUiThread {
                    if(weather!=null && weather.status=="ok"){
                        val editor=PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity).edit()
                        editor.putString("weather",responseText)
                        editor.apply()
                        mWeatherId=weather.basic?.weatherId!!
                        showWeatherInfo(weather)
                    }else{
                        Toast.makeText(this@WeatherActivity,"获取天气信息失败",Toast.LENGTH_SHORT).show()
                    }
                    swipe_refresh.isRefreshing=false
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                runOnUiThread {
                    Toast.makeText(this@WeatherActivity,"获取天气信息失败",Toast.LENGTH_SHORT).show()
                    swipe_refresh.isRefreshing=false
                }
            }
        })
    }
    //显示天气数据
    fun showWeatherInfo(weather:Weather){
        val cityName=weather.basic?.cityName
        val updateTime=weather.basic?.update?.updateTime?.split(" ")!![1]
        val degree=weather.now?.temperature+"℃"
        val weatherInfo=weather.now?.more?.info
        title_city.text=cityName
        degree_text.text=degree
        title_update_time.text=updateTime
        weather_info_text.text=weatherInfo
        forecast_layout.removeAllViews()

        for(forecast in weather.forecastList!!){
            val view=LayoutInflater.from(this).inflate(R.layout.forecast_item,forecast_layout,false)
            view.findViewById<TextView>(R.id.date_text).text=forecast.date
            view.findViewById<TextView>(R.id.info_text).text=forecast.more?.info
            val maxT="最高温度：${forecast.temperature?.max}℃"
            val minT="最低温度：${forecast.temperature?.min}℃"
            view.findViewById<TextView>(R.id.max_text).text=maxT
            view.findViewById<TextView>(R.id.min_text).text=minT

            forecast_layout.addView(view)
        }

        if(weather.aqi!=null){
            aqi_text.text=weather.aqi.city?.aqi
            pm25_text.text=weather.aqi.city?.pm25
        }
        val comfort="舒适度：${weather.suggestion?.comfort?.info}"
        val carWashi="洗车指数：${weather.suggestion?.carwash?.info}"
        val sport="运动建议：${weather.suggestion?.sport?.info}"
        comfort_text.text=comfort
        car_wash_text.text=carWashi
        sport_text.text=sport

        weather_layout.visibility= View.VISIBLE
        val intent=Intent(this,AutoUpdateService::class.java)
        startService(intent)
    }

    //加载每日一图
    private fun loadBingPic(){
        val requestUrl="http://guolin.tech/api/bing_pic"
        HttpUtil.sendOkHttpRequest(requestUrl,object :okhttp3.Callback{
            override fun onResponse(call: Call?, response: Response?) {
                val bingPic=response?.body()?.string()
                Log.d("d",bingPic)
                val editor=PreferenceManager.getDefaultSharedPreferences(this@WeatherActivity).edit()
                editor.putString("bing_pic",bingPic)
                editor.apply()
                runOnUiThread {
                    Glide.with(this@WeatherActivity).load(requestUrl).into(bingPicImg)
                }

            }

            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }
        })
    }

}
