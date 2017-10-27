package de.viasurance.vertragssystem.service;

import de.viasurance.model.Vertrag;

public interface VertragService {

    Vertrag getVertragByVsnr(String vsnr);
}