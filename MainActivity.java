package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView currentWeatherText = (TextView) findViewById(R.id.currentWeather);
        TextView weeklyForecastInformationText = (TextView) findViewById(R.id.weeklyForecastInformation);
        TextView hourlyForecastInformationText = (TextView) findViewById(R.id.hourlyForecastInformation);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        WeatherData austinData = new WeatherData();
        Thread austinThread = new Thread(austinData);
        austinThread.run();

        currentWeatherText.setText(austinData.getCurrentWeather());
        weeklyForecastInformationText.setText(austinData.getWeatherByDay());
        hourlyForecastInformationText.setText(austinData.getWeatherByHour());


        String uri = austinData.findIcon();

        int imageResource = getResources().getIdentifier(uri, null, getPackageName());

        ImageView imageView= (ImageView)findViewById(R.id.currentWeatherIcon);
        Drawable res = getResources().getDrawable(imageResource);
        imageView.setImageDrawable(res);
    }
}