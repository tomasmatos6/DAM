package com.example.optional

import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val deviceInfoTextView = findViewById<TextView>(R.id.editTextTextMultiLine2)

        val deviceModel = Build.MODEL
        val deviceManufacturer = Build.MANUFACTURER
        val deviceBrand = Build.BRAND
        val deviceProduct = Build.PRODUCT
        val sdkVersion = Build.VERSION.SDK_INT
        val releaseVersion = Build.VERSION.RELEASE

        val deviceInfo = "Model: $deviceModel" +
                "\nManufacturer: $deviceManufacturer" +
                "\nBrand: $deviceBrand" +
                "\nProduct: $deviceProduct" +
                "\nSDK Version: $sdkVersion" +
                "\nRelease Version: $releaseVersion"

        deviceInfoTextView.text = deviceInfo
    }
}