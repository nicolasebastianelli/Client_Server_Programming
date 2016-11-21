
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerImpl extends UnicastRemoteObject implements
		Server {
	
	private static final long serialVersionUID = -7544080675853750240L;

	// Costruttore
	public ServerImpl() throws RemoteException {
		super();
	}

	
	public int[] conta_fileTesto(String nomeFile, String parola) throws RemoteException {
		int[] ris = new int[4];
		ris[0]=0;
		ris[1]=0;
		ris[2]=0;
		ris[3]=0;
		String buff;
		String[] wstr;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(nomeFile));
			System.out.println("File aperto: " + nomeFile);
			while((buff=in.readLine()) != null){
				ris[0]+=buff.length()+1;
				wstr=buff.split("[,\\s\\-:\\?]");
				ris[1]+=wstr.length;
				ris[2]++;	
				for(int i=0; i<wstr.length;i++){
					if(wstr[i].equals(parola))
						ris[3]++;
				}
			}
			in.close();
		} catch (Exception e) {
			throw new RemoteException();	
		}
		return ris;
	}
	
	public String[] rinomina_file(String nomeDir, String old_nomeFile, String new_nomeFile) throws RemoteException {
		// File (or directory) with old name
		File dir = new File(nomeDir);
		File file = new File(nomeDir+old_nomeFile);
		File[] listOfFile;
		String[] res = new String[256];
		File file2 = new File(nomeDir+new_nomeFile);
		if (!dir.isDirectory())
			throw new RemoteException();
		if (!file.exists())
		   throw new RemoteException();
		if (file2.exists())
		   throw new RemoteException();

		boolean success = file.renameTo(file2);

		if (!success) {
			throw new RemoteException();
		}
		else{
			listOfFile= dir.listFiles();
			for (int i = 0; i < listOfFile.length; i++) {
				res[i]=listOfFile[i].getName();
			}
			return res;
		}
			
	}

	

	// Avvio del Server RMI
	public static void main(String[] args) {

		int REGISTRYPORT = -1;
		String registryHost = "localhost";
		String serviceName = "Server";		//lookup name...
		
		if(args.length != 1){
			System.out.println("Sintassi: registryPort");
			System.exit(1);
		}
		try {
			REGISTRYPORT = Integer.parseInt(args[0]);
			if (REGISTRYPORT < 1024 || REGISTRYPORT > 65535) {
				System.out.println("Usage: java Server [registryPort>1024 && <65535]");
				System.exit(1);
			}
		} catch (NumberFormatException e) {
			System.out.println("Usage: java Server registryPort not a number");
			System.exit(1);
		}
		// Registrazione del servizio RMI
		String completeName = "//" + registryHost + ":" + REGISTRYPORT + "/"
				+ serviceName;
		try{
			ServerImpl serverRMI = new ServerImpl();
			Naming.rebind(completeName, serverRMI);
			System.out.println("Server RMI: Servizio \"" + serviceName
					+ "\" registrato");
		}
		catch(Exception e){
			System.err.println("Server RMI \"" + serviceName + "\": "
					+ e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
}