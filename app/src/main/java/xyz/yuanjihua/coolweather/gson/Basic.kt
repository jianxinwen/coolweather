package xyz.yuanjihua.coolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by jianli on 2017/11/23
 */
class Basic {
    @SerializedName("city")
    val cityName:String?=null

    @SerializedName("id")
    val weatherId:String?=null

    val update:Update?=null

    class Update{
        @SerializedName("loc")
        val updateTime:String?=null
    }
}