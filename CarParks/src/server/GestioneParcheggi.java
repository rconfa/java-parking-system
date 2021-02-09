/*
 * 
 * made by:
 * Confalonieri Riccardo
 * riccardoconfalonieri98@gmail.com
 * github.com/rconfa
 *
 */

package server;

import carParks.Automobile;
import carParks.Parcheggiatore;
import carParks.Parcheggio;
import carParks.Posto;

class GestioneParcheggi {
	private Parcheggio[] arrayParcheggi;

	public void init() {
		this.arrayParcheggi = new Parcheggio[4];

		//PARCHEGGIO 1
		
		// inizializzo i posti e i parcheggiatori del parcheggio1
		Posto[] postiP1 = new Posto[4];
		Parcheggiatore[] parcheggiatoriP1 = new Parcheggiatore[3];

		// inizializzo tutti i posti come disponibili
		for (int i = 0; i < postiP1.length; i++)
			postiP1[i] = new Posto(i, false);

		// inizializzo tutti i parcheggiatori come disponibili
		for (int i = 0; i < parcheggiatoriP1.length; i++)
			parcheggiatoriP1[i] = new Parcheggiatore(i, false);

		this.arrayParcheggi[0] = new Parcheggio("Pippo", "via Milano 60", postiP1, parcheggiatoriP1);
		
		
		//PARCHEGGIO 2
		
		// inizializzo i posti e i parcheggiatori del parcheggio2
		Posto[] postiP2 = new Posto[10];
		Parcheggiatore[] parcheggiatori2 = new Parcheggiatore[1];

		// inizializzo tutti i posti come disponibili
		for (int i = 0; i < postiP2.length; i++)
			postiP2[i] = new Posto(i, false);

		// inizializzo tutti i parcheggiatori come disponibili
		for (int i = 0; i < parcheggiatori2.length; i++)
			parcheggiatori2[i] = new Parcheggiatore(i, false);

		this.arrayParcheggi[1] = new Parcheggio("Pluto", "via Roma 90", postiP2, parcheggiatori2);
		
		
		//PARCHEGGIO 3
		
		// inizializzo i posti e i parcheggiatori del parcheggio3
		Posto[] postiP3 = new Posto[20];
		Parcheggiatore[] parcheggiatori3 = new Parcheggiatore[6];

		// inizializzo tutti i posti come disponibili
		for (int i = 0; i < postiP3.length; i++)
			postiP3[i] = new Posto(i, false);

		// inizializzo tutti i parcheggiatori come disponibili
		for (int i = 0; i < parcheggiatori3.length; i++)
			parcheggiatori3[i] = new Parcheggiatore(i, false);

		this.arrayParcheggi[2] = new Parcheggio("Paperino", "via pace 20", postiP3, parcheggiatori3);
		
		
		//PARCHEGGIO 4
		
		// inizializzo i posti e i parcheggiatori del parcheggio4
		Posto[] postiP4 = new Posto[15];
		Parcheggiatore[] parcheggiatori4 = new Parcheggiatore[2];

		// inizializzo tutti i posti come disponibili
		for (int i = 0; i < postiP2.length; i++)
			postiP4[i] = new Posto(i, false);

		// inizializzo tutti i parcheggiatori come disponibili
		for (int i = 0; i < parcheggiatori4.length; i++)
			parcheggiatori4[i] = new Parcheggiatore(i, false);
				
		this.arrayParcheggi[3] = new Parcheggio("Minnie", "via Venezia 4", postiP4, parcheggiatori4);
	}

	/*
	 * Permette di ottenere una stringa del formato
	 * "NomePark;NomePark2;...."
	 * contenente tutti i nomi dei parcheggi disponibili
	 */
	public String parcheggiDisponibili() {
		String parcheggiDisponibili = "";
		for (int i = 0; i < this.arrayParcheggi.length; i++) {
			if (this.arrayParcheggi[i].getNumPostiDisponibili() > 0)
				parcheggiDisponibili += this.arrayParcheggi[i].getNome() + ";";
			else
				parcheggiDisponibili += ";";
		}

		return parcheggiDisponibili;
	}
	
	//restituisce i posti disponibili di un determinato parcheggio
	public String postiDisponibili(int indexParcheggio) {
		return this.arrayParcheggi[indexParcheggio].postiDisponibili();
	}

	/*
	 * Permette di parcheggiare una specifica auto in un parcheggio.
	 * Per poter parcheggiare in quello specifico momento il parcheggio
	 * deve avere ancora posti disponibili, in questo caso viene tornato il
	 * ticket di avvenuto parcheggio.
	 * Altrimenti viene tornato un messaggio di errore.
	 */
	public String parcheggia(int indexParcheggio, Automobile auto) {
		String ticket = "";
		if (this.arrayParcheggi[indexParcheggio].getNumPostiDisponibili() > 0) {
			try {
				ticket = this.arrayParcheggi[indexParcheggio].ritira(auto);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return ticket;
	}
	
	/*
	 * Permette di parcheggiare una specifica auto in un posto precedentemente
	 * prenotato. Per poterlo fare il ticket del posto e quello di prenotazione
	 * devono coincidere, altrimenti viene tornato un messaggio di errore.
	 * In questo caso il ticket non viene rilasciato, rimane valido quello
	 * della prenotazione.
	 */
	public String parcheggiaConPrenotazione(int indexParcheggio, Automobile auto, int indexPosto, String ticketPrenotazione) {
		if (this.arrayParcheggi[indexParcheggio].checkPrenotazione(indexPosto,ticketPrenotazione) == true) {
			try {
				this.arrayParcheggi[indexParcheggio].ritiraConPrenotazione(auto, indexPosto);
				return "";
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return "error";	
	}
	
	/*
	 * Permette di ritirare una specifica auto da un parcheggio.
	 * Per poter ritirare il ticket e la targa devono coincidere con un auto
	 * parcheggiata. In questo caso viene restituita al cliente la sua auto
	 * Altrimenti viene tornato un messaggio di errore.
	 */
	public Automobile ritira(int indexParcheggio, String ticket, String targa) {
		Automobile auto = null;
		try {
			auto = this.arrayParcheggi[indexParcheggio].restituisci(targa, ticket);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return auto;
	}
	
	/*
	 * permette di prenotare un posto. Se disponibile viene ritornato
	 * il ticket di prenotazione altrimenti viene tornato un messaggio di errore
	 */
	public String prenotaPosto(int indexParcheggio,int indexPosto) {
		return this.arrayParcheggi[indexParcheggio].prenotaPosto(indexPosto);
	}
	
	/*
	 * permette di annullare la prenotazione di un posto.
	 * Se il ticket del posto corrisponde a quello che si vuole annullare il posto viene
	 * liberato, altrimenti viene segnalato un errore.
	 */
	public String annullaPrenotazionePosto(int indexParcheggio,int indexPosto, String ticket) {
		return this.arrayParcheggi[indexParcheggio].annullaPrenotazionePosto(indexPosto, ticket);
	}

}
