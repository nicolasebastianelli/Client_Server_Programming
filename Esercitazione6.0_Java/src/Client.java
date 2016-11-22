//Nicola Sebastianelli 0000722894 Esercitazione 6

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.NotBoundException;

class Client {

	public static void main(String[] args) {
		int REGISTRYPORT = -1;
		String registryHost = "localhost";					
		String serviceName = "ServerN";
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		// Controllo dei parametri della riga di comando
		if(args.length != 1){
			System.out.println("Sintassi: registryPort");
			System.exit(1);
		}
		try {
			REGISTRYPORT = Integer.parseInt(args[0]);
			if (REGISTRYPORT < 1024 || REGISTRYPORT > 65535) {
				System.out.println("Usage: java Client [registryPort>1024 && <65535]");
				System.exit(1);
			}
		} catch (NumberFormatException e) {
			System.out.println("Usage: java Client registryPort not a number");
			System.exit(1);
		}

		System.out.println("Invio richieste a "+registryHost+" per il servizio di nome "+serviceName);

		// Connessione al servizio RMI remoto
		try{
			String completeName = "//" + registryHost + ":" + REGISTRYPORT + "/"
					+ serviceName;
			RemOp serverRMI = (RemOp) Naming.lookup(completeName);
			System.out.println("ClientRMI: Servizio \"" + serviceName + "\" connesso");

			System.out.println("\nRichieste di servizio fino a fine file");

			String service;
			System.out.print("Servizio (R=Rinomina, C=Conta): ");

			/*ciclo accettazione richieste utente*/
			while ((service = stdIn.readLine()) != null){

				if(service.equals("R")){
					boolean ok = false; //stato [VALID|IVALID] della richiesta
					String dir;
					System.out.print("Nome del direttorio?: ");
					dir = stdIn.readLine();
					String nf = null ;
					System.out.print("Nome del file da sovrascrivvere?: ");
					while (ok != true){
						nf = stdIn.readLine();
						if(!nf.endsWith(".txt")){
							System.out.println("Non è un file di testo");
							System.out.print("Nome del file da sovrascrivvere?: ");
							continue;
						} else ok = true;	
					}
					String nnf ;
					System.out.print("Nuovo nome del file da sovrascrivvere?: ");
					nnf = stdIn.readLine();	
					try{
						String[] res = serverRMI.rinomina_file(dir, nf, nnf);
						System.out.println("Lista dei file del direttorio: "+dir);
						System.out.println("");
						System.out.println("Lista dei file contenuti nella directory:");
						for(int i=0 ;i<res.length;i++)
						{
							System.out.print(res[i]+"\t");
						}
						System.out.println("");
						System.out.println("");
					}
					catch(Exception e)
					{
						System.out.println("Errore");
					}
				}

				else if(service.equals("C")){
					String nf;
					String parola;
					System.out.print("Nome del file?: ");
					nf = stdIn.readLine();	
					System.out.print("Parola da contare?: ");
					parola = stdIn.readLine();
					try{
						int[] res=serverRMI.conta_fileTesto(nf, parola);
						if(res[0]==-1)
						{
							System.out.println("Errore nell'apertura del file");
						}
						else
							System.out.println("Numero di caratteri: "+res[0]+"\nNumero di parole: "+res[1]+"\nNumero di righe: "+res[2]+"\nNumero di occorrenze della parola "+parola+": "+res[3]);
					}
					catch(Exception e){
						System.out.println("Errore");
					}
				} 

				else System.out.println("Servizio non disponibile");

				System.out.print("Servizio (R=Rinomina, C=Conta): ");
			} // while (!EOF), fine richieste utente

		}
		catch(NotBoundException nbe){
			System.err.println("ClientRMI: il nome fornito non risulta registrato; " + nbe.getMessage());
			//nbe.printStackTrace();
			System.exit(1);
		}
		catch(Exception e){
			System.err.println("ClientRMI: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}
