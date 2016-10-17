//Nicola Sebastianelli 0000722894 esercitazione 1

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class DiscoveryServer {

	public static void main(String[] args) {
		System.out.println("[DiscoveryServer]: avviato");
		int port = -1,nwc=(args.length-1)/3;
		WCServer []wcserver =new WCServer[nwc];
		String []nomelogico =new String[nwc];
		int []portwc =new int[nwc];
		//Arguments control
		if (args.length<4 ) {
			System.out.println("Usage: invalid number of arguments");
			System.exit(1);
		}
		if ((args.length-1)%3 ==0 ) {
			try {
				port = Integer.parseInt(args[0]);
				if (port < 1024 || port > 65535) {
					System.out.println("Usage: java DiscoveryServer [serverPort>1024]");
					System.exit(1);
				}
			} catch (NumberFormatException e) {
				System.out.println("Usage: java DiscoveryServer portaDiscoveryServer not a number");
				System.exit(1);
			}
		} else {
			System.out.println("Usage: java portaDiscoveryServer nomeLogico1 file1 port1...");
			System.exit(1);
		}
		for(int i =0 ; i<nwc ; i++){
			try {
				portwc[i] = Integer.parseInt(args[(i+1)*3]);
				nomelogico[i] = args[(i+1)*3-2];
				if (portwc[i] < 1024 || portwc[i] > 65535) {
					System.out.println("Usage: java DiscoveryServer [port"+(i+1)+">1024]");
					System.exit(1);
				}
				for(int j=0;j<i;j++)
				{
					if(portwc[j]==portwc[i] ||port==portwc[i]){
					System.out.println("Usage: assignment of equal ports");
					System.exit(1);
					}
					if(nomelogico[j].equals(nomelogico[i])){
						System.out.println("Usage: assignment of equal logic name");
						System.exit(1);
					}
				}
			} catch (NumberFormatException e) {
				System.out.println("Usage: java DiscoveryServer, port"+(i+1)+" not a number");
				System.exit(1);
			}
			// creation of WCServers
			wcserver[i] =new WCServer(nomelogico[i],args[(i+1)*3-1],portwc[i]);
		}
		for(int i =0 ; i<nwc ; i++){
			System.out.println("[DiscoveryServer]: creato WCServer "+nomelogico[i]);
			wcserver[i].start();
		}
			DatagramSocket socket = null;
			DatagramPacket packet = null;
			byte[] buf = new byte[256];
			try {
				socket = new DatagramSocket(port);
				packet = new DatagramPacket(buf, buf.length);
				System.out.println("[DiscoveryServer]: creata la socket: " + socket);
			}
			catch (SocketException e) {
				System.out.println("[DiscoveryServer]: Problemi nella creazione della socket: ");
				e.printStackTrace();
				System.exit(1);
			}
			//receiving datagram from WCClient
			try {
				String richiesta = null;
				String risposta = null;
				ByteArrayInputStream biStream = null;
				DataInputStream diStream = null;
				ByteArrayOutputStream boStream = null;
				DataOutputStream doStream = null;
				byte[] data = null;
				while (true) {
					System.out.println("\n[DiscoveryServer]: In attesa di richieste...");
					try {
						packet.setData(buf);
						socket.receive(packet);
					}
					catch (IOException e) {
						System.err.println("[DiscoveryServer]: Problemi nella ricezione del datagramma: "
								+ e.getMessage());
						e.printStackTrace();
						continue;
					}
					try {
						biStream = new ByteArrayInputStream(packet.getData(), 0, packet.getLength());
						diStream = new DataInputStream(biStream);
						richiesta = diStream.readUTF();
						System.out.println("[DiscoveryServer]: Richiesto server: " + richiesta);
					}
					catch (Exception e) {
						System.err.println("[DiscoveryServer]: Problemi nella lettura della richiesta");
						e.printStackTrace();
						continue;
					}
					//looking for an existing logical name
					try {
						risposta ="file non disponibile";
						for(int i=0; i<nwc;i++)
						{
							if(nomelogico[i].equals(richiesta))
								risposta=String.valueOf(portwc[i]);
						}
						//sending datagram to WCClient
						boStream = new ByteArrayOutputStream();
						doStream = new DataOutputStream(boStream);
						doStream.writeUTF(risposta);
						data = boStream.toByteArray();
						packet.setData(data, 0, data.length);
						socket.send(packet);
					}
					catch (IOException e) {
						System.err.println("[DiscoveryServer]: Problemi nell'invio della risposta: "
								+ e.getMessage());
						e.printStackTrace();
						continue;
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("[DiscoveryServer]: termino...");
				socket.close();	
			}
			socket.close();	
	}	
}