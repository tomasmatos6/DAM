package dam_48286.coolweatherapp

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone


class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocationAndWeatherData(false)

        val myButton: Button = findViewById(R.id.updateButton)
        myButton.setOnClickListener {
            fetchLocationAndWeatherData(true)
        }
    }

    private fun fetchLocationAndWeatherData(click: Boolean) {
        val lat = findViewById<TextView>(R.id.latitudeInput)
        val long = findViewById<TextView>(R.id.longitudeInput)
        if (click) {
            fetchWeatherData(lat.text.toString().toFloat(), lat.text.toString().toFloat())
            return
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        val latitude = it.latitude
                        val longitude = it.longitude
                        lat.text = latitude.toString()
                        long.text = longitude.toString()
                        fetchWeatherData(latitude.toFloat(), longitude.toFloat())
                    } ?: Log.e("Location Error", "Failed to get location")
                }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            fetchLocationAndWeatherData(false)
        }
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

    private fun fetchWeatherData(lat: Float, long: Float) {
        Thread {
            val weather = WeatherAPI_Call(lat, long)
            runOnUiThread {
                if (weather != null)
                    updateUI(weather)
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(request: WeatherData) {
        runOnUiThread {
            // Getting the textViews
            val weatherImage: ImageView = findViewById(R.id.imageView)
            val pressure: TextView = findViewById(R.id.slpText)
            val windDirection: TextView = findViewById(R.id.wdText)
            val windSpeed: TextView = findViewById(R.id.wsText)
            val temperature: TextView = findViewById(R.id.temperatureText)
            val time: TextView = findViewById(R.id.timeText)

            pressure.text = request.hourly.pressure_msl[12].toString() + " hPa"
            windDirection.text = request.current_weather.winddirection.toString()
            windSpeed.text = request.current_weather.windspeed.toString() + " km/h"
            temperature.text = request.current_weather.temperature.toString() + "ºC"
            time.text = Calendar.getInstance(TimeZone.getTimeZone(request.timezone)).time.toString()
            val debug1 = request.timezone
            val debug = Calendar.getInstance(TimeZone.getTimeZone(request.timezone)).time.toString()
            // Retrieving weather codes array from resources
            val weatherCodes = WMO_WeatherCode.getWeatherCodesArray(resources)

            val hora = Calendar.getInstance(TimeZone.getTimeZone(request.timezone)).get(Calendar.HOUR_OF_DAY)
            val day = hora in 8..18

            // Retrieving the weather code corresponding to the request
            val wCode = weatherCodes.find { it.code == request.current_weather.weathercode }

            // Setting weather image based on the retrieved weather code
            wCode?.let {
                val wImage = if (it.code == 0 ||
                    it.code == 1 ||
                    it.code == 2
                ) {
                    if (day) it.image + "day" else it.image + "night"
                } else {
                    it.image
                }

                val resID = resources.getIdentifier(wImage, "drawable", packageName)
                if (resID != 0) {
                    weatherImage.setImageResource(resID)
                } else {
                    Log.e("ERROR", "Resource ID for image $wImage not found")
                }
            } ?: Log.e(
                "ERROR",
                "Weather code ${request.current_weather.weathercode} not found in resources"
            )
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
