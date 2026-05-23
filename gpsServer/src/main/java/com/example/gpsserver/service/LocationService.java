package com.example.gpsserver.service;

import com.example.gpsserver.model.LocationData;
import com.example.gpsserver.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository repository;

    private static final long SESSION_TIMEOUT = 10 * 60 * 1000; // 10 minute session

    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    public void save(LocationData data) {

        LocationData last =
                repository.findTopByVehicleIdOrderByTimestampDesc(
                        data.getVehicleId()
                );

        if (last == null) {

            data.setSessionId(1L);

        } else {

            long diff = data.getTimestamp() - last.getTimestamp();

            if (diff > SESSION_TIMEOUT) {
                data.setSessionId(last.getSessionId() + 1);
            } else {
                data.setSessionId(last.getSessionId());
            }
        }

        repository.save(data);
    }

    public List<LocationData> getRoute(String vehicleId) {
        return repository.findByVehicleIdOrderByTimestampAsc(vehicleId);
    }

    public List<LocationData> getSessionRoute(
            String vehicleId,
            Long sessionId
    ) {
        return repository.findByVehicleIdAndSessionIdOrderByTimestampAsc(
                vehicleId,
                sessionId
        );
    }

    public List<LocationData> getRouteBetween(
            String vehicleId,
            long start,
            long end
    ) {
        return repository
                .findByVehicleIdAndTimestampBetweenOrderByTimestampAsc(
                        vehicleId,
                        start,
                        end
                );
    }

    public LocationData getCurrent(String vehicleId) {
        return repository.findTopByVehicleIdOrderByTimestampDesc(vehicleId);
    }

    public LocationData getPositionAt(
            String vehicleId,
            long timestamp
    ) {
        return repository
                .findTopByVehicleIdAndTimestampLessThanEqualOrderByTimestampDesc(
                        vehicleId,
                        timestamp
                );
    }

    public double getAverageSpeed(List<LocationData> list) {

        if (list.isEmpty()) return 0;

        double sum = 0;

        for (LocationData l : list) {
            sum += l.getSpeed();
        }

        return sum / list.size();
    }

    public double getTotalDistance(List<LocationData> list) {

        double distance = 0;

        for (int i = 1; i < list.size(); i++) {
            distance += distanceBetween(
                    list.get(i - 1),
                    list.get(i)
            );
        }

        return distance;
    }


    private double distanceBetween(LocationData a, LocationData b) {

        double R = 6371;

        double dLat =
                Math.toRadians(
                        b.getLatitude() - a.getLatitude()
                );

        double dLon =
                Math.toRadians(
                        b.getLongitude() - a.getLongitude()
                );

        double lat1 = Math.toRadians(a.getLatitude());

        double lat2 = Math.toRadians(b.getLatitude());

        double x =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(lat1)
                        * Math.cos(lat2)
                        * Math.sin(dLon / 2)
                        * Math.sin(dLon / 2);

        double c =
                2 * Math.atan2(Math.sqrt(x), Math.sqrt(1 - x));

        return R * c;
    }
}