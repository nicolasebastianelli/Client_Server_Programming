//Nicola Sebastianelli 0000722894 esercitazione 0
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Produttore {
	public static void main(String[] args) {		
		BufferedReader in = null;
		String inputl;
		FileWriter fout;
		try {
			in = new BufferedReader(new InputStreamReader(System.in, "CP850"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			fout = new FileWriter(args[0]);
			System.out.println("Inserisci il testo:");
			while((inputl= in.readLine())!= null){
				inputl=inputl+"\n";
				fout.write(inputl, 0, inputl.length());
			}		
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

