package de.viarepair.abrechnungssystem.ui;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.Theme;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;

import de.viarepair.abrechnungssystem.ui.view.LoginView;

@SuppressWarnings("serial")
@SpringUI
@Theme("custom-valo")
public class AppUI extends UI {

    @Autowired
    private SpringViewProvider viewProvider;

    @Override
    protected void init(VaadinRequest request) {
        Navigator navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);
        setNavigator(navigator);
        navigator.navigateTo(LoginView.VIEW_NAME);
    }
}