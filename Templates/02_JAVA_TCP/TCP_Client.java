/**
 * Nicola	Sebastianelli
 * 
 * 0000722894
 * 
 * TCP_Client.java
 */

import java.net.*;
import java.io.*;

public class TCP_Client {
	public static void main(String[] args) throws IOException {

		// Dichiarazione endopoint di comunicazione ottenuti dai parametri
		InetAddress addr = null;
		int port = -1;

		//Controllo arogmenti
		try {
			if (args.length == 2) {
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
			} else {
				System.out.println("Usage: java TCP_Stream_Client serverAddr serverPort");
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
			System.out.println("Usage: java TCP_Stream_Client serverAddr serverPort");
			System.exit(2);
		}

		//Dichiarazione oggetti utilizzati dal client per la comunicazione
		Socket socket = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		//Dichiarare variabili per la logica del programma
		String richiesta = null; // TODO IMPOSTARE TIPO VARIABILE
		String risposta=null;    // TODO IMPOSTARE TIPO VARIABILE

		//Creazione socket
		try {
			socket = new Socket(addr, port);
			socket.setSoTimeout(30000);
			System.out.println("TCP_Stream_Client: Creata la socket: " + socket);
		} catch (Exception e) {
			System.out.println("Problemi nella creazione della socket: ");
			e.printStackTrace();
			System.exit(1);
		}
        
        //Creazione stream di I/O su socket
        try {
            inSock = new DataInputStream(socket.getInputStream());
            outSock = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Problemi nella creazione degli stream su socket: ");
            e.printStackTrace();
            System.exit(1);
        }
        
        //BODY CLIENT
		String cycleMessage ="\n^D(Unix)/^Z(Win)+invio per uscire, immetti input: ";
		System.out.print(cycleMessage);
		while ((richiesta=stdIn.readLine()) != null) {
			try {
				
				//TODO INTERAZIONE CON L'UTENTE
				
				outSock.writeUTF(richiesta); //TODO CAMBIARE TIPO DI WRITE
				// NB: per file usare: trasferisci_a_byte_file_binario(new DataInputStream(inFile), outSock);						
				System.out.println("TCP_Stream_Client: Inviato: "+richiesta);
                
                risposta = inSock.readUTF(); //TODO CAMBIARE TIPO DI READ
                //TODO ELABORAZIONE RISPOSTA
                System.out.println("TCP_Stream_Client: Ricevuto: " + risposta);

            } catch (SocketTimeoutException ste) {
				System.out.println("Timeout scattato: ");
				ste.printStackTrace();
                break;
			} catch (Exception e) {
				System.out.println("Error: ");
				e.printStackTrace();
                break;
			}
			System.out.print(cycleMessage);
		}
		
		// libero le risorse occupate: socket, file, ...
        try{
            outSock.flush();
            socket.shutdownOutput();
            socket.shutdownInput();
        } catch (IOException e) {
            System.out.println("Problemi nella chiusura della socket: ");
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            System.out.println("Error: ");
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("TCP_Stream_Client: Termino...");
    }
}
