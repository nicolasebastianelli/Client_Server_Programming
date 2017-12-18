/**
 * Nicola	Sebastianelli
 * 
 * 0000722894
 * 
 * TCP_Client.java
 */

import java.net.*;
import java.io.*;

public class TCP_Client {
	
	static void trasferisci_N_byte_file_binario(DataInputStream src, DataOutputStream dest, long daTrasferire) throws IOException
	{
	    int cont=0;
	    int buffer=0;
	    try
	    {
	        while (cont < daTrasferire)
	        {
	            buffer=src.read();
	            dest.write(buffer);
	            cont++;
	        }
	        dest.flush();
	        System.out.println("Byte trasferiti: " + cont);
	    }
	    catch (EOFException e)
	    {
	        System.out.println("Problemi, i seguenti: ");
	        e.printStackTrace();
	    }
	}
	
	
	public static void main(String[] args) throws IOException {

		// Dichiarazione endopoint di comunicazione ottenuti dai parametri
		InetAddress addr = null;
		int port = -1;

		//Controllo arogmenti
		try {
			if (args.length == 2) {
				addr = InetAddress.getByName(args[0]);
				port = Integer.parseInt(args[1]);
			} else {
				System.out.println("Usage: java TCP_Stream_Client serverAddr serverPort");
				System.exit(1);
			}
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
			System.out.println("Usage: java TCP_Stream_Client serverAddr serverPort");
			System.exit(2);
		}

		//Dichiarazione oggetti utilizzati dal client per la comunicazione
		Socket socket = null;
		DataInputStream inSock = null;
		DataOutputStream outSock = null;
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		//Dichiarare variabili per la logica del programma
		String richiesta = null,risposta=null,buff; // TODO IMPOSTARE TIPO VARIABILE
		String nome=null,nomef=null,parola=null;
		int count,durata,nparole,ok;
		//Creazione socket
		try {
			socket = new Socket(addr, port);
			socket.setSoTimeout(30000);
			System.out.println("TCP_Stream_Client: Creata la socket: " + socket);
		} catch (Exception e) {
			System.out.println("Problemi nella creazione della socket: ");
			e.printStackTrace();
			System.exit(1);
		}

		//Creazione stream di I/O su socket
		try {
			inSock = new DataInputStream(socket.getInputStream());
			outSock = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("Problemi nella creazione degli stream su socket: ");
			e.printStackTrace();
			System.exit(1);
		}

		//BODY CLIENT
		String cycleMessage ="\nInserire: \nA) per inserire un provino\nB) per scaricare file dei provini data parola\n^D(Unix)/^Z(Win)+invio per uscire :";
		System.out.print(cycleMessage);
		while ((richiesta=stdIn.readLine()) != null) {
			try {
				if(richiesta.equals("A")){
					System.out.print("Inserire nome provino: ");
					nome=stdIn.readLine();
					if(nome.equals("L"))
					{
						System.out.println("Immissione non valida");
						System.out.print(cycleMessage);
						continue;
					}
					System.out.print("Inserire nome file: ");
					nomef=stdIn.readLine();
					File file = new File(nomef);
					if(!file.exists())
					{
						System.out.println("Nome file inesistente");
						System.out.print(cycleMessage);
						continue;
					}
					System.out.print("Inserire durata: ");
					buff=stdIn.readLine();
					try{
						durata= Integer.parseInt(buff);
						if(durata<0)
							throw new IllegalArgumentException();
					}
					catch(Exception e){
						System.out.println("Inserimento non valido");
						System.out.print(cycleMessage);
						continue;
					}
					System.out.print("Inserire numero di parole da inserire (0-5): ");
					buff=stdIn.readLine();
					try{
						nparole= Integer.parseInt(buff);
						if(nparole<0||nparole>5)
							throw new IllegalArgumentException();
					}
					catch(Exception e){
						System.out.println("Inserimento non valido");
						System.out.print(cycleMessage);
						continue;
					}
					outSock.writeUTF(richiesta);
					outSock.writeUTF(nome);
					outSock.writeUTF(nomef);
					outSock.writeInt(durata);
					outSock.writeInt(nparole);
					for(int i =0;i<nparole;i++){
						System.out.print("Inserire parola "+(i+1)+": ");
						buff=stdIn.readLine();
						outSock.writeUTF(buff);						
					}					
					System.out.println("TCP_Stream_Client: Inviata richiesta per operazione: "+richiesta);
					ok=inSock.readInt();
					if(ok == -1){
						System.out.println("TCP_Stream_Client: Tabella piena");
						System.out.print(cycleMessage);
						continue;
					}
					else{
						System.out.println("TCP_Stream_Client: Inserimento avvenuto con successo");
						File a = new File(nomef);
						FileInputStream fin = new FileInputStream(a);
						DataInputStream din = new DataInputStream(fin);
						trasferisci_N_byte_file_binario(din, outSock, a.length());
						din.close();
						fin.close();
						System.out.println("TCP_Stream_Client: file "+nomef+" inviato");
					}
				}
				else if(richiesta.equals("B")){
					System.out.print("Inserire parola: ");
					parola=stdIn.readLine();
					if(parola.equals("L"))
					{
						System.out.println("Immissione non valida");
						System.out.print(cycleMessage);
						continue;
					}
					outSock.writeUTF(richiesta);
					System.out.println("TCP_Stream_Client: Inviata richiesta per operazione: "+richiesta);
					outSock.writeUTF(parola);
					count = inSock.readInt();
					if(count == 0){
						System.out.println("TCP_Stream_Client: Nessun provino trovato");
						System.out.print(cycleMessage);
						continue;
					}              
					for(int i=0;i<count;i++){
						nomef= inSock.readUTF();
						File a = new File(nomef);
						FileOutputStream fout = new FileOutputStream(a);
						DataOutputStream dout = new DataOutputStream(fout);
						long l = inSock.readLong();
						trasferisci_N_byte_file_binario(inSock, dout, l);
						dout.close();
						fout.close();
						System.out.println("Fine invio file: "+nomef);
					}
				}
				else{
					System.out.println("TCP_Stream_Client: Immissione non valida");
					System.out.print(cycleMessage);
					continue;
				}	
			} catch (SocketTimeoutException ste) {
				System.out.println("Timeout scattato: ");
				ste.printStackTrace();
				break;
			} catch (Exception e) {
				System.out.println("Error: ");
				e.printStackTrace();
				break;
			}
			System.out.print(cycleMessage);
		}

		// libero le risorse occupate: socket, file, ...
		try{
			outSock.flush();
			socket.shutdownOutput();
			socket.shutdownInput();
		} catch (IOException e) {
			System.out.println("Problemi nella chiusura della socket: ");
			e.printStackTrace();
			System.exit(1);
		} catch (Exception e) {
			System.out.println("Error: ");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("TCP_Stream_Client: Termino...");
	}
}
