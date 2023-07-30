package com.example.weatherapp.network;

import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiHelper {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    OkHttpClient.Builder builder = new OkHttpClient.Builder()
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor);

    ApiHelper apiHelper = new Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(builder.build())
            .build()
            .create(ApiHelper.class);

    @GET("weather")
    Call<JsonObject> getCurrentWeather(@Query("lat") String lat, @Query("lon") String longi, @Query("appid") String apikey);
    @GET("weather")
    Call<JsonObject> getWeatherByCity(@Query("q") String city, @Query("appid") String apikey);
    @GET("forecast")
    Call<JsonObject> getHourWeather(@Query("lat") String lat, @Query("lon") String longi, @Query("appid") String apikey);

}
