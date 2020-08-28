package org.example.carcharger;

import org.example.location.Geolocation;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarChargerServiceTest {

    private static final String MOCK_URL = "http://mock-url?output=json";

    private static final String CAR_CHARGER_JSON = "[{\n" +
            "\"ID\":15176,\n" +
            "\"AddressInfo\":\n" +
            "    {\n" +
            "    \"ID\":15522,\n" +
            "    \"Title\":\"Dublin International Hostel\",\n" +
            "    \"AddressLine1\":\"Dublin International Hostel, 61 Mountjoy Street, Dublin 7\",\n" +
            "    \"Town\":\"Dublin\",\n" +
            "    \"CountryID\":3,\n" +
            "    \"Distance\":0.1587797076350614\n" +
            "    },\n" +
            "\"NumberOfPoints\":1\n" +
            "}]";

    private static final CarCharger CAR_CHARGER = new CarCharger("Dublin International Hostel",
            0.15877971f,
            "Dublin International Hostel, 61 Mountjoy Street, Dublin 7",
            1);

    // This is the class to test
    private CarChargerService carChargerService;

    @Mock
    private HttpClient httpClient;
    @Mock
    private HttpResponse<String> httpResponse;

    @BeforeEach
    void setUp() {
        this.carChargerService = new CarChargerService(httpClient, new JSONParser(), MOCK_URL);
    }

    @Test
    void findClosestWhenResponseOkWithBody() throws IOException, InterruptedException {
        // Given
        when(httpResponse.statusCode()).thenReturn(HTTP_OK);
        when(httpResponse.body()).thenReturn(CAR_CHARGER_JSON);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);
        final Geolocation geolocation = new Geolocation(53.355510, -6.266000);

        // When
        Optional<CarCharger> closestCarCharger = carChargerService.findClosest(geolocation);

        // Then
        assertTrue(closestCarCharger.isPresent(), "Closest car charger must be present!");
        assertEquals(CAR_CHARGER, closestCarCharger.get(), "Closest car charger must match!");
    }

    @Test
    void findClosestWhenResponseNotFound() throws IOException, InterruptedException {
        // Given
        when(httpResponse.statusCode()).thenReturn(HTTP_NOT_FOUND);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);
        final Geolocation geolocation = new Geolocation(53.355510, -6.266000);

        // When
        Optional<CarCharger> closestCarCharger = carChargerService.findClosest(geolocation);

        // Then
        assertTrue(closestCarCharger.isEmpty(), "Closest car charger must not be present!");
    }
}