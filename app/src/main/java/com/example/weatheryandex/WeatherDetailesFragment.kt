package com.example.weatheryandex

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatheryandex.databinding.FragmentWeatherDetailesBinding
import com.example.weatheryandex.model.City
import com.example.weatheryandex.model.Weather
import com.example.weatheryandex.model.WeatherDTO
import com.example.weatheryandex.model.getDefaultCity
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

private const val YANDEX_API_KEY = "67b1446d-066a-4ac9-a6e4-6b1c7294d5ce" // ключ разработчика API Яндекса (https://developer.tech.yandex.ru/services/). Удалить. после проверки приложения.
private const val MAIN_LINK = "https://api.weather.yandex.ru/v2/informers?" // Основной урл запроса для получения погоды
private const val REQUEST_API_KEY = "X-Yandex-API-Key" // заголовок ключа APi Яндекс Погода (https://yandex.ru/dev/weather/doc/dg/concepts/about.html).

class WeatherDetailesFragment : Fragment() {

    private var _binding: FragmentWeatherDetailesBinding? = null
    private val binding get() = _binding!!
    private lateinit var weatherBundle: Weather

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherDetailesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherBundle = Weather(getDefaultCity())
        getWeather()

    }

    companion object {
        fun newInstance(): WeatherDetailesFragment {
            val fragment = WeatherDetailesFragment()
            return fragment
        }
    }

    private fun getWeather() {
        val client = OkHttpClient()
        val builder: Request.Builder = Request.Builder()
        builder.header(REQUEST_API_KEY, YANDEX_API_KEY)
        builder.url(MAIN_LINK + "lat=${weatherBundle.city.lat}&lon=${weatherBundle.city.long}&lang=kk_KZ")
        val request: Request = builder.build()
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            val handler: Handler = Handler()

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val serverResponce: String? = response.body()?.string()
                if (response.isSuccessful && serverResponce != null) {
                    handler.post {
                        renderData(Gson().fromJson(serverResponce, WeatherDTO::class.java))
                        // елси код ответа 200 или 300 и сам ответ не пустой, то выполняем заполнение данных через renderData
                    }
                } else {
                    // ошибка
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                // ошибка. Выводится в случае если не пришел никакой ответ.
            }
        })
    }

    private fun renderData(weatherDTO: WeatherDTO) {
        val fact = weatherDTO.fact

        if (fact == null || fact.temp == null || fact.feels_like == null ||
            fact.condition.isNullOrEmpty()
        ) {
            TODO("PROCESS_ERROR")
        } else {
            val city = weatherBundle.city
            binding.cityCoordinates.text = String.format(
                getString(R.string.city_coordinates),
                city.lat.toString(),
                city.long.toString()
            )
            binding.temperatureValue.text = fact.temp.toString()
            binding.feelsLikeValue.text = fact.feels_like.toString()
            binding.weatherCondition.text = fact.condition
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}




