package HW7;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class AccuweatherModel implements WeatherModel{
    private static final String PROTOCOL = "https";
    private static final String BASE_HOST = "dataservice.accuweather.com";
    private static final String FORECASTS = "forecasts";
    private  static final String VERSION = "v1";
    private static final String DAILY = "daily";
    private static final String ONE_DAY = "1day";
    private static final String FIVE_DAY = "5day";
    private static final String API_KEY = "nUsk3RouO9yCtUXcH3QqKZeBQEGlYKYL";
    private static final String API_KEY_QUERY_PROPERTY = "apikey";
    private static final String LOCATIONS = "locations";
    private static final String CITIES = "cities";
    private static final String AUTOCOMPLETE = "autocomplete";

    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void getWeather(String city, Period period) throws IOException {
        switch (period) {
            case NOW:
                HttpUrl httpUrl;
                httpUrl = new HttpUrl.Builder()
                        .scheme(PROTOCOL)
                        .host(BASE_HOST)
                        .addPathSegment(FORECASTS)
                        .addPathSegment(VERSION)
                        .addPathSegment(DAILY)
                        .addPathSegment(ONE_DAY)
                        .addPathSegment(detectCityKey(city))
                        .addQueryParameter(API_KEY_QUERY_PROPERTY, API_KEY)
                        .build();

                Request request = new Request.Builder()
                        .url(httpUrl)
                        .build();
                Response oneDayForecastResponse = okHttpClient.newCall(request).execute();
                String weatherResponse = oneDayForecastResponse.body().string();
                System.out.println(weatherResponse);

                break;
            case FIVE_DAYS:

                break;
        }
    }

    public static void main(String[] args) {
        try {
            (new AccuweatherModel()).detectCityKey("Moscow");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String detectCityKey (String city) throws IOException, JsonMappingException {
        HttpUrl httpUrl = new HttpUrl.Builder()
                .scheme(PROTOCOL)
                .host(BASE_HOST)
                .addPathSegment(LOCATIONS)
                .addPathSegment(VERSION)
                .addPathSegment(CITIES)
                .addPathSegment(AUTOCOMPLETE)
                .addQueryParameter(API_KEY_QUERY_PROPERTY, API_KEY)
                .addQueryParameter("q", city)
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .get()
                .addHeader("accept", "application/json")
                .build();

        Response locationResponse = okHttpClient.newCall(request).execute();
        String locationResponseString = locationResponse.body().string();
        System.out.println(locationResponseString);

        String cityKey = objectMapper.readTree(locationResponseString).get(0).at("/Key").asText();

        return cityKey;
    }
}
