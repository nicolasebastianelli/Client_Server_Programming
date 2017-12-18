import java.io.Serializable;

public class Tabella implements Serializable{

	private static final long serialVersionUID = 1L;
	private String id="L";
	private String npers="L";
	private String tipologia="L";
	private String veicolo ="L";
	private String targa = "L";
	private String immagine ="L";
	

	public Tabella(){}
	
	public Tabella(String id, String npers, String tipologia, String veicolo, String targa, String immagine) {
		super();
		this.id = id;
		this.npers = npers;
		this.tipologia = tipologia;
		this.veicolo = veicolo;
		this.targa = targa;
		this.immagine = immagine;
	}

	public String getId() {
		return id;
	}

	public String getNpers() {
		return npers;
	}

	public String getTipologia() {
		return tipologia;
	}

	public String getVeicolo() {
		return veicolo;
	}

	public String getTarga() {
		return targa;
	}

	public String getImmagine() {
		return immagine;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setNpers(String npers) {
		this.npers = npers;
	}

	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}

	public void setVeicolo(String veicolo) {
		this.veicolo = veicolo;
	}

	public void setTarga(String targa) {
		this.targa = targa;
	}

	public void setImmagine(String immagine) {
		this.immagine = immagine;
	}
	
	@Override
	public String toString() {
		return id + "\t" + npers + "\t" + tipologia + "\t" + veicolo + "\t" + targa
				+ "\t" + immagine;
	}
}
