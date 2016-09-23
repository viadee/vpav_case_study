package de.viasurance.kfzglasbruch.servlet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import de.viasurance.kfzglasbruch.config.EnvironmentProperties;

/**
 * Dieses Servlet liefert eine Datei zurück, deren Pfad über den Requestparameter "name" übergeben wurde.
 */
@SuppressWarnings("serial")
public class GetFileServlet extends HttpServlet {

    private final static Logger LOGGER = Logger.getLogger(GetFileServlet.class.getName());

    @Autowired
    private EnvironmentProperties environmentProperties;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String fileName = request.getParameter("name");
        Path requestedFile = Paths.get(environmentProperties.getArchivOrdner() + File.separator + fileName);

        if (Files.exists(requestedFile)) {

            response.setContentType(Files.probeContentType(requestedFile));
            response.setContentLength((int) Files.size(requestedFile));

            OutputStream outputStream = response.getOutputStream();
            Files.copy(requestedFile, outputStream);
            outputStream.flush();

        } else {
            LOGGER.warning("Datei \"" + fileName + "\" wurde nicht gefunden -> Erzeuge Fehler 404");
            response.setStatus(404);
        }
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
        AutowireCapableBeanFactory factory = context.getAutowireCapableBeanFactory();
        factory.autowireBean(this);
    }

    public void setEnvironmentProperties(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }
}