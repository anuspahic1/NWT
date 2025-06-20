package com.medicinska.rezervacija.obavijesti_dokumentacija.model;

public enum RacunStatus {
    PLACEN("Plaćen"),
    NEPLACEN("Neplaćen"),
    STORNIRAN("Storniran");

    private final String displayValue;

    RacunStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }
}
