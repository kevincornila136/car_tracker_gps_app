# GPS Car Tracker

Aplicație pentru localizarea și monitorizarea autoturismelor realizată utilizând Android, Java Spring Boot și MySQL.

Sistemul colectează coordonatele GPS de pe telefonul Android, transmite datele prin HTTP către un server central și afișează traseul și statisticile într-o interfață desktop Java Swing folosind OpenStreetMap.

Funcționalități principale:
- transmitere automată a locației la intervale regulate
- reconstituirea traseului pe hartă
- afișarea poziției la un anumit timestamp
- calcul distanță totală și viteză medie
- separarea traseelor pe sesiuni de deplasare
- comunicație securizată prin Tailscale VPN

Tehnologii utilizate:
- Android Studio / Java
- Spring Boot REST API
- MySQL + Hibernate + Spring Data
- Java Swing
- OpenStreetMap
- Tailscale (WireGuard VPN)

Pentru rulare:
1. Se configurează baza de date MySQL și fișierul `application.properties`
2. Se pornește serverul Spring Boot
3. Se configurează IP-ul serverului în aplicația Android
4. Se rulează aplicația Android și interfața desktop Java Swing
5. Datele GPS vor fi transmise și afișate în timp real
