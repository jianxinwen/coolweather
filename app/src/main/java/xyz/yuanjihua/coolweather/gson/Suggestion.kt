package xyz.yuanjihua.coolweather.gson

import com.google.gson.annotations.SerializedName

/**
 * Created by jianli on 2017/11/23
 */
class Suggestion {

    @SerializedName("comf")
    val comfort:Comfort?=null

    @SerializedName("cw")
    val carwash:CarWash?=null

    val sport:Sport?=null

    class Comfort{
        @SerializedName("txt")
        val info:String?=null
    }
    class CarWash{
        @SerializedName("txt")
        val info:String?=null
    }
    class Sport{
        @SerializedName("txt")
        val info:String?=null
    }
}