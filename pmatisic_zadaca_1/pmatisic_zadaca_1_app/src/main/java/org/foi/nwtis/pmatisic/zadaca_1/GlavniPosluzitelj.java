package org.foi.nwtis.pmatisic.zadaca_1;

import org.foi.nwtis.Konfiguracija;

public class GlavniPosluzitelj {

	protected Konfiguracija konf;;
	protected int brojRadnika;
	protected int maksVrijemeNeaktivnosti;

	public GlavniPosluzitelj(Konfiguracija konf) {
		this.konf = konf;
		this.brojRadnika = Integer.parseInt(konf.dajPostavku("brojRadnika"));
		this.maksVrijemeNeaktivnosti = Integer.parseInt(konf.dajPostavku("maksVrijemeNeaktivnosti"));
	}

	public void pokreniPosluzitelja() {

	}
}