/**
 * Nicola	Sebastianelli
 * 
 * 0000722894
 * 
 * RPC_xFile.x
 */

const N = 256;
const K = 5;

struct Parola{
	char parola[N];
};

struct Provino{
  char nome[N];
  char file[N];
  int durata;
  Parola parole[K];
};

struct Istanti{
int inizio;
int fine;
};

struct AggiungiP{
	char nome[N];
  	Parola parola;
 };

struct Provini{
	Provino provini[8];
	int nelem;
};

program PROGRAM {
  version PROGRAMVERS{
    Provini RICERCA_PROVINI(Istanti) = 1;
    int AGGIUNGI_PAROLA(AggiungiP) = 2;
  } = 1;
} = 0x20000013;
