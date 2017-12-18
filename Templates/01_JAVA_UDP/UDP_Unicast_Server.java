/**
 * Nicola Sebastianelli
 * 
 * 0000722894
 * 
 * UDP_Unicast_Server.java
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class UDP_Unicast_Server {

	private static final int PORT = 54321;

	public static void main(String[] args) {

		// Dichiarazione socket e strutture dati di supporto
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		byte[] buff = new byte[256];
		int port = -1;

		// Controllo argomenti input
		if ((args.length == 0)) {
			port = PORT;
			System.out.println("Using port: " + PORT);
		} else if (args.length == 1) {
			try {
				port = Integer.parseInt(args[0]);
				if (port < 1024 || port > 65535) {
					System.out.println("Error: 1024<serverPort<65535.");
					port=PORT;
				}
				System.out.println("Using port: " + port);
			} catch (NumberFormatException e) {
				System.out.println("Usage: java UDP_Unicast_Server [serverPort>1024]");
				System.exit(1);
			}
		} else {
			System.out.println("Usage: java UDP_Unicast_Server [serverPort>1024]");
			System.exit(1);
		}

		// Inizializzazione socket e strutture dati di supporto
		try {
			socket = new DatagramSocket(port);
			packet = new DatagramPacket(buff, buff.length);
			System.out.println("UDP_Unicast_Server: creata la socket: " + socket);
		} catch (SocketException e) {
			System.out.println("Problemi nella creazione della socket: ");
			e.printStackTrace();
			System.exit(1);
		}

		// BODY SERVER
		try {
			// Dichiarazione oggetti per la lettura/scrittura dei dati come stream di byte nel pacchetto UDP
			ByteArrayInputStream biStream = null;
			DataInputStream diStream = null;
			ByteArrayOutputStream boStream = null;
			DataOutputStream doStream = null;
			byte[] data = null;

			// Dichiarazione variabili necessarie alla logica del programma			
			String richiesta = null; // TODO IMPOSTARE TIPO VARIABILE
			String risposta = null; // TODO IMPOSTARE TIPO VARIABILE
			System.out.println("UDP_Unicast_Server: Avviato!");
			while (true) {
				System.out.println("\nUDP_Unicast_Server: In attesa di richieste...");

				// Ricezione del datagram
				try {
					packet.setData(buff);
					socket.receive(packet); //Sospensiva
				} catch (IOException e) {
					System.err.println("Problemi nella ricezione del datagramma: " + e.getMessage());
					e.printStackTrace();
					continue;
				}

				try {
					biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
					diStream = new DataInputStream(biStream);		

					//Lettura datagram			
					richiesta = diStream.readUTF(); //TODO CAMBIARE TIPO DI READ
					System.out.println("UDP_Unicast_Server: Ricevuto: " + richiesta);


					//TODO ELABORAZIONE RICHIESTA
					risposta= richiesta;


				} catch (Exception e) {
					System.err.println("Problemi nella lettura della richiesta: ");
					e.printStackTrace();
					continue;
				}

				//Invio datagram
				try {				
					boStream = new ByteArrayOutputStream();
					doStream = new DataOutputStream(boStream);

					// Scrittura datagram
					doStream.writeUTF(risposta); //TODO CAMBIARE TIPO DI WRITE

					// Inserimento dati nel pacchetto UDP
					data = boStream.toByteArray();
					packet.setData(data, 0, data.length);

					// Invio pacchetto
					socket.send(packet);
					System.out.println("UDP_Unicast_Server: Inviato: " + risposta);
				} catch (IOException e) {
					System.err.println("Problemi nell'invio della risposta: " + e.getMessage());
					e.printStackTrace();
					continue;
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("UDP_Unicast_Server: termino...");
		socket.close();
	}

}
