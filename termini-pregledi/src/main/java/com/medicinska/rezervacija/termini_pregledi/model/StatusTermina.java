package com.medicinska.rezervacija.termini_pregledi.model;

public enum StatusTermina {
    DOSTUPAN,   //Termin je slobodan i može se zakazati
    ZAKAZAN,    //Termin je zauzet pregledom
    OTKAZAN     //Termin je bio zakazan, ali je otkazan (možda se ponovo može koristiti ili je trajno otkazan)
}