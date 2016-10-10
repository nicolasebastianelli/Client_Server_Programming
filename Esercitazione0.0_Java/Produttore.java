import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Produttore {
	public static void main(String[] args) {		
		BufferedReader in = null;
		int res = 0;
		FileWriter fout;
		try {
			in = new BufferedReader(new InputStreamReader(System.in, "CP850"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			fout = new FileWriter(args[0]);
			System.out.println("Quante righe vuoi inserire?");
			res = Integer.parseInt(in.readLine());
			for (int i =0; i<res; i++){
				System.out.println("Inserisci la nuova riga");
				String inputl = in.readLine()+"\n";
				fout.write(inputl, 0, inputl.length());
			}		
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

