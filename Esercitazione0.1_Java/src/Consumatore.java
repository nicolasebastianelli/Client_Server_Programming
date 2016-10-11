//Nicola Sebastianelli 0000722894 esercitazione 0
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Consumatore {
	public static void main(String[] args) {
		InputStreamReader r = null;
		int x,i,npar=args.length,j=args[npar-1].length(); char ch; String str="";boolean flag=true;
		try {
			if(npar==2)
				r = new FileReader(args[0]);
			if(npar==1)
				r = new InputStreamReader(System.in);	
				
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
							if(str.charAt(i)!=args[npar-1].charAt(i))
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
