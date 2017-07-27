# viadee Process Application Validator (vPAV)
Das vPAV überprüft die Konsistenz von Camunda-Projekten und deckt dabei typische Fehler in prozessgesteuerten Anwendungen auf. Über ein Maven-Plugin werden potenzielle Fehler im Zusammenspiel von Prozessmodell und Quellcode aufgedeckt.

-- TEASER
kurzer Text mit Inhalten aus der Kurzanleitung und evtl. einem Beispiel

## Features
### Java Delegate Checker
Prüfung, ob die folgenden Bedingungen für Service Tasks, Send Tasks, Receive Tasks, Script Tasks oder Business Rule Tasks gültig sind.
- Keine Implementierung angegeben
- Klasse als Implementierung angegeben, aber nicht gefunden
- Klasse implementiert nicht die Schnittstelle JavaDelegate

### Embedded Groovy Script Checker
Der Embedded Groovy Script Checker überprüft eingebettete Skripte in Listenern und Script Tasks auf Validität. Dafür prüft er folgende Bedingungen ab:
- Es ist kein Skriptformat angegeben
- Es ist kein Skriptinhalt angegeben
- Nur für Groovy: Der Skriptinhalt passt nicht zum Skriptformat (Syntax-Prüfung)

### Versioning Checker
Der Versioning Checker überprüft Referenzen in Service Tasks, Script Tasks, Business Rule Tasks, Send Tasks, Listenern und Message Events auf die Angabe einer versionierten Java-Implementierung.

### DMN Task Checker
Proof of Concept (experimentell): Der DMN Task Checker überprüft, ob für „Business Rule Tasks“ eine Geschäftsregel referenziert wird.

### Process Variables Model Checker
Proof of Concept (experimentell): Der Process Variables Model Checker überprüft ein Modell auf Datenflussanomalien. Darunter befinden sich Anomalien nach den Mustern DD („Überschrieben“), DU („Definiert-Gelöscht“) und UR („Undefiniert-Gelesen“).

### Process Variables Name Convention Checker
Der Process Variables Name Convention Checker überprüft, ob definierte Prozessvariablen interne und externe Namenskonventionen erfüllen.

### Task Naming Convention Checker
Der Task Naming Convention Checker überprüft, ob Tasks einer vormals definierten Namenskonvention folgen.

## Einstieg
Prozessbeschreibung/Einstieg in Klasse
master/viadeeProcessApplicationValidator/src/main/java/de/viadee/bpm/vPAV/BpmnCheckerMojo.java

# Contribution
- Lizenz
- BSD4-Lizenz
- Austausch: Wer mag kann uns gerne einen Pull-Request schicken.

## Lizenz
pom.xml für vPAV und vPAV Utils um das "license-maven-plugin" erweitert. Beim Build wird automatisch die BSD4-Lizenz als Header in jede Klasse gesetzt.

## Getestete Camunda BPM Versionen
- Camunda BPM Engine 7.4.0
- Camunda BPM Engine 7.5.0
- Camunda BPM Engine 7.6.0
- (Camunda BPM Engine 7.7.0)


