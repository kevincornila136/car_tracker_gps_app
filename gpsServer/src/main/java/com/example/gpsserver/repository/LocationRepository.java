package com.example.gpsserver.repository;

import com.example.gpsserver.model.LocationData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<LocationData, Long> {

    List<LocationData> findByVehicleIdOrderByTimestampAsc(String vehicleId);

    LocationData findTopByVehicleIdOrderByTimestampDesc(String vehicleId);

    List<LocationData> findByVehicleIdAndSessionIdOrderByTimestampAsc(
            String vehicleId,
            Long sessionId
    );

    List<LocationData> findByVehicleIdAndTimestampBetweenOrderByTimestampAsc(
            String vehicleId,
            long start,
            long end
    );

    LocationData findTopByVehicleIdAndTimestampLessThanEqualOrderByTimestampDesc(
            String vehicleId,
            long timestamp
    );
}