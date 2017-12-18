/**
 * Nicola	Sebastianelli
 * 
 * 0000722894
 * 
 * TCP_Server.java
 */

import java.io.*;
import java.net.*;
import java.util.Stack;

public class TCP_Server {
	public static final int TABSIZE=5;
	public static final int K=5;
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
		String parole[];
		parole = new String[5];
		for (int i=0;i<5;i++) {
			parole[i] ="L";
		}
		parole[1]="basso";
		parole[4]="acuro";
		table[1]=new Tabella("provino 1", "1.avi", 180, parole);
		parole = new String[5];
		for (int i=0;i<5;i++) {
			parole[i] ="L";
		}
		parole[3]="andante";
		table[2]=new Tabella("provino 2", "2.avi", 345, parole);
		parole = new String[5];
		for (int i=0;i<5;i++) {
			parole[i] ="L";
		}
		parole[3]="dolce";
		parole[4]="andante";
		table[3]=new Tabella("provino 3", "3.avi", 830, parole);

		System.out.print("Nome\tFile\tDurata");
		for(int i =0 ; i<TABSIZE;i++)
		{
			System.out.print("\tParola "+(i+1));
		}
		System.out.println();
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

	static void trasferisci_N_byte_file_binario(DataInputStream src, DataOutputStream dest, long daTrasferire) throws IOException
	{
		int cont=0;
		int buffer=0;
		try
		{
			while (cont < daTrasferire)
			{
				buffer=src.read();
				dest.write(buffer);
				cont++;
			}
			dest.flush();
			System.out.println("Byte trasferiti: " + cont);
		}
		catch (EOFException e)
		{
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
		}
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
		String nome=null,nomef=null,parola=null;
		String parole[] = new String[5];
		int durata,nparole,ok=-1,count;
		try{
			//BODY SERVER
			while(true){
				try	{
					System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Attendo richiesta\n");

					//Lettura stream
					richiesta = inSock.readUTF(); //TODO cambiare tipo di read
					if(richiesta.equals("A")){
						ok =-1;
						System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Ricevuta richiesta di inserimeto provini");
						nome = inSock.readUTF();
						nomef = inSock.readUTF();
						durata = inSock.readInt();
						nparole = inSock.readInt();
						for (int i=0;i<5;i++) {
							parole[i] ="L";
						}
						for(int i =0;i<nparole;i++){
							parole[i]=inSock.readUTF();
						}
						for(int i=0;i<TCP_Server.TABSIZE;i++)
						{
							if(TCP_Server.getTable()[i].getProvino().equals("L"))
							{
								TCP_Server.getTable()[i] = new Tabella(nome, nomef, durata, parole);
								ok=1;
								break;
							}
						}
						outSock.writeInt(ok);
						if(ok==-1){
							System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Tabella piena");
							continue;
						}
						else
						{
							System.out.print("Nome\tFile\tDurata");
							for(int i =0 ; i<TCP_Server.TABSIZE;i++)
							{
								System.out.print("\tParola "+(i+1));
							}
							System.out.println();
							for(int i =0 ; i<TCP_Server.TABSIZE;i++)
								System.out.println(TCP_Server.getTable()[i].toString());	
							File a = new File(nomef);
							FileOutputStream fout = new FileOutputStream(a);
							DataOutputStream dout = new DataOutputStream(fout);
							long l = inSock.readLong();
							trasferisci_N_byte_file_binario(inSock, dout, l);
							dout.close();
							fout.close();
							System.out.println("File "+nomef+" ricevuto");
						}
					}
					else if(richiesta.equals("B")){
						System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Ricevuta richiesta di download file provini data una parola");
						parola= inSock.readUTF();
						count=0;
						for(int i=0;i<TCP_Server.TABSIZE;i++)
						{
							for(int j=0;j<TCP_Server.K;j++)
							{
								if(TCP_Server.getTable()[i].getParole()[j].equals(parola))
								{
									count++;
									break;
								}
							}
						}
						outSock.writeInt(count);
						if(count==0){
							System.out.println("TCP_Stream_ServerThread: "+ Thread.currentThread().getName()+" Nessun provino con quella parola trovata");
							continue;
						}
						for(int i=0;i<TCP_Server.TABSIZE;i++)
						{
							for(int j=0;j<TCP_Server.K;j++)
							{
								if(TCP_Server.getTable()[i].getParole()[j].equals(parola))
								{
									outSock.writeUTF(TCP_Server.getTable()[i].getFile());
									File a = new File(TCP_Server.getTable()[i].getFile());
									FileInputStream fin = new FileInputStream(a);
									DataInputStream din = new DataInputStream(fin);
									trasferisci_N_byte_file_binario(din, outSock, a.length());
									din.close();
									fin.close();
									break;
								}
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
