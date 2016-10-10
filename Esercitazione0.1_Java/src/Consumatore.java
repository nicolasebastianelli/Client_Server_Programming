//Nicola Sebastianelli 0000722894 esercitazione 0
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Consumatore {
	public static void main(String[] args) {
		FileReader r = null;
		int x,i,j=args[1].length(); char ch; String str="";boolean flag=true;
		try {
			r = new FileReader(args[0]);
		} catch(FileNotFoundException e){
			System.out.println("File non trovato");
			System.exit(1);
		}
		try {
			while ((x = r.read())>=0) { 
				ch = (char) x;
				str=str+ch;
				if(ch=='\n')
				{	
					if(str.length()>=j){
						for(i=0;i<j;i++)
						{
							if(str.charAt(i)!=args[1].charAt(i))
							{
								flag=false;
							}
						}
						if(flag==true)
						{
							System.out.print(str);
							
						}
						else{
							flag=true;
						}
					}
					str="";
				}				
			}
		} catch(IOException ex){
			System.out.println("Errore di input");
			System.exit(2);
		}
}}
