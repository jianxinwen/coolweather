package xyz.yuanjihua.coolweather.db

import org.litepal.crud.DataSupport

/**
 * Created by jianli on 2017/11/23
 */
class City: DataSupport(){
    var id:Int=0
    var cityName=""
    var cityCode=0
    var provinceId=0
}