/*
 * 
 * made by:
 * Confalonieri Riccardo
 * riccardoconfalonieri98@gmail.com
 * github.com/rconfa
 *
 */

package carParks;

import java.io.Serializable;

// Classe con le proprietà essenziali di un'automobile.
public class Automobile implements Serializable{
	private String targa;
	private String marca;
	private String modello;
	private int cilindrata;

	public Automobile(String targa, String marca, String modello, int cilindrata) {
		this.targa = targa;
		this.marca = marca;
		this.modello = modello;
		this.cilindrata = cilindrata;
	}

	public String getTarga() {
		return targa;
	}
}
