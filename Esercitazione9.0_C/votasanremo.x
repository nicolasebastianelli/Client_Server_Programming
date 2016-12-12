/* 
 * Nicola Sebastianelli
 * 0000722894
 * Esercitazione 9
 *	
 */

const LUNGHPOD=3;
const NUMFILE=10;

struct Canzone{
	string cantante <64>;
	string titolo <64>;
	string nomeFile <64>;
	int voti;
};

struct Podio{
	Canzone canzoni[LUNGHPOD];
};

program VOTASANREMO {
	version VOTASANREMOVERS{
		Podio  VISUALIZZA_PODIO(void) = 1;
		int ESPRIMI_VOTO(string) = 2;
	} = 1;
} = 0x20000013;
