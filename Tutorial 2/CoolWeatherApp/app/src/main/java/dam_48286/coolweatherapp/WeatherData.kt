package dam_48286.coolweatherapp

import android.content.res.Resources

data class WeatherData (
    var latitude : String ,
    var longitude : String ,
    var timezone : String ,
    var current_weather : CurrentWeather ,
    var hourly : Hourly
)
data class CurrentWeather (
    var temperature : Float ,
    var windspeed : Float ,
    var winddirection : Int ,
    var weathercode : Int ,
    var time : String
)
data class Hourly (
    var time : ArrayList <String>,
    var temperature_2m : ArrayList <Float>,
    var weathercode : ArrayList <Int>,
    var pressure_msl : ArrayList <Double>
)
data class WMO_WeatherCode(var code: Int, var description: String, var image: String) {
    companion object {
        fun getWeatherCodesArray(resources: Resources): List<WMO_WeatherCode> {
            val weatherCodes = mutableListOf<WMO_WeatherCode>()
            val codesArray = resources.getIntArray(R.array.weather_codes)
            val descriptionsArray = resources.getStringArray(R.array.weather_descriptions)
            val imagesArray = resources.getStringArray(R.array.weather_images)

            for (i in codesArray.indices) {
                weatherCodes.add(WMO_WeatherCode(codesArray[i], descriptionsArray[i], imagesArray[i]))
            }
            return weatherCodes
        }
    }
}
