package com.example.weather

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.weather.RemoteFetch.getJSON
import org.json.JSONObject
import java.text.DateFormat
import java.util.*

class WeatherFragment : Fragment() { //這個事處理抓下來的資料用的城市，以activity_main為主要的layout
    var weatherFont: Typeface? = null
    var cityField: TextView? = null
    var updatedField: TextView? = null
    var detailsField: TextView? = null
    var currentTemperatureField: TextView? = null
    var weatherIcon: TextView? = null
    var handler: Handler  //定義這些天氣資訊的物件並在onCreate裡面啟用他們
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.activity_main, container, false)
        cityField = rootView.findViewById<View>(R.id.city_field) as TextView
        updatedField = rootView.findViewById<View>(R.id.updated_field) as TextView
        detailsField = rootView.findViewById<View>(R.id.details_field) as TextView
        currentTemperatureField = rootView.findViewById<View>(R.id.current_temperature_field) as TextView
        weatherIcon = rootView.findViewById<View>(R.id.weather_icon) as TextView
        weatherIcon!!.typeface = weatherFont   //設定watherIcon由我們在fonts資料夾中新增的字型來顯示
        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {//啟用並指定字型檔位置
        super.onCreate(savedInstanceState)
        weatherFont = Typeface.createFromAsset(activity!!.assets, "fonts/weather.ttf") //字型檔位置
        updateWeatherData(CityPreference(activity!!).city)
    }

    private fun updateWeatherData(city: String?) {//這個thread將我們從remoteFetch抓下來的getJSON做判斷，如果資料正確將啟用renderWeather的這個方法
        object : Thread() {
            override fun run() {
                val json = getJSON(activity!!, city)
                if (json == null) {//判斷有無資料
                    handler.post { //只有主要的thread可以更新UI的資訊，若是我們直接從背景的thread去更新UI會出錯，所以我們用handler.post的方法
                        Toast.makeText(activity,
                                activity!!.getString(R.string.place_not_found),
                                Toast.LENGTH_LONG).show()
                    }
                } else {
                    handler.post { renderWeather(json) }//資料無誤將啟用renderWeather的方法
                }
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun renderWeather(json: JSONObject) {//從抓取的jason去找我們的資料並顯示出來
        try {
            cityField!!.text = json.getString("name").toUpperCase(Locale.TAIWAN) +
                    ", " +
                    json.getJSONObject("sys").getString("country")
            val details = json.getJSONArray("weather").getJSONObject(0)
            val main = json.getJSONObject("main")
            detailsField!!.text = """${details.getString("description").toUpperCase(Locale.TAIWAN)}
濕度: ${main.getString("humidity")}%
氣壓: ${main.getString("pressure")} 百帕"""
            currentTemperatureField!!.text = String.format("%.2f", main.getDouble("temp")) + " ℃"
            val df = DateFormat.getDateTimeInstance()
            val updatedOn = df.format(Date(json.getLong("dt") * 1000))
            updatedField!!.text = "更新時間: $updatedOn"//這裡顯示的是英國格林威治時間，台灣時間我還要在改一下
            setWeatherIcon(details.getInt("id"),
            )
        } catch (e: Exception) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data")
        }
    }

    private fun setWeatherIcon(actualId: Int) {
        val id = actualId / 100
        var icon = ""

        when (id) {
            2 -> icon = activity!!.getString(R.string.weather_thunder)
            3 -> icon = activity!!.getString(R.string.weather_drizzle)
            7 -> icon = activity!!.getString(R.string.weather_foggy)
            8 -> icon = activity!!.getString(R.string.weather_cloudy)
            6 -> icon = activity!!.getString(R.string.weather_snowy)
            5 -> icon = activity!!.getString(R.string.weather_rainy)//這裡網站上有給對應的id名稱，哪個ID對應哪個圖案
        }

        weatherIcon!!.text = icon
    }

    fun changeCity(city: String?) {//若是要更改城市時用，本專題不需要
        updateWeatherData(city)
    }

    init {
        handler = Handler()

    }
}