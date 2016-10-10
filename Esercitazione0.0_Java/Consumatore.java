import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Consumatore {
	public static void main(String[] args) {
		FileReader r = null;
		int x; char ch;
		try {
			r = new FileReader(args[0]);
		} catch(FileNotFoundException e){
			System.out.println("File non trovato");
			System.exit(1);
		}
		try {
			while ((x = r.read())>=0) { 
				ch = (char) x;
				System.out.print(ch);
			}
		} catch(IOException ex){
			System.out.println("Errore di input");
			System.exit(2);
		}
}}
