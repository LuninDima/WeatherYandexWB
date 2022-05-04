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

    private fun getWeatherForNotification() {

        val rs = requireActivity().contentResolver.query(
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

    private fun startNotification() {
        binding.buttonStartService.setOnClickListener {
            getWeatherForNotification()
            ServiceNotification.startService(requireContext(), "$weather")
            Toast.makeText(requireContext(), "Отображение погоды в Notification запущено.", Toast.LENGTH_SHORT).show()
        }

        binding.buttonStopService.setOnClickListener {
            ServiceNotification.stopService(requireContext())
            Toast.makeText(requireContext(), "Отображение погоды в Notification остановлено.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {

        fun newInstance() = NotificationFragment()

    }
}