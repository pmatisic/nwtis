package org.foi.nwtis.pmatisic.zadaca_1.pomocnici;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.pmatisic.zadaca_1.podaci.Korisnik;

public class CitanjeKorisnika {

	public Map<String, Korisnik> ucitajDatoteku(String nazivDatoteke) throws IOException {
		var putanja = Path.of(nazivDatoteke);
		if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
			throw new IOException("Datoteka '" + nazivDatoteke + "' ne postoji ili nije datoteka.");
		}

		var korisnici = new HashMap<String, Korisnik>();
		var citac = Files.newBufferedReader(putanja, Charset.forName("UTF-8"));

		while (true) {
			var red = citac.readLine();
			if (red == null)
				break;

			var kolone = red.split(";");
			if (!redImaPetStupaca(kolone)) {
				Logger.getGlobal().log(Level.WARNING, red);
			} else {
				var admin = isAdministrator(kolone[4]);
				var korisnik = new Korisnik(kolone[0], kolone[1], kolone[2], kolone[3], admin);
				korisnici.put(kolone[2], korisnik);
			}
		}
		return korisnici;
	}

	private boolean isAdministrator(String kolona) {
		return kolona.compareTo("1") == 0 ? true : false;
	}

	private boolean redImaPetStupaca(String[] kolone) {
		return kolone.length == 5;
	}
}