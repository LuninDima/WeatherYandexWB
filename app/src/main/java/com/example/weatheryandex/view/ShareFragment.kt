package com.example.weatheryandex.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatheryandex.ContentProvider.WeatherContentProvider
import com.example.weatheryandex.databinding.FragmentShareBinding

/**
 * Пример использования Content Provider'а (WeatherContentProvider)
 *
 * 1.4 Content Provider часто используется в файловых менеджерах, социальных сетях и т.д. Например, FaceBook
 * https://developers.facebook.com/docs/reference/android/current/class/FacebookContentProvider/
 * Content Provider хорошо подходит для экосистемы приложений, когда все нужные данные хранятся внутри устройтсва.
 * Например, система учета клиентов с помощью контент провайдера делиться данными с системой аналитики и учета доходов/расходов
 * Разделение приложений на несколько узкоспециализированных позволяет упростить пользовательский интерфейс каждого приложения,
 * ускорить загрузку приложения и упростить разработку каждого приложения.
 */
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

        getWeatherByButton()

    }
    private fun getWeatherByButton() {
    // по клику на кнопку "Отобразить сохраненные данные" делается запрос на получение данных из базы данных через contentResolver
        binding.buttonGetWeather.setOnClickListener {
            val rs = requireActivity().contentResolver.query(
                WeatherContentProvider.CONTENT_URI, arrayOf(
                ),
                null, null, null
            )
                // если contentResolver не пустой, то выбирается элемент с позицией 0 и его данные заполняются в текстовые поля на экране.
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