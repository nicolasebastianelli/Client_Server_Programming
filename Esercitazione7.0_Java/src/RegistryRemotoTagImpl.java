// Nicola Sebastianelli 0000722894 Esercitazione 7

/**

 * 	Implementazione del Registry Remoto.
 *	Metodi descritti nelle interfacce.  
 */

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class RegistryRemotoTagImpl extends RegistryRemotoImpl implements
RegistryRemotoTagServer {

	// num. entry [nomelogico][ref]

	public RegistryRemotoTagImpl() throws RemoteException {
		super();
	}

	public synchronized String[] cercaTag(String[] tags) throws RemoteException {
		int conttag=0;
		int contrem=0;
		if( tags.length == 0 ) return new String[0];
		for (int i = 0; i < tableSize; i++){
			conttag=0;
			for (int j = 0; j < tagSize; j++){				
				for (int k = 0; k < tags.length; k++)
					if ( tags[k].equals(tag[i][j]) )
						conttag++;
			}
			if(conttag==tags.length)
				contrem++;
		}
		String[] risultato = new String[contrem];
		// Ora lo uso come indice per il riempimento
		conttag = 0;
		contrem = 0;
		for (int i = 0; i < tableSize; i++){
			conttag=0;
			for (int j = 0; j < tagSize; j++){				
				for (int k = 0; k < tags.length; k++)
					if ( tags[k].equals(tag[i][j]) )
						conttag++;
			}
			if(conttag==tags.length)
				risultato[contrem++]=(String) table[i][0];;
		}
		return risultato;
	}

	public synchronized boolean associaTags(String logicServer, String[] tags)
			throws RemoteException {
		boolean risultato = false;
		if( (logicServer == null) || (tags.length==0) )
			return risultato;
		for (int i = 0; i < tableSize; i++){
			if (table[i][0].equals(logicServer)) {
				for(int j=0 ; j<tags.length;j++){
					tag[i][j] = tags[j];
					risultato = true;	
				}
				break;
			}
		}
		return risultato;
	}

	// Avvio del Server RMI
	public static void main(String[] args) {

		int registryRemotoPort = 1099;
		String registryRemotoHost = "localhost";
		String registryRemotoName = "RegistryRemoto";

		// Controllo dei parametri della riga di comando
		if (args.length != 0 && args.length != 1) {
			System.out.println("Sintassi: ServerImpl [registryPort]");
			System.exit(1);
		}
		if (args.length == 1) {
			try {
				registryRemotoPort = Integer.parseInt(args[0]);
			} catch (Exception e) {
				System.out
				.println("Sintassi: ServerImpl [registryPort], registryPort intero");
				System.exit(2);
			}
		}

		// Impostazione del SecurityManager
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());

		// Registrazione del servizio RMI
		String completeName = "//" + registryRemotoHost + ":" + registryRemotoPort
				+ "/" + registryRemotoName;
		try {
			RegistryRemotoTagImpl serverRMI = new RegistryRemotoTagImpl();
			Naming.rebind(completeName, serverRMI);
			System.out.println("Server RMI: Servizio \"" + registryRemotoName
					+ "\" registrato");
		} catch (Exception e) {
			System.err.println("Server RMI \"" + registryRemotoName + "\": "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}