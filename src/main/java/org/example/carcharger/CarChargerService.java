package org.example.carcharger;

import org.example.location.Geolocation;
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
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_OK;

@Service
public class CarChargerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CarChargerService.class);
    private static final String DEFAULT_CARCHARGER_SERVICE_URL = "https://api.openchargemap.io/v3/poi/?output=json&countrycode=IE&maxresults=1&compact=true&verbose=false&distance=10&distanceunit=KM";

    private final HttpClient httpClient;
    private final JSONParser jsonParser;
    private final String carChargerServiceUrl;

    @Autowired
    public CarChargerService(HttpClient httpClient,
                             JSONParser jsonParser,
                             @Value("${carcharger.service.url:" + DEFAULT_CARCHARGER_SERVICE_URL + "}") String carChargerServiceUrl) {
        this.httpClient = httpClient;
        this.jsonParser = jsonParser;
        this.carChargerServiceUrl = carChargerServiceUrl;
    }

    public Optional<CarCharger> findClosest(Geolocation currentPosition) {
        String requestUrl = carChargerServiceUrl +
                "&latitude=" + currentPosition.latitude() +
                "&longitude=" + currentPosition.longitude();

        // REST call to car charger API
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(requestUrl))
                .setHeader("User-Agent", "ClosestCarCharger App")
                .setHeader("X-API-Key", "ClosestCarCharger App")
                .timeout(Duration.ofMillis(3000))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HTTP_OK) {
                JSONArray carChargers = (JSONArray) jsonParser.parse(response.body());
                // We get the first one
                JSONObject carCharger = (JSONObject) carChargers.get(0);
                JSONObject addressInfo = (JSONObject) carCharger.get("AddressInfo");

                return Optional.of(new CarCharger(
                        (String) addressInfo.get("Title"),
                        ((Double) addressInfo.get("Distance")).floatValue(),
                        (String) addressInfo.get("AddressLine1"),
                        ((Long) carCharger.get("NumberOfPoints")).intValue()));
            }
            return Optional.empty();
        } catch (IOException | ParseException | InterruptedException e) {
            LOGGER.error("Error: " + e.getMessage());
            throw new CarChargerUnexpectedException(e);
        }
    }
}
