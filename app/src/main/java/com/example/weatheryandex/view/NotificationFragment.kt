package com.example.weatheryandex.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.weatheryandex.ContentProvider.WeatherContentProvider
import com.example.weatheryandex.ServiceNotification.ServiceNotification
import com.example.weatheryandex.databinding.FragmentNotificationBinding

/**
 * Экран настрйки Notification. При включении этой настройки данные о погоде будут отображаться сверху в Notification (Уведомлении)
 *
 *
 * 1.4 Notification широко используются в музыкальных приложениях. Например. Spotify
 * */


class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private var weather = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startNotification()

    }
// передача данных погоды в Notification
    private fun getWeatherForNotification() {
        val rs = requireActivity().contentResolver.query(
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
    }
// включение отображения Notification
    fun startNotification() {
        binding.buttonStartService.setOnClickListener {
            getWeatherForNotification()
            ServiceNotification.startService(requireContext(), "$weather")
            Toast.makeText(
                requireContext(),
                "Отображение погоды в Notification запущено.",
                Toast.LENGTH_SHORT
            ).show()
        }
// выключение отображения Notification
        binding.buttonStopService.setOnClickListener {
            ServiceNotification.stopService(requireContext())
            Toast.makeText(
                requireContext(),
                "Отображение погоды в Notification остановлено.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {

        fun newInstance() = NotificationFragment()

        }
    }