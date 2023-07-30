package com.example.weatherapp.presenter;

import com.example.weatherapp.network.WeatherModel;
import com.google.gson.JsonObject;

public interface MainInterface {
    void onGetHourWeatherSuccess(JsonObject hourWeatherData);

    void onGetCurrentWeatherSuccess(JsonObject weatherData);

    void setCurrentCity(WeatherModel weatherModel);
}
