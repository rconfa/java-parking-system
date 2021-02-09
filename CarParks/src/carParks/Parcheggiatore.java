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

public class Parcheggiatore {
	private boolean occupato;
	private int id;

	public Parcheggiatore(int id, boolean occupato) {
		this.id = id;
		this.setOccupato(occupato);
	}

	/*
	 * Permette di effettuare l'operazione di consegna di un'automobile. Simula un
	 * tempo di attesa "reale" e genera un ticket univoco.
	 */
	public String prendere() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		UUID uuid1 = UUID.randomUUID();
		String ticket = uuid1.toString();
		return ticket;
	}

	/*
	 * Permette di consegnare l'automobile al suo legittimo proprietario. Simula il
	 * tempo "reale" dell'operazione.
	 */
	public void restituisci() {
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean isOccupato() {
		return occupato;
	}

	public void setOccupato(boolean occupato) {
		this.occupato = occupato;
	}

	@Override
	public String toString() {
		return "id: " + this.id + " occupato: " + this.occupato;
	}

}
