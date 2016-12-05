/* 
 * Nicola Sebastianelli 0000722894 Esercitazione 8
 */

struct Result{
	int caratteri;
	int parole;
	int linee;
	int parolaspec;
};

struct Contaocc{
	string nomeFile <64>;
	string parola <64>;
};

struct Contafile{
	string dir <256>;
	string pref <64>;
	int dim;
};


program CONTAPROG {
	version CONTAVERS {
		Result CONTAOCC(Contaocc) = 1;
		int CONTAFILE(Contafile) = 2;
	} = 1;
} = 0x20000013;


