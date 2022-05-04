package com.example.weatheryandex.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatheryandex.ContentProvider.WeatherContentProvider
import com.example.weatheryandex.databinding.FragmentShareBinding


class ShareFragment : Fragment() {
    private var _binding: FragmentShareBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentShareBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getWeather()

    }

    private fun getWeather() {
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
        binding.buttonGetWeather.setOnClickListener {
            if (rs != null) {
                if (rs.moveToPosition(0)) {
                    binding.cityCoordinates.text = String.format(
                        "Широта/Долгота: %s, %s",
                        rs.getString(1),
                        rs.getString(2)
                    )
                    binding.weatherCondition.text = rs.getString(3)
                    binding.temperatureValue.text = rs.getString(4)
                    binding.feelsLikeValue.text = rs.getString(5)
                }
            }
        }
    }

    companion object {

        fun newInstance() = ShareFragment()

    }
}