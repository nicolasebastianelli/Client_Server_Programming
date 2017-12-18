/**
 * Nicola Sebastianelli
 * 
 * 0000722894
 * 
 * UDP_Unicast_Client.java
 */

import java.io.*;
import java.net.*;

public class UDP_Unicast_Client {

	public static void main(String[] args) {

		//Dichiarazione variabili di default
		int defaultTimeout = 3000;

		// Dichiarazione endopoint di comunicazione 
		InetAddress addr = null;
		int port = -1;

		// Controllo argomenti di input
		try {
			if (args.length == 2) {
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
			} else {
				System.out.println("Usage: java UDP_Unicast_Client serverIP serverPort");
				System.exit(1);
			}
		} catch (UnknownHostException e) {
			System.out.println("Problemi nella determinazione dell'endpoint del server : ");
			e.printStackTrace();
			System.out.println("UDP_Unicast_Client: interrompo...");
			System.exit(2);
		}

		// Dichiarazione socket e strutture dati di supporto
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		byte[] buf = new byte[256];

		// Creazione della socket datagram, settaggio timeout e creazione datagram packet
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(defaultTimeout); //TODO Da settare opportunamente
			packet = new DatagramPacket(buf, buf.length, addr, port);
			System.out.println("\nClient: avviato");
			System.out.println("UDP_Unicast_Client: Creata la socket: " + socket);		
		} catch (SocketException e) {
			System.out.println("Problemi nella creazione della socket: ");
			e.printStackTrace();
			System.out.println("UDP_Unicast_Client: interrompo...");
			System.exit(1);
		}

		// Preparazione standard input per l'interazione con l'utente
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, immetti input: ");

		try {
			// Dichiarazione oggetti per la lettura/scrittura dei dati
			ByteArrayOutputStream boStream = null;
			DataOutputStream doStream = null;
			ByteArrayInputStream biStream = null;
			DataInputStream diStream = null;
			byte[] data = null;

			//Dichiarazione variabili necesarie alla logica del programma
			String richiesta=null; // TODO IMPOSTARE TIPO VARIABILE
			String risposta = null;// TODO IMPOSTARE TIPO VARIABILE

			//BODY CLIENT
			while ((richiesta=stdIn.readLine()) != null) {
				// Interazione con l'utente
				System.out.println("UDP_Unicast_Client: Inviato: " + richiesta);
				try {


					//TODO INTERAZIONE CON UTENTE


				} catch (Exception e) {
					System.out.println("Problemi nell'interazione da console: ");
					e.printStackTrace();
					System.out
					.print("\n^D(Unix)/^Z(Win)+invio per uscire, immetti input: ");
					continue;
				}

				// Invio datagram
				try {
					boStream = new ByteArrayOutputStream();
					doStream = new DataOutputStream(boStream);

					//Scrittura datagram
					doStream.writeUTF(richiesta); //TODO CAMBIARE TIPO DI WRITE

					// Inserimento dati nel pacchetto udp
					data = boStream.toByteArray();
					packet.setData(data);

					// Invio pacchetto UDP
					socket.send(packet);
				} catch (IOException e) {
					System.out.println("Problemi nell'invio della richiesta: ");
					e.printStackTrace();
					System.out
					.print("\n^D(Unix)/^Z(Win)+invio per uscire, immetti input: ");
					continue;
				}

				//Ricezione datagram
				try {
					// Settaggio del buffer di ricezione
					packet.setData(buf);

					// Ricezione pacchetto UDP
					socket.receive(packet);			
				} catch (IOException e) {
					System.out.println("Problemi nella ricezione del datagramma: ");
					e.printStackTrace();
					System.out
					.print("\n^D(Unix)/^Z(Win)+invio per uscire, immetti input: ");
					continue;
				}
				try {
					// Estrazione della risposta
					biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
					diStream = new DataInputStream(biStream);

					//Lettura datagram
					risposta = diStream.readUTF(); //TODO CAMBIARE TIPO DI READ


					//TODO ELABORAZIONE RISPOSTA
					System.out.println("UDP_Unicast_Client: Risultato: " + risposta);


				} catch (IOException e) {
					System.out.println("Problemi nella lettura della risposta: ");
					e.printStackTrace();
					System.out
					.print("\n^D(Unix)/^Z(Win)+invio per uscire, immetti input: ");
					continue;
				}

				System.out.print("\n^D(Unix)/^Z(Win)+invio per uscire, immetti input: ");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("UDP_Unicast_Client: termino...");

		// libero le risorse occupate: socket, file, ...
		socket.close();
	}
}
