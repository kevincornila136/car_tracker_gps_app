import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ApiService {

    private static final String BASE_URL = "http://a.b.c.d:8080/api/location"; //IP SERVER TAILSCALE

    private Gson gson = new Gson();

    public List<LocationData> getRoute(String vehicleId) throws Exception {

        String json = sendGet(
                BASE_URL + "/route/" + vehicleId
        );

        return gson.fromJson(
                json,
                new TypeToken<List<LocationData>>(){}.getType()
        );
    }

    public LocationData getCurrent(String vehicleId) throws Exception {

        String json = sendGet(
                BASE_URL + "/current/" + vehicleId
        );

        return gson.fromJson(json, LocationData.class);
    }

    public StatsResponse getStats(String vehicleId) throws Exception {

        String json = sendGet(
                BASE_URL + "/stats/" + vehicleId
        );

        return gson.fromJson(json, StatsResponse.class);
    }

    public LocationData getPositionAt(
            String vehicleId,
            long timestamp
    ) throws Exception {

        String json = sendGet(
                BASE_URL + "/position/"
                        + vehicleId
                        + "?timestamp="
                        + timestamp
        );

        return gson.fromJson(json, LocationData.class);
    }

    private String sendGet(String urlStr) throws Exception {

        URL url = new URL(urlStr);

        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );

        StringBuilder response = new StringBuilder();

        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();

        return response.toString();
    }

    public static class StatsResponse {

        public double averageSpeed;
        public double totalDistance;
    }
}
