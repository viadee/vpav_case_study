package de.viasurance.partnersystem.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.springframework.beans.factory.annotation.Autowired;

import de.viasurance.model.Kontoverbindung;
import de.viasurance.partnersystem.service.KontoService;

@WebService(serviceName = "Partnersystem")
@SOAPBinding(style = Style.RPC, use = Use.LITERAL)
public class PartnersystemWebService {

    @Autowired
    private KontoService kontoService;

    @WebMethod
    @WebResult(name = "kontoverbindung")
    public Kontoverbindung getKontoverbindungByKundennummer(@WebParam(name = "kundennummer") String kundennummer) {
        return kontoService.getKontoverbindungByKundennummer(kundennummer);
    }

    @WebMethod(exclude = true)
    public void setKontoService(KontoService kontoService) {
        this.kontoService = kontoService;
    }
}