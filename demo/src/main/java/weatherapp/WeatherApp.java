package weatherapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.json.JSONObject;

public class WeatherApp {

    public static void main(String[] args) {
        // OpenWeatherMap API URL and API key
        String apiKey = "e04159bad661e79201f4cc4f9b0d877e"; // Replace with your OpenWeatherMap API key
        String city = "Ahmedabad"; // City name
        String requestUrl = String.format(
                "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric",
                city, apiKey
        );

        try {
            // Build the URI and URL
            URI requestUri = new URI(requestUrl);
            URL url = requestUri.toURL();

            // Open HTTP connection
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Get the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder responseContent = new StringBuilder();
                String line;

                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream())
                )) {
                    while ((line = reader.readLine()) != null) {
                        responseContent.append(line);
                    }
                }

                // Parse and display JSON response
                displayWeatherData(responseContent.toString());

            } else {
                System.out.println("Error: Unable to fetch weather data. HTTP Response Code: " + responseCode);
            }

        } catch (URISyntaxException e) {
            System.err.println("A URI syntax error occurred: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("An I/O error occurred: " + e.getMessage());
        }
    }

    /**
     * Parse and display weather data from JSON response.
     */
    private static void displayWeatherData(String jsonResponse) {
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);

            // Extract main weather information
            JSONObject main = jsonObject.getJSONObject("main");
            double temperature = main.getDouble("temp");
            double feelsLike = main.getDouble("feels_like");
            int humidity = main.getInt("humidity");

            // Extract weather description
            JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
            String description = weather.getString("description");

            // Extract city and country information
            String cityName = jsonObject.getString("name");
            String country = jsonObject.getJSONObject("sys").getString("country");

            // Print weather information in a structured format
            System.out.println("\nWeather Report:");
            System.out.println("City: " + cityName + ", " + country);
            System.out.println("Temperature: " + temperature + "°C");
            System.out.println("Feels Like: " + feelsLike + "°C");
            System.out.println("Humidity: " + humidity + "%");
            System.out.println("Condition: " + description);

        } catch (org.json.JSONException e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
        } catch (NullPointerException e) {
            System.err.println("A null pointer error occurred: " + e.getMessage());
        }
    }
}
