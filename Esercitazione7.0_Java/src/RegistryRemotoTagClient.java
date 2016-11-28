// Nicola Sebastianelli 0000722894 Esercitazione 7

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RegistryRemotoTagClient extends RegistryRemotoClient {
	public String[] cercaTag(String[] tags) throws RemoteException;
}