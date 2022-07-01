package com.example.weatherapp

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationRequest
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.nio.charset.Charset
import java.security.acl.LastOwnerException
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.gms.location.LocationResult

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult.create
import java.net.URI.create
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
     val API_KEY= "2e946f4187793601835e8d14571daf08"
    lateinit var fusedLocation: FusedLocationProviderClient
    lateinit var lat:String
    lateinit var longi:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        fusedLocation=LocationServices.getFusedLocationProviderClient(this)


        checkPermission()



    }

    inner class weatherTask(val lat:String, val longi:String): AsyncTask<String, Void, String>(){
        override fun onPreExecute() {
            binding.loader.visibility=View.VISIBLE
            binding.tempContainer.visibility=View.GONE

        }
        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try {
                response=URL("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$longi&appid=$API_KEY")
                    .readText(Charsets.UTF_8)
            }catch (e:Exception){
                response=null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
                  super.onPostExecute(result)
            try {
                val jsonObj=JSONObject(result)
                val main=jsonObj.getJSONObject("main")
                val sys=jsonObj.getJSONObject("sys")
                val wind=jsonObj.getJSONObject("wind")
                val weather=jsonObj.getJSONArray("weather").getJSONObject(0)
                val updatedAt:Long=jsonObj.getLong("dt")
                val updatedAtText="Updated at: "+
                        SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH)
                            .format(Date(updatedAt*1000))

                val temp=main.getLong("temp")/10
                val minTemp="Min temp: "+ main.getString("temp_min")+"°C"
                val maxTemp="Max temp: "+ main.getString("temp_max")+"°C"
                val pressure=main.getString("pressure")
                val humidity=main.getString("humidity")
                val sunrise=sys.getLong("sunrise")
                val sunset=sys.getLong("sunset")
                val windSpeed= wind.getString("speed")
                val weatherDesc= weather.getString("description")
                val location= jsonObj.getString("name")+"${sys.getString("country")}"

                binding.loader.visibility=View.GONE
                binding.tempContainer.visibility=View.VISIBLE

                binding.location.text=location
                binding.updatedAtText.text=updatedAtText
                binding.status.text=weatherDesc.capitalize()
                binding.temp.text=temp.toString()+"°C"
             /*   binding.minTemp.text=minTemp
                binding.maxTemp.text=maxTemp*/
                binding.sunrise.text= SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                    .format(Date(sunrise*1000))
                binding.sunset.text= SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                    .format(Date(sunset*1000))
                binding.wind.text=windSpeed
                binding.pressure.text=pressure
                binding.humidity.text=humidity
            }catch (e:Exception){
                binding.loader.visibility=View.GONE
               binding.errorText.visibility=View.VISIBLE
            }
        }

    }
    private fun checkPermission() {

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 52)

        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 52)

        }


        fusedLocation.lastLocation.addOnCompleteListener {
            val location:Location=it.result
            if (location!=null){
                lat=location.latitude.toString()
                longi=location.longitude.toString()
                weatherTask(lat,longi).execute()

            }
        }



    }
}