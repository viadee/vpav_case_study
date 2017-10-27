package de.viarepair.abrechnungssystem.ui.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@SpringView(name = LoginView.VIEW_NAME)
public class LoginView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "login";

    @Autowired
    private AuthenticationProvider authenticationProvider;

    private TextField benutzerField = new TextField("Benutzer:");

    private PasswordField passwortField = new PasswordField("Passwort:");

    public LoginView() {

        super();

        Label headline = new Label("<b>via</b>repair " + FontAwesome.CAR.getHtml() + " Schadensmeldung Kfz-Glasbruch");
        headline.setContentMode(ContentMode.HTML);
        headline.addStyleName(ValoTheme.LABEL_H3);
        headline.addStyleName(ValoTheme.LABEL_BOLD);
        headline.addStyleName(ValoTheme.LABEL_NO_MARGIN);

        FormLayout loginForm = new FormLayout();
        loginForm.setWidth("300px");
        loginForm.setMargin(false);
        loginForm.setCaption("Anmeldung");
        loginForm.addComponent(benutzerField);
        loginForm.addComponent(passwortField);

        benutzerField.setRequired(true);
        passwortField.setRequired(true);

        Button button = new Button("Anmelden");
        button.setClickShortcut(KeyCode.ENTER);
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                doLogin();
            }
        });

        setMargin(true);
        setSpacing(true);
        addComponent(headline);
        addComponent(loginForm);
        addComponent(button);
    }

    private void doLogin() {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(benutzerField.getValue(),
                passwortField.getValue());

        try {
            Authentication result = authenticationProvider.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(result);

            if (result.isAuthenticated()) {
                UI.getCurrent().getNavigator().navigateTo(SchadensmeldungView.VIEW_NAME);
            }

        } catch (AuthenticationException e) {

            String description = "Unbekannter Fehler";
            if (e instanceof BadCredentialsException) {
                description = "Benutzer oder Passwort ungültig";
            }

            Notification notification = new Notification("Anmeldung fehlgeschlagen", description);
            notification.setDelayMsec(-1);
            notification.setStyleName(ValoTheme.NOTIFICATION_ERROR);
            notification.show(UI.getCurrent().getPage());
        }
    };

    @Override
    public void enter(ViewChangeEvent event) {
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }
}