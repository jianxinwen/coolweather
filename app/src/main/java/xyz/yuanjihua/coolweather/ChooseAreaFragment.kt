package xyz.yuanjihua.coolweather

import android.app.Activity
import android.app.DialogFragment
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_weather.*
import okhttp3.Call
import okhttp3.Response
import org.litepal.crud.DataSupport
import xyz.yuanjihua.coolweather.db.City
import xyz.yuanjihua.coolweather.db.County
import xyz.yuanjihua.coolweather.db.Province
import xyz.yuanjihua.coolweather.util.HttpUtil
import xyz.yuanjihua.coolweather.util.Utility
import java.io.IOException

/**
 * Created by jianli on 2017/11/23
 */
class ChooseAreaFragment: Fragment() {
    companion object {
        val LEVEL_PROVINCE=0
        val LEVEL_CITY=1
        val LEVEL_COUNTY=2
    }
    private var progressDialog:DialogFragment?=null
    //控件
    private lateinit var titleText:TextView
    private lateinit var backButton:Button
    private lateinit var listView:ListView
    //ListView
    private lateinit var adapter:ArrayAdapter<String>
    private var dataList=ArrayList<String>()
    //省市县...
    private lateinit var provinceList:List<Province>
    private lateinit var cityList:List<City>
    private lateinit var countyList:List<County>
    //选中状态
    private var selectedProvince:Province?=null
    private var selectedCity:City?=null
    private var currentLevel=LEVEL_PROVINCE

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view=inflater!!.inflate(R.layout.choose_area,container,false)
        titleText=view.findViewById(R.id.title_text)
        backButton=view.findViewById(R.id.back_btn)
        listView=view.findViewById<View>(R.id.list_view) as ListView
        adapter= ArrayAdapter(activity,android.R.layout.simple_list_item_1,dataList)
        listView.adapter=adapter
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        listView.setOnItemClickListener{
            _, _, position, _ ->
            if(currentLevel== LEVEL_PROVINCE){
                selectedProvince = provinceList[position]
                queryCities()
            }else if(currentLevel== LEVEL_CITY){
                selectedCity=cityList[position]
                queryCounties()
            }else if(currentLevel== LEVEL_COUNTY){
                val weatherId=countyList[position].weatherId

                if(activity is MainActivity){
                    val intent=Intent(activity,WeatherActivity::class.java)
                    intent.putExtra("weather_id",weatherId)
                    startActivity(intent)
                    activity.finish()
                }else if(activity is WeatherActivity){
                    val a=activity as WeatherActivity
                    a.drawer_layout.closeDrawer(GravityCompat.START)
                    a.swipe_refresh.isRefreshing=true
                    a.requestWeather(weatherId)
                }
            }
            Log.d("d","currentLevel:${currentLevel}")
        }
        backButton.setOnClickListener {
            when(currentLevel){
                LEVEL_COUNTY->queryCities()
                LEVEL_CITY->queryProvinces()
            }
        }
        queryProvinces()
    }
    //查询省信息，优先从本地数据中查询
    private fun queryProvinces(){
        titleText.text="中国"
        backButton.visibility=View.GONE
        provinceList=DataSupport.findAll(Province::class.java)
        if(provinceList.size>0){
            dataList.clear()
            for (province in provinceList){
                dataList.add(province.provinceName)
            }
            adapter.notifyDataSetChanged()
            listView.setSelection(0)
            currentLevel= LEVEL_PROVINCE
        }else{
            val address="http://guolin.tech/api/china"
            queryFromServer(address,"province")
        }

    }
    //查询市信息，优先从本地数据中查询
    private fun queryCities(){
        titleText.text=selectedProvince?.provinceName
        backButton.visibility=View.VISIBLE
        cityList=DataSupport.where("provinceid=?","${selectedProvince?.id}").find(City::class.java)
        if(cityList.size>0){
            dataList.clear()
            for(city in cityList){
                dataList.add(city.cityName)
            }
            adapter.notifyDataSetChanged()
            listView.setSelection(0)
            currentLevel= LEVEL_CITY
        }else{

            val provinceCode=selectedProvince?.provinceCode
            val address="http://guolin.tech/api/china/${provinceCode}"
            queryFromServer(address,"city")
        }
    }
    //查询县信息，优先从本地数据中查询
    private fun queryCounties(){
        titleText.text=selectedCity?.cityName
        backButton.visibility=View.VISIBLE
        countyList=DataSupport.where("cityid=?", "${selectedCity?.id}").find(County::class.java)
        if(countyList.size>0){
            dataList.clear()
            for(county in countyList){
                dataList.add(county.countyName)
            }
            adapter.notifyDataSetChanged()
            currentLevel= LEVEL_COUNTY
        }else{
            val provinceCode=selectedProvince?.provinceCode
            val cityCode=selectedCity?.cityCode
            val address="http://guolin.tech/api/china/${provinceCode}/${cityCode}"
            queryFromServer(address,"county")
        }

    }
    //从网络中查询省市县信息
    private fun queryFromServer(address:String,type:String){
        showProgressDialog()
        HttpUtil.sendOkHttpRequest(address,object :okhttp3.Callback{
            override fun onResponse(call: Call?, response: Response) {
                val responseText=response.body()!!.string()
                var result=false
                when(type){
                    "province"->result=Utility.handleProvinceResponse(responseText)
                    "city"->result=Utility.handleCityResponse(responseText,selectedProvince?.id!!)
                    "county"->result=Utility.handleCountyResponse(responseText,selectedCity?.id!!)
                }
                if(result){
                    activity.runOnUiThread{
                        closeProgressDialog()
                        when(type){
                            "province"->queryProvinces()
                            "city"->queryCities()
                            "county"->queryCounties()
                        }
                    }
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {

                activity.runOnUiThread {
                    closeProgressDialog()
                    Toast.makeText(activity,"加载失败",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    private fun showProgressDialog(){
    }
    private fun closeProgressDialog(){
    }




}