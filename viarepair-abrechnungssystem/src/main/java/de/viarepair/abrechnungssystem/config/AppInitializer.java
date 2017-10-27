package de.viarepair.abrechnungssystem.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.vaadin.spring.server.SpringVaadinServlet;

/**
 * Diese Klasse ersetzt die "klassische" Konfiguration in der web.xml.
 */
public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(AppConfig.class);
        ctx.scan("de.viarepair.abrechnungssystem.ui");

        servletContext.addListener(new ContextLoaderListener(ctx));

        ServletRegistration.Dynamic vaadin = servletContext.addServlet("vaadin", SpringVaadinServlet.class);
        vaadin.setLoadOnStartup(1);
        vaadin.setInitParameter("productionMode", "true");
        vaadin.addMapping("/*");
    }
}