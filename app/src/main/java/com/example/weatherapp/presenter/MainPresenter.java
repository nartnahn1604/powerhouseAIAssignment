package com.example.weatherapp.presenter;

import com.example.weatherapp.network.ApiHelper;
import com.example.weatherapp.network.WeatherModel;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainPresenter {
    private MainInterface mainInterface;
    private JsonObject hourWeatherData;
    private JsonObject weatherData;
    private static final String API_KEY = "5f8622a9fbd391a7b6d40777bdd9790b";

    public MainPresenter(MainInterface mainInterface) {
        this.mainInterface = mainInterface;
    }
    public void getCurrentWeather(String latitude, String longitude){
        Call<JsonObject> call = ApiHelper.apiHelper.getCurrentWeather(latitude, longitude, API_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    weatherData = response.body();
                    mainInterface.onGetCurrentWeatherSuccess(weatherData);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }
    public void getHourWeather(String latitude, String longitude){
        Call<JsonObject> call = ApiHelper.apiHelper.getHourWeather(latitude, longitude, API_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()){
                    hourWeatherData = response.body();
                    mainInterface.onGetHourWeatherSuccess(hourWeatherData);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    public void getCityWeather(String city){
        Call<JsonObject> call = ApiHelper.apiHelper.getWeatherByCity(city, API_KEY);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && !call.isCanceled()){
                    JsonObject item = response.body();
                    String temp = String.valueOf(Math.ceil(item.getAsJsonObject().get("main").getAsJsonObject().get("temp").getAsFloat() - 275.25));
                    String imgIcon = item.getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString();
                    mainInterface.setCurrentCity(new WeatherModel(city, temp, imgIcon));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    public ArrayList<WeatherModel> createHourWeatherList(JsonObject hourWeatherData) {
        ArrayList<WeatherModel> hourWeatherModelArrayList = new ArrayList<>();
        for(JsonElement item : hourWeatherData.get("list").getAsJsonArray()){
            String temp = String.valueOf(Math.ceil(item.getAsJsonObject().get("main").getAsJsonObject().get("temp").getAsFloat() - 275.25));
            String[] times = item.getAsJsonObject().get("dt_txt").getAsString().split(" ");
            String time = "";
            if (times[1].equals("00:00:00"))
                time = Utils.convertDateFormat(times[0]);
            else
                time = times[1].split(":")[0] + ":" + times[1].split(":")[1];
            String imgIcon = item.getAsJsonObject().get("weather").getAsJsonArray().get(0).getAsJsonObject().get("icon").getAsString();
            WeatherModel weatherModel = new WeatherModel(time, temp, imgIcon);
            hourWeatherModelArrayList.add(weatherModel);
        }
        
        return hourWeatherModelArrayList;
    }
}
