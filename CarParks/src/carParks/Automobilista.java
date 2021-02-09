/*
 * 
 * made by:
 * Confalonieri Riccardo
 * riccardoconfalonieri98@gmail.com
 * github.com/rconfa
 *
 */

package carParks;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/*
 * Questa classe ha due funzionalità:
 * 
 * 1) nella versioneBase funge da thread e permette di parcheggiare e ritirare ciclicamente una macchina
 * 	  in stato di concorrenza con altri thread dello stesso tipo. In questa versione l'automobilista
 * 	  ha una sua specifica istanza di parcheggio che funge da destinazione e con la quale interagisce
 * 	  direttamente.
 * 
 * 2) negli upgrade funge da client collegandosi col server e accedendo tramite esso alle funzionalità
 * 	  offerte dai parcheggi.
 * 	  In questa versione l'automobista conosce solo il numero del parcheggio in cui recarsi.
 * 
 */
public class Automobilista extends Thread {
	private String nome;
	private String cognome;
	private String codFis;
	private Automobile auto;
	private String targa;
	private Parcheggio parcheggio;
	private boolean stopThread = false; // Attributo per fermare il ciclo di run del thread

	//attributi per comunicazione col Server.
	/*
	 * ticket del parcheggio, corrisponde a "" se non ha ancora 
	 * parcheggiato o prenotato un posto.
	 */
	private String ticket;
	// numero del posto prenotato, vale -1 in caso non prenoti.
	private int numPosto;
	private Socket client;
	private ObjectInputStream socketInput;
	private ObjectOutputStream socketOutput;
	/*
	 * numero del parcheggio selezionato, vale -1 in caso non l'abbia ancora
	 * selezionato
	 */
	private int numParcheggio;

	public Automobilista(String nome, String cognome, String codFis, Automobile auto) {
		this.nome = nome;
		this.cognome = cognome;
		this.codFis = codFis;
		this.auto = auto;
		this.client = null;
		this.socketInput = null;
		this.socketOutput = null;
		this.numParcheggio = -1;
		this.ticket = "";
		this.targa = this.auto.getTarga();
		this.numPosto = -1;
	}

	public Automobilista(String nome, String cognome, String codFis, Automobile auto, Parcheggio parcheggio) {
		this.nome = nome;
		this.cognome = cognome;
		this.codFis = codFis;
		this.auto = auto;
		this.parcheggio = parcheggio;
		this.targa = this.auto.getTarga();
	}

	// permette all'automobilista di parcheggiare in uno specifico parcheggio la sua
	// auto.
	public void parcheggia() {

		Parcheggio p = this.parcheggio;
		try {
			this.ticket = p.ritira(this.auto);
			/*
			 * l'auto è parcheggiata, l'automobilista non ne è più un possesso. Simulo
			 * questa condizione settando auto a null
			 */
			this.auto = null;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/*
	 * permette all'automobilista di ritirare da uno specifico parcheggio la sua
	 * auto. Riaggiora l'oggetto auto che torna a disponibilità dell'automobilista.
	 */
	public void ritira() {
		Parcheggio p = this.parcheggio;
		try {
			this.auto = p.restituisci(this.targa, this.ticket);
			this.ticket = "";
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		/*
		 * L'eccezione interrupted è generata volontariamente per controllare il caso in
		 * cui un automobilista tenti di ritirare un automobile non sua o con un errato
		 * ticket.
		 */
		while (!stopThread && !this.isInterrupted()) {
			try {
				this.parcheggia();
				Thread.sleep(100);
				this.ritira();
			} catch (InterruptedException e) {
				System.out.println("Thread interrupted.");
			}
		}
	}

	public void stopExecution() {
		stopThread = true;
	}

	/*
	 * 
	 * 
	 * DA QUI IN POI CI SONO I METODI NECESSARI PER LA COMUNICAZIONE COL SERVER.
	 * 
	 * 
	 */

	// Metodo per inizializzare la connessione col server e salvare I/O stream.
	private void inizializzaClient() {
		try {
			this.client = new Socket("localhost", 8080);
			this.socketOutput = new ObjectOutputStream(client.getOutputStream());
			this.socketInput = new ObjectInputStream(client.getInputStream());
		} catch (Exception e) {
			System.err.println("Server down, riprova più tardi! ");
			System.exit(-1);
		}
	}

	/*
	 * Permette all'automobilista di fare una richiesta al server per conoscere la
	 * lista dei parcheggi in quel momento disponibili. Un parcheggio si considera
	 * disponibile se ha ancora almeno un posto libero.
	 */
	public String richiediParcheggiDisponibili() {
		if (this.client == null)
			inizializzaClient();

		try {
			// chiedo al server di inviarmi i parcheggi disponibili
			this.socketOutput.writeInt(1);
			this.socketOutput.flush();

			// leggo la risposta del server
			String response = this.socketInput.readUTF();

			if (response.equals(""))
				return "Tutti i parcheggi sono momentaneamente occupati, riprova più tardi!";
			else
				return response;

		} catch (IOException e) {
			System.err.println("Errore nella comunicazione col server, riprova più tardi! ");
			System.exit(-1);
		}
		return "error";
	}

	/*
	 * Permette all'automobilista di fare una richiesta al server per selezionare il
	 * parcheggio desiderato. Quando un automobilista seleziona un parcheggio, quel
	 * determinato parcheggio deve avere ancora almeno un posto disponibile,
	 * altrimenti viene ritornato un messaggiodi errore.
	 */
	public String selezionaParcheggio(int numParcheggio) {
		/*
		 * Se non ha l'auto vuol dire che è già parcheggiata quindi non gli è permesso
		 * selezionare un nuovo parcheggio finche non la ritira
		 */
		if (this.auto != null) {
			/*
			 * Se non ha il ticket e non ha prenotato nessun posto è possibile procedere col
			 * selezionare il parcheggio
			 */
			if (this.ticket.equals("") && this.numPosto == -1) {
				String response = richiediParcheggiDisponibili();
				String[] parcheggiDispo = response.split(";");

				if (numParcheggio > 0 && (numParcheggio <= parcheggiDispo.length)
						&& parcheggiDispo[numParcheggio - 1].equals("") == false) {
					this.numParcheggio = numParcheggio - 1;
					return "Parcheggio selezionato correttamente!";
				} else {
					this.numParcheggio = -1;
					return "Numero parcheggio non valido!";
				}
			} else// Se possiedo auto e possiedo ticket, ho prenotato
				return "Hai prenotato un posto, Non puoi cambiare parcheggio!";
		} else
			return "Hai già parcheggiato la macchina, non puoi cambiare parcheggio, prima ritirala! \n"
					+ "Il tuo parcheggio è il numero: " + (this.numParcheggio + 1);
	}

	/*
	 * Permette all'automobilista di fare una richiesta al server per parcheggiare
	 * la propria auto. Per poter parcheggiare l'automobilista deve aver
	 * correttamente selezionato un parcheggio ed essere ancora in possesso
	 * dell'auto. Il metodo si differenzia con la comunicazione al server in base se
	 * l'automobilista possiede un proprio numero di posto (assegnato quando viene
	 * effettuata una prenotazione) oppure meno. Se non ne possiede uno l'auto verrà
	 * parcheggiata nel primo posto disponibile del parcheggio selezionato. Se il
	 * parcheggio va a buon fine l'auto viene data in consegna al parcheggio (via
	 * server) e quindi tale attributo viene settato a null in modo che
	 * l'automobilista non possa più parcheggiare o effettuare altre operazioni se
	 * non il ritiro. Nel caso in cui il parcheggio si sia riempito viene ritornato
	 * un messaggio di errore (solo nel caso in cui non abbia prenotato)
	 */
	public String parcheggiaConServer() {
		if (this.client != null && this.numParcheggio != -1) {
			if (this.auto != null) {
				if (this.ticket.equals("") && this.numPosto == -1) {
					try {
						this.socketOutput.writeInt(3);
						this.socketOutput.flush();

						this.socketOutput.writeInt(this.numParcheggio);
						this.socketOutput.flush();

						this.socketOutput.writeObject(this.auto);
						this.socketOutput.flush();

						this.ticket = this.socketInput.readUTF();
					} catch (IOException e) {
						System.err.println("Errore nella comunicazione col server, riprova più tardi! ");
						System.exit(-1);
					}
					this.auto = null;
					return "Auto parcheggiata correttamente!";
				} else {
					return this.parcheggiaConPrenotazione();
				}
			} else
				return "Non possiedi nessuna auto! Ritirala!";
		} else
			return "Prima seleziona dove parcheggiare";
	}

	/*
	 * Permette all'automobilista di fare una richiesta al server per ritirare la
	 * propria auto. Per poter ritirare l'automobilista deve aver correttamente
	 * selezionato un parcheggio e non deve essere in possesso dell'auto. Il metodo
	 * comunica al server la targa dell'auto che si vuole ritirare e il ticket
	 * corrispondente. Se tutti i valori sono corretti il server restituirà
	 * l'istanza specifica dell'auto che verrà salvata come attributo
	 * dell'automobilista. Se un automobilista cerca di ritirare un auto non sua o
	 * con un errato ticket oppure ancora senza aver parcheggiato prima l'auto gli
	 * viene segnalato lo stato di errore.
	 */
	public String ritiraConServer() {
		if (this.auto == null) {
			if (this.client != null && this.numParcheggio != -1) {
				if (!this.ticket.equals("")) {
					try {
						this.socketOutput.writeInt(4);
						this.socketOutput.flush();

						this.socketOutput.writeInt(this.numParcheggio);
						this.socketOutput.flush();

						this.socketOutput.writeUTF(this.targa);
						this.socketOutput.flush();

						this.socketOutput.writeUTF(this.ticket);
						this.socketOutput.flush();

						this.auto = (Automobile) this.socketInput.readObject();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						System.exit(-1);
					} catch (IOException e) {
						System.err.println("Errore nella comunicazione col server, riprova più tardi! ");
						System.exit(-1);
					}
					this.ticket = "";
					this.numParcheggio = -1;
					this.numPosto = -1;
					return "Auto ritirata correttamente";

				} else
					return "Non possiedi nessun ticket! Parcheggia!";
			} else
				return "Prima seleziona da quale parcheggio ritirare la macchina"; // Potrebbe capitare? penso di no
		} else
			return "Prima devi parcheggiare la macchina!";
	}

	/*
	 * Permette all'automobilista di fare una richiesta al server per ottenere la
	 * lista dei posti disponibili di un determinato parcheggio precedentemente
	 * selezionato
	 */
	public String richiediPostiDisponibili() {
		String listaPosti = "";
		if (this.client == null)
			inizializzaClient();
		if (this.numParcheggio != -1) {
			try {
				this.socketOutput.writeInt(5);
				this.socketOutput.flush();

				this.socketOutput.writeInt(this.numParcheggio);
				this.socketOutput.flush();

				listaPosti = this.socketInput.readUTF();
			} catch (IOException e) {
				System.err.println("Errore nella comunicazione col server, riprova più tardi! ");
				System.exit(-1);
			}
			if (listaPosti.equals(""))
				return "Tutti i posti del parcheggio selezionato sono momentaneamente occupati o prenotati!"
						+ "Seleziona un altro parcheggio, o riprova più tardi!";
			else
				return listaPosti;
		} else
			return "Prima seleziona il parcheggio del quale vuoi sapere i posti";
	}

	/*
	 * Permette all'automobilista di selezionare un determinato posto. Il posto deve
	 * essere disponibile al momento della selezione.
	 */
	private void selezionaPosto(int numPosto) {
		String response = richiediPostiDisponibili();
		String toCheck = numPosto + ";";
		if (response.contains(toCheck)) {
			this.numPosto = numPosto;
		} else {
			this.numPosto = -1;
		}
	}

	/*
	 * Permette all'automobilista di prenotare uno specifico posto per farlo deve
	 * aver correttamente selezionato il parcheggio nel quale selezionare il posto,
	 * essere in possesso dell'auto e non avere altri posti prenotati. Viene
	 * ritornato un messaggio di errore nel caso una delle condizioni non sia
	 * verificata.
	 */
	public String prenotaPosto(int numPosto) {
		if (this.client == null)
			inizializzaClient();

		// controllo se ha selezionato il parcheggio in cui prenotare il posto
		if (this.numParcheggio != -1) {
			if (this.auto != null) {

				if (this.ticket.equals("") && this.numPosto == -1) {
					this.selezionaPosto(numPosto);

					if (this.numPosto != -1) {
						try {
							this.socketOutput.writeInt(6);
							this.socketOutput.flush();

							this.socketOutput.writeInt(this.numParcheggio);
							this.socketOutput.flush();

							this.socketOutput.writeInt(this.numPosto);
							this.socketOutput.flush();

							String ticket = this.socketInput.readUTF();
							if (ticket.equals("error")) {
								this.ticket = "";
								this.numPosto = -1;
								return "Il posto che vuoi prenotare non è piu disponibile!";
							}
							this.ticket = ticket;
						} catch (IOException e) {
							System.err.println("Errore nella comunicazione col server, riprova più tardi! ");
							System.exit(-1);
						}
						return ticket;
					} else
						return "Posto non valido!";
				} else
					return "Hai già prenotato un posto, parcheggia!";
			} else
				return "Hai già parcheggiato non puoi prenotare un posto, ritira la macchina!";
		} else
			return "Prima seleziona il parcheggio in cui vuoi prenotare un posto";
	}

	/*
	 * Permette all'automobilista di chiudere la connessione col server. Un
	 * automobilista puo chiudere la connessione solo se non ha piu nulla a che fare
	 * col server quindi non deve avere la macchina parcheggiata e nemmeno posti
	 * prenotati, in quest'ultimo caso verra annullata in automatico la
	 * prenotazione.
	 */
	public String chiudiConnessione() {
		if (this.client != null) {
			if (this.auto != null) {
				if (this.ticket.equals("")) {
					closing();
					return "Connessione chiusa correttamente!";
				} else {
					String annullato = this.annullaPrenotazione();
					if (annullato.contains("Errore"))
						return "Impossibile cancellare automaticamente la prenotazione!";
					else {
						closing();
						return "errorPrenotato";
					}
				}
			} else
				return "errorParcheggiato";
		}
		return ""; // Non è ancora stata stabilita alcuna connessione, non serve chiuderla
	}

	// metodo per la chiusura della connessione
	private void closing() {
		try {
			this.socketOutput.writeInt(7);
			this.socketOutput.flush();
			this.client.close();
		} catch (IOException e) {
			System.err.println("Errore nella comunicazione col server, riprova più tardi! ");
			System.exit(-1);
		}
	}

	/*
	 * metodo interno per la gestione del parcheggio con prenotazione Un
	 * automobilista può parcheggiare in un posto prenotato solo se il ticket in suo
	 * possesso e quello di prenotazione coincidono.
	 */
	private String parcheggiaConPrenotazione() {
		try {
			this.socketOutput.writeInt(8);
			this.socketOutput.flush();

			this.socketOutput.writeInt(this.numParcheggio);
			this.socketOutput.flush();

			this.socketOutput.writeObject(this.auto);
			this.socketOutput.flush();

			this.socketOutput.writeInt(this.numPosto);
			this.socketOutput.flush();

			this.socketOutput.writeUTF(this.ticket);
			this.socketOutput.flush();

			String esito = this.socketInput.readUTF();

			if (esito.equals("error"))
				return "Errore non è il posto che hai prenotato";
		} catch (IOException e) {
			System.err.println("Errore nella comunicazione col server, riprova più tardi! ");
			System.exit(-1);
		}

		this.auto = null;
		return "Auto parcheggiata correttamente";
	}

	/*
	 * permette all'automobilista di annullare una prenotazione precedentemente
	 * effettuata Se cerca di annullare una prenotazione non realmente effettuata o
	 * se la macchina è parcheggiata viene ritornato un messaggio di errore.
	 */
	public String annullaPrenotazione() {
		if (this.client != null && this.auto != null && !this.ticket.equals("")) {
			try {
				this.socketOutput.writeInt(9);
				this.socketOutput.flush();

				this.socketOutput.writeInt(this.numParcheggio);
				this.socketOutput.flush();

				this.socketOutput.writeInt(this.numPosto);
				this.socketOutput.flush();

				this.socketOutput.writeUTF(this.ticket);
				this.socketOutput.flush();

				String annullato = this.socketInput.readUTF();

				if (annullato.equals("error"))

					return "Errore non è il posto che hai prenotato";

			} catch (IOException e) {
				System.err.println("Errore nella comunicazione col server, riprova più tardi! ");
				System.exit(-1);
			}
			this.ticket = "";
			this.numPosto = -1;
			return "Prenotazione annullata con successo!";
		} else if (this.auto != null && this.ticket.equals(""))
			return "Non hai prenotato alcun posto, parcheggia o prenota!";
		else
			return "Hai già parcheggiato, ritira la macchina!";
	}
}
