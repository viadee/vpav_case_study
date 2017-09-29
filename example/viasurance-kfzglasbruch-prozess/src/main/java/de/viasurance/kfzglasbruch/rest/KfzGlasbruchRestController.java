package de.viasurance.kfzglasbruch.rest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.viadee.bpm.vPAV.beans.InitialProcessVariablesBase;
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

    class InitialProcessVariables extends InitialProcessVariablesBase {

        ObjectValue ext_kunde;

        String ext_vsnr;

        ObjectValue ext_kfz;

        ObjectValue ext_schaden;

        Double ext_schadenshoehe;

        ObjectValue ext_anhang;

        String dateiname;
    }

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

        // Prozessvariablen setzen und Prozess starten; die Serialisierung der
        // Variablen erfolgt explizit als JSON

        ObjectValue kunde = Variables.objectValue(schadensmeldung.getKunde())
                .serializationDataFormat("application/json").create();
        ObjectValue kfz = Variables.objectValue(schadensmeldung.getKfz()).serializationDataFormat("application/json")
                .create();
        ObjectValue schaden = Variables.objectValue(schadensmeldung.getSchaden())
                .serializationDataFormat("application/json").create();
        ObjectValue anhangT = Variables.objectValue(schadensmeldung.getAnhang())
                .serializationDataFormat("application/json").create();

        final InitialProcessVariables processVariables = new InitialProcessVariables();
        processVariables.ext_kunde = kunde;
        processVariables.ext_vsnr = schadensmeldung.getVersicherungsscheinnummer();
        processVariables.ext_kfz = kfz;
        processVariables.ext_schaden = schaden;
        processVariables.ext_schadenshoehe = Double.valueOf(schadensmeldung.getSchaden().getSchadenshoehe()) / 100;
        processVariables.ext_anhang = anhangT;
        processVariables.dateiname = path.getFileName().toString();

        ProcessInstance instance = runtimeService.startProcessInstanceByMessage("schadensmeldungKfzGlasbruch",
                processVariables.createVariableMap());

        return instance.getId();
    }

    /**
     * simulates some process instances
     * 
     * @param numberOfInstances
     *            number of process instances
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