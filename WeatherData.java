package com.example.weatherapp;

import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.Scanner;
import android.content.res.Resources;

/*
1. Show current weather conditions (temperature, humidity, wind speed, pressure) in Austin.
2. Show hour-by-hour temperature for each of the next 5 hours in Austin.
3. Show predicted temperature for upcoming week on day-by-day basis in Austin.
 */

public class WeatherData implements Runnable{
    private String openWeatherOut;
    private String openWeatherOutDaily;
    private String curTemp;
    private String curHumid;
    private String curWindSpeed;
    private String curPressure;
    private String code;
    private ArrayList<String> tempHourly = new ArrayList<String>();
    private ArrayList<String> time = new ArrayList<String>();
    private ArrayList<String> date = new ArrayList<String>();
    private ArrayList<String> dailyHigh = new ArrayList<>();
    private ArrayList<String> dailyLow = new ArrayList<>();
    private ArrayList<String> icon = new ArrayList<>();



    public WeatherData(){
        getWeatherData();
    }
    public void run(){
        getWeatherData();
        try {
            parseWeatherData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void getWeatherData(){
        try {
            System.out.println("Getting weather data...");
            URL openWeather = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=30.267153&lon=-97.743057&appid=f28773c4683db8396a5607ea462867b0&units=imperial&exclude=minutely");
            URL openWeatherDaily = new URL("https://api.openweathermap.org/data/2.5/onecall?lat=30.267153&lon=-97.743057&appid=f28773c4683db8396a5607ea462867b0&units=imperial&exclude=current,minutely,hourly,alerts");
            HttpURLConnection connection = (HttpURLConnection) openWeather.openConnection();
            HttpURLConnection connectionDaily = (HttpURLConnection) openWeatherDaily.openConnection();
            connection.setRequestMethod("GET");
            connectionDaily.setRequestMethod("GET");
            connection.connect();
            connectionDaily.connect();
            openWeatherOut = "";
            openWeatherOutDaily = "";

            Scanner scnr = new Scanner(openWeather.openStream());
            System.out.println("New Line From OW");
            while(scnr.hasNext()) {
                openWeatherOut += scnr.nextLine();

            }
            scnr.close();
            Scanner scnrDaily = new Scanner(openWeatherDaily.openStream());
            while(scnrDaily.hasNext()) {
                openWeatherOutDaily += scnrDaily.nextLine();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void parseWeatherData() throws JSONException {
        JSONObject obj = new JSONObject(openWeatherOut);
        String currentString = obj.getString("current");
        JSONObject current = new JSONObject(currentString);
        curTemp = current.getString("temp");
        curHumid = current.getString("humidity");
        curPressure = current.getString("pressure");
        curWindSpeed = current.getString("wind_speed");
        String weatherString = current.getString("weather");

        JSONArray weatherArr = new JSONArray(weatherString);
        JSONObject weatherObj = weatherArr.getJSONObject(0);
        code = weatherObj.getString("id");

        JSONArray arrHourly = obj.getJSONArray("hourly");

        for(int i = 0; i < 5; i++){
            tempHourly.add(arrHourly.getJSONObject(i).getString("temp"));
            String timestampString = arrHourly.getJSONObject(i).getString("dt");
            Integer timestamp = Integer.parseInt(timestampString);
            Date timeConvert=new Date((long)(timestamp)*1000);
            DateFormat dateFormat = new SimpleDateFormat("hh:mm");
            String strDate = dateFormat.format(timeConvert);
            time.add(strDate);

        }


        JSONObject dailyObj = new JSONObject(openWeatherOutDaily);
        String dailyString = dailyObj.getString("daily");
        JSONArray arrDaily = dailyObj.getJSONArray("daily");



        for(int i = 0; i < 7; i++){
            String curDayTemp = arrDaily.getJSONObject(i).getString("temp");
            String curDate = arrDaily.getJSONObject(i).getString("dt");
            JSONObject curDayTempObj = new JSONObject(curDayTemp);
            dailyLow.add(curDayTempObj.getString("min"));
            dailyHigh.add(curDayTempObj.getString("max"));
            //System.out.println("Day " + (i + 1) + "  High: " + dailyHigh.get(i) + " Low: " + dailyLow.get(i));

            String timestampString = curDate;
            Integer timestamp = Integer.parseInt(timestampString);
            Date timeConvert=new Date((long)(timestamp)*1000);
            DateFormat dateFormat = new SimpleDateFormat("MM/dd");
            String strDate = dateFormat.format(timeConvert);

            date.add(strDate);


        }

        }
    public String getCurrentWeather(){
        return("Current Temperature: " + curTemp + "\u00B0" + "F" + "\n" + "Humidity: " + curHumid + "%" + "\n" + "Current Wind Speed: " + curWindSpeed + " MPH" + "\n" + "Current Pressure: " + curPressure + " Millibars" );
    }
    public String getWeatherByDay(){
        String format = "";
        for(int i = 0; i < 7; i++){
            format += date.get(i) + "\n" + "High: " + dailyHigh.get(i) + "\u00B0" + "F" + "\n" + "Low: " + dailyLow.get(i) + "\u00B0" + "F" + "\n";
        }
        System.out.println(format);
        return(format);
    }
    public String getWeatherByHour(){
        String format = "";
        for(int i = 0; i < 5; i++){
            format += time.get(i) + ": " + tempHourly.get(i) + "\u00B0" + "F" +  "\n";
        }
        System.out.println(format);
        return(format);
    }

    public String findIcon(){
        if(code.substring(0,1).equals("2")){
            return ("@drawable/_11d");
        }
        if(code.substring(0,1).equals("3")){
            return ("@drawable/_9d");
        }
        if(code.substring(0,1).equals("5")){
            return ("@drawable/_10d");
        }
        if(code.substring(0,1).equals("6")){
            return ("@drawable/_13d");
        }
        if(code.substring(0,1).equals("7")){
            return ("@drawable/_50d");
        }
        if(code.equals("800")){
            return ("@drawable/_1d");
        }
        if(code.substring(0,2).equals("80")){
            return ("@drawable/_4d");
        }
        return "error";
    }
}
