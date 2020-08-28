package org.example.api;

import org.example.carcharger.CarCharger;
import org.example.carcharger.CarChargerService;
import org.example.location.GeolocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ClosestCarChargerController {

    private final GeolocationService geolocationService;
    private final CarChargerService carChargerService;

    @Autowired
    public ClosestCarChargerController(GeolocationService geolocationService,
                                       CarChargerService carChargerService) {
        this.geolocationService = geolocationService;
        this.carChargerService = carChargerService;
    }

    @GetMapping
    public ResponseEntity<CarCharger> getClosestCarCharger(
            @RequestParam(name = "address") String address) {
        return geolocationService.findFromAddress(address)
                .flatMap(carChargerService::findClosest)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
