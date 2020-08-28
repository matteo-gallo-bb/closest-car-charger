package org.example.location;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_OK;

@Service
public class GeolocationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeolocationService.class);
    private static final String DEFAULT_GEOLOCATION_SERVICE_URL = "https://nominatim.openstreetmap.org/search?format=jsonv2&addressdetails=1&countrycodes=ie&country=Ireland&city=Dublin&street=";

    private final HttpClient httpClient;
    private final JSONParser jsonParser;
    private final String geolocationServiceUrl;

    @Autowired
    public GeolocationService(HttpClient httpClient,
                              JSONParser jsonParser,
                              @Value("${geolocation.service.url:" + DEFAULT_GEOLOCATION_SERVICE_URL + "}") String geolocationServiceUrl) {
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
        this.geolocationServiceUrl = geolocationServiceUrl;
    }

    public Optional<Geolocation> findFromAddress(String address) {
        String requestUrl = geolocationServiceUrl + URLEncoder.encode(address, StandardCharsets.UTF_8);

        // REST call to geolocation API
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(requestUrl))
                .setHeader("User-Agent", "ClosestCarCharger App")
                .timeout(Duration.ofMillis(3000))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HTTP_OK) {
                JSONArray geolocatedPlaces = (JSONArray) jsonParser.parse(response.body());
                // We get the first one
                JSONObject geolocatedPlace = (JSONObject) geolocatedPlaces.get(0);

                return Optional.of(new Geolocation(
                        Double.parseDouble((String) geolocatedPlace.get("lat")),
                        Double.parseDouble((String) geolocatedPlace.get("lon"))));
            }
            return Optional.empty();
        } catch (IOException | ParseException | InterruptedException e) {
            LOGGER.error("Error: " + e.getMessage());
            throw new GeolocationUnexpectedException(e);
        }
    }
}
