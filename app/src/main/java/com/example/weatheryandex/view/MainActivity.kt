package com.example.weatheryandex.view

import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weatheryandex.BroadcastReciever.BroadcastReciever
import com.example.weatheryandex.R
import com.example.weatheryandex.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    var broadcastReceiver: BroadcastReciever? = BroadcastReciever()
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
        registerReceiver(broadcastReceiver, IntentFilter("my.action"))
        registerReceiver(broadcastReceiver, IntentFilter("android.intent.action.BOOT_COMPLETED"))
        registerReceiver(broadcastReceiver, IntentFilter("android.intent.action.PROVIDER_CHANGED"))





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

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}