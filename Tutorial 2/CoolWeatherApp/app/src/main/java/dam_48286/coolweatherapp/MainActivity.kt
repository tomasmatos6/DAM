package dam_48286.coolweatherapp

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Calendar
import java.util.TimeZone


class MainActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val viewModel: WeatherViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocationAndWeatherData(false)

        val myButton: Button = findViewById(R.id.updateButton)
        myButton.setOnClickListener {
            fetchLocationAndWeatherData(true)
        }

        viewModel.weatherData.observe(this) { weatherData ->
            updateUI(weatherData)
        }
    }

    private fun fetchLocationAndWeatherData(click: Boolean) {
        val lat = findViewById<TextView>(R.id.latitudeInput)
        val long = findViewById<TextView>(R.id.longitudeInput)
        if (click) {
            viewModel.fetchWeatherData(lat.text.toString().toFloat(), long.text.toString().toFloat())
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
                        viewModel.fetchWeatherData(latitude.toFloat(), longitude.toFloat())
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
