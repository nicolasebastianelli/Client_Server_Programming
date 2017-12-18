/**
 * Nicola	Sebastianelli
 * 
 * 0000722894
 * 
 * RMI_Client.java
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;


public class RMI_Client {

	// Avvio del Client RMI
	public static final int REGISTRYPORT = 1099;
	public static void main(String[] args) {
		int registryPort = -1;
		String registryHost = null;
		String serviceName = "RMI_ServerService";
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		// Controllo argomenti
		try {
			if (args.length == 2) {
				registryHost = args[0];
				registryPort = Integer.parseInt(args[1]);
			} else {
				System.out.println("Usage: java RMI_Client serverAddr registryPort");
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
			System.out.println("Usage: java TCP_Stream_Client serverAddr serverPort");
			System.exit(2);
		}

		// Connessione al servizio RMI remoto
		try{
			String completeName = "//" + registryHost + ":" + registryPort + "/" + serviceName;
			RMI_InterfaceFile serverRMI = (RMI_InterfaceFile) Naming.lookup(completeName);
			System.out.println("RMI_Client: Service \"" + serviceName + "\" connected");

			//BODY CLIENT
			String cycleMessage ="\n^D(Unix)/^Z(Win)+invio per uscire, immetti input: ";
			System.out.print(cycleMessage);
			String richiesta;
			String risposta;
			while ((richiesta= stdIn.readLine()) != null){
				
				//TODO INTERAZIONE CON L'UTENTE
				System.out.println("RMI_Client: Inviato: " + richiesta+"");
				risposta = serverRMI.function(richiesta);
				
				//TODO ELABORAZIONE RISPOSTA
				System.out.println("RMI_Client: Ricevuto: " + risposta);
				
				System.out.print(cycleMessage);
			}
			
		}	catch(Exception e){
			System.err.println("RMI_Client: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}
