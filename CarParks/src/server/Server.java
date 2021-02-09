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
import java.net.ServerSocket;
import java.net.Socket;

class Server {

	public static void main(String[] args) throws IOException {
		
		GestioneParcheggi gp = new GestioneParcheggi();
		gp.init();
		
		int serverPort = 8080;
		
		// Server in ascolto sulla porta 8080
		ServerSocket serverSocket = new ServerSocket(serverPort);

		// loop infinito per accettare le richieste
		while (true) {
			Socket socket = null;

			try {
				//sincronizzo l'accept perchè non è threadSafe
				synchronized(serverSocket) {
					// ricevo la richiesta di un client
					socket = serverSocket.accept();
				}
				
				System.out.println("Nuovo client connesso: " + socket);
				
				// Creazione del nuovo oggetto thread
				Thread t = new ClientHandler(socket, gp);
				t.start();

			} catch (Exception e) {
				socket.close();
				e.printStackTrace();
				break;
			}
		}
	}
}
