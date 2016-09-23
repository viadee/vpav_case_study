package de.viasurance.kfzglasbruch.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import de.viasurance.kfzglasbruch.servlet.GetFileServlet;

/**
 * Diese Klasse ersetzt die "klassische" Konfiguration in der web.xml.
 */
public class AppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(final ServletContext servletContext) throws ServletException {

        final AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(AppConfig.class);
        servletContext.addListener(new ContextLoaderListener(ctx));

        final ServletRegistration.Dynamic servlet = servletContext.addServlet("rest", new DispatcherServlet(ctx));
        servlet.setLoadOnStartup(1);
        servlet.addMapping("/rest");
        servlet.addMapping("/simulation");

        final ServletRegistration.Dynamic fileServlet = servletContext.addServlet("getFile", new GetFileServlet());
        fileServlet.addMapping("/getFile");
    }
}