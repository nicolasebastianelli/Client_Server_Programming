//Nicola Sebastianelli 0000722894 esercitazione 2

import java.net.*;
import java.io.*;

public class PutFileClient {

	public static void main(String[] args) throws IOException {

		InetAddress addr = null;
		int port = -1;

		try{ //check args
			if(args.length == 2){
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
			} else{
				System.out.println("Usage: java PutFileClient serverAddr serverPort");
				System.exit(1);
			}
		} //try
		// Per esercizio si possono dividere le diverse eccezioni
		catch(Exception e){
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
			System.out.println("Usage: java PutFileClient serverAddr serverPort");
			System.exit(2);
		}

		// oggetti utilizzati dal client per la comunicazione e la lettura del file
		// locale
		Socket socket = null;
		FileInputStream inFile = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		String directory = null;

		// creazione stream di input da tastiera
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		try{
			socket = new Socket(addr, port);
			socket.setSoTimeout(30000);
			System.out.println("Creata la socket: " + socket);
		}
		catch(Exception e){
			System.out.println("Problemi nella creazione della socket: ");
			e.printStackTrace();
			return;
		}
		System.out
		.print("PutFileClient Started.\n\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");

		try{
			while ( (directory=stdIn.readLine()) != null){
				File folder = new File(directory);
				File[] listOfFiles =null;
				if(folder.isDirectory()){
					// creazione socket
					listOfFiles = folder.listFiles();

					// creazione stream di input/output su socket
					try{
						inSock = new DataInputStream(socket.getInputStream());
						outSock = new DataOutputStream(socket.getOutputStream());
					}
					catch(IOException e){
						System.out
						.println("Problemi nella creazione degli stream su socket: ");
						e.printStackTrace();
						System.out
						.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
						continue;
						// il client continua l'esecuzione riprendendo dall'inizio del ciclo
					}
				}
				// se la richiesta non e corretta non proseguo
				else{
					System.out.println(folder+" non e un direttorio");
					System.out
					.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
					// il client continua l'esecuzione riprendendo dall'inizio del ciclo
					continue;
				}
				if(listOfFiles.length==0)
				{
					System.out.println("Direttorio vuoto");
					System.out
					.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
					// il client continua l'esecuzione riprendendo dall'inizio del ciclo
					continue;
				}
				String esito;
				long length;
				for(int i =0 ; i< listOfFiles.length;i++){
					if(!listOfFiles[i].isFile())
					{
						System.out
						.println(listOfFiles[i].getName()+ " Non e un file");
						continue;
					}
					try{
						inFile = new FileInputStream(listOfFiles[i]);
					}
					/*
					 * abbiamo gia' verificato che esiste, a meno di inconvenienti, es.
					 * cancellazione concorrente del file da parte di un altro processo, non
					 * dovremmo mai incorrere in questa eccezione.
					 */
					catch(FileNotFoundException e){
						System.out
						.println("Problemi nella creazione dello stream di input da "
								+ listOfFiles[i].getName() + ": ");
						e.printStackTrace();
						socket.close();
						System.out
						.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
						break;
					}

					// trasmissione del nome

					if(!listOfFiles[i].getName().matches("^[aeiouAEIOU].*[0-9]$")){
						System.out
						.println("File "+ listOfFiles[i].getName() + " non inizia per vocale o non finisce per numero");
						continue;
					}
					try{
						outSock.writeUTF(listOfFiles[i].getName());
						System.out.println("Inviato il nome del file " + listOfFiles[i].getName());
					}
					catch(Exception e){
						System.out.println("Problemi nell'invio del nome di " + listOfFiles[i].getName()
								+ ": ");
						e.printStackTrace();
						socket.close();
						System.out
						.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
						break;
					}

					try{
						esito = inSock.readUTF();
						System.out.println("Esito trasmissione: " + esito);
					}
					catch(SocketTimeoutException ste){
						System.out.println("Timeout scattato: ");
						ste.printStackTrace();
						socket.close();
						System.out
						.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
						// il client continua l'esecuzione riprendendo dall'inizio del ciclo
						break;          
					}
					catch(Exception e){
						System.out
						.println("Problemi nella ricezione dell'esito, i seguenti: ");
						e.printStackTrace();
						socket.close();
						System.out
						.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
						break;
						// il client continua l'esecuzione riprendendo dall'inizio del ciclo
					}

					if(esito.equals("salta")){
						System.out
						.println("File "+listOfFiles[i].getName()+" gia presente");
						continue;
					}					
					try{
						length=listOfFiles[i].length();
						outSock.writeLong(length);
						System.out.println("Invio lunghezza file " + listOfFiles[i].getName());
					}
					catch(Exception e){
						System.out.println("Problemi nell'invio del nome di " + listOfFiles[i].getName()
								+ ": ");
						e.printStackTrace();
						socket.close();
						System.out
						.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
						break;
					}

					System.out.println("Inizio la trasmissione di " + listOfFiles[i].getName());

					// trasferimento file
					try{
						//FileUtility.trasferisci_a_linee_UTF_e_stampa_a_video(new DataInputStream(inFile), outSock);
						FileUtility.trasferisci_a_byte_file_binario(new DataInputStream(inFile), outSock,length);
						inFile.close(); 			// chiusura file
						System.out.println("Trasmissione di " + listOfFiles[i].getName() + " terminata ");
					}
					catch(SocketTimeoutException ste){
						System.out.println("Timeout scattato: ");
						ste.printStackTrace();
						socket.close();
						System.out
						.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
						// il client continua l'esecuzione riprendendo dall'inizio del ciclo
						break;          
					}
					catch(Exception e){
						System.out.println("Problemi nell'invio di " +  listOfFiles[i].getName()  + ": ");
						e.printStackTrace();
						socket.close();
						System.out
						.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
						break;
					}		
				}
				System.out
				.print("\n^D(Unix)/^Z(Win)+invio per uscire, oppure immetti nome directory: ");
			}
			socket.shutdownInput();
			socket.shutdownOutput();
			System.out.println("PutFileClient: termino...");
		}
		// qui catturo le eccezioni non catturate all'interno del while
		// quali per esempio la caduta della connessione con il server
		// in seguito alle quali il client termina l'esecuzione
		catch(Exception e){
			System.err.println("Errore irreversibile, il seguente: ");
			e.printStackTrace();
			System.err.println("Chiudo!");
			System.exit(3); 
		}
	} // main
} // PutFileClient
