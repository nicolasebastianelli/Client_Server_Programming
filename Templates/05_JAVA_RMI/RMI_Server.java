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

	//Costruttore
	public RMI_Server() throws RemoteException	{
		super();
	}

	//TODO IMPLEMENTARE INTERFACCIE 
	public String function(String richiesta) throws RemoteException {

        String risposta;
        
        risposta = richiesta
		
		return risposta;
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
