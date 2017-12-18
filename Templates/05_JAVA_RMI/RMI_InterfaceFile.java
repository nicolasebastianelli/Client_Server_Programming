/**
 * Nicola	Sebastianelli
 * 
 * 0000722894
 * 
 * RMI_InterfaceFile.java
 */

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMI_InterfaceFile extends Remote {
	
	//TODO DICHIARARE FUNZIONI
	String function(String richiesta) throws java.rmi.RemoteException;

}
