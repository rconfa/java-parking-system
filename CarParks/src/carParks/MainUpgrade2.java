/*
 * 
 * made by:
 * Confalonieri Riccardo
 * riccardoconfalonieri98@gmail.com
 * github.com/rconfa
 *
 */

package carParks;

import java.util.Scanner;

public class MainUpgrade2 {

	public static void main(String[] args) {

		Scanner tastiera = new Scanner(System.in);

		/*
		 * RICHIESTA DEI DATI ALL'UTENTE - per semplicità e velocità di testing abbiamo
		 * deciso dare dei valori di default. Per avere una targa univoca prendiamo i
		 * millisecondi attuali.
		 * 
		 * String nome = tastiera.next(); String cognome = tastiera.next(); String cf =
		 * tastiera.next(); String targa = tastiera.next(); String marca =
		 * tastiera.next(); String modello = tastiera.next(); int cilindrata =
		 * tastiera.nextInt(); Automobile auto1 = new Automobile(targa, marca, modello,
		 * cilindrata); Automobilista a1 = new Automobilista(nome, cognome, cf, auto1);
		 */

		Automobile auto1 = new Automobile("" + System.currentTimeMillis(), "Fiat", "Punto", 1000);
		Automobilista a1 = new Automobilista("Name", "Surname", "Cf", auto1);

		System.out.println(auto1.getTarga());
		int scelta;
		String response = "";
		boolean errorChiusura = false;

		do {
			System.out.println("\nCiao, cosa vuoi fare? Scegli il numero: ");
			System.out.println("1) Stampa i parcheggi disponibili ");
			System.out.println("2) Scegli un parcheggio (numero) ");
			System.out.println("3) Parcheggia ");
			System.out.println("4) Ritira ");
			System.out.println("5) Stampa posti disponibili ");
			System.out.println("6) Prenota posto ");
			System.out.println("7) Annulla prenotazione posto ");
			System.out.println("8) Chiudi ");
			scelta = tastiera.nextInt();
			response = "";
			errorChiusura = false;
			switch (scelta) {
			case 1:
				response = a1.richiediParcheggiDisponibili();
				printParcheggi(response);
				break;
			case 2:
				System.out.println("Inserisci il numero del parcheggio voluto: ");
				int numParcheggio = tastiera.nextInt();
				response = a1.selezionaParcheggio(numParcheggio);
				System.out.println(response);
				break;
			case 3:
				response = a1.parcheggiaConServer();
				System.out.println(response);
				break;
			case 4:
				response = a1.ritiraConServer();
				System.out.println(response);
				break;
			case 5:
				response = a1.richiediPostiDisponibili();
				printPosti(response);
				break;
			case 6:
				System.out.println("Inserisci il numero del posto voluto: ");
				int numPosto = tastiera.nextInt();
				response = a1.prenotaPosto(numPosto);
				System.out.println(response);
				break;
			case 7:
				response = a1.annullaPrenotazione();
				System.out.println(response);
				break;
			case 8:
				response = a1.chiudiConnessione();
				if (response.equals("errorParcheggiato")) {
					System.out.println("Hai parcheggiato, ritira la macchina prima di chiudere la connessione!");
					errorChiusura = true;
				} else if (response.equals("errorPrenotato")) {
					System.out.println("Avevi effettuato la prenotazione di un posto. "
							+ "La tua prenotazione è stata annullata. " + "\nConnessione chiusa correttamente!");
				} else
					System.out.println(response);
				break;
			default:
				System.out.println("input non riconosciuto");
				break;
			}
		} while (scelta != 8 || errorChiusura);

		tastiera.close();
	}

	private static void printParcheggi(String response) {
		if (response.contains(";")) {
			String[] parcheggiDispo = response.split(";");

			for (int i = 0; i < parcheggiDispo.length; i++) {
				if (parcheggiDispo[i].equals("") == false)
					System.out.println((i + 1) + ") " + parcheggiDispo[i]);
			}
		} else
			System.out.println(response);
	}

	private static void printPosti(String response) {
		if (response.contains(";")) {
			String[] postiDispo = response.split(";");

			System.out.print("Lista dei posti disponibili: ");

			// mi fermo ad un posto prima per stampare correttamente i '-' tra i vari numeri
			for (int i = 0; i < postiDispo.length - 1; i++) {
				System.out.print(postiDispo[i] + " - ");
			}

			System.out.println(postiDispo[postiDispo.length - 1]);
		} else
			System.out.println(response);
	}

}
