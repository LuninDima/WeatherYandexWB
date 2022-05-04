package com.example.weatheryandex.ServiceNotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.weatheryandex.ContentProvider.WeatherContentProvider
import com.example.weatheryandex.R
import com.example.weatheryandex.databinding.FragmentNotificationBinding

class ServiceNotification : Service() {
    private val CHANNEL_ID = "ForegroundServiceNotification"

    companion object {
        var isNotification = false
        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, ServiceNotification::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
            isNotification = true
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, ServiceNotification::class.java)
            context.stopService(stopIntent)
            isNotification = false
        }


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()

        val notificationIntent = Intent(this, FragmentNotificationBinding::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Погода")
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
        getWeatherForNotification()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    open fun getWeatherForNotification() {
        var weather = ""
        val rs = applicationContext.contentResolver.query(

            WeatherContentProvider.CONTENT_URI, arrayOf(
                WeatherContentProvider._ID,
                WeatherContentProvider.LAT,
                WeatherContentProvider.LON,
                WeatherContentProvider.CONDITION,
                WeatherContentProvider.TEMPERATURE,
                WeatherContentProvider.FEELSLIKE
            ),
            null, null, null
        )
        if (rs != null) {
            if (rs.moveToPosition(0)) {
                weather = String.format(
                    "lat/lon: %s, %s," +
                            " Условия: %s." +
                            " t: %s",
                    rs.getString(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4)
                )
            }
        }
    }

}