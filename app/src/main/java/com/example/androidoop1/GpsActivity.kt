package com.example.AndroidOOP1

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class GpsActivity : AppCompatActivity(), LocationListener {

    private val LOG_TAG = "GpsActivity"
    private lateinit var locationManager: LocationManager
    private lateinit var tvLat: TextView
    private lateinit var tvLon: TextView
    private lateinit var tvAlt: TextView
    private lateinit var tvTime: TextView
    private lateinit var btnBack: Button

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gps)


        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        tvLat = findViewById(R.id.tvLat)
        tvLon = findViewById(R.id.tvLon)
        tvAlt = findViewById(R.id.tvAlt)
        tvTime = findViewById(R.id.tvTime)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        updateLocation()
    }

    override fun onPause() {
        super.onPause()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.removeUpdates(this)
        }
    }

    private fun updateLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L,
                    1f,
                    this
                )

                // Получаем последнее известное местоположение
                val lastLocation = getLastKnownLocation()
                if (lastLocation != null) {
                    updateUI(lastLocation)
                    saveToJson(lastLocation)
                }
            } else {
                Toast.makeText(this, "Включите геолокацию в настройках", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun getLastKnownLocation(): Location? {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        val gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        return if (gpsLocation != null && networkLocation != null) {
            if (gpsLocation.time > networkLocation.time) gpsLocation else networkLocation
        } else {
            gpsLocation ?: networkLocation
        }
    }


    override fun onLocationChanged(location: Location) {
        updateUI(location)
        saveToJson(location)
    }

    private fun updateUI(location: Location) {
        tvLat.text = "Latitude: ${String.format("%.6f", location.latitude)}"
        tvLon.text = "Longitude: ${String.format("%.6f", location.longitude)}"
        tvAlt.text = "Altitude: ${String.format("%.2f", location.altitude)} м"
        tvTime.text = "Time: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(location.time))}"
    }


    private fun saveToJson(location: Location) {
        try {
            val json = JSONObject()
            json.put("latitude", location.latitude)
            json.put("longitude", location.longitude)
            json.put("altitude", location.altitude)
            json.put("accuracy", location.accuracy)
            json.put("timestamp", location.time)
            json.put("time_string", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(location.time)))

            val file = File(filesDir, "location.json")
            file.writeText(json.toString(4))
        } catch (e: Exception) {

        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Разрешение получено", Toast.LENGTH_SHORT).show()
                updateLocation()
            } else {
                Toast.makeText(this, "Разрешение отклонено", Toast.LENGTH_SHORT).show()
                tvLat.text = "Permission denied"
                tvLon.text = "Permission denied"
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}