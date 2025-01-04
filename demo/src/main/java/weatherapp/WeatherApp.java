package weatherapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherApp {

    public static void main(String[] args) {
        // OpenWeatherMap API base URL and API key
        String baseUrl = "https://api.openweathermap.org/data/2.5/weather";
        String apiKey = "e04159bad661e79201f4cc4f9b0d877e"; // Replace with your OpenWeatherMap API key
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the name of the city: ");
        String city = scanner.nextLine();

        try {
            // Build the API request URL
            String requestUrl = String.format("%s?q=%s&appid=%s&units=metric", baseUrl, city, apiKey);

            // Fetch weather data
            String weatherResponse = fetchApiResponse(requestUrl);

            // Format and print the weather data
            printFormattedWeatherData(weatherResponse);

        } catch (IOException e) {
            System.err.println("An I/O error occurred: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("An illegal argument error occurred: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Fetch API response as a string.
     *
     * @param requestUrl The URL to send the HTTP request.
     * @return The API response as a String.
     * @throws IOException if an error occurs while reading the response.
     */
    private static String fetchApiResponse(String requestUrl) throws IOException {
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            StringBuilder responseContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
            }
            return responseContent.toString();
        } else {
            throw new IOException("HTTP response code: " + responseCode);
        }
    }

    /**
     * Extract and print weather data in a readable format.
     *
     * @param jsonResponse The raw JSON response from the weather API.
     */
    private static void printFormattedWeatherData(String jsonResponse) {
        try {
            // Extract weather information from the JSON response
            String cityName = extractValue(jsonResponse, "\"name\":\"", "\"");
            String temperature = extractValue(jsonResponse, "\"temp\":", ",");
            String humidity = extractValue(jsonResponse, "\"humidity\":", ",");
            String weatherState = extractValue(jsonResponse, "\"description\":\"", "\"");
            String windSpeed = extractValue(jsonResponse, "\"speed\":", ",");
            String pressure = extractValue(jsonResponse, "\"pressure\":", ",");

            // Print formatted weather data
            System.out.println("\nWeather Report:");
            System.out.println("City: " + cityName);
            System.out.println("Temperature: " + temperature + "°C");
            System.out.println("Humidity: " + humidity + "%");
            System.out.println("Condition: " + weatherState);
            System.out.println("Wind Speed: " + windSpeed + " m/s");
            System.out.println("Pressure: " + pressure + " hPa");

        } catch (Exception e) {
            System.err.println("Error processing the weather data: " + e.getMessage());
        }
    }

    /**
     * Helper method to extract values from JSON response using simple string
     * operations.
     *
     * @param jsonResponse The JSON response from the API.
     * @param key The key to search for in the JSON.
     * @param delimiter The delimiter to mark the end of the value.
     * @return The extracted value.
     */
    private static String extractValue(String jsonResponse, String key, String delimiter) {
        int startIndex = jsonResponse.indexOf(key) + key.length();
        if (startIndex == -1 + key.length()) {
            return null; // Key not found

        }
        int endIndex = jsonResponse.indexOf(delimiter, startIndex);
        return jsonResponse.substring(startIndex, endIndex).trim().replace("\"", "");
    }
}
