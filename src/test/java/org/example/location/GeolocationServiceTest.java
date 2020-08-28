package org.example.location;

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
class GeolocationServiceTest {

    private static final String MOCK_URL = "http://mock-url?output=json";

    private static final String GEOLOCATION_JSON = "[{\n" +
            "\"place_id\":14193296,\n" +
            "\"lat\":\"53.3554511\",\n" +
            "\"lon\":\"-6.2655209\",\n" +
            "\"display_name\":\"111 Dorset Street Upper, Dublin, Ireland\",\n" +
            "\"country_code\":\"ie\"\n" +
            "}]";
    private static final Geolocation GEOLOCATION = new Geolocation(53.3554511, -6.2655209);

    // This is the class to test
    private GeolocationService geolocationService;

    @Mock
    private HttpClient httpClient;
    @Mock
    private HttpResponse<String> httpResponse;

    @BeforeEach
    void setUp() {
        this.geolocationService = new GeolocationService(httpClient, new JSONParser(), MOCK_URL);
    }

    @Test
    void findFromAddressWhenResponseOkWithBody() throws IOException, InterruptedException {
        // Given
        when(httpResponse.statusCode()).thenReturn(HTTP_OK);
        when(httpResponse.body()).thenReturn(GEOLOCATION_JSON);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);
        String address = "111 Dorset Street Upper";

        // When
        Optional<Geolocation> foundGeolocation = geolocationService.findFromAddress(address);

        // Then
        assertTrue(foundGeolocation.isPresent(), "Geolocation must be present!");
        assertEquals(GEOLOCATION, foundGeolocation.get(), "Geolocation must match!");
    }

    @Test
    void findFromAddressWhenResponseNotFound() throws IOException, InterruptedException {
        // Given
        when(httpResponse.statusCode()).thenReturn(HTTP_NOT_FOUND);
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString()))).thenReturn(httpResponse);
        String address = "111 Dorset Street Upper";

        // When
        Optional<Geolocation> foundGeolocation = geolocationService.findFromAddress(address);

        // Then
        assertTrue(foundGeolocation.isEmpty(), "Geolocation must not be present!");
    }
}