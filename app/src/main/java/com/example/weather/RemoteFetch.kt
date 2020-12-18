package com.example.weather

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object RemoteFetch {
    private const val OPEN_WEATHER_MAP_API = "https://api.openweathermap.org/data/2.5/weather?q=Taipei&units=metric&appid=29f055a6d81c137260e7bcc7824e27fe" //這是api網址
    @JvmStatic
    fun getJSON(context: Context, city: String?): JSONObject? {
        return try {
            val url = URL(String.format(OPEN_WEATHER_MAP_API))
            val connection = url.openConnection() as HttpURLConnection
            connection.addRequestProperty("29f055a6d81c137260e7bcc7824e27fe",  //這是你申請的帳號金鑰
                    context.getString(R.string.open_weather_maps_app_id))
            val reader = BufferedReader(
                    InputStreamReader(connection.inputStream))
            val json = StringBuffer(1024) //將資料存成string
            var tmp: String? = ""
            while (reader.readLine().also { tmp = it } != null) json.append(tmp).append("\n")
            reader.close()
            val data = JSONObject(json.toString()) //將string存成JSON的物件



            if (data.getInt("cod") != 200) {
                null  // 抓下來的jason檔裡面有一個測試的數值叫做cod 如果這個cod的值不是200則顯示錯誤
            } else data
        } catch (e: Exception) {
            null
        }
    }
}