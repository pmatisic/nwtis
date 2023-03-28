package org.foi.nwtis.pmatisic.zadaca_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlavniKlijent {

	public static void main(String[] args) {

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			if (args[i].contains(" ")) {
				String trenutni = args[i];
				args[i] = "'" + trenutni + "'";
			}
			sb.append(args[i]).append(" ");
		}
		String s = sb.toString().trim();

		// String[] adresaPortKomanda = regexProvjera(s);

		var gk = new GlavniKlijent();
		Matcher unos = gk.provjeriArgumente(s);

		String komanda;
		if (unos == null) {
			Logger.getGlobal().log(Level.SEVERE, "Nisu unešeni argumenti!");
			return;
		} else {
			komanda = obradiKomandu(unos);
		}
		gk.spojiSeNaPosluzitelj(unos.group("ADRESA"), Integer.parseInt(unos.group("MREZNAVRATA")), komanda);
	}

	private Matcher provjeriArgumente(String s) {
		// (-k) ([0-9a-zA-Z_-]{3,10}) (-l) ([0-9a-zA-Z!#_-]{3,10}) (-a) ([0-9a-z.]+)
		// (-v) ([0-9]{4}) (-t) ([0-9]+) ()
		String sintaksa = "nesto";
		Pattern p = Pattern.compile(sintaksa);
		Matcher m = p.matcher(sintaksa);
		if (!m.matches()) {
			return null;
		} else {
			return m;
		}
	}

	private static String obradiKomandu(Matcher m) {
		String kljucevi[] = new String[] { "METEO", "ALARM", "UDALJENOST", "SPREMI", "MAKSTEMP", "MAKSVLAGA",
				"MAKSTLAK", "KRAJ" };

		StringBuilder sb = new StringBuilder();
		sb.append("KORISNIK ").append(m.group("KORISNIK")).append(" LOZINKA").append(m.group("LOZINKA")).append(" ");

		for (var kljuc : kljucevi) {
			if (m.group(kljuc) != null) {
				if (kljuc == "SPREMI") {
					sb.append(kljuc.substring(0, 10) + " " + kljuc.substring(10));
				} else {
					sb.append(kljuc.substring(0, 4) + " " + kljuc.substring(4) + " ").append(m.group(kljuc));
				}
				break;
			}
		}
		return sb.toString();
	}

	private void spojiSeNaPosluzitelj(String adresa, int mreznaVrata, String komanda) {
		try {
			Socket mreznaUticnica = new Socket(adresa, mreznaVrata);
			var citac = new BufferedReader(
					new InputStreamReader(mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
			var pisac = new BufferedWriter(
					new OutputStreamWriter(mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));

			pisac.write(komanda);
			pisac.flush();
			mreznaUticnica.shutdownOutput();

			var poruka = new StringBuilder();

			while (true) {
				var red = citac.readLine();
				if (red == null)
					break;

				// Logger.getGlobal().log(Level.INFO, red);

				poruka.append(red);
			}
			Logger.getGlobal().log(Level.INFO, poruka.toString());
			mreznaUticnica.shutdownInput();
			mreznaUticnica.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
