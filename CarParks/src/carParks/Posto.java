/*
 * 
 * made by:
 * Confalonieri Riccardo
 * riccardoconfalonieri98@gmail.com
 * github.com/rconfa
 *
 */

package carParks;

import java.util.UUID;

public class Posto {
	private int numero;
	private boolean occupato;
	private boolean prenotato;
	private String targa;
	private String ticket;
	private Automobile autoParcheggiata;

	public Posto(int numero, boolean occupato) {
		this.targa = "";
		this.numero = numero;
		this.setOccupato(occupato);
		this.autoParcheggiata = null;
		this.prenotato = false;
	}

	// Se un automoblista prenota, setto il posto a prenotato e ritorno il ticket.
	// Non metto la targa, questa verrà inserita nel momento in cui parcheggerà.
	public String prenota() {
		UUID uuid1 = UUID.randomUUID();
		String ticket = uuid1.toString();
		this.ticket = ticket;
		this.setPrenotato(true);
		return ticket;
	}

	public void annullaPrenotazione() {
		this.setPrenotato(false);
		this.ticket = "";
	}
	
	
	public int getNumero() {
		return numero;
	}

	public boolean isPrenotato() {
		return prenotato;
	}

	public void setPrenotato(boolean prenotato) {
		this.prenotato = prenotato;
	}

	public Automobile getAutoParcheggiata() {
		return autoParcheggiata;
	}

	public void setAutoParcheggiata(Automobile autoParcheggiata) {
		this.autoParcheggiata = autoParcheggiata;
	}

	public boolean isOccupato() {
		return occupato;
	}

	public void setOccupato(boolean occupato) {
		this.occupato = occupato;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;

	}

	public String getTarga() {
		return targa;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}

	@Override
	public String toString() {
		return "Numero: " + this.numero + " targa: " + this.targa + " isOccupato: " + this.isOccupato() + " ticket: "
				+ this.ticket + " isPrenotato: " + this.isPrenotato();
	}
}
