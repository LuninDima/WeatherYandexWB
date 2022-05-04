package com.example.weatheryandex.view

import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatheryandex.BroadcastReciever.BroadcastReciever
import com.example.weatheryandex.R
import com.example.weatheryandex.databinding.FragmentNotificationBinding


class SettingsFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)

        binding.buttonStartService.setOnClickListener {
            val i = Intent("my.action")
            context?.sendBroadcast(i)
        }
        return binding.root   }



    companion object {

        fun newInstance() = SettingsFragment()

            }
    }