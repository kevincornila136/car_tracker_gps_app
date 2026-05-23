import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainUI {

    private static final ApiService api = new ApiService();
    private static List<LocationData> lastRoute;
    private static JTextArea textArea;

    private static final Color BG_DARK = new Color(30, 34, 42);       
    private static final Color TEXT_LIGHT = new Color(220, 223, 228); 
    private static final Color ACCENT_BLUE = new Color(40, 140, 240);
    private static final Color ACCENT_GREEN = new Color(46, 196, 114); 
    private static final Color PANEL_BG = new Color(240, 244, 248);  

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        JFrame frame = new JFrame("GPS Vehicle Tracker - Live Monitoring");
        frame.setSize(950, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 12));
        topPanel.setBackground(PANEL_BG);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 205, 210)));

        JButton btnRefresh = createStyledButton("Refresh", ACCENT_BLUE);
        JButton btnMap = createStyledButton("Open Map", ACCENT_BLUE);
        JButton btnChart = createStyledButton("Speed Chart", ACCENT_BLUE);
        JButton btnTimestamp = createStyledButton("Find Position", ACCENT_GREEN);

        topPanel.add(btnRefresh);
        topPanel.add(btnMap);
        topPanel.add(btnChart);
        topPanel.add(btnTimestamp);

        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 15));
        textArea.setBackground(BG_DARK);
        textArea.setForeground(TEXT_LIGHT);
        textArea.setCaretColor(TEXT_LIGHT);
        textArea.setEditable(false);
        textArea.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(null);

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);

        btnRefresh.addActionListener(e -> refreshData());

        btnMap.addActionListener(e -> {
            try {
                if (lastRoute != null && !lastRoute.isEmpty()) {
                    openMap(lastRoute);
                } else {
                    showWarning(frame, "Nu există date de traseu încărcate pentru a deschide harta.");
                }
            } catch (Exception ex) {
                showError(frame, "Eroare la generarea traseului pe hartă:\n" + ex.getMessage());
            }
        });

        btnChart.addActionListener(e -> {
            if (lastRoute != null && !lastRoute.isEmpty()) {
                ChartWindow.showChart(lastRoute);
            } else {
                showWarning(frame, "Nu există date pentru a genera graficul de viteză.");
            }
        });

        btnTimestamp.addActionListener(e -> handleFindPosition(frame));

        // refresh 10 secunde
        new Timer(10000, e -> refreshData()).start();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        refreshData();
    }

    private static JButton createStyledButton(String text, Color baseColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.BLACK);
        btn.setBackground(baseColor);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(baseColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(baseColor);
            }
        });
        return btn;
    }

    private static void handleFindPosition(JFrame frame) {
        try {
            String value = JOptionPane.showInputDialog(
                    frame,
                    "Introduceți ora dorită (HH:mm):\n(Sistemul va căuta locația din ziua curentă)",
                    "Căutare punct istoric",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (value == null || value.trim().isEmpty()) {
                return;
            }

            LocalTime time = LocalTime.parse(value.trim(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalDate today = LocalDate.now();
            LocalDateTime dateTime = LocalDateTime.of(today, time);

            long timestamp = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

            LocationData position = api.getPositionAt("CAR_1", timestamp);

            if (position == null || position.latitude == 0) {
                showWarning(frame, "Nu s-a găsit nicio înregistrare GPS pentru ora " + value + ".");
                return;
            }

            openSinglePointMap(position, value);

        } catch (DateTimeParseException ex) {
            showWarning(frame, "Formatul orei este invalid!\nVă rugăm utilizați formatul de 24h: HH:mm (ex: 14:30)");
        } catch (Exception ex) {
            showError(frame, "Eroare de comunicație cu serverul:\n" + ex.getMessage());
        }
    }

    private static void refreshData() {
        try {
            String vehicleId = "CAR_1";
            LocationData current = api.getCurrent(vehicleId);
            ApiService.StatsResponse stats = api.getStats(vehicleId);
            List<LocationData> route = api.getRoute(vehicleId);

            lastRoute = route;

            StringBuilder sb = new StringBuilder();
            sb.append("VEHICLE LIVE STATUS: ").append(vehicleId).append("\n");
            sb.append(" ==========================================================\n\n");

            if (current != null && current.latitude != 0) {
                sb.append("POZIȚIE CURENTĂ : ").append(current.latitude).append(", ").append(current.longitude).append("\n");
                sb.append("VITEZĂ INSTANT  : ").append(String.format(Locale.US, "%.1f", current.speed)).append(" m/s (")
                        .append(String.format(Locale.US, "%.1f", current.speed * 3.6)).append(" km/h)\n");
            } else {
                sb.append("POZIȚIE CURENTĂ : [În așteptare date GPS...]\n");
            }

            sb.append("\nSTATISTICI GLOBALE\n");
            sb.append(" ----------------------------------------------------------\n");
            sb.append("Distanță Totală : ").append(stats != null ? String.format(Locale.US, "%.2f", stats.totalDistance) : "0.00").append(" km\n");
            sb.append("Viteză Medie    : ").append(stats != null ? String.format(Locale.US, "%.1f", stats.averageSpeed) : "0.0").append(" m/s\n");
            sb.append("Pachete GPS     : ").append(route != null ? route.size() : 0).append(" puncte stocate\n\n");

            sb.append("ULTIMELE PACHETE RECEPTIONATE\n");
            sb.append(" ----------------------------------------------------------\n");

            if (route != null && !route.isEmpty()) {
                int start = Math.max(route.size() - 12, 0);
                for (int i = start; i < route.size(); i++) {
                    LocationData l = route.get(i);
                    sb.append(String.format(Locale.US, "  [Sesiune: %-4s] Lat: %-10.5f | Lon: %-10.5f | Viteza: %-4.1f m/s\n",
                            l.sessionId != null ? l.sessionId : "N/A", l.latitude, l.longitude, l.speed));
                }
            } else {
                sb.append("  [Nu s-au înregistrat coordonate în această sesiune]\n");
            }

            textArea.setText(sb.toString());

        } catch (Exception ex) {
            textArea.setText("\nEROARE DE CONEXIUNE:\n Nu s-au putut prelua datele de la server.\n Verificați dacă serverul Spring Boot este pornit și accesibil.\n\n Detalii tehnice: " + ex.getMessage());
        }
    }

    private static void openMap(List<LocationData> route) throws Exception {
        Map<Long, List<LocationData>> sessions = new HashMap<>();
        for (LocationData l : route) {
            long sId = (l.sessionId != null) ? l.sessionId : 0L;
            sessions.computeIfAbsent(sId, k -> new ArrayList<>()).add(l);
        }

        StringBuilder json = new StringBuilder("[");
        boolean firstSession = true;

        for (List<LocationData> session : sessions.values()) {
            if (!firstSession) json.append(",");
            json.append("[");
            boolean firstPoint = true;

            for (LocationData l : session) {
                if (!firstPoint) json.append(",");
                json.append(String.format(Locale.US, "{\"latitude\":%f,\"longitude\":%f}", l.latitude, l.longitude));
                firstPoint = false;
            }
            json.append("]");
            firstSession = false;
        }
        json.append("]");

        String html = Files.readString(new File("map_template.html").toPath());
        html = html.replace("ROUTES_DATA", json.toString());

        File file = new File("map_generated.html");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(html);
        }
        Desktop.getDesktop().browse(file.toURI());
    }

    private static void openSinglePointMap(LocationData pos, String searchedTime) throws Exception {
        String latStr = String.format(Locale.US, "%f", pos.latitude);
        String lonStr = String.format(Locale.US, "%f", pos.longitude);
        String speedKmh = String.format(Locale.US, "%.1f", pos.speed * 3.6);

        String html = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Poziție Istorică - %s</title>
                    <link rel="stylesheet" href="https://unpkg.com/leaflet/dist/leaflet.css"/>
                    <style>
                        html, body, #map { height: 100%%; margin: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; }
                        .popup-content { font-size: 14px; line-height: 1.5; }
                        .popup-title { font-weight: bold; color: #288cf0; border-bottom: 1px solid #e0e0e0; padding-bottom: 5px; margin-bottom: 8px; }
                    </style>
                </head>
                <body>
                <div id="map"></div>
                <script src="https://unpkg.com/leaflet/dist/leaflet.js"></script>
                <script>
                    var map = L.map('map').setView([%s, %s], 16);
                    
                   
                    L.tileLayer('https://tile.openstreetmap.de/{z}/{x}/{y}.png', {
                        maxZoom: 19,
                        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    }).addTo(map);

                    var popupHtml = '<div class="popup-content">' +
                                    '<div class="popup-title">🚗 Punct GPS identificat</div>' +
                                    '<b>Ora căutată:</b> %s<br>' +
                                    '<b>Coordonate:</b> %s, %s<br>' +
                                    '<b>Viteză:</b> %s km/h<br>' +
                                    '<b>Sesiune ID:</b> %s' +
                                    '</div>';

                    L.marker([%s, %s])
                        .addTo(map)
                        .bindPopup(popupHtml)
                        .openPopup();
                </script>
                </body>
                </html>
                """.formatted(
                searchedTime,
                latStr, lonStr,
                searchedTime, latStr, lonStr, speedKmh, (pos.sessionId != null ? pos.sessionId : "N/A"), 
                latStr, lonStr  
        );

        File file = new File("position_map.html");
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(html);
        }
        Desktop.getDesktop().browse(file.toURI());
    }

    private static void showWarning(JFrame frame, String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Avertisment", JOptionPane.WARNING_MESSAGE);
    }

    private static void showError(JFrame frame, String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Eroare Critică", JOptionPane.ERROR_MESSAGE);
    }
}