//Nicola Sebastianelli 0000722894 Esercitazione 6

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemOp extends Remote{
	int[] conta_fileTesto(String nomeFile, String parola)
			throws RemoteException;
	
	String[] rinomina_file(String nomeDir,String old_nomeFile,String new_nomeFile) 
		throws RemoteException;
	
}
