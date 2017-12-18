import java.io.Serializable;

public class Tabella implements Serializable{

	private static final long serialVersionUID = 1L;
	private String provino="L";
	private String file="L";
	private int durata = -1;
	private String parole[] = new String[5];

	public Tabella(){
		for (int i=0;i<5;i++) {
			parole[i] ="L";
		}
	}

	public Tabella(String provino, String file, int durata, String[] parole) {
		super();
		this.provino = provino;
		this.file = file;
		this.durata = durata;
		this.parole = parole;
	}

	public String getProvino() {
		return provino;
	}

	public void setProvino(String provino) {
		this.provino = provino;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getDurata() {
		return durata;
	}

	public void setDurata(int durata) {
		this.durata = durata;
	}

	public String[] getParole() {
		return parole;
	}

	public void setParole(String[] parole) {
		this.parole = parole;
	}

	public String toString() {
		String result = provino + "\t" + file + "\t"	+ durata + "\t";
		for (String parola : parole) {
			result += parola+"\t";
		}
		result+="\n";
		return result;
	}
	

	
}
