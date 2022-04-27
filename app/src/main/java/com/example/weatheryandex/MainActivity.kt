package com.example.weatheryandex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
                .commitAllowingStateLoss()}

                  moveToSettingsFragment()
        moveToShareFragment()
        moveToShareFragment()
    }

    fun moveToSettingsFragment(){
        binding.buttonSettings.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SettingsFragment.newInstance())
                .commitAllowingStateLoss()
        }
    }

    fun moveToShareFragment(){
        binding.buttonShare.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ShareFragment.newInstance())
                .commitAllowingStateLoss()
        }
    }

}