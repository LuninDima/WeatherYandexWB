package com.example.weatheryandex.view

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.weatheryandex.BroadcastReciever.BroadcastReciever
import com.example.weatheryandex.R
import com.example.weatheryandex.databinding.ActivityMainBinding

/**04.05.2022
 * Приложение получает по заданным координатам данные о погоде от Yandex API погоды (https://api.weather.yandex.ru/)
 * Приложение построено по принципу  Single-Activity. MainActivity.kt является стартовым активити-контейнером,
 * которое отображает фрагменты с экранами приложения. Пользователь может переключаться между экранами с помощью кнопок.
 *
 *  1.4 Активити используется во всех приложениях, где необходим пользовательский интерфейс и взаимодействие с пользователем. Например, приложение "Яндекс.Погода"
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        savedInstanceState.let {
            // инициализация supportFragmentManager для открытия экрана fragment_weather_detailes.xml.
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WeatherDetailesFragment.newInstance())
                .commitAllowingStateLoss()
        }
        moveToFragmentsByButtons()

    }

    private fun moveToFragmentsByButtons() {
        moveToSettingsFragment()
        moveToShareFragment()
        moveToWeatherDetailesFragment()
        moveToNotificationFragment()
    }
    // инициализация supportFragmentManager для открытия Стартового экрана (fragment_weather_detailes.xml)
    private fun moveToSettingsFragment() {
        binding.buttonSettings.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment.newInstance())
                .commitAllowingStateLoss()
        }
    }

    private fun moveToShareFragment() {
        binding.buttonShare.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShareFragment.newInstance())
                .commitAllowingStateLoss()
        }
    }

    private fun moveToWeatherDetailesFragment() {
        binding.buttonToMain.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WeatherDetailesFragment.newInstance())
                .commitAllowingStateLoss()
        }
    }

    private fun moveToNotificationFragment() {
        binding.buttonNotification.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotificationFragment.newInstance())
                .commitAllowingStateLoss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}