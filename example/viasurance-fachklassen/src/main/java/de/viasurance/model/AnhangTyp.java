package de.viasurance.model;

import java.util.Arrays;
import java.util.List;

public enum AnhangTyp {

    RECHNUNG, KOSTENVORANSCHLAG;

    public static List<AnhangTyp> getAsList() {
        return Arrays.asList(values());
    }
}