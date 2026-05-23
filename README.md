# GPS Car Tracker

Application for vehicle localization and monitoring developed using Android, Java Spring Boot and MySQL.

The system collects GPS coordinates from the Android phone, sends the data through HTTP to a central server and displays the route and statistics in a Java Swing desktop interface using OpenStreetMap.

Main functionalities:
- automatic location transmission at regular intervals
- route reconstruction on the map
- displaying the position at a specific timestamp
- total distance and average speed calculation
- separation of routes into driving sessions
- secure communication through Tailscale VPN

Technologies used:
- Android Studio / Java
- Spring Boot REST API
- MySQL + Hibernate + Spring Data
- Java Swing
- OpenStreetMap
- Tailscale (WireGuard VPN)

Running the project:
1. Configure the MySQL database and the `application.properties` file
2. Start the Spring Boot server
3. Configure the server IP inside the Android application
4. Run the Android application and the Java Swing desktop interface
5. GPS data will be transmitted and displayed in real time
