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
			System.out.print("\nInserire:\nA) per visualizzare i cantanti di una categoria\nB) per visualizzare i più votati\n^D(Unix)/^Z(Win) per uscire");
			String categoria;
			String risposta;
			String buff;
			Cantante result[];
			while ((buff= stdIn.readLine()) != null){
				if(buff.equals("A")){
					System.out.print("Inserire categoria: ");
					categoria= stdIn.readLine();
					System.out.println("RMI_Client: Inviato: richiesta di visualizzazione cantanti per categoria: " + categoria+"");
					result = serverRMI.visualizza_cantanti(categoria);
					if(result.length!=0){
						System.out.println("Cantante\tVoto");
						for(int i=0; i<result.length;i++){
							System.out.println(result[i].nome+"\t"+result[i].voto);
						}
					}
					else
						System.out.println("RMI_Client: Nessun cantante per quella categoria");
				}
				else if(buff.equals("B")){
					System.out.println("RMI_Client: Inviato richiesta dei cantanti più votati");
					result = serverRMI.visualizzazione_piu_votato();
					System.out.println("Cantante\tCategoria\tVoto");
					for(int i =0 ; i<result.length;i++){
						System.out.println(result[i].nome+"\t"+result[i].categoria+"\t"+result[i].voto);
					}
				}
				System.out.print("\nInserire:\nA) per visualizzare i cantanti di una categoria\nB) per visualizzare i più votati\n^D(Unix)/^Z(Win) per uscire");
			}

		}	catch(Exception e){
			System.err.println("RMI_Client: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

}
