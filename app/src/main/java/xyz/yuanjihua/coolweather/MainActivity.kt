package xyz.yuanjihua.coolweather

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import okhttp3.Call
import okhttp3.Response
import xyz.yuanjihua.coolweather.util.HttpUtil
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs=PreferenceManager.getDefaultSharedPreferences(this)
        if(prefs.getString("weather",null)!=null){
            val intent=Intent(this,WeatherActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
