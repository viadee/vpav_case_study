package de.viasurance.model;

import java.util.Arrays;
import java.util.List;

public enum ReparaturTyp {

    AUSTAUSCH, REPARATUR;

    public static List<ReparaturTyp> getAsList() {
        return Arrays.asList(values());
    }
}