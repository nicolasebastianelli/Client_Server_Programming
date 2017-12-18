/**
 * Nicola	Sebastianelli
 * 
 * 0000722894
 * 
 * RPC_xFile.x
 */

const N = 256;

struct Prenotazione{
  char id[N];
  char volo[N];
  int ora;
  int min;
  char nome[N];
  int gate;
};

program PROGRAM {
  version PROGRAMVERS{
    Prenotazione VISUALIZZA_DATI(string) = 1;
    int ELIMINA_VOLO(string) = 2;
  } = 1;
} = 0x20000013;
