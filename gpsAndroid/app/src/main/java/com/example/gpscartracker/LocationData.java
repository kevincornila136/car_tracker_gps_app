package com.example.gpscartracker;

public class LocationData {
    private String vehicleId;
    private double latitude;
    private double longitude;
    private float speed;
    private long timestamp;

    public LocationData(String vehicleId, double latitude, double longitude, float speed, long timestamp) {
        this.vehicleId = vehicleId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.timestamp = timestamp;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public long getTimestamp() {
        return timestamp;
    }
}