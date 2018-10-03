package core;

import java.io.*;
import java.net.*;

/**
 * Handles the main API call to OpenWeatherAPI
 *
 * @author Ahmed Sadman Muhib
 */
public class WeatherApi {

    private final String key = "d5042434ef7e9dde3b8a123c5a6774bd";
    private String baseUrl = "http://api.openweathermap.org/data/2.5/%s&APPID=" + key;
    private String query_city;
    private String req_url;

    WeatherApi(String city) {
        this.query_city = city;
        // default request url, can also be changed dynamically using setQueryURL()
        req_url = String.format(baseUrl, "weather?q=" + query_city);
    }

    // get the server response as string
    String getResponse() {
        String res = "";

        try {
            URL url = new URL(req_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Request Failed : HTTP Error Code: " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            StringBuilder response = new StringBuilder();

            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            res = response.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("URL is malformed");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    // dynamically change the API request URL, for multipurpose API calls
    public void setQueryURL(String query) {
        this.req_url = String.format(baseUrl, query + query_city);
    }

    public static void main(String args[]) {
        WeatherApi ap = new WeatherApi("Rangpur,bd");
        String response = ap.getResponse();
        System.out.println(response);
    }
}
