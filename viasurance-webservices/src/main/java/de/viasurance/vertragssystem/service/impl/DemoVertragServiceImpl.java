package de.viasurance.vertragssystem.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.viasurance.model.Deckungstyp;
import de.viasurance.model.Vertrag;
import de.viasurance.model.Zahlungsstatus;
import de.viasurance.vertragssystem.service.VertragService;

public class DemoVertragServiceImpl implements VertragService {

    private static final String VS_001 = "VS-001";

    private static final String VS_002 = "VS-002";

    private static final String VS_003 = "VS-003";

    private static final String VS_004 = "VS-004";

    private static final String VS_005 = "VS-005";

    private static final String VS_006 = "VS-006";

    private Map<String, Vertrag> map = new HashMap<>();

    private Vertrag createVertrag(String kundennummer, String vsnr, Deckungstyp deckungstyp,
            Zahlungsstatus zahlungsstatus) {
        Vertrag v = new Vertrag();
        v.setKundennummer(kundennummer);
        v.setVersicherungsscheinnummer(vsnr);
        v.setDeckungstyp(deckungstyp);
        v.setZahlungsstatus(zahlungsstatus);
        return v;
    }

    public DemoVertragServiceImpl() {
        map.put(VS_001, createVertrag("KN-001", VS_001, Deckungstyp.HAFTPFLICHT, Zahlungsstatus.OK));
        map.put(VS_002, createVertrag("KN-002", VS_002, Deckungstyp.HAFTPFLICHT, Zahlungsstatus.MAHNUNG));
        map.put(VS_003, createVertrag("KN-003", VS_003, Deckungstyp.TEILKASKO, Zahlungsstatus.OK));
        map.put(VS_004, createVertrag("KN-004", VS_004, Deckungstyp.TEILKASKO, Zahlungsstatus.MAHNUNG));
        map.put(VS_005, createVertrag("KN-005", VS_005, Deckungstyp.VOLLKASKO, Zahlungsstatus.OK));
        map.put(VS_006, createVertrag("KN-006", VS_006, Deckungstyp.VOLLKASKO, Zahlungsstatus.MAHNUNG));
    }

    public Vertrag getVertragByVsnr(String vsnr) {

        Vertrag vertrag = map.get(vsnr);

        // falls VSNR unbekannt, zufälligen Vertrag erzeugen
        if (vertrag == null) {

            vertrag = new Vertrag();
            vertrag.setKundennummer("KN-001");
            vertrag.setVersicherungsscheinnummer(vsnr);

            Random r = new Random();
            vertrag.setDeckungstyp(Deckungstyp.values()[r.nextInt(3)]);
            vertrag.setZahlungsstatus(Zahlungsstatus.values()[r.nextInt(2)]);
        }

        return vertrag;
    }
}