package com.example.weatheryandex.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatheryandex.R
import com.example.weatheryandex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        savedInstanceState.let {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WeatherDetailesFragment.newInstance())
                .commitAllowingStateLoss()
        }
        moveToFragmentsByButtons()
    }

    private fun moveToFragmentsByButtons() {
        moveToSettingsFragment()
        moveToShareFragment()
        moveToShareFragment()
        moveToWeatherDetailesFragment()
        moveToNotificationFragment()
    }

    fun moveToSettingsFragment() {
        binding.buttonSettings.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment.newInstance())
                .commitAllowingStateLoss()
        }
    }

    fun moveToShareFragment() {
        binding.buttonShare.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShareFragment.newInstance())
                .commitAllowingStateLoss()
        }
    }

    fun moveToWeatherDetailesFragment() {
        binding.buttonToMain.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WeatherDetailesFragment.newInstance())
                .commitAllowingStateLoss()
        }
    }

    fun moveToNotificationFragment() {
        binding.buttonNotification.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, NotificationFragment.newInstance())
                .commitAllowingStateLoss()
        }
    }
}