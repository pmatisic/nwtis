package org.foi.nwtis.pmatisic.projekt.podatak;

import java.sql.Timestamp;

public record Dnevnik(String vrsta, Timestamp vrijemePristupa, String putanja, int korisnik) {

}
