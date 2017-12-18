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
		String richiesta = null,risposta=null,buff; // TODO IMPOSTARE TIPO VARIABILE
		String luogo=null,tipo=null;
		int count,soglia = 0;
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
		String cycleMessage ="\nInserire: \nA) per visualizzare eventi dato tipo e luogo\nB) per visualizzare eventi dato prezzo massimo\n^D(Unix)/^Z(Win)+invio per uscire :";
		System.out.print(cycleMessage);
		while ((richiesta=stdIn.readLine()) != null) {
			try {
				if(richiesta.equals("A")){
					System.out.print("Inserire tipo evento: ");
					tipo=stdIn.readLine();
					System.out.print("Inserire luogo evento: ");
					luogo=stdIn.readLine();
					outSock.writeUTF(richiesta);
					System.out.println("TCP_Stream_Client: Inviata richiesta per operazione: "+richiesta);
					outSock.writeUTF(tipo);
					outSock.writeUTF(luogo); 
					count = inSock.readInt();
					if(count == 0){
						System.out.println("TCP_Stream_Client: Nessun evento trovato");
						System.out.print(cycleMessage);
						continue;
					}
					System.out.println("Descrizione\tTipoe\tData\tLuogo\tDisponibilita\tPrezzo");
					for(int i=0;i<count;i++){
						risposta= inSock.readUTF();
						System.out.println(risposta);
					}
				}
				else if(richiesta.equals("B")){
					System.out.print("Inserire prezzo massimo: ");
					buff=stdIn.readLine();
					try{
						soglia=Integer.parseInt(buff);
					}
					catch(Exception e){
						System.out.println("TCP_Stream_Client: prezzo inserito non intero");
						System.out.print(cycleMessage);
						continue;
					}
					outSock.writeUTF(richiesta);
					System.out.println("TCP_Stream_Client: Inviata richiesta per operazione: "+richiesta);
					outSock.writeInt(soglia);
					count = inSock.readInt();
					if(count == 0){
						System.out.println("TCP_Stream_Client: Nessun evento trovato");
						System.out.print(cycleMessage);
						continue;
					}               System.out.println("Descrizione\tTipoe\tData\tLuogo\tDisponibilita\tPrezzo");
					for(int i=0;i<count;i++){
						risposta= inSock.readUTF();
						System.out.println(risposta);
					}
				}
				else{
					System.out.println("TCP_Stream_Client: Immissione non valida");
					System.out.print(cycleMessage);
					continue;
				}	
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
