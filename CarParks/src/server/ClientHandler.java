/*
 * 
 * made by:
 * Confalonieri Riccardo
 * riccardoconfalonieri98@gmail.com
 * github.com/rconfa
 *
 */

package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import carParks.Automobile;

class ClientHandler extends Thread {

	private Socket socket;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	private GestioneParcheggi gp;

	public ClientHandler(Socket socket, GestioneParcheggi gp) {
		this.socket = socket;
		this.gp = gp;

		try {
			this.input = new ObjectInputStream(this.socket.getInputStream());
			this.output = new ObjectOutputStream(this.socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		int richiesta;
		while (true) {
			try {
				richiesta = input.readInt();

				//7 -> il client vuole chiudere la connessione
				if (richiesta == 7) {
					System.out.println("Chiudo connessione con il client " + this.socket);
					closeConn();
					break;
				}

				switch (richiesta) {
				
				//1 -> Torno la lista dei parcheggi disponibili
				case 1: {
					String parcheggi = this.gp.parcheggiDisponibili();
					output.writeUTF(parcheggi);
					output.flush();
					break;
				}
				//3 -> Parcheggia l'auto e torno il ticket del 
				case 3: {
					int park = input.readInt();
					Automobile auto = (Automobile) input.readObject();

					String ticket = this.gp.parcheggia(park, auto);
					output.writeUTF(ticket);
					output.flush();
					break;
				}
				//4 -> Ritira e torno l'auto
				case 4: {
					int park = input.readInt();
					String targa = input.readUTF();
					String ticket = input.readUTF();
	
					Automobile auto = this.gp.ritira(park, ticket, targa);

					output.writeObject(auto);
					output.flush();
					break;
				}
				//5 -> Torna la lista dei posti disponibili
				case 5: {
					int park = input.readInt();

					String posti = this.gp.postiDisponibili(park);

					output.writeUTF(posti);
					output.flush();
					break;
				}
				//6 -> Prenota un posto
				case 6: {
					int park = input.readInt();
					int posto = input.readInt();

					String ticket = this.gp.prenotaPosto(park, posto);
					
					output.writeUTF(ticket);
					output.flush();
					break;
				}
				//8 -> Parcheggia l'auto nel posto prenotato
				case 8: {
					int park = input.readInt();
					Automobile auto = (Automobile) input.readObject();
					int posto = input.readInt();
					String ticketPrenotazione = input.readUTF();
					
					String esito = this.gp.parcheggiaConPrenotazione(park, auto, posto,ticketPrenotazione);

					output.writeUTF(esito);
					output.flush();
					break;
				}
				//9 -> Annulla la prenotazione di un posto
				case 9: {
					int park = input.readInt();
					int posto = input.readInt();
					String ticketPrenotazione = input.readUTF();
					
					String annullato = this.gp.annullaPrenotazionePosto(park, posto, ticketPrenotazione);
					output.writeUTF(annullato);
					output.flush();
					break;
				}
				default: {
					output.writeUTF("Input non valido");
					output.flush();
					break;
				}
				}
			} catch (IOException | ClassNotFoundException e) {
				System.err.println("ERROR: Il client ha chiuso forzatamente la connessione!");
				closeConn();
				break;
			}
		}

		
	}
	
	private void closeConn() {
		try {
			// Chiudo gli I/O
			this.input.close();
			this.output.close();
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}