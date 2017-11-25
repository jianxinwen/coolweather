package xyz.yuanjihua.coolweather.db

import org.litepal.crud.DataSupport

/**
 * Created by jianli on 2017/11/23
 */
class Province:DataSupport() {
    var id: Int=0
    var provinceName = ""
    var provinceCode = 0
}