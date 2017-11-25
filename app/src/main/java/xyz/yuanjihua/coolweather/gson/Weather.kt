package xyz.yuanjihua.coolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by jianli on 2017/11/23
 */
class Weather {
    val status:String?=null
    val basic:Basic?=null
    val aqi:AQI?=null
    val now:Now?=null
    val suggestion:Suggestion?=null
    @SerializedName("daily_forecast")
    val forecastList:List<Forcast>?=null
}
