package dam_48286.coolweatherapp

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL

class WeatherViewModel : ViewModel() {
    val weatherData = MutableLiveData<WeatherData>()

    fun fetchWeatherData(lat: Float, long: Float) {
        Thread {
            val weather = WeatherAPI_Call(lat, long)
            weatherData.postValue(weather)
        }.start()
    }

    private fun WeatherAPI_Call(lat: Float, long: Float): WeatherData? {
        try {
            val reqString = buildString {
                append("https://api.open-meteo.com/v1/forecast?")
                append("latitude=$lat&longitude=$long&")
                append("current_weather=true&")
                append("hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m,wind_direction_10m")
            }
            val url = URL(reqString)
            url.openStream().use {
                return Gson().fromJson(InputStreamReader(it, "UTF-8"), WeatherData::class.java)
            }
        } catch (e: Exception) {
            Log.e("ERROR", "Error trying to fetch weather data: ${e.message}")
        }
        return null
    }
}