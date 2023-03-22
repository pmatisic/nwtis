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

public class GlavniKlijent {

	public static void main(String[] args) {
		var gk = new GlavniKlijent();
		if (!gk.provjeriArgumente(args)) {
			Logger.getGlobal().log(Level.SEVERE, "Nisu ispravni ulazni argumenti!");
			return;
		}

		String posluizelj = args[0];
		int mreznaVrata = Integer.parseInt(args[1]);
		gk.spojiSeNaPosluzitelj(posluizelj, mreznaVrata);
	}

	private boolean provjeriArgumente(String[] args) {
		// TODO OVO nije ispravno treba prema opisu zadaće
		return args.length == 2 ? true : false;
	}

	private void spojiSeNaPosluzitelj(String adresa, int mreznaVrata) {
		try {
			var mreznaUticnica = new Socket(adresa, mreznaVrata);
			var citac = new BufferedReader(
					new InputStreamReader(mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
			var pisac = new BufferedWriter(
					new OutputStreamWriter(mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));

			String zahtjev = "TEST";// TODO ovdje treba stvarni zahtjev prema opisu zadaće
			pisac.write(zahtjev);
			pisac.flush();
			mreznaUticnica.shutdownOutput();

			var poruka = new StringBuilder();
			while (true) {
				var red = citac.readLine();
				if (red == null)
					break;

				Logger.getGlobal().log(Level.INFO, red);
				poruka.append("RED:" + red);
			}
			Logger.getGlobal().log(Level.INFO, "Odgovor: " + poruka);
			mreznaUticnica.shutdownInput();
			mreznaUticnica.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}