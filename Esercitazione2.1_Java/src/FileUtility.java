//Nicola Sebastianelli 0000722894 esercitazione 2

import java.io.*;

public class FileUtility {

	static protected void trasferisci_a_byte_file_binario(DataInputStream src,
			DataOutputStream dest,long length) throws IOException {

		// ciclo di lettura da sorgente e scrittura su destinazione
		int buffer;    
		try {
			// esco dal ciclo all lettura di un valore negativo -> EOF
			// N.B.: la funzione consuma l'EOF
			for(int i =0 ; i<length ;i++){
				buffer=src.read();
				dest.write(buffer);
			}
			dest.flush();
		}
		catch (EOFException e) {
			System.out.println("Problemi, i seguenti: ");
			e.printStackTrace();
		}
	}
}