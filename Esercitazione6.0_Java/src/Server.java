
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote{
	int[] conta_fileTesto(String nomeFile, String parola)
			throws RemoteException;
	
	String[] rinomina_file(String nomeDir,String old_nomeFile,String new_nomeFile) 
		throws RemoteException;
	
}
