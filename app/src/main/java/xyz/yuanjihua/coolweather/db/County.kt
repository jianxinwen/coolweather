package xyz.yuanjihua.coolweather.db

import org.litepal.crud.DataSupport

/**
 * Created by jianli on 2017/11/23
 */
class County: DataSupport(){
    var id=0
    var countyName=""
    var weatherId=""
    var cityId=0
}