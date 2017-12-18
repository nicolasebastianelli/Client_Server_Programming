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

// Dichiarazione eventuali funzioni
int checkval(char str[]){
	int i = 0;
	while( str[i]!= '\0' )  {
		if((str[i] < '0') || (str[i] > '9'))  {
			printf ("Stringa non composta da numeri\n");
			return -1;
		}
		i++;
	}
	return 1;
}

int main (int argc, char *argv[])	{	// main client datagram
	char *host;
	CLIENT *cl;

	//Variabili per la logica del programma
	Prenotazione *prenotazione;
	int *result;
	char id[256],volo[256],buff[256];

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

	char cycleMessage[] ="Inserire: \nA) per visualizzare dati dato id\nB) per eliminare volo dato id volo\n^D per terminare: ";

	//Inizio della logica del programma
	printf("%s",cycleMessage);
	while (gets (buff))	{

		if( strcmp(buff, "A")==0 )	{
			//prima funzione

			//logica per recuperare la scelta specifica
			printf("Inserire id prenotazione: ");
			gets(id);
			char *prt = &(id[0]);
			printf("RPC_Client: Invio richiesta visualizzazione dati per id : %s\n",id);
			prenotazione = visualizza_dati_1(&prt, cl);
			if(prenotazione == NULL)	{
				clnt_perror(cl, host);
				exit(1);
			}
			if(!strcmp(prenotazione->id,"L"))
				printf("Errore , nessuna prenotazione presente con quell' id\n");
			else{
				printf("Id\tVolo\tOra\tNome\tGate\n");
					printf("%s\t%s\t%d:%d\t%s\t%d\n",prenotazione->id,prenotazione->volo,prenotazione->ora,prenotazione->min,prenotazione->nome,prenotazione->gate);
			}
		}
		else if(strcmp(buff,"B")==0 )	{
			//seconda funzione
			printf("Inserire numero volo: ");
			gets(volo);
			char *prt = &(volo[0]);
			result = elimina_volo_1(&prt,cl);
			if(result == NULL)	{
				clnt_perror(cl, host);
				exit(1);
			}
			if(*result==0)
				printf("RPC_Client: Eliminazione andata a buon fine\n");
			else
				printf("RPC_Client: Errore, Id volo non trovato\n");
		} else
			printf("Errore!\n");
		printf("%s",cycleMessage);
	}
	printf("RPC_Client: termino...");
	// Libero le risorse, distruggendo il gestore di trasporto
	clnt_destroy (cl);
	exit (0);

}
