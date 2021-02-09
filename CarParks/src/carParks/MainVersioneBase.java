/*
 * 
 * made by:
 * Confalonieri Riccardo
 * riccardoconfalonieri98@gmail.com
 * github.com/rconfa
 *
 */

package carParks;

public class MainVersioneBase {

	public static void main(String[] args){
		Posto[] posti = new Posto[10];
		Parcheggiatore[] parcheggiatori = new Parcheggiatore[8];
		Parcheggio p1 = new Parcheggio("parcheggio1", "via Milano 60", posti, parcheggiatori);
		Automobile[] auto = new Automobile[12];
		Automobilista[] automobilisti = new Automobilista[12];
		
		for (int i = 0; i < posti.length; i++)
			posti[i] = new Posto(i, false);

		for (int i = 0; i < parcheggiatori.length; i++)
			parcheggiatori[i] = new Parcheggiatore(i, false);
			
		for (int i = 0; i < auto.length; i++)
			auto[i] = new Automobile("targa" + i, "Fiat", "Punto", i * 30);

		for (int i = 0; i < automobilisti.length; i++)
			automobilisti[i] = new Automobilista("Name" + i, "Surname" + i, "CodFis" + i, auto[i], p1);

		for (Automobilista a : automobilisti) {
			a.start();
		}

		try {
			Thread.sleep(10000);
			for (Automobilista a : automobilisti)
				a.stopExecution();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			for (Automobilista a : automobilisti)
				a.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		System.out.println("Main terminated");

	}

}
