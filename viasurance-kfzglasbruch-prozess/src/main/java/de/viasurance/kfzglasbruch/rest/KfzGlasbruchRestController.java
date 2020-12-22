package de.viasurance.kfzglasbruch.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.viasurance.kfzglasbruch.config.EnvironmentProperties;
import de.viasurance.model.Anhang;
import de.viasurance.model.AnhangTyp;
import de.viasurance.model.Kfz;
import de.viasurance.model.Kunde;
import de.viasurance.model.ReparaturTyp;
import de.viasurance.model.Schaden;
import de.viasurance.model.Schadensmeldung;

@RestController
public class KfzGlasbruchRestController {

    private final static Logger LOGGER = Logger.getLogger(KfzGlasbruchRestController.class.getName());

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private EnvironmentProperties environmentProperties;

    private Random random;

    public KfzGlasbruchRestController() {
        super();
        random = new Random();
    }

    @RequestMapping(value = "/rest", method = RequestMethod.POST, consumes = "application/json", produces = "text/plain")
    public String startKfzGlasbruchProzess(@RequestBody Schadensmeldung schadensmeldung)
            throws IllegalArgumentException, IllegalAccessException {

        // Rechnungsdatei in byte-Array umwandeln und aus Schadensmeldung
        // entfernen, da sie im weiteren Prozess nicht benötigt wird
        Anhang anhang = schadensmeldung.getAnhang();
        byte[] fileAsByteArray = Base64Utils.decodeFromString(anhang.getDateiBase64());
        anhang.setDateiBase64(null);

        // Datei im Dateisystem speichern (neuer Dateiname = Zeitstempel +
        // ursprünglicher Dateiname)
        Path path = Paths.get(environmentProperties.getArchivOrdner() + File.separator + System.currentTimeMillis()
                + "_" + anhang.getDateiname());
        try {
            Files.write(path, fileAsByteArray);
        } catch (IOException e) {
            LOGGER.warning("Datei konnte nicht gespeichert werden - " + e.toString());
        }

        final Map<String, Object> processVariables1 = Variables.createVariables()
                .putValue("ext_kunde", schadensmeldung.getKunde())
                .putValue("ext_vsnr", schadensmeldung.getVersicherungsscheinnummer())
                .putValue("kfz", schadensmeldung.getKfz()).putValue("ext_schaden", schadensmeldung.getSchaden())
                .putValue("ext_schadenshoehe", Double.valueOf(schadensmeldung.getSchaden().getSchadenshoehe()) / 100)
                .putValue("ext_anhang", schadensmeldung.getAnhang())
                .putValue("dateiname", path.getFileName().toString());

        ProcessInstance instance = runtimeService.startProcessInstanceByMessage("schadensmeldungKfzGlasbruch",
                processVariables1);

        return instance.getId();
    }

    /**
     * simulates some process instances
     *
     * @param numberOfInstances number of process instances
     * @return list of process instance Ids
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @RequestMapping(value = "/simulation", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public List<String> simulateInstances(@RequestBody int numberOfInstances)
            throws IllegalArgumentException, IllegalAccessException {
        LOGGER.log(Level.INFO, "Simulate " + numberOfInstances + " process instances");

        List<String> procInstIds = new ArrayList<String>();

        for (int i = 0; i < numberOfInstances; i++) {
            Schadensmeldung schadensmeldung = new Schadensmeldung();
            // choose the manual or automatic process path randomly
            if (random.nextInt(100) < 75) {
                schadensmeldung.setVersicherungsscheinnummer("VS-003");
            } else {
                schadensmeldung.setVersicherungsscheinnummer("VS-002");
            }

            Kunde kunde = new Kunde();
            kunde.setNachname("Mustermann");
            kunde.setVorname("Max");
            kunde.setStrasse("Teststr. 5");
            kunde.setPlz("12345");
            kunde.setOrt("Testhausen");
            schadensmeldung.setKunde(kunde);

            Schaden schaden = new Schaden();
            schaden.setBeschreibung("Glasbruch");
            schaden.setSchadenshoehe(random.nextInt(200000));
            schaden.setReparaturTyp(ReparaturTyp.AUSTAUSCH);
            schadensmeldung.setSchaden(schaden);

            Kfz kfz = new Kfz();
            kfz.setFahrzeugtyp("PKW");
            kfz.setKennzeichen("MS-MS-123");
            schadensmeldung.setKfz(kfz);

            InputStream fileInputStreamReader = KfzGlasbruchRestController.class.getClassLoader()
                    .getResourceAsStream("Test.pdf");

            byte[] b;
            String content = null;
            try {
                b = IOUtils.toByteArray(fileInputStreamReader);
                byte[] bytes64 = Base64Utils.encode(b);
                content = new String(bytes64);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }

            Anhang anhang = new Anhang();
            anhang.setAnhangTyp(AnhangTyp.RECHNUNG);
            anhang.setDateiname("Test.pdf");
            anhang.setDateiBase64(content);
            schadensmeldung.setAnhang(anhang);

            // start instance
            String procInstId = startKfzGlasbruchProzess(schadensmeldung);

            LOGGER.log(Level.INFO, "Process instance started: " + procInstId);
            procInstIds.add(procInstId);
        }

        return procInstIds;

    }

    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public void setEnvironmentProperties(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }
}
