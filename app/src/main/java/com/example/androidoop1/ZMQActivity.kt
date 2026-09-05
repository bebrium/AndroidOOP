package com.example.AndroidOOP1

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class ZMQActivity : AppCompatActivity() {

    private lateinit var txtStatus: TextView
    private lateinit var txtServerResponse: TextView
    private lateinit var txtCoords: TextView
    private lateinit var txtSignal: TextView
    private lateinit var txtNetwork: TextView
    private lateinit var btnStart: Button
    private lateinit var btnStop: Button

    private var isServiceRunning = false

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val BACKGROUND_PERMISSION_CODE = 101
        private const val ACTION_STATUS_UPDATE = "BackGroundUpdate"
    }

    private val normalPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.POST_NOTIFICATIONS
    )


    private val backgroundPermission = arrayOf(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val serverStatus = intent?.getStringExtra("Status") ?: ""
            val lat = intent?.getDoubleExtra("Latitude", 0.0) ?: 0.0
            val lon = intent?.getDoubleExtra("Longitude", 0.0) ?: 0.0
            val rsrp = intent?.getIntExtra("RSRP", 0) ?: 0
            val netType = intent?.getStringExtra("NetType") ?: "N/A"
            val cellsCount = intent?.getIntExtra("CellsCount", 0) ?: 0
            val buffered = intent?.getIntExtra("Buffered", 0) ?: 0

            txtServerResponse.text = if (buffered > 0)
                "Сервер: $serverStatus | Буфер: $buffered"
            else
                "Сервер: $serverStatus"

            txtCoords.text = String.format("Координаты: %.5f, %.5f", lat, lon)
            txtSignal.text = "RSRP: $rsrp dBm"
            txtNetwork.text = "Сеть: $netType | Сот: $cellsCount"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zmq)

        txtStatus = findViewById(R.id.txtStatus)
        txtServerResponse = findViewById(R.id.txtServerResponse)
        txtCoords = findViewById(R.id.txtCoords)
        txtSignal = findViewById(R.id.txtSignal)
        txtNetwork = findViewById(R.id.txtNetwork)
        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)

        btnStart.setOnClickListener {
            if (checkNormalPermissions()) startTelemetryService()
            else requestNormalPermissions()
        }

        btnStop.setOnClickListener {
            stopTelemetryService()
        }

        updateStatusText()
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter(ACTION_STATUS_UPDATE))
        updateStatusText()
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    }

    private fun checkNormalPermissions(): Boolean {
        return normalPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestNormalPermissions() {
        ActivityCompat.requestPermissions(this, normalPermissions, PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                val allGranted = grantResults.isNotEmpty() &&
                        grantResults.all { it == PackageManager.PERMISSION_GRANTED }
                if (allGranted) {
                    Toast.makeText(this, "Основные разрешения получены", Toast.LENGTH_SHORT).show()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ActivityCompat.requestPermissions(
                            this,
                            backgroundPermission,
                            BACKGROUND_PERMISSION_CODE
                        )
                    } else {
                        startTelemetryService()
                    }
                } else {
                    Toast.makeText(this, "Разрешения отклонены", Toast.LENGTH_LONG).show()
                    txtStatus.text = "Статус: Нет прав"
                }
                updateStatusText()
            }

            BACKGROUND_PERMISSION_CODE -> {

                val granted = grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (!granted) {
                    Toast.makeText(
                        this,
                        "Фоновая геолокация не разрешена — сервис будет работать, пока приложение открыто",
                        Toast.LENGTH_LONG
                    ).show()
                }
                startTelemetryService()
            }
        }
    }

    private fun updateStatusText() {
        if (checkNormalPermissions()) {
            txtStatus.text = if (isServiceRunning) "Статус: Запущен" else "Статус: Готов к запуску"
            txtStatus.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            txtStatus.text = "Статус: Нужны разрешения"
            txtStatus.setTextColor(getColor(android.R.color.holo_red_dark))
        }
    }

    private fun startTelemetryService() {
        if (isServiceRunning) {
            Toast.makeText(this, "Сервис уже запущен", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, TelemetryService::class.java)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            isServiceRunning = true
            txtStatus.text = "Статус: Запущен"
            txtServerResponse.text = "Сервер: Ожидание..."
            txtCoords.text = "Координаты: -"
            txtSignal.text = "RSRP: -"
            txtNetwork.text = "Сеть: -"
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка запуска: ${e.message}", Toast.LENGTH_LONG).show()
            txtStatus.text = "Статус: Ошибка"
        }
    }

    private fun stopTelemetryService() {
        if (!isServiceRunning) {
            Toast.makeText(this, "Сервис не запущен", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, TelemetryService::class.java)
        stopService(intent)
        isServiceRunning = false
        txtStatus.text = "Статус: Остановлен"
        txtServerResponse.text = "Сервер: -"
        txtCoords.text = "Координаты: -"
        txtSignal.text = "RSRP: -"
        txtNetwork.text = "Сеть: -"
    }
}