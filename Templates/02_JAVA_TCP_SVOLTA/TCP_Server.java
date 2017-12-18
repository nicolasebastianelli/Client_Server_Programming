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
	public static final int TABSIZE=5;
	private static Tabella[] table;
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
		
		//inizzializzazione struttura
		table=new Tabella[TABSIZE];
		for(int i =0 ;i<TABSIZE;i++){
			table[i]=new Tabella();
		}
		table[1]=new Tabella("Parita del cuore", "Calcio", "12/01/2014", "Milano", 30, 30);
		table[2]=new Tabella("Juve-Inter", "Calcio", "14/01/2014", "Milano", 30, 30);
		table[3]=new Tabella("Napoli-Juve", "Calcio", "13/01/2014", "Napoli", 30, 31);
		table[4]=new Tabella("Inter-Napoli", "Calcio", "15/01/2014", "Napoli", 30, 40);
		System.out.println("Descrizione\tTipoe\tData\tLuogo\tDisponibilita\tPrezzo");
		for(int i =0 ; i<TABSIZE;i++)
			System.out.println(table[i].toString());
		
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

	public static Tabella[] getTable() {
		return table;
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
		String richiesta=null; 
		String tipo=null,luogo=null;
		int soglia,count;
		try{
			//BODY SERVER
			while(true){
				try	{
					System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Attendo richiesta\n");

					//Lettura stream
					richiesta = inSock.readUTF(); //TODO cambiare tipo di read
					if(richiesta.equals("A")){
						count=0;
						System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Ricevuta richiesta di visualizzazione eventi dato tipo e luogo");
						tipo = inSock.readUTF();
						luogo = inSock.readUTF();
						for(int i=0;i<TCP_Server.TABSIZE;i++)
						{
							if(TCP_Server.getTable()[i].getTipo().equals(tipo)&&TCP_Server.getTable()[i].getLuogo().equals(luogo))
							{
								count++;
							}
						}
						outSock.writeInt(count);
						if(count==0){
							System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Nessun evento trovato");
							continue;
						}
						for(int i=0;i<TCP_Server.TABSIZE;i++)
						{
							if(TCP_Server.getTable()[i].getTipo().equals(tipo)&&TCP_Server.getTable()[i].getLuogo().equals(luogo))
							{
								System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Invio: "+TCP_Server.getTable()[i].getDescrizione());
								outSock.writeUTF(TCP_Server.getTable()[i].toString());
							}
						}						
					}
					else if(richiesta.equals("B")){
						System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Ricevuta richiesta di visualizzazione eventi dato prezzo massimo");
						soglia= inSock.readInt();
						count=0;
						for(int i=0;i<TCP_Server.TABSIZE;i++)
						{
							if(TCP_Server.getTable()[i].getPrezzo()<=soglia&&!TCP_Server.getTable()[i].getDescrizione().equals("L"))
							{
								count++;
							}
						}
						outSock.writeInt(count);
						if(count==0){
							System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Nessun evento trovato");
							continue;
						}
						for(int i=0;i<TCP_Server.TABSIZE;i++)
						{
							if(TCP_Server.getTable()[i].getPrezzo()<=soglia&&!TCP_Server.getTable()[i].getDescrizione().equals("L"))
							{
								System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Invio: "+TCP_Server.getTable()[i].getDescrizione());
								outSock.writeUTF(TCP_Server.getTable()[i].toString());
							}
						}
					}
				}
				catch (EOFException eof) {
					System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" raggiunto EOF");
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
			e.printStackTrace();
			// chiusura di stream e socket
			System.out
			.println("Errore TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" termino...");
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
