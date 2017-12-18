/**
 * Nicola	Sebastianelli
 * 
 * 0000722894
 * 
 * RMI_Server.java
 */

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMI_Server extends UnicastRemoteObject implements RMI_InterfaceFile {
	private static final long serialVersionUID = 1L;
	static final int TABSIZE=5;
	private static Tabella[] table;
	//Costruttore
	public RMI_Server() throws RemoteException	{
		super();
		table = new Tabella[TABSIZE];
		for(int i =0 ; i<TABSIZE;i++)
			table[i] = new Tabella();
		table[0] = new Tabella("HGFD89", "3", "mezza piazzola", "niente", "L", "mezza_piazz1.jpg");
		table[1] = new Tabella("WERT26", "5", "piazzola", "camper", "AA567AA", "piazz_deluxe1.jpg");
		table[3] = new Tabella("QLJC33", "7", "mezza piazzola", "niente", "L", "mezza_piazz2.jpg");
		table[4] = new Tabella("QWER67", "8", "piazzola deluxe", "niente", "L", "piazz1.jpg");

		System.out.println("ID\tNumero persone\tTipologia\tVeicolo\tTarga\tFile");
		for(int i =0 ; i<TABSIZE;i++)
			System.out.println(table[i].toString());
	}

	//TODO IMPLEMENTARE INTERFACCIE 
	public synchronized int elimina_prenotazione(String id) throws RemoteException {
		int result=-1;
		System.out.println("RMI_Server: Ricevuto ID: " + id);
		for (int i=0;i<TABSIZE;i++){
			if(table[i].getId().equals(id)){
				table[i] = new Tabella();
				result=1;
				break;
			}
		}
		if (result==-1)
			System.out.println("RMI_Server: ID non trovato");
		else{
			System.out.println("RMI_Server: Prenotazione eliminata");
			System.out.println("ID\tNumero persone\tTipologia\tVeicolo\tTarga\tFile");
			for(int i =0 ; i<TABSIZE;i++)
				System.out.println(table[i].toString());
		}
		return result;
	}

	public Tabella[] visualizza_prenotazioni(int npers, String tipologia) throws RemoteException {
		int count=0,tabnpers;
		Tabella[] result;
		System.out.println("RMI_Server: Ricevuto soglia: " + npers+", tipologia: "+tipologia);
		for(int i =0; i<TABSIZE;i++){
			try{tabnpers=Integer.parseInt(table[i].getNpers());}
			catch(Exception e){continue;}
			if(tabnpers>npers&&table[i].getTipologia().equals(tipologia)&&table[i].getVeicolo().equals("niente")){
				count++;
			}
		}
		if(count==0){
			System.out.println("RMI_Server: Nessuna prenotazione trovata per queste specifiche");
			result = new Tabella[1];
			result[0]= new Tabella();
			return result;
		}else{
			result = new Tabella[count];
			count=0;
			for(int i =0; i<TABSIZE;i++){
				try{tabnpers=Integer.parseInt(table[i].getNpers());}
				catch(Exception e){continue;}
				if(tabnpers>npers&&table[i].getTipologia().equals(tipologia)&&table[i].getVeicolo().equals("niente")){
					result[count]=table[i];
					count++;
				}
			}
			System.out.println("RMI_Server: Invio "+count+" elementi");
			return result;
		}
	}

	public static final int REGISTRYPORT = 1099;
	//Main
	public static void main(String[] args) {
		int registryPort = -1;
		String registryHost = "localhost";
		String serviceName = "RMI_ServerService";

		//Controllo parametri
		try {
			if (args.length == 1) {
				registryPort = Integer.parseInt(args[0]);
				if (registryPort < 1024 || registryPort > 65535) {
					System.out.println("Usage: 1024<registryPort<65535");
					registryPort = REGISTRYPORT;
				}
				System.out.println("Usage port: " + registryPort);
			} else if (args.length == 0) {
				registryPort = REGISTRYPORT;
				System.out.println("Usage port: " + registryPort);
			} else {
				System.out.println("Usage: java RMI_Server or java RMI_Server registryPort");
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
			System.out.println("Usage: java TCP_Stream_Server or java TCP_Stream_Server port");
			System.exit(1);
		}

		// Registrazione del servizio RMI
		String completeName = "//" + registryHost + ":" + registryPort + "/" + serviceName;
		try{
			RMI_Server serverRMI = new RMI_Server();
			Naming.rebind(completeName, serverRMI);
			System.out.println("RMI_Server: Servizio \"" + serviceName + "\" registrato");
		}
		catch(Exception e){
			System.err.println("RMI_Server \"" + serviceName + "\": " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}
