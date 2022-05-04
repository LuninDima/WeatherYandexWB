package com.example.weatheryandex.view

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.weatheryandex.ContentProvider.WeatherContentProvider
import com.example.weatheryandex.R
import com.example.weatheryandex.databinding.FragmentWeatherDetailesBinding
import com.example.weatheryandex.model.*
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException


/**
 * Стартовый экран приложения. Здесь пользователь может получить данные погоды по заданным координатам
 * или по GPS данным. Здесь формируется и отправляется запрос в API яндекс Погоды. В случае успешного
 * ответа данные отображаются на экране и делается запрос на сохранение данных в БД.
 *
 *
 *
 *
 **/

//0e7a90df-1b5c-4bde-b811-478b2ed254a9
//67b1446d-066a-4ac9-a6e4-6b1c7294d5ce
private const val YANDEX_API_KEY =
    "67b1446d-066a-4ac9-a6e4-6b1c7294d5ce" // ключ разработчика API Яндекса (https://developer.tech.yandex.ru/services/). Удалить. после проверки приложения.
private const val MAIN_LINK =
    "https://api.weather.yandex.ru/v2/informers?" // Основной урл запроса для получения погоды
private const val REQUEST_API_KEY =
    "X-Yandex-API-Key" // заголовок ключа APi Яндекс Погода (https://yandex.ru/dev/weather/doc/dg/concepts/about.html).
private const val REQUEST_CODE = 12345
private const val REFRESH_PERIOD = 60L
private const val MINIMAL_DISTANCE = 10000f


open class WeatherDetailesFragment : Fragment() {

    private var _binding: FragmentWeatherDetailesBinding? = null
    private val binding get() = _binding!!
   //- private lateinit var weatherBundle: Weather

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWeatherDetailesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            //-    weatherBundle = Weather(getDefaultCity())
        // Вызываем функцю получения данных по координатам, которые задал пользователь.
        getDataByUserCoordinates()
        //  // Вызываем функцю получения данных по координатам, которые задал пользователь.
        getDataByGPS()


    }
    // функция получения данных по координатам, которые задал пользователь.
    private fun getDataByUserCoordinates() {
        // по нажатию на кнопку "показать погоду". Долгота (lat) и Широта (lon) будут считаны из текстовых полей
        // editTextLatitude и editTextTextLongitude, преобразованы в дробное значение и записаны в переменные lat и lon
        //
        binding.setWeatherToDB.setOnClickListener {
            // считываем Долготу из текстового поля editTextLatitude
            var lat = (binding.editTextLatitude.text.toString()).toDouble()
            // считываем Широту из текстового поля editTextLatitude
            var lon = (binding.editTextTextLongitude.text.toString()).toDouble()
                // вызываем функцию получения данных от удаленного сервера https://api.weather.yandex.ru/v2/informers?
            getWeather(lat, lon)
        }
    }
     //функция получения данных от удаленного сервера https://api.weather.yandex.ru/v2/informers?.
    //Принимает дробные значения Долготы (lat) и Широты (long)
        private fun getWeather(lat: Double, long: Double) {

         // объявляем клиент OkHttpClient()
        val client = OkHttpClient()
            // создаем Билдер для запроса
        val builder: Request.Builder = Request.Builder()
         // заголовок запроса
        builder.header(REQUEST_API_KEY, YANDEX_API_KEY)
            // полный URL запроса
        builder.url(MAIN_LINK + "lat=${lat}&lon=${long}&lang=kk_KZ")
            // инициализируем запрос билдера
        val request: Request = builder.build()
            // ставим запрос в очередь
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            val handler: Handler = Handler()

            // если ответ от сервера пришёл, то выполняем функцию onResponce
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                // сохраняем полученные данные в переменную serverResponce
                val serverResponce: String? = response.body()?.string()
                // елси код ответа 200 или 300 и сам ответ не пустой, то выполняем
                // преобразование полученных данных из Gson объекта в объект класса factDTO.
                // передаем данные в функцию renderData для
                if (response.isSuccessful && serverResponce != null) {
                    var factDTO = Gson().fromJson(serverResponce, WeatherDTO::class.java).fact
                    saveWeatherToDB(factDTO, lat, long)
                    handler.post {
                        renderData(factDTO)

                   }
                } else {
                    Toast.makeText(context, "Ошибка, от сервера получены некорректные данные",
                        Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call, e: IOException) {

                    Toast.makeText(context, "Ошибка, невозможно подключиться к серверу. Проверьте интернет соединение",
                        Toast.LENGTH_LONG).show()
                   // ошибка. Выводится в случае если не пришел никакой ответ.
            }
        })
    }
// cохранение полученных данных в базу данных.
    private fun saveWeatherToDB(factDTO: FactDTO?, lat: Double, long: Double) {
        var rs = requireActivity().contentResolver.query(
            WeatherContentProvider.CONTENT_URI,
            arrayOf(
                WeatherContentProvider._ID,
                WeatherContentProvider.LAT,
                WeatherContentProvider.LON,
                WeatherContentProvider.CONDITION,
                WeatherContentProvider.TEMPERATURE,
                WeatherContentProvider.FEELSLIKE
            ),
            null,
            null,
            null
        )
        var cv = ContentValues()
        cv.put(WeatherContentProvider.LAT, lat)
        cv.put(WeatherContentProvider.LON, long)
        cv.put(WeatherContentProvider.CONDITION, factDTO?.condition)
        cv.put(WeatherContentProvider.TEMPERATURE, factDTO?.temp)
        cv.put(WeatherContentProvider.FEELSLIKE, factDTO?.feels_like)
        requireActivity().contentResolver.update(
            WeatherContentProvider.CONTENT_URI,
            cv, "_id = 1", null
        )
        rs?.requery()
            // передача данных в Notification, если он включен.
        val i = Intent("my.action")
        context?.sendBroadcast(i)
    }
    // вывод полученных данных на экран
    private fun renderData(factDTO: FactDTO?) {

        if (factDTO?.temp == null || factDTO.feels_like == null ||
            factDTO.condition.isNullOrEmpty()
        ) {
            TODO("PROCESS_ERROR")
        } else {
            binding.temperatureValue.text = factDTO.temp.toString()
            binding.feelsLikeValue.text = factDTO.feels_like.toString()
            binding.weatherCondition.text = factDTO.condition
        }


    }

    // функция получения данных по GPS
    private fun getDataByGPS() {
        binding.buttonGetWeather.setOnClickListener { checkPermissoin() }


    }


    // проверяем наличие разрешение на использование GPS. В случае его отсутствия - запрашиваем у пользователя.
    private fun checkPermissoin() {
        activity?.let {
            when {
                ContextCompat.checkSelfPermission(
                    it,
                     Manifest.permission.ACCESS_FINE_LOCATION
                )
                        == PackageManager.PERMISSION_GRANTED -> {
                    getLocation()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)
                -> {
                    showRationaleDialog()
                }
                else -> {
                    requestPermission()
                }
            }
        }
    }

    private fun showRationaleDialog() {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_rationale_title))
                .setMessage(getString(R.string.dialog_rationale_meaasge))
                .setPositiveButton(getString(R.string.dialog_rationale_give_access))
                { _, _ ->
                    requestPermission()

                }
                .setNegativeButton(getString(R.string.dialog_rationale_decline)) { dialog, _ -> dialog.dismiss() }
                .create()
                .show()
        }
    }

    private fun requestPermission() {
        requestPermissions(
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        checkPermissionsResult(requestCode, grantResults)

    }

    private fun checkPermissionsResult(requestCode: Int, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                var grantedPermission = 0
                if ((grantResults.isNotEmpty())) {
                    for (i in grantResults) {
                        if (i == PackageManager.PERMISSION_GRANTED) {
                            grantedPermission++
                        }
                    }
                    if (grantResults.size == grantedPermission) {
                        getLocation()
                    } else {
                        showDialog(
                            getString(R.string.dialog_title_no_gps),
                            getString(R.string.dialog_message_no_gps)
                        )
                    }
                } else {
                    showDialog(
                        getString(R.string.dialog_title_no_gps),
                        getString(R.string.dialog_message_no_gps)
                    )
                }
                return
            }
        }
    }

    private fun showDialog(title: String, message: String) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.dialog_button_close))
                { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }
// получение координат по GPS
    private fun getLocation() {
        activity?.let { context ->
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
// Получить менеджер геолокаций
                val locationManager =
                    context.getSystemService(Context.LOCATION_SERVICE) as
                            LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    val provider =
                        locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    provider?.let {
// Будем получать геоположение через каждые 60 секунд или каждые 100 метров
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            REFRESH_PERIOD,
                            MINIMAL_DISTANCE,
                            onLocationListener
                        )
                    }
                } else {
                    val location =
                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location == null) {
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_location_unknown)
                        )
                    } else {
                        getAddressAsync(context, location)
                        showDialog(
                            getString(R.string.dialog_title_gps_turned_off),
                            getString(R.string.dialog_message_last_known_location)
                        )
                    }
                }
            } else {
                showRationaleDialog()
            }
        }
    }

    private val onLocationListener = LocationListener { location ->
        context?.let {
            getAddressAsync(it, location)
        }
    }
// получение адреса пользователя по GPS координатам
    private fun getAddressAsync(
        context: Context,
        location: Location
    ) {

        val geoCoder = Geocoder(context)
        Thread {
            try {
                val addresses = geoCoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                binding.buttonGetWeather.post {
                    showAddressDialog(addresses[0].getAddressLine(0), location)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }
    // отображение адреса и запрос на получение погоды по этому адрессу
    private fun showAddressDialog(address: String, location: Location) {
        activity?.let {
            AlertDialog.Builder(it)
                .setTitle(getString(R.string.dialog_address_title))
                .setMessage(address)
                .setPositiveButton(getString(R.string.dialog_address_get_weather)) { _, _ ->
                    getWeather(location.latitude, location.longitude)
                }
                .setNegativeButton(getString(R.string.dialog_button_close)) { dialog,
                                                                              _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(): WeatherDetailesFragment {
            return WeatherDetailesFragment()
        }
    }
}




