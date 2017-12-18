/**
 * Nicola	Sebastianelli
 * 
 * 0000722894
 * 
 * RMI_InterfaceFile.java
 */

import java.rmi.Remote;

public interface RMI_InterfaceFile extends Remote {
	
	//TODO DICHIARARE FUNZIONI
	int elimina_prenotazione(String id) throws java.rmi.RemoteException;
	Tabella[] visualizza_prenotazioni(int npers, String tipologia) throws java.rmi.RemoteException;
}
