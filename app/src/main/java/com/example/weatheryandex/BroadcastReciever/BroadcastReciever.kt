package com.example.weatheryandex.BroadcastReciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.weatheryandex.ContentProvider.WeatherContentProvider
import com.example.weatheryandex.ServiceNotification.ServiceNotification


class BroadcastReciever : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        var ser = ServiceNotification.isNotification
        if (ServiceNotification.isNotification) {
            var weather = ""
            val rs = context.contentResolver.query(
                WeatherContentProvider.CONTENT_URI, arrayOf(),
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

            ServiceNotification.startService(context, weather)
            StringBuilder().apply {
                append("Notification обновлен\n")
                append("Action: ${intent.action}")
                toString().also {
                    Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                }
            }

        }
    }
}


