package com.example.weatheryandex.view

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatheryandex.BroadcastReciever.BroadcastReciever
import com.example.weatheryandex.R
import com.example.weatheryandex.databinding.FragmentNotificationBinding
import com.example.weatheryandex.databinding.FragmentSettingsBinding


/**
 * Экран включения BroadCastReciever'а. В случае включения ресивера данные в Notification будут обновлены одновременно
 * с обновлением данных в БД (если ServiceNotification включен).
 *
 * 1.4 BroadCastReciever широко используется в банковских приложениях. К примеру, приложения Тинькоф Банка ловит
 * сообщения SMS_RECEIVED_ACTION.
 *
 */
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    var broadcastReceiver: BroadcastReciever? = BroadcastReciever()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

            //по нажатию на кнопку broadcastReceiver запускается и выполняется  broadcastReceiver
        binding.buttonStartBroadcast.setOnClickListener {
            requireActivity().registerReceiver(broadcastReceiver, IntentFilter("my.action"))
            val i = Intent("my.action")
            context?.sendBroadcast(i)
        }
        return binding.root   }

    override fun onDestroy() {
        super.onDestroy()


    }

    companion object {

        fun newInstance() = SettingsFragment()

            }
    }