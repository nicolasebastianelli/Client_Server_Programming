/**
 * Nicola	Sebastianelli
 * 
 * 0000722894
 * 
 * RMI_Server.java
 */

import java.math.BigInteger;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class RMI_Server extends UnicastRemoteObject implements RMI_InterfaceFile {
	private static final long serialVersionUID = 1L;
	private final static int TABDIM = 10;
	public static Cantante[] table;
	//Costruttore
	public RMI_Server() throws RemoteException	{
		super();	
		table = new Cantante[TABDIM];				
		for(int i =0 ; i <TABDIM;i++){
			table[i]= new Cantante();
		}
		table[0]=new Cantante("Mandarino", "Campioni", 1500, "vicino.avi");
		table[2]=new Cantante("Amara Bianca", "Campioni", 2000, "immobilismo.avi");
		table[4]=new Cantante("Zucchero", "NuoveProposte", 550, "ascolto.avi");
		table[6]=new Cantante("Amari", "NuoveProposte", 800, "cosaE.avi");
		table[8]=new Cantante("Franca", "Campioni", 3300, "rosso.avi");
		table[1]=new Cantante("Gloria", "Resto", 1500, "manchitu.avi");
		System.out.println("Cantante\tCategoria\tVoto\tAudio");
		for(int i =0 ; i<TABDIM;i++){
			System.out.println(table[i].nome+"\t"+table[i].categoria+"\t"+table[i].voto+"\t"+table[i].audio);
		}
	}

	public Cantante[] visualizza_cantanti(String categoria)
			throws RemoteException {
		int j=0;
		System.out.println("RMI_Server: Ricevuto richiesta di visualizzazione cantanti per categoria: " + categoria);
		for (int i =0; i< TABDIM;i++){
			if(categoria.equals(table[i].categoria)){
				j++;
			}
		}
		Cantante[] result=new Cantante[j];	
		j=0;
		for (int i =0; i< TABDIM;i++){
			if(categoria.equals(table[i].categoria)){
				result[j]=table[i];
				j++;
			}
		}
		System.out.println("RMI_Server: Invio risultato di "+j+" elementi");
		return result;
	}
	public Cantante[] visualizzazione_piu_votato() throws RemoteException {
		int ok,k=0;
		String[] supp= new String[TABDIM];
		Cantante[] result;
		System.out.println("RMI_Server: Ricevuto richiesta di visualizzazione cantante più votato");
		for (int i =0; i< TABDIM;i++){
			ok=1;
			for(int j=0; j< k;j++)
			{
				if((table[i].categoria.equals(supp[j]))||table[i].categoria.equals("L"))
					ok=0;
			}
			if(ok ==1){
				supp[k]=table[i].categoria;
				k++;
			}

		}
		result = new Cantante[k*2];
		int b=0;
		for(int i =0 ; i <k;i++)
		{
			result[b]= new Cantante("L",supp[i],-1,"L");
			result[b+1]= new Cantante("L",supp[i],-1,"L");
			b=b+2;
		}
		for(int i =0; i<k;i++)
		{
			for(int j=0;j<TABDIM;j++){
				ok=1;
				if(table[j].categoria.equals(supp[i])){
					if((table[j].voto>result[i*2].voto)&&(table[j].voto>result[i*2+1].voto))	
					{
						if(result[i*2].voto<result[i*2+1].voto)
							result[i*2]=table[j];
						else
							result[i*2+1]=table[j];
					}
					else if(table[j].voto>result[i*2+1].voto){
						result[i*2+1]=table[j];
					}
					else if(table[j].voto>result[i*2].voto){
						result[i*2]=table[j];
					}
				}
			}
		}
		System.out.println("RMI_Server: Invio risultato di "+k*2+" elementi");
		return result;
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
