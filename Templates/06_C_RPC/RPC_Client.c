/**
 * Nicola	Sebastianelli
 *
 * 0000722894
 *
 * RPC_Server.c
 */

#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <rpc/rpc.h>
#include <stdlib.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include "RPC_xFile.h"

#define LUNGHFILA 7
#define NUMFILE 10

// Dichiarazione eventuali funzioni

int main (int argc, char *argv[])	{	// main client datagram
	char *host;
	CLIENT *cl;

	//Variabili per la logica del programma
	int *ris, *start_ok;
	void * in;
	Output *output;
	Input input;
	char str[256];
	int intero;
	int  i, j;
	char c, buff[256];

	//Controllo del numero di argomenti
	if (argc != 2)	{
		printf("Using: %s serverAddress\n", argv[0]);
		exit (1);
	}
	host = argv[1];

	cl = clnt_create(host, PROGRAM, PROGRAMVERS, "udp");
	if (cl == NULL)	{
		clnt_pcreateerror (host);
		exit (1);
	}


	//Inizio della logica del programma
	char cycleMessage[] ="Inserire: \nA) per prima funzione\nB) per seconda funzinoe\n^D per terminare: ";
    printf("%s",cycleMessage);
	while (gets (buff))	{

		if( strcmp(buff, "A")==0 )	{
			//prima funzione

			//logica per recuperare la scelta specifica
			printf("Inserire input: ");
			gets(buff);
			input.richiesta=buff;
			
			// Invocazione remota
			printf("RPC_Client: Richiedo: %s\n",input.richiesta);			
			output = funzione_uno_1(&input, cl);
			if(output == NULL)	{
				clnt_perror(cl, host);
				exit(1);
			}
			printf("RPC_Client: Ricevuto: %s\n\n", output->risultato);

		}	else if(strcmp(buff,"B")==0 )	{
			//seconda funzione

			output = funzione_due_1(in,cl);
			printf("RPC_Client: %s\n",output->risultato);
		} else
			printf("Errore!\n");
		printf("%s",cycleMessage);
	}
	printf("RPC_Client: termino...");
	// Libero le risorse, distruggendo il gestore di trasporto
	clnt_destroy (cl);
	exit (0);

}
