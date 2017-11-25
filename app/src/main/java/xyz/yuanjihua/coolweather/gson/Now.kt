package xyz.yuanjihua.coolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by jianli on 2017/11/23
 */
class Now {
    @SerializedName("tmp")
    val temperature:String?=null

    @SerializedName("cond")
    val more:More?=null

    class More{
        @SerializedName("txt")
        val info:String?=null
    }

}