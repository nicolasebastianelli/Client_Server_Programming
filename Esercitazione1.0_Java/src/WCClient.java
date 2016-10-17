//Nicola Sebastianelli 0000722894 esercitazione 1

import java.io.*;
import java.net.*;

public class WCClient {

	public static void main(String[] args) {
		InetAddress addr = null;
		int port = -1;		
		try {
			if (args.length == 3) {
		    addr = InetAddress.getByName(args[0]);
		    port = Integer.parseInt(args[1]);
			} else {
				System.out.println("Usage: java WCClient IPDiscoveryServer portDiscoveryServer nomeLogico");
			    System.exit(1);
			}
		} catch (UnknownHostException e) {
			System.out
		      .println("[WCClient]: Problemi nella determinazione dell'endpoint del server : ");
			e.printStackTrace();
			System.out.println("[WCClient]: interrompo...");
			System.exit(2);
		}
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		byte[] buf = new byte[256];
		
		try {
			socket = new DatagramSocket();
			socket.setSoTimeout(10000);
			packet = new DatagramPacket(buf, buf.length, addr, port);
			System.out.println("\n[WCClient]: avviato");
			System.out.println("[WCClient]: Creata la socket: " + socket);
		} catch (SocketException e) {
			System.out.println("[WCClient]: Problemi nella creazione della socket: ");
			e.printStackTrace();
			System.out.println("[WCClient]: interrompo...");
			System.exit(1);
		}
		//sending datagram to DiscoveryServer
		try {
			ByteArrayOutputStream boStream = null;
			DataOutputStream doStream = null;
			byte[] data = null;
			int wcport = -1;
			String richiesta = null;
			String risposta = null;
			ByteArrayInputStream biStream = null;
			DataInputStream diStream = null;
			try {
					richiesta = args[2];
					boStream = new ByteArrayOutputStream();
					doStream = new DataOutputStream(boStream);
					doStream.writeUTF(richiesta);
					data = boStream.toByteArray();
					packet.setData(data);
					socket.send(packet);
					System.out.println("[WCClient]: Richiesta inviata a " + addr + ", " + port);
				} catch (IOException e) {
					System.out.println("[WCClient]: Problemi nell'invio della richiesta: ");
					e.printStackTrace();
					System.out.println("[WCClient]: interrompo...");
					System.exit(1);
				}
			//receiving datagram from DiscoveryServer
				try {
					packet.setData(buf);
					socket.receive(packet);
				} catch (IOException e) {
					System.out.println("[WCClient]: Problemi nella ricezione del datagramma: ");
					e.printStackTrace();
					System.out.println("[WCClient]: interrompo...");
					System.exit(1);
				}
				try {
					biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
					diStream = new DataInputStream(biStream);
					risposta = diStream.readUTF();
					System.out.println("[Risposta DiscoveryServer]: " + risposta);
				} catch (IOException e) {
					System.out.println("[WCClient]: Problemi nella lettura della risposta: ");
					e.printStackTrace();
					System.out.println("[WCClient]: Interrompo...");
					System.exit(1);
				}
				try{
				wcport=Integer.parseInt(risposta);
				} catch(Exception e){
					System.exit(1);
				}	
				//sending datagram to WCServer
				try {
					packet.setPort(wcport);
					socket.send(packet);
				}
				catch (IOException e) {
					System.out.println("[WCClient]:Problemi nell'invio della domanda a WCServer: ");
					e.printStackTrace();
					System.out.println("[WCClient]: Interrompo...");
					System.exit(1);
				}
				//receiving datagram from DiscoveryServer
				try {
					packet.setData(buf);
					socket.receive(packet);
				} catch (IOException e) {
					System.out.println("[WCClient]: Problemi nella ricezione del datagramma: ");
					e.printStackTrace();
					System.out.println("[WCClient]: Interrompo...");
					System.exit(1);
				}
				try {
					int []rispostawc = new int[3];
					biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
					diStream = new DataInputStream(biStream);
					for(int i = 0; i <3; i++) 
						rispostawc[i] = diStream.readInt();
					System.out.println("[Risposta WCServer]: Caratteri:"+rispostawc[0]+" Parole:"+rispostawc[1]+" Linee:"+rispostawc[2]);
				} catch (IOException e) {
					System.out.println("[WCClient]: Problemi nella lettura della risposta: ");
					e.printStackTrace();
					System.out.println("[WCClient]: interrompo...");
					System.exit(1);
				}				
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("[WCClient]: termino...");
			socket.close();
		}
		socket.close();		
	}
}