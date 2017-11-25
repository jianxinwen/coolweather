package xyz.yuanjihua.coolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by jianli on 2017/11/23
 */
class Forcast {
    val date:String?=null
    @SerializedName("tmp")
    val temperature:Temperature?=null
    @SerializedName("cond")
    val more:More?=null

    class Temperature{
        val max:String?=null
        val min:String?=null
    }
    class More{
        @SerializedName("txt_d")
        val info:String?=null
    }
}