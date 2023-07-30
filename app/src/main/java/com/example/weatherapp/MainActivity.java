package com.example.weatherapp;

import static com.example.weatherapp.presenter.Utils.isNetworkAvailable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.example.weatherapp.databinding.ActivityMainBinding;
import com.example.weatherapp.network.ApiHelper;
import com.example.weatherapp.network.WeatherModel;
import com.example.weatherapp.presenter.MainInterface;
import com.example.weatherapp.presenter.MainPresenter;
import com.example.weatherapp.presenter.Utils;
import com.example.weatherapp.view.adapter.CityWeatherAdapter;
import com.example.weatherapp.view.adapter.HourWeatherAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MainInterface {
    private static final int REQUEST_LOCATION = 1;
    private static final String[] cities = {"New York","Singapore","Mumbai","Delhi","Sydney","Melbourne"};
    private String latitude;
    private String longitude;
    private LocationManager locationManager;
    ActivityMainBinding binding;
    private JsonObject weatherData, hourWeatherData;
    private ArrayList<WeatherModel> hourWeatherModelArrayList;
    private ArrayList<WeatherModel> cityWeatherModelArrayList;
    private static final String WEATHER_DATA = "WEATHER_DATA";
    private static final String LATEST_CURRENT_WEATHER_DATA = "LATEST_CURRENT_WEATHER_DATA";
    private static final String LATEST_HOUR_WEATHER_DATA = "LATEST_HOUR_WEATHER_DATA";
    private static final String LATEST_CITIES_WEATHER_DATA = "LATEST_CITIES_WEATHER_DATA";
    private ProgressDialog progressDialog;
    private MainPresenter mainPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mainPresenter = new MainPresenter(this);

        progressDialog = new ProgressDialog(this, R.style.CustomProgressDialog);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        Gson gson = new Gson();
        weatherData = gson.fromJson(getSharedPreferences(WEATHER_DATA, MODE_PRIVATE).getString(LATEST_CURRENT_WEATHER_DATA, null), JsonObject.class);

        Type type = new TypeToken<ArrayList<WeatherModel>>() {}.getType();
        hourWeatherModelArrayList =  gson.fromJson(getSharedPreferences(WEATHER_DATA, MODE_PRIVATE).getString(LATEST_HOUR_WEATHER_DATA, null), type);
        cityWeatherModelArrayList =  gson.fromJson(getSharedPreferences(WEATHER_DATA, MODE_PRIVATE).getString(LATEST_CITIES_WEATHER_DATA, null), type);

        if (cityWeatherModelArrayList == null)
            cityWeatherModelArrayList = new ArrayList<>();

        if (hourWeatherModelArrayList == null)
            hourWeatherModelArrayList = new ArrayList<>();

        if (!isNetworkAvailable(this)){
            if (weatherData != null)
                loadUI();
        }
        else{
            cityWeatherModelArrayList = new ArrayList<>();
            hourWeatherModelArrayList = new ArrayList<>();
            loadData();
        }

        binding.btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable(getApplicationContext()))
                    loadData();
                else
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isNetworkAvailable(getApplicationContext()))
            loadData();
    }



    private void loadData(){
        progressDialog.show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }
    }
    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            binding.main.setVisibility(View.GONE);
            binding.allowPermission.setVisibility(View.VISIBLE);
            progressDialog.dismiss();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            binding.main.setVisibility(View.VISIBLE);
            binding.allowPermission.setVisibility(View.GONE);
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS != null) {
                double lat = locationGPS.getLatitude();
                double longi = locationGPS.getLongitude();
                latitude = String.valueOf(lat);
                longitude = String.valueOf(longi);
                mainPresenter.getCurrentWeather(latitude, longitude);
            } else {
                progressDialog.dismiss();
            }
        }
    }

    @Override
    public void onGetCurrentWeatherSuccess(JsonObject weatherData){
        this.weatherData = weatherData;
        Gson gson = new Gson();
        String jsonString = gson.toJson(this.weatherData);

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(WEATHER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LATEST_CURRENT_WEATHER_DATA, jsonString);
        editor.apply();

        mainPresenter.getHourWeather(latitude, longitude);
    }

    @Override
    public void onGetHourWeatherSuccess(JsonObject hourWeatherData){
        this.hourWeatherData = hourWeatherData;
        this.hourWeatherModelArrayList = mainPresenter.createHourWeatherList(this.hourWeatherData);


        Gson gson = new Gson();
        String json = gson.toJson(this.hourWeatherModelArrayList);

        SharedPreferences sharedPreferences = getSharedPreferences(WEATHER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(LATEST_HOUR_WEATHER_DATA, json);
        editor.apply();
        for (String city : cities){
            mainPresenter.getCityWeather(city);
        }

        loadUI();
    }


    @Override
    public void setCurrentCity(WeatherModel currentCity){
        boolean isExists = false;
        if (cityWeatherModelArrayList.size() > 0)
            for (WeatherModel weatherModel : cityWeatherModelArrayList){
                if (weatherModel.getTime().equals(currentCity.getTime())){
                    isExists = true;
                    break;
                }
            }
        if (!isExists)
            cityWeatherModelArrayList.add(currentCity);
    }

    @SuppressLint("SetTextI18n")
    private void loadUI(){
        String iconUrl = "http://openweathermap.org/img/w/" +  weatherData.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString() + ".png";
        Picasso.get().load(iconUrl).into(binding.imgIcon);

        String Location = weatherData.get("name").getAsString() + ", " + weatherData.get("sys").getAsJsonObject().get("country").getAsString();
        if(Location == null)
            Location = "";
        String MainWeather = weatherData.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("main").getAsString();
        if(MainWeather == null)
            MainWeather = "";
        String SubWeather = weatherData.get("weather").getAsJsonArray().get(0).getAsJsonObject().get("description").getAsString();
        if(SubWeather == null)
            SubWeather = "";
        String Temp = Math.ceil(weatherData.get("main").getAsJsonObject().get("temp").getAsFloat() - 272.15) + "\u00B0" + "C";
        if(Temp == null)
            Temp = "";
        String Feel = "Feels like " + Math.ceil(weatherData.get("main").getAsJsonObject().get("feels_like").getAsFloat() - 272.15) + "\u00B0" + "C";
        if(Feel == null)
            Feel = "";
        String Max = "Max: " + Math.ceil(weatherData.get("main").getAsJsonObject().get("temp_max").getAsFloat() - 272.15) + "\u00B0" + "C";
        if(Max == null)
            Max = "";
        String Min = "Min: " + Math.ceil(weatherData.get("main").getAsJsonObject().get("temp_min").getAsFloat() - 272.15) + "\u00B0" + "C";
        if(Min == null)
            Min = "";
        String Wind = "Wind: " + Math.ceil(weatherData.get("wind").getAsJsonObject().get("speed").getAsFloat() * 3.6) + "km/h";
        if(Wind == null)
            Wind = "";
        String Humidity = "Humidity: " + weatherData.get("main").getAsJsonObject().get("humidity").getAsString() + "%";
        if(Humidity == null)
            Humidity = "";
        String Visibility = "Visibility: " + (weatherData.get("visibility").getAsInt() / 1000) + "km";
        if(Visibility == null)
            Visibility = "";
        String Pressure = "Pressure: " + weatherData.get("main").getAsJsonObject().get("humidity").getAsString() + "hPa";
        if(Pressure == null)
            Pressure = "";
        String SeaLevel = "Sea: " + weatherData.get("main").getAsJsonObject().get("sea_level").getAsString() + "hPa";
        if(SeaLevel == null)
            SeaLevel = "";
        String GroundLevel = "Ground: " + weatherData.get("main").getAsJsonObject().get("grnd_level").getAsString() + "hPa";
        if(GroundLevel == null)
            GroundLevel = "";

        binding.tvLocation.setText(Location);
        binding.tvMainWeather.setText(MainWeather);
        binding.tvSubWeather.setText(SubWeather);
        binding.tvTemp.setText(Temp);
        binding.tvFeel.setText(Feel);
        binding.tvMax.setText(Max);
        binding.tvMin.setText(Min);
        binding.tvWind.setText(Wind);
        binding.tvHumidity.setText(Humidity);
        binding.tvVisibility.setText(Visibility);
        binding.tvPressure.setText(Pressure);
        binding.tvSeaLevel.setText(SeaLevel);
        binding.tvGroundLevel.setText(GroundLevel);

        HourWeatherAdapter hourWeatherAdapter = new HourWeatherAdapter(hourWeatherModelArrayList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL, false);
        binding.rvHourWeather.setLayoutManager(layoutManager);
        binding.rvHourWeather.setAdapter(hourWeatherAdapter);

        Gson gson = new Gson();
        String json = gson.toJson(cityWeatherModelArrayList);

        SharedPreferences sharedPreferences = getSharedPreferences(WEATHER_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LATEST_CITIES_WEATHER_DATA, json);
        editor.apply();

        CityWeatherAdapter cityWeatherAdapter = new CityWeatherAdapter(cityWeatherModelArrayList);
        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        binding.rvCity.setLayoutManager(layoutManager1);
        binding.rvCity.setAdapter(cityWeatherAdapter);
        if (isNetworkAvailable(this))
            progressDialog.dismiss();
    }
}