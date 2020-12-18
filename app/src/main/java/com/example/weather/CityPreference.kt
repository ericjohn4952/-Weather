package com.example.weather

import android.app.Activity
import android.content.SharedPreferences

class CityPreference(activity: Activity) {
    var prefs: SharedPreferences

    // 這是如果以後要用搜尋的去找其他城市的資料用的，與這次的專題無關
    var city: String?
        get() = prefs.getString("city", "台北, 台灣") //這是如果使用者未輸入哪個城市時的預設城市
        set(city) {
            prefs.edit().putString("city", city).commit()
        }

    init {
        prefs = activity.getPreferences(Activity.MODE_PRIVATE)
    }
}