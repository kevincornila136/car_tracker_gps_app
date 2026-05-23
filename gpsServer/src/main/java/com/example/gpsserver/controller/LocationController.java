package com.example.gpsserver.controller;

import com.example.gpsserver.model.LocationData;
import com.example.gpsserver.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/location")
@CrossOrigin
public class LocationController {

    private final LocationService service;

    public LocationController(LocationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<String> receive(
            @RequestBody LocationData data
    ) {

        service.save(data);

        return ResponseEntity.ok("Saved");
    }

    @GetMapping("/route/{vehicleId}")
    public List<LocationData> getRoute(
            @PathVariable String vehicleId
    ) {
        return service.getRoute(vehicleId);
    }

    @GetMapping("/route/{vehicleId}/session/{sessionId}")
    public List<LocationData> getSessionRoute(
            @PathVariable String vehicleId,
            @PathVariable Long sessionId
    ) {
        return service.getSessionRoute(vehicleId, sessionId);
    }

    @GetMapping("/route/{vehicleId}/between")
    public List<LocationData> getBetween(
            @PathVariable String vehicleId,
            @RequestParam long start,
            @RequestParam long end
    ) {

        return service.getRouteBetween(
                vehicleId,
                start,
                end
        );
    }

    @GetMapping("/current/{vehicleId}")
    public LocationData getCurrent(
            @PathVariable String vehicleId
    ) {
        return service.getCurrent(vehicleId);
    }

    @GetMapping("/position/{vehicleId}")
    public LocationData getPositionAt(
            @PathVariable String vehicleId,
            @RequestParam long timestamp
    ) {
        return service.getPositionAt(
                vehicleId,
                timestamp
        );
    }

    @GetMapping("/stats/{vehicleId}")
    public ResponseEntity<String> getStats(
            @PathVariable String vehicleId
    ) {

        List<LocationData> route =
                service.getRoute(vehicleId);

        double avg =
                service.getAverageSpeed(route);

        double dist =
                service.getTotalDistance(route);

        String json =
                """
                {
                  "averageSpeed": %f,
                  "totalDistance": %f
                }
                """.formatted(avg, dist);

        return ResponseEntity.ok(json);
    }
}