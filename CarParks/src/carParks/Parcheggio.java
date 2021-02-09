/*
 * 
 * made by:
 * Confalonieri Riccardo
 * riccardoconfalonieri98@gmail.com
 * github.com/rconfa
 *
 */

package carParks;

public class Parcheggio {
	private String nome;
	private String via;
	private Posto[] posti;
	private Parcheggiatore[] parcheggiatori;
	private int numPostiDisponibili;
	private int numParcheggiatoriDisponibili;

	public Parcheggio(String nome, String via, Posto[] posti, Parcheggiatore[] parcheggiatori) {
		this.nome = nome;
		this.via = via;
		this.posti = posti;
		this.parcheggiatori = parcheggiatori;
		this.numPostiDisponibili = this.posti.length;
		this.numParcheggiatoriDisponibili = this.parcheggiatori.length;
	}

	public String getNome() {
		return nome;
	}

	public int getNumPostiDisponibili() {
		return this.numPostiDisponibili;
	}

	/*
	 * Permette ad un automobilista di lasciare la sua macchina in un parcheggio
	 * scelto dopo aver precedentemente effettuato una prenotazione. Perchè ciò
	 * avvenga deve esserci un parcheggiatore disponibile.
	 */
	public void ritiraConPrenotazione(Automobile auto, int posto) throws InterruptedException {
		int indexParcheggiatoreLibero = this.preparaRitiroConPrenotazione(auto.getTarga(), posto);

		// delega la funzione di ritiro al parcheggiatore
		this.parcheggiatori[indexParcheggiatoreLibero].prendere();
		// Aggiorna il posto con il ticket emesso dal parcheggiatore.
		// this.posti[posto].setTicket(ticket);
		this.posti[posto].setAutoParcheggiata(auto);
		this.terminaRitiroConPrenotazione(indexParcheggiatoreLibero, auto.getTarga(), posto);

	}

	/*
	 * Permette di trovare un parcheggiatore disponibile. Finchè non si trova il parcheggiatore il
	 * thread che entra in questo metodo finisce in wait. Ritorna l'indice del parcheggiatore.
	 */
	public synchronized int preparaRitiroConPrenotazione(String targa, int posto) throws InterruptedException {
		int indexParcheggiatoreLibero;

		// Finchè non ci sono parcheggiatori liberi
		while (this.numParcheggiatoriDisponibili == 0) {
			System.out.println("targa: " + targa + " is waiting for parking");
			wait();
		}
		indexParcheggiatoreLibero = cercaParcheggiatoreLibero();

		// aggiorno la situazione del posto selezionato come libero.
		setOccupazione(targa, posto, indexParcheggiatoreLibero, true);
		this.numPostiDisponibili++;

		System.out.println("targa: " + targa + " PARK!");

		return indexParcheggiatoreLibero;
	}

	/*
	 * Permette di concludere l'operazione di ritiro di un automobile con prenotazione. Libera il
	 * parcheggiatore che era stato selezionato per completare l'operazione.
	 * Notifica tutti i thread in attesa
	 */
	public synchronized void terminaRitiroConPrenotazione(int indexParcheggiatoreLibero, String targa, int posto) {
		this.parcheggiatori[indexParcheggiatoreLibero].setOccupato(false);
		this.posti[posto].setPrenotato(false);
		// una volta che termino il ritiro e "libero" il parcheggiatore incremento il
		// relativo contatore
		this.numParcheggiatoriDisponibili++;
		notifyAll();
	}

	/*
	 * Permette ad un automobilista di lasciare la sua macchina in un parcheggio.
	 * Perchè ciò avvenga deve esserci un posto libero e un parcheggiatore
	 * disponibile.
	 */
	public String ritira(Automobile auto) throws InterruptedException {

		int[] vectorIndex = this.preparaRitiro(auto.getTarga());

		int indexPostoLibero = vectorIndex[0];
		int indexParcheggiatoreLibero = vectorIndex[1];

		String ticket = null;
		// delega la funzione di ritiro al parcheggiatore
		ticket = this.parcheggiatori[indexParcheggiatoreLibero].prendere();
		// Aggiorna il posto con il ticket emesso dal parcheggiatore.
		this.posti[indexPostoLibero].setTicket(ticket);
		this.posti[indexPostoLibero].setAutoParcheggiata(auto);
		this.terminaRitiro(indexParcheggiatoreLibero, auto.getTarga());

		return ticket;
	}

	/*
	 * Permette di cercare un posto libero e di trovare un parcheggiatore
	 * disponibile. Finchè non si trovano sia il posto sia il parcheggiatore il
	 * thread che entra in questo metodo finisce in wait. Ritorna gli indici del
	 * posto libero e del parcheggiatore nei corrispettivi vettori
	 */
	public synchronized int[] preparaRitiro(String targa) throws InterruptedException {
		int indexPostoLibero = -1;
		int indexParcheggiatoreLibero;
		int vectorIndex[] = new int[2];

		// Finchè non ci sono posti disponibili o non ci sono parcheggiatori liberi
		while (this.numPostiDisponibili == 0 || this.numParcheggiatoriDisponibili == 0) {
			System.out.println("targa: " + targa + " is waiting for parking");
			wait();
		}
		indexPostoLibero = cercaPostoLibero();
		indexParcheggiatoreLibero = cercaParcheggiatoreLibero();

		// aggiorno la situazione del posto selezionato come libero.
		setOccupazione(targa, indexPostoLibero, indexParcheggiatoreLibero, true);

		System.out.println("targa: " + targa + " PARK!");

		vectorIndex[0] = indexPostoLibero;
		vectorIndex[1] = indexParcheggiatoreLibero;

		return vectorIndex;

	}

	/*
	 * Permette di concludere l'operazione di ritiro di un automobile. Libera il
	 * parcheggiatore che era stato selezionato per completare l'operazione.
	 * Notifica tutti i thread in attesa
	 */
	public synchronized void terminaRitiro(int indexParcheggiatoreLibero, String targa) {
		this.parcheggiatori[indexParcheggiatoreLibero].setOccupato(false);

		// una volta che termino il ritiro e "libero" il parcheggiatore incremento il
		// relativo contatore
		this.numParcheggiatoriDisponibili++;
		notifyAll();
	}

	// Permette di aggiornare la situazione reale di un posto.
	private void setOccupazione(String targa, int indexPosto, int indexParcheggiatore, boolean occupato) {
		this.posti[indexPosto].setOccupato(occupato); // Setto Posto come non disponibile
		this.posti[indexPosto].setTarga(targa); // Aggiorno la targa presente nel Posto
		this.parcheggiatori[indexParcheggiatore].setOccupato(occupato); // Setto Parcheggiatore come non disponibile

		// se sto occupando il posto e il parcheggiatore decremento i relativi contatore
		if (occupato == true) {
			this.numPostiDisponibili--;
			this.numParcheggiatoriDisponibili--;
		} else {
			this.numPostiDisponibili++;
			this.numParcheggiatoriDisponibili++;
		}
	}

	/*
	 * cerca se esiste un posto libero nell'array posti. se trova ne ritorna la sua
	 * posizione nell'array altrimenti torna un valore di errore (-1)
	 */
	private int cercaPostoLibero() {
		int i = 0;

		while (i < posti.length && posti[i].isOccupato())
			i++;

		if (i < posti.length)
			return i;
		else
			return -1;
	}

	/*
	 * cerca se esiste un parcheggiatore libero se lo trova ne ritorna la sua
	 * posizione nell'array altrimenti torna un valore di errore (-1)
	 */
	private int cercaParcheggiatoreLibero() {
		int i = 0;

		while (i < parcheggiatori.length && parcheggiatori[i].isOccupato())
			i++;

		if (i < parcheggiatori.length)
			return i;
		else
			return -1;
	}

	/*
	 * Permette ad un automobilista di farsi consegnare la sua macchina. Perchè ciò
	 * avvenga deve esserci un parcheggiatore disponibile, l'automobilista deve
	 * avere il ticket corretto, e la macchina deve essere effettivamente nel
	 * parcheggio. Se l'automobilista fa una richiesta errata (ticket sbagliato o la
	 * macchina non si trova verrà generata un'eccezione)
	 */
	public Automobile restituisci(String targa, String ticket) throws InterruptedException {

		int indexParcheggiatoreLibero = this.preparaRestituzione(targa, ticket);

		int indexPostoDaLiberare = this.cercaPostoDaLiberare(targa, ticket);

		Automobile auto = null;
		// Error: L'automobilista ha fatto una richiesta errata.
		if (indexPostoDaLiberare == -1) {
			System.err.println("targa: " + targa + " has done an illegal action.");
			Thread.currentThread().interrupt();
		} else {
			this.parcheggiatori[indexParcheggiatoreLibero].restituisci();
			this.posti[indexPostoDaLiberare].setTicket(""); // Aggiorno il ticket presente nel Posto

			// Prendo auto parcheggiata da ritornare all'automobilista
			auto = this.posti[indexPostoDaLiberare].getAutoParcheggiata();
			// Setto auto nel posto = null
			this.posti[indexPostoDaLiberare].setAutoParcheggiata(null);
			this.terminaRestituzione(targa, indexPostoDaLiberare, indexParcheggiatoreLibero);
		}
		return auto;
	}

	/*
	 * Permette di verificare se c'è un parcheggiatore libero per effettuare una
	 * restituzione Finchè non si trova un parcheggiatore libero l'automobilista che
	 * ha effettuato la richiesta rimane in attesa.
	 */
	public synchronized int preparaRestituzione(String targa, String ticket) throws InterruptedException {
		int indexParcheggiatoreLibero;

		// indexParcheggiatoreLibero = this.cercaParcheggiatoreLibero();
		// Finchè non ci sono posti liberi o non ci sono parcheggiatori liberi
		while (this.numParcheggiatoriDisponibili == 0) {
			System.out.println("targa: " + targa + " is waiting to leave the parking");
			wait();

		}

		indexParcheggiatoreLibero = this.cercaParcheggiatoreLibero();

		// Nel momento in cui trovo un parcheggiatore libero che devo assegnare
		// decremento il numParcheggiatoriDisponibili
		this.numParcheggiatoriDisponibili--;

		return indexParcheggiatoreLibero;
	}

	/*
	 * Permette di concludere l'operazione di restituzione. Libera il posto
	 * precedentemente occupato, rilascia il parcheggiatore notifica tutti gli altri
	 * automobilisti che sono in attesa.
	 */
	public synchronized void terminaRestituzione(String targa, int indexPostoDaLiberare,
			int indexParcheggiatoreLibero) {

		setOccupazione("", indexPostoDaLiberare, indexParcheggiatoreLibero, false);

		System.out.println("targa: " + targa + " LEAVE!");

		notifyAll();
	}

	/*
	 * cerca se esiste un posto da liberare con una specifica targa e uno specifico
	 * ticket, se trova un posto con queste caratteristiche ne ritorna la sua
	 * posizione nell'array altrimenti torna un valore di errore (-1)
	 */
	private int cercaPostoDaLiberare(String targa, String ticket) {
		int i = 0;

		while (i < posti.length && (!posti[i].getTarga().equals(targa) || !posti[i].getTicket().equals(ticket)))
			i++;

		if (i < posti.length)
			return i;
		else
			return -1;
	}

	/*
	 * permette di ottenere una stringa nel formato
	 * "numPosto;numPosto2;...."
	 * contenente tutti i posti disponibili del parcheggio.
	 */
	public String postiDisponibili() {
		String postiDisponibili = "";
		for (int i = 0; i < this.posti.length; i++) {
			if (!this.posti[i].isOccupato() && !this.posti[i].isPrenotato())
				postiDisponibili += this.posti[i].getNumero() + ";";
		}

		return postiDisponibili;
	}

	/*
	 * permette di prenotare un determinato posto del parcheggio.
	 * Un posto è prenotabile solo se non è occupato e se non è prenotato
	 * da nessun'altro utente.
	 */
	public synchronized String prenotaPosto(int posto) {

		if (this.posti[posto].isOccupato() == false && this.posti[posto].isPrenotato() == false) {
			String p = this.posti[posto].prenota();
			this.numPostiDisponibili--;
			System.out.println("Posto: " + posto + " prenotato!");
			return p;
		} else
			return "error";
	}

	/*
	 * permette di annullare una precedente prenotazione di un posto. In questo
	 * modo il posto torna ad essere disponibile per i clienti sia per nuove
	 * prenotazioni sia per essere occupato.
	 */
	public synchronized String annullaPrenotazionePosto(int posto, String ticket) {
		if (this.posti[posto].getTicket().equals(ticket)) {
			this.posti[posto].annullaPrenotazione();
			this.numPostiDisponibili++;
			System.out.println("Posto: " + posto + " annullata prenotazione!");
			return "Prenotazione annullata!";
		} else
			return "error";
	}

	@Override
	public String toString() {
		return "\nNome: " + this.nome + "\nVia: " + this.via + "\nPosti: " + this.posti.toString()
				+ "\nParcheggiatori: " + this.parcheggiatori.toString();
	}

	/*
	 * permette di verificare se un determinato posto è prenotato e se il ticket 
	 * di prenotazione corrisponde con il ticket della richiesta
	 */
	public boolean checkPrenotazione(int indexPosto, String ticketPrenotazione) {
		if (this.posti[indexPosto].getTicket().equals(ticketPrenotazione))
			return true;
		else
			return false;
	}
}
