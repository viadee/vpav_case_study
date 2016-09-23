package de.viasurance.vertragssystem.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.springframework.beans.factory.annotation.Autowired;

import de.viasurance.model.Vertrag;
import de.viasurance.vertragssystem.service.VertragService;

@WebService(serviceName = "Vertragssystem")
@SOAPBinding(style = Style.RPC, use = Use.LITERAL)
public class VertragssystemWebService {

    @Autowired
    private VertragService vertragService;

    @WebMethod
    @WebResult(name = "vertrag")
    public Vertrag getVertragByVsnr(@WebParam(name = "vsnr") String vsnr) {
        return vertragService.getVertragByVsnr(vsnr);
    }

    @WebMethod(exclude = true)
    public void setVertragService(VertragService vertragService) {
        this.vertragService = vertragService;
    }
}