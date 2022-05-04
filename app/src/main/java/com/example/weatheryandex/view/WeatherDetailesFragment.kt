package com.example.weatheryandex.view

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
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
import androidx.core.content.ContextCompat
import com.example.weatheryandex.ContentProvider.WeatherContentProvider
import com.example.weatheryandex.R
import com.example.weatheryandex.databinding.FragmentWeatherDetailesBinding
import com.example.weatheryandex.model.*
import com.google.gson.Gson
import okhttp3.*
import java.io.IOException

//0e7a90df-1b5c-4bde-b811-478b2ed254a9
//67b1446d-066a-4ac9-a6e4-6b1c7294d5ce
private const val YANDEX_API_KEY =
    "0e7a90df-1b5c-4bde-b811-478b2ed254a9" // ключ разработчика API Яндекса (https://developer.tech.yandex.ru/services/). Удалить. после проверки приложения.
private const val MAIN_LINK =
    "https://api.weather.yandex.ru/v2/informers?" // Основной урл запроса для получения погоды
private const val REQUEST_API_KEY =
    "X-Yandex-API-Key" // заголовок ключа APi Яндекс Погода (https://yandex.ru/dev/weather/doc/dg/concepts/about.html).
private const val REQUEST_CODE = 12345
private const val REFRESH_PERIOD = 600L
private const val MINIMAL_DISTANCE = 10000f


open class WeatherDetailesFragment : Fragment() {

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
        var lat = (binding.editTextLatitude.text.toString()).toDouble()
        var lon = (binding.editTextTextLongitude.text.toString()).toDouble()
        getDataByUserCoordinates()
        getDataByGPS()


    }

    private fun getDataByUserCoordinates() {

        binding.setWeatherToDB.setOnClickListener {
            var lat = (binding.editTextLatitude.text.toString()).toDouble()
            var lon = (binding.editTextTextLongitude.text.toString()).toDouble()
            getWeather(lat, lon)
        }
    }

    open fun getWeather(lat: Double, long: Double) {
        val client = OkHttpClient()
        val builder: Request.Builder = Request.Builder()
        builder.header(REQUEST_API_KEY, YANDEX_API_KEY)
        builder.url(MAIN_LINK + "lat=${lat}&lon=${long}&lang=kk_KZ")
        val request: Request = builder.build()
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            val handler: Handler = Handler()

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val serverResponce: String? = response.body()?.string()
                Log.d("mylogs", "$serverResponce")
                if (response.isSuccessful && serverResponce != null) {
                    handler.post {
                        var factDTO = Gson().fromJson(serverResponce, WeatherDTO::class.java).fact
                        saveWeatherToDB(factDTO, lat, long)
                        renderData(factDTO)

                        // елси код ответа 200 или 300 и сам ответ не пустой, то выполняем заполнение данных через renderData
                    }
                } else {
                    Log.d("mylogs", "ошибка")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                Log.d(
                    "mylogs",
                    "ошибка"
                )    // ошибка. Выводится в случае если не пришел никакой ответ.
            }
        })
    }

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

    }

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

    private fun getDataByGPS() {
        binding.buttonGetWeather.setOnClickListener { checkPermissoin() }


    }

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
                var tar = 1
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

    private val onLocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            context?.let {
                getAddressAsync(it, location)
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

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




