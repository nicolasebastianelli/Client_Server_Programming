// Nicola Sebastianelli 0000722894 Esercitazione 7
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistryRemotoTagServer extends RegistryRemotoTagClient,RegistryRemotoServer {
	public boolean associaTags(String logicServer, String[] tags) throws RemoteException;
}