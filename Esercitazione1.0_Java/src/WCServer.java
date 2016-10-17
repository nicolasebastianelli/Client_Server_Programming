import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;



public class WCServer extends Thread{
	private String nomeLogico;
	private String file;
	private int port;
	
	public WCServer(String nomeLogico,String file, int port){
	this.nomeLogico = nomeLogico;
	this.file = file;
	this.port = port;
	}
	
	public String getNomeLogico() {
		return nomeLogico;
	}

	public String getFile() {
		return file;
	}

	public int getPort() {
		return port;
	}
	
	static int[] contaFile(String nomeFile,String nomelogico){
		int []result = new int[3];
		result[0]=0;
		result[1]=0;
		result[2]=0;
		String buff;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(nomeFile));
			System.out.println("[WCServer "+nomelogico+"]: File aperto: " + nomeFile);
			while((buff=in.readLine()) != null){
				result[0]+=buff.length()+1;
				result[1]+=buff.split(" ").length;
				result[2]+=1;	
			}
			in.close();
		} catch (Exception e) {
			result[0]=-1;
			result[1]=-1;
			result[2]=-1;		
		}
		return result;
	}
	
	public void run(){
	while(true){
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		byte[] buf = new byte[256];
			try {
				socket = new DatagramSocket(port);
				packet = new DatagramPacket(buf, buf.length);
				System.out.println("[WCServer "+this.getNomeLogico()+"]: Creata la socket: " + socket);
			}
			catch (SocketException e) {
				System.out.println("[WCServer "+this.getNomeLogico()+"]: Problemi nella creazione della socket");
				e.printStackTrace();
				System.exit(1);
			}
	
			try {
				ByteArrayOutputStream boStream = null;
				DataOutputStream doStream = null;
				int[] risposta = new int[3];
				String output = null;
				byte[] data = null;
	
				while (true) {
					
					// ricezione del datagramma
					try {
						packet.setData(buf);
						socket.receive(packet);
					}
					catch (IOException e) {
						System.err.println("[WCServer "+this.getNomeLogico()+"]: Problemi nella ricezione del datagramma: "
								+ e.getMessage());
						e.printStackTrace();
						continue;
						// il server continua a fornire il servizio ricominciando dall'inizio
						// del ciclo
					}
					// preparazione della linea e invio della risposta
					try {
						risposta = WCServer.contaFile(this.getFile(),this.getNomeLogico());
						boStream = new ByteArrayOutputStream();
						doStream = new DataOutputStream(boStream);
						for(int i = 0; i <3; i++) 
							doStream.writeInt(risposta[i]);
						data = boStream.toByteArray();
						packet.setData(data, 0, data.length);
						System.out.println("[WCServer "+this.getNomeLogico()+"]: Invio risposta, Caratteri:"+risposta[0]+" Parole:"+risposta[1]+" Linee:"+risposta[2]);
						socket.send(packet);
					}
					catch (IOException e) {
						System.err.println("[WCServer "+this.getNomeLogico()+"]: Problemi nell'invio della risposta: "
					      + e.getMessage());
						e.printStackTrace();
						continue;
						// il server continua a fornire il servizio ricominciando dall'inizio
						// del ciclo
					}
	
				} // while
	
			}
			// qui catturo le eccezioni non catturate all'interno del while
			// in seguito alle quali il server termina l'esecuzione
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("[WCServer "+this.getNomeLogico()+"] termino...");
				socket.close();
			}
			socket.close();
		}
	}
}
