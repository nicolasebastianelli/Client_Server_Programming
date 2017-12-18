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
			String cycleMessage = "Inserire:\nA)Per eliminare la prenotazione\nB)Per visualizzare la prenotazione\n^D(Unix)/^Z(Win)+invio per uscire: ";
			System.out.print(cycleMessage);
			String buff;
			String id;
			String tipologia;
			int npers,result;
			Tabella table[];
			while ((buff= stdIn.readLine()) != null){
				if(buff.equals("A")){
					System.out.print("RMI_Client: Inserire ID prenotazione: ");
					id= stdIn.readLine();
					if(id.equals("L"))
					{
						System.out.println("RMI_Client: ID inserito non valido");
						System.out.print(cycleMessage);
						continue;
					}
					System.out.println("RMI_Client: Inviato ID: " + id+"");
					result = serverRMI.elimina_prenotazione(id);
					if(result<0)
						System.out.println("RMI_Client: Errore , Id non trovato");
					else
						System.out.println("RMI_Client: Prenotazione eliminata con successo");
				}
				else if(buff.equals("B")){
					System.out.print("RMI_Client: Inserire tipologia prenotazione: ");
					tipologia= stdIn.readLine();
					if(tipologia.equals("L"))
					{
						System.out.println("RMI_Client: tipologia inserita non valida");
						System.out.print(cycleMessage);
						continue;
					}
					System.out.print("RMI_Client: Inserire numero minimo di persone: ");
					buff= stdIn.readLine();
					try{
					npers=Integer.parseInt(buff);
					}
					catch(Exception e){
						System.out.println("RMI_Client: Inserimento non numerico");
						System.out.print(cycleMessage);
						continue;
					}
					table = serverRMI.visualizza_prenotazioni(npers, tipologia);
					if(table[0].getId().equals("L"))
						System.out.println("RMI_Client: Nessuna prenotazione trovata per questa tipologia e soglia");
					else{
						System.out.println("ID\tNumero persone\tTipologia\tVeicolo\tTarga\tFile");
						for(int i =0;i<table.length;i++){
							System.out.println(table[i].toString());
						}
					}
				}
				else
					System.out.println("Inserimento non valido");
				System.out.print(cycleMessage);
			}
			
		}	catch(Exception e){
			System.err.println("RMI_Client: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}
