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
	Cantante[] visualizza_cantanti(String categoria) throws java.rmi.RemoteException;
	Cantante[] visualizzazione_piu_votato() throws java.rmi.RemoteException;
}
