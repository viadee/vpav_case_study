package de.viarepair.abrechnungssystem.ui.view;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestClientException;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.Receiver;
import com.vaadin.ui.Upload.StartedEvent;
import com.vaadin.ui.Upload.StartedListener;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Upload.SucceededListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import de.viarepair.abrechnungssystem.rest.RestService;
import de.viarepair.abrechnungssystem.ui.converter.CurrencyConverter;
import de.viasurance.model.Anhang;
import de.viasurance.model.AnhangTyp;
import de.viasurance.model.ReparaturTyp;
import de.viasurance.model.Schadensmeldung;

@SuppressWarnings("serial")
@SpringView(name = SchadensmeldungView.VIEW_NAME)
public class SchadensmeldungView extends VerticalLayout implements View, Receiver, SucceededListener {

    public static final String VIEW_NAME = "schadensmeldung";

    private static final String REQUIRED_ERROR = "Pflichtfeld";

    private static final String DEFAULT_FIELD_WIDTH = "275px";

    private BeanFieldGroup<Schadensmeldung> fieldGroup = new BeanFieldGroup<Schadensmeldung>(Schadensmeldung.class);

    private ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

    @Autowired
    private RestService restService;

    public void setRestService(RestService restService) {
        this.restService = restService;
    }

    private void init() {

        fieldGroup.setBuffered(true);
        fieldGroup.setItemDataSource(new Schadensmeldung());

        Label headline = new Label("<b>via</b>repair " + FontAwesome.CAR.getHtml() + " Schadensmeldung Kfz-Glasbruch");
        headline.setContentMode(ContentMode.HTML);
        headline.addStyleName(ValoTheme.LABEL_H3);
        headline.addStyleName(ValoTheme.LABEL_BOLD);
        headline.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        // Kunde

        FormLayout formKunde = new FormLayout();
        formKunde.setCaption("Kunde");
        formKunde.setMargin(false);

        formKunde.addComponent(createTextField("Vorname:", "kunde.vorname", true));
        formKunde.addComponent(createTextField("Nachname:", "kunde.nachname", true));
        formKunde.addComponent(createTextField("Straße und Hausnummer:", "kunde.strasse", true));

        TextField plz = createTextField("PLZ:", "kunde.plz", true);
        plz.setRequired(true);
        plz.setMaxLength(5);
        plz.setWidth("70px");
        plz.addValidator(new RegexpValidator("[0-9]{5}", true, "Bitte geben Sie eine gültige Postleitzahl ein"));
        formKunde.addComponent(plz);

        formKunde.addComponent(createTextField("Ort:", "kunde.ort", true));

        // Versicherungsscheinnummer

        FormLayout formVersicherung = new FormLayout();
        formVersicherung.setCaption("Versicherung");
        formVersicherung.setMargin(false);

        formVersicherung
                .addComponent(createTextField("Versicherungsscheinnummer:", "versicherungsscheinnummer", false));

        // Kfz

        FormLayout formKfz = new FormLayout();
        formKfz.setCaption("Kfz");
        formKfz.setMargin(false);

        TextField kennzeichen = createTextField("Amtliches Kennzeichen:", "kfz.kennzeichen", true);
        formKfz.addComponent(kennzeichen);

        formKfz.addComponent(createTextField("Fahrzeugtyp:", "kfz.fahrzeugtyp", true));

        // Schaden

        FormLayout formSchaden = new FormLayout();
        formSchaden.setCaption("Schaden");
        formSchaden.setMargin(false);

        OptionGroup reparaturTyp = new OptionGroup("Reparaturtyp:");
        reparaturTyp.setRequired(true);
        reparaturTyp.setRequiredError(REQUIRED_ERROR);
        reparaturTyp.addItems(ReparaturTyp.getAsList());
        reparaturTyp.setWidth(DEFAULT_FIELD_WIDTH);
        formSchaden.addComponent(reparaturTyp);
        fieldGroup.bind(reparaturTyp, "schaden.reparaturTyp");

        TextArea beschreibung = new TextArea("Schadensbeschreibung:");
        beschreibung.setRequired(true);
        beschreibung.setRequiredError(REQUIRED_ERROR);
        beschreibung.setValidationVisible(false);
        beschreibung.setNullRepresentation("");
        beschreibung.setRows(4);
        beschreibung.setWidth(DEFAULT_FIELD_WIDTH);
        fieldGroup.bind(beschreibung, "schaden.beschreibung");
        formSchaden.addComponent(beschreibung);

        TextField rechnungsbetrag = createTextField("Schadenshöhe:", "schaden.schadenshoehe", true);
        rechnungsbetrag.setWidth("100px");
        rechnungsbetrag.setInputPrompt("€");
        rechnungsbetrag.setValue(null);
        rechnungsbetrag.setConverter(new CurrencyConverter());
        rechnungsbetrag.setConversionError("Bitte geben Sie einen gültigen Betrag ein");
        formSchaden.addComponent(rechnungsbetrag);

        // Anhang

        FormLayout formAnhang = new FormLayout();
        formAnhang.setCaption("Anhang");
        formAnhang.setMargin(false);

        OptionGroup anhangTyp = new OptionGroup("Typ:");
        anhangTyp.setRequired(true);
        anhangTyp.setRequiredError(REQUIRED_ERROR);
        anhangTyp.addItems(AnhangTyp.getAsList());
        anhangTyp.setWidth(DEFAULT_FIELD_WIDTH);
        formAnhang.addComponent(anhangTyp);
        fieldGroup.bind(anhangTyp, "anhang.anhangTyp");

        final Upload upload = new Upload("Datei:", this);
        upload.setButtonCaption(null);
        upload.setImmediate(false);
        upload.addSucceededListener(this);
        upload.addStartedListener(new StartedListener() {

            @Override
            public void uploadStarted(StartedEvent event) {
                String filename = event.getFilename();
                if (filename == null || filename.isEmpty()) {
                    Notification.show("Datei fehlt", "Bitte wählen Sie eine Datei als Anhang aus (PDF, JPG oder PNG).",
                            Type.ERROR_MESSAGE);
                    upload.interruptUpload();
                } else {
                    filename = filename.toLowerCase();
                    if (!(filename.endsWith("pdf") || filename.endsWith("jpg") || filename.endsWith("png"))) {
                        Notification.show("Falscher Dateityp", "Es sind nur Dateien vom Typ PDF, JPG oder PNG erlaubt.",
                                Type.ERROR_MESSAGE);
                        upload.interruptUpload();
                    }

                }
            }
        });
        formAnhang.addComponent(upload);

        VerticalLayout layoutLeft = new VerticalLayout();
        layoutLeft.setSpacing(true);
        layoutLeft.addComponent(formKunde);
        layoutLeft.addComponent(formVersicherung);
        layoutLeft.addComponent(formKfz);
        layoutLeft.addStyleName("form-left");

        VerticalLayout layoutRight = new VerticalLayout();
        layoutRight.setSpacing(true);
        layoutRight.addComponent(formSchaden);
        layoutRight.addComponent(formAnhang);
        layoutRight.addStyleName("form-right");

        Button button = new Button("Schadensmeldung senden");
        button.setIcon(FontAwesome.ARROW_CIRCLE_O_RIGHT);
        button.addClickListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                onButtonClick(upload);
            }
        });

        setSpacing(true);
        setMargin(new MarginInfo(true, false, false, true));
        addComponent(headline);
        addComponent(new HorizontalLayout(layoutLeft, layoutRight));
        addComponent(button);
    }

    private TextField createTextField(String caption, String propertyId, boolean required) {
        TextField textField = (TextField) fieldGroup.buildAndBind(caption, propertyId);
        textField.setRequired(required);
        textField.setRequiredError(REQUIRED_ERROR);
        textField.setNullRepresentation("");
        textField.setValidationVisible(false);
        textField.setWidth(DEFAULT_FIELD_WIDTH);
        return textField;
    }

    private void onButtonClick(Upload upload) {
        if (!fieldGroup.isValid()) {
            // http://stackoverflow.com/questions/23753565/how-to-only-show-errors-on-commit-of-beanfieldgroup
            for (Field<?> f : fieldGroup.getFields()) {
                AbstractField<?> tf = (AbstractField<?>) f;
                tf.setValidationVisible(true);
            }
        } else {
            try {
                fieldGroup.commit();
                upload.submitUpload();
            } catch (CommitException e) {
                Notification.show("Fehler beim Senden der Schadensmeldung", e.toString(), Type.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void uploadSucceeded(SucceededEvent event) {

        BeanItem<Schadensmeldung> beanItem = fieldGroup.getItemDataSource();

        Schadensmeldung schadensmeldung = beanItem.getBean();

        Anhang anhang = schadensmeldung.getAnhang();
        anhang.setDateiBase64(Base64Utils.encodeToString(byteStream.toByteArray()));
        anhang.setDateiname(event.getFilename());

        try {
            String vorgangsnummer = restService.sendeKfzGlasbruchSchadensmeldung(schadensmeldung);

            Notification notification = new Notification("Schadensmeldung erfolgreich gesendet",
                    "Vorgangsnummer: " + vorgangsnummer);
            notification.setDelayMsec(-1);
            notification.setStyleName(ValoTheme.NOTIFICATION_SUCCESS);
            notification.show(UI.getCurrent().getPage());

        } catch (RestClientException e) {
            Notification.show("Fehler beim Senden der Schadensmeldung", e.toString(), Type.ERROR_MESSAGE);
        }
    }

    @Override
    public OutputStream receiveUpload(String filename, String mimeType) {
        return byteStream;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            init();
        } else {
            Navigator navigator = UI.getCurrent().getNavigator();
            navigator.navigateTo(LoginView.VIEW_NAME);
        }
    }
}