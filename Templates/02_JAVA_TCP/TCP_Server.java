/**
 * Nicola	Sebastianelli
 * 
 * 0000722894
 * 
 * TCP_Server.java
 */

import java.io.*;
import java.net.*;

public class TCP_Server {
	public static final int PORT = 54321;

	public static void main(String[] args) throws IOException {
		int port = -1; // Porta sulla quale ascolta il server

		//Controllo arogmenti
		try {
			if (args.length == 1) {
				port = Integer.parseInt(args[0]);
				if (port < 1024 || port > 65535) {
					System.out.println("Usage: 1024<serverPort<65535");
					port = PORT;
				}
				System.out.println("Usage port: " + port);
			} else if (args.length == 0) {
				port = PORT;
				System.out.println("Usage port: " + port);
			} else {
				System.out.println("Usage: java TCP_Stream_Server or java TCP_Stream_Server port");
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
			System.out.println("Usage: java TCP_Stream_Server or java TCP_Stream_Server port");
			System.exit(1);
		}

		//Dichiarazione di oggetti utilizzati dal server per la comunicazione
		ServerSocket serverSocket = null;
		Socket clientSocket = null;

		//Non dichiarare qua le variabili per la logica del programma, ma nel ChildServer

		//Creazione server socket
		try {
			serverSocket = new ServerSocket(port);

			// Se il demone deve ripartire in questo modo può farlo sullo stesso indirizzo
			serverSocket.setReuseAddress(true);	
			System.out.println("TCP_Stream_Server: creata la server socket: " + serverSocket);		
		} catch (Exception e) {
			System.err.println("Error Server: problemi nella creazione della server socket: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
		try {
			System.out.println("TCP_Stream_Server: avviato!");
			while (true) {
				System.out.println("TCP_Stream_Server: in attesa di richieste...");
				try {
					clientSocket = serverSocket.accept(); // bloccante
					//clientSocket.setSoTimeout(60000);  non indispensabile
					System.out.println("TCP_Stream_Server: connessione accettata: " + clientSocket);
				} catch (Exception e) {
					System.err.println("TCP_Stream_Server: problemi nella accettazione della connessione: " + e.getMessage());
					e.printStackTrace();
					continue;
				}

				// Creazione e avvio del Server child
				try {
					new ChildServerThread(clientSocket).start();
				} catch (Exception e) {
					System.err.println("TCP_Stream_Server: problemi nel server thread: " + e.getMessage());
					e.printStackTrace();
					continue;
				}
			}
		}
		catch (Exception e) {
			System.err.println("Error Server: ");
			e.printStackTrace();
			// Chiusura di stream e socket
			System.out.println("TCP_Stream_Server: termino...");
			System.exit(2);
		}
	}
}

/**
 * Thread lanciato per ogni richiesta accettata versione per il trasferimento di file binari
 */
class ChildServerThread extends Thread{

	private Socket clientSocket = null;

	// Costruttore
	public ChildServerThread(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	//Main invocabile con 0 o 1 argomenti. Argomento possibile -> porta su cui il server ascolta.
	public void run() {

		//Dichiarazione di oggetti utilizzati dal server per la comunicazione
		DataInputStream inSock;
		DataOutputStream outSock;
        try {
            // Creazione stream di input e out da socket
            inSock = new DataInputStream(clientSocket.getInputStream());
            outSock = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException ioe) {
            System.out.println("Problemi nella creazione degli stream di input/output su socket: ");
            ioe.printStackTrace();
            return;
        } catch (Exception e) {
            System.out.println("Problemi nella creazione degli stream di input/output su socket: ");
            e.printStackTrace();
            return;
        }
        
        //Dichiarare variabili per la logica del programma
		String richiesta=null; //TODO impostare tipo di variabile
		String risposta=null; //TODO impostare tipo di variabile
		try{
			//BODY SERVER
			while(true){
				try	{
					System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Attendo richiesta\n");

					//Lettura stream
					richiesta = inSock.readUTF(); //TODO cambiare tipo di read
					System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Ricevuto: "+richiesta);

					
					//TODO ELABORAZIONE RICHIESTA
					risposta = richiesta;

					
					//Scrittura stream
					System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Invio: "+risposta);
					outSock.writeUTF(risposta); //TODO cambiare tipo di write	
				}
				catch (EOFException eof) {
					System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" raggiunto EOF");
					//eof.printStackTrace();
					// finito il ciclo di ricezioni termino la comunicazione
					break;
				} catch (SocketTimeoutException ste) {
					System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Timeout scattato: ");
					ste.printStackTrace();
					break;
				} catch (Exception e) {
					System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Problemi, i seguenti : ");
					e.printStackTrace();
					break;
				}
			}
		}
		catch (Exception e) {
            System.out
            .println("Errore TCP_Stream_ServerThread: "+ Thread.currentThread().getName());
			e.printStackTrace();
		}
        try{
            outSock.flush();
            clientSocket.shutdownOutput();
            clientSocket.shutdownInput();
        } catch (IOException e) {
            System.out.println("Problemi nella chiusura della socket: ");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error: ");
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("TCP_Stream_ServerThread: "
				+ Thread.currentThread().getName()+" Termino...");
	}
}
