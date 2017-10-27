package de.viasurance.partnersystem.service;

import de.viasurance.model.Kontoverbindung;

public interface KontoService {

    Kontoverbindung getKontoverbindungByKundennummer(String kundennummer);
}