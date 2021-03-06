package core;

import org.json.*;

import javax.swing.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * The Weather information data model
 * @author Ahmed Sadman Muhib
 */
public class WeatherData {

    private String city_name;
    private String weather_type;
    private String weather_desc;
    private int humidity;
    private float temperature;
    private float temperature_min;
    private float temperature_max;
    private float pressure;
    private int weather_id;

    public void updateData(String query_city) {
        WeatherApi api = new WeatherApi(query_city);
        JSONObject response = new JSONObject(api.getResponse());

        // info like temperature, pressure etc
        JSONObject weather_main = response.getJSONObject("main");

        // weather description, like "light rain", "thunderstorm"
        JSONObject weather_info = (response.getJSONArray("weather")).getJSONObject(0);

        this.city_name = response.getString("name");
        this.temperature = weather_main.getFloat("temp");
        this.pressure = weather_main.getFloat("pressure");
        this.humidity = weather_main.getInt("humidity");
        this.temperature_min = weather_main.getFloat("temp_min");
        this.temperature_max = weather_main.getFloat("temp_min");
        this.weather_type = weather_info.getString("main");
        this.weather_desc = weather_info.getString("description");
        this.weather_id = weather_info.getInt("id");
    }

    public double kelvinToCelsius(double temperature) {
        return temperature - 273;
    }

    void showData() {
        System.out.println("City Name: " + this.city_name);
        System.out.println("Temp: " + kelvinToCelsius(this.temperature));
        System.out.println("Temp (Min): " + kelvinToCelsius(this.temperature_min));
        System.out.println("Temp (Max): " + kelvinToCelsius(this.temperature_max));
        System.out.println("Weather Type: " + this.weather_type);
        System.out.println("Weather Desc: " + this.weather_desc);
        System.out.println("Pressure: " + this.pressure);
        System.out.println("Humidity: " + this.humidity);
        System.out.println("Weather ID: " + this.weather_id);
    }

    public ImageIcon getWeatherIcon() {
        String image_path = "images/";
        String ico = null;
        ImageIcon img = null;

        if (this.weather_id >=  200 && this.weather_id < 300)
            ico = "thunderstorm.png";
        else if (this.weather_id >= 300 && this.weather_id < 400)
            ico = "drizzle.png";
        else if (this.weather_id >= 500 && this.weather_id < 600)
            ico = "rain.png";
        else if (this.weather_id >= 700 && this.weather_id < 800)
            ico = "atmosphere.png";
        else if (this.weather_id == 800)
            ico = "clear.png";
        else
            ico = "cloud.png";

        img = new ImageIcon(image_path + ico);
        return img;
    }

    public String getCity_name() {
        return city_name;
    }

    public String getWeather_type() {
        return weather_type;
    }

    public String getWeather_desc() {
        return weather_desc;
    }

    public double getTemperature() {
        return kelvinToCelsius(temperature);
    }

    public double getTemperature_min() {
        return temperature_min;
    }

    public double getTemperature_max() {
        return temperature_max;
    }

    public double getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public static String roundValue(double val) {
        DecimalFormat df = new DecimalFormat("####.##");
        df.setRoundingMode(RoundingMode.CEILING);
        return df.format(val);
    }

    public static void main(String args[]) {
        WeatherData ob = new WeatherData();
        ob.updateData("Gazipur");
        ob.showData();
    }
}
