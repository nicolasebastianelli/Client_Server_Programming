import java.io.Serializable;

public class Tabella implements Serializable{

	private static final long serialVersionUID = 1L;
	private String descrizione="L";
	private String tipo="L";
	private String data="L";
	private String luogo ="L";
	private int disp = -1;
	private int prezzo = -1;
	

	public Tabella(){}
	
	

	public Tabella(String descrizione, String tipo, String data, String luogo, int disp, int prezzo) {
		super();
		this.descrizione = descrizione;
		this.tipo = tipo;
		this.data = data;
		this.luogo = luogo;
		this.disp = disp;
		this.prezzo = prezzo;
	}



	public String getDescrizione() {
		return descrizione;
	}



	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}



	public String getTipo() {
		return tipo;
	}



	public void setTipo(String tipo) {
		this.tipo = tipo;
	}



	public String getData() {
		return data;
	}



	public void setData(String data) {
		this.data = data;
	}



	public String getLuogo() {
		return luogo;
	}



	public void setLuogo(String luogo) {
		this.luogo = luogo;
	}



	public int getDisp() {
		return disp;
	}



	public void setDisp(int disp) {
		this.disp = disp;
	}



	public int getPrezzo() {
		return prezzo;
	}



	public void setPrezzo(int prezzo) {
		this.prezzo = prezzo;
	}
	
	@Override
	public String toString() {
		return descrizione + "\t" + tipo + "\t" + data + "\t" + luogo + "\t" + disp
				+ "\t" + prezzo;
	}

	
}
