package xyz.yuanjihua.coolweather.util

import android.util.Log
import com.google.gson.Gson
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import xyz.yuanjihua.coolweather.db.City
import xyz.yuanjihua.coolweather.db.County
import xyz.yuanjihua.coolweather.db.Province
import xyz.yuanjihua.coolweather.gson.Weather

/**
 * Created by jianli on 2017/11/23
 */
class Utility {
    companion object {
        //解析省数据
        fun handleProvinceResponse(response:String):Boolean{

            if(response!=""){
                try{
                    val provinces=JSONArray(response)
                    for(i in 0 until provinces.length()){
                        val provinceObject=provinces.getJSONObject(i)
                        val province=Province()
                        province.provinceName=provinceObject.getString("name")
                        province.provinceCode=provinceObject.getInt("id")
                        province.save()
                    }
                    return true
                }catch (e:JSONException){
                    e.printStackTrace()
                }
            }
            return false
        }
        //解析市数据
        fun handleCityResponse(response: String,provinceId:Int):Boolean{
            if(response!=""){
                    try{
                        val cities=JSONArray(response)
                        for(i in 0 until cities.length()){
                            val cityObject=cities.getJSONObject(i)
                            val city=City()
                            city.cityName=cityObject.getString("name")
                            city.cityCode=cityObject.getInt("id")
                            city.provinceId=provinceId
                            city.save()
                        }
                        return true
                    }catch (e:JSONException){
                        e.printStackTrace()
                    }
            }
            return false
        }
        //解析县数据
        fun handleCountyResponse(response: String,cityId:Int): Boolean{
            if(response!=""){
                try{
                    val counties=JSONArray(response)
                    for(i in 0 until counties.length()){
                        val countyObject=counties.getJSONObject(i)
                        val county= County()
                        county.countyName=countyObject.getString("name")
                        county.weatherId=countyObject.getString("weather_id")
                        county.cityId=cityId
                        county.save()
                    }
                    return true
                }catch (e:JSONException){
                    e.printStackTrace()
                }
            }
            return false
        }


        //解析天气数据
        fun handleWeatherResponse(response: String): Weather?{
            try {
                val jsonObject=JSONObject(response)
                val jsonArray=jsonObject.getJSONArray("HeWeather")
                val weatherContent=jsonArray.getJSONObject(0).toString()
                return Gson().fromJson(weatherContent,Weather::class.java)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return null
        }
    }
}