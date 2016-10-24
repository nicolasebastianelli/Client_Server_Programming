//Nicola Sebastianelli 0000722894 esercitazione 1

import java.io.*;
import java.net.*;

// Thread lanciato per ogni richiesta accettata
// versione per il trasferimento di file binari
class PutFileServerThread extends Thread{

	private Socket clientSocket = null;

	public PutFileServerThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public void run() {
		DataInputStream inSock;
		DataOutputStream outSock;
		try {
			String nomeFile;
			long length;
			while(true){
				try {
					// creazione stream di input e out da socket
					inSock = new DataInputStream(clientSocket.getInputStream());
					outSock = new DataOutputStream(clientSocket.getOutputStream());
				}      
				catch (IOException ioe) {
					System.out
					.println("Problemi nella creazione degli stream di input/output "
							+ "su socket: ");
					ioe.printStackTrace();
					// il server continua l'esecuzione riprendendo dall'inizio del ciclo
					return;
				}
				catch (Exception e) {
					System.out
					.println("Problemi nella creazione degli stream di input/output "
							+ "su socket: ");
					e.printStackTrace();
					return;
				}
				try{
					nomeFile = inSock.readUTF();
				}
				catch(Exception e){
					System.out.println("Terminata connessione con " + clientSocket);
					clientSocket.close();
					return;
				}
				String esito;
				if (nomeFile == null) {
					System.out.println("Problemi nella ricezione del nome del file: ");
					clientSocket.close();
					return;
				}
				FileOutputStream outFile = null;
				File curFile = new File(nomeFile);
				if (curFile.exists()) {
					esito = "salta";
				} else esito = "attiva";
				
				try{
					outSock.writeUTF(esito);
					System.out.println("Inviato esito: " + esito + " nella ricezione del file "+nomeFile);
				}
				catch(Exception e){
					System.out.println("Problemi nell'invio esito: " + esito + " nella ricezione del file "+nomeFile+":");
					e.printStackTrace();
					clientSocket.close();
					System.out
					.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
					return;
				}
				
				if(esito.equals("salta"))
					continue;
				outFile = new FileOutputStream(nomeFile);	
				
				// ricevo lunghezza file
				try {
					length = inSock.readLong();
					System.out.println("Ricevuta dimensione "+length+"byte del file "+nomeFile);
				}      
				catch (Exception e) {
					System.err
					.println("\nProblemi durante la ricezione della dimensione del file: "
							+ e.getMessage());
					e.printStackTrace();
					clientSocket.close();
					System.out.println("Terminata connessione con " + clientSocket);
					return;
				}
				if (length < 0) {
					System.out.println("Problemi nella dimensione del file < 0");
					clientSocket.close();
					return;
				}			
				try {
					System.out.println("Ricevo il file " + nomeFile + ": \n");
					FileUtility.trasferisci_a_byte_file_binario(inSock,
							new DataOutputStream(outFile),length);
					System.out.println("\nRicezione del file " + nomeFile + " terminata\n");
					// chiusura file
					outFile.close();
				}           
				catch (Exception e) {
					System.err
					.println("\nProblemi durante la ricezione e scrittura del file: "
							+ e.getMessage());
					e.printStackTrace();
					clientSocket.close();
					System.out.println("Terminata connessione con " + clientSocket);
					return;
				}
			}
		}
		// qui catturo le eccezioni non catturate all'interno del while
		// in seguito alle quali il server termina l'esecuzione
		catch (Exception e) {
			e.printStackTrace();
			System.out
			.println("Errore irreversibile, PutFileServerThread: termino...");
			System.exit(3);
		}
	} // run

} // PutFileServerThread class

public class PutFileServerCon {
	public static final int PORT = 1050; //default port

	public static void main(String[] args) throws IOException {

		int port = -1;

		/* controllo argomenti */
		try {
			if (args.length == 1) {
				port = Integer.parseInt(args[0]);
				if (port < 1024 || port > 65535) {
					System.out.println("Usage: java LineServer [serverPort>1024]");
					System.exit(1);
				}
			} else if (args.length == 0) {
				port = PORT;
			} else {
				System.out
				.println("Usage: java PutFileServerThread or java PutFileServerThread port");
				System.exit(1);
			}
		} //try
		catch (Exception e) {
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
			System.out
			.println("Usage: java PutFileServerThread or java PutFileServerThread port");
			System.exit(1);
		}

		ServerSocket serverSocket = null;
		Socket clientSocket = null;

		try {
			serverSocket = new ServerSocket(port);
			serverSocket.setReuseAddress(true);
			System.out.println("PutFileServerCon: avviato ");
			System.out.println("Server: creata la server socket: " + serverSocket);
		}
		catch (Exception e) {
			System.err
			.println("Server: problemi nella creazione della server socket: "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}

		try {

			while (true) {
				System.out.println("Server: in attesa di richieste...\n");

				try {
					// bloccante fino ad una pervenuta connessione
					clientSocket = serverSocket.accept();
					System.out.println("Server: connessione accettata: " + clientSocket);
				}
				catch (Exception e) {
					System.err
					.println("Server: problemi nella accettazione della connessione: "
							+ e.getMessage());
					e.printStackTrace();
					continue;
				}

				// serizio delegato ad un nuovo thread
				try {
					new PutFileServerThread(clientSocket).start();
				}
				catch (Exception e) {
					System.err.println("Server: problemi nel server thread: "
							+ e.getMessage());
					e.printStackTrace();
					continue;
				}

			} // while
		}
		// qui catturo le eccezioni non catturate all'interno del while
		// in seguito alle quali il server termina l'esecuzione
		catch (Exception e) {
			e.printStackTrace();
			// chiusura di stream e socket
			System.out.println("PutFileServerCon: termino...");
			System.exit(2);
		}

	}
} // PutFileServerCon class
