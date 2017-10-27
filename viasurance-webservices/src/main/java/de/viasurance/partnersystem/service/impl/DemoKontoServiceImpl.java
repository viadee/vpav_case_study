package de.viasurance.partnersystem.service.impl;

import de.viasurance.model.Kontoverbindung;
import de.viasurance.partnersystem.service.KontoService;

public class DemoKontoServiceImpl implements KontoService {

    public Kontoverbindung getKontoverbindungByKundennummer(String kundennummer) {

        Kontoverbindung kontoverbindung = new Kontoverbindung();
        kontoverbindung.setBankName("Deutsche Kreditbank Berlin");
        kontoverbindung.setBic("BYLADEM1001");
        kontoverbindung.setIban("DE01120300001234567890");

        return kontoverbindung;
    }
}