import java.io.Serializable;


public class Cantante implements Serializable{
String nome="L";
String categoria="L";
int voto=-1;
String audio="L";
public Cantante(){}
public Cantante(String nome, String categoria, int voto, String audio) {
	super();
	this.nome = nome;
	this.categoria = categoria;
	this.voto = voto;
	this.audio = audio;
}
public String getNome() {
	return nome;
}
public String getCategoria() {
	return categoria;
}
public int getVoto() {
	return voto;
}
public String getAudio() {
	return audio;
}
}
