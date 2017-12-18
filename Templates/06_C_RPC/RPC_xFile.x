/**
 * Nicola	Sebastianelli
 * 
 * 0000722894
 * 
 * RPC_xFile.x
 */

struct Input{
  string richiesta <256>;
};

struct Output{
  string risultato <256>;
};


program PROGRAM {
  version PROGRAMVERS{
    Output FUNZIONE_UNO(Input) = 1;
    Output FUNZIONE_DUE(void) = 2;
  } = 1;
} = 0x20000013;
