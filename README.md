# App Einkaufsliste

## Geplante Funktionen
- [x] Erstellen der Einkaufsliste mit wenigen Klicks
- [x] Suchfunktion für Datenbank
- [ ] typische Verkaufseinheiten in Datenbank hinterlegen
- [ ] Möglichkeit, eigene Fotos in der Datenbank zu hinterlegen
- [ ] Möglichkeit, favorisierte Produkte in der Datenbank zu hinterlegen
- [ ] automatische Preisabfrage / Angebotsabfrage nach Zustimmung des Nutzers
- [ ] Preisaktualisierung der Datenbank nach Zustimmung des Nutzers
- [ ] manuelle Preisangabe
- [ ] weitere manuelle Anpassungsmöglichkeiten
- [ ] Scanfunktionen
  * Barcode
  * QR-Code
  
## Optionale Funktionen
- [ ] Automatische Berechnung der voraussichtlichen Einkaufssumme
- [ ] Automatisches Hinzufügen von Zutaten aus Rezepten (KI gestützt)
- [ ] Graphische Darstellung der Preisentwicklung ausgewählter Produkte
- [ ] Möglichkeit, eigene Rezepte zu hinterlegen

## Dokumentation erzeugen
Terminal öffnen und folgenden Befehl ausführen:

```bash
./gradlew clean dokkaGenerateHtml
```

Der Befehl erzeugt die Dokumentation der App anhand von speziell formatierten Kommentaren und
legt eine html Datei im Pfad `app/build/dokka/html/index.html` ab. Diese kann dann mit dem
Browser geöffnet und durchsucht werden.
