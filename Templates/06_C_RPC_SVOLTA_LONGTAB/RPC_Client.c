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
	Provini *provini;
	int *result,i,j;
	Istanti istanti;
	AggiungiP aggiungi;
	char buff[256];

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

	char cycleMessage[] ="Inserire: \nA) per ricerca di provini con intervallo compreso\nB) per aggiungere una parola\n^D per terminare: ";

	//Inizio della logica del programma
	printf("%s",cycleMessage);
	while (gets (buff))	{

		if( strcmp(buff, "A")==0 )	{
			//prima funzione

			//logica per recuperare la scelta specifica
			printf("Inserire intervallo minore: ");
			gets(buff);
			istanti.inizio=atoi(buff);
			printf("Inserire intervallo maggiore: ");
			gets(buff);
			istanti.fine=atoi(buff);
			if(istanti.inizio>=istanti.fine){
				printf("Errore inizio maggiore di fine\n: ");
				printf("%s",cycleMessage);
				continue;
			}
			printf("RPC_Client: Invio richiesta\n");
			provini = ricerca_provini_1(&istanti, cl);
			if(provini == NULL)	{
				clnt_perror(cl, host);
				exit(1);
			}
			if(!strcmp(provini->provini[0].nome,"L"))
				printf("Nessun provino trovato\n");
			else{
				printf("Nome\tFile\tDurata\t");
				for(i=0;i<provini->nelem;i++)
					printf("Parola %d\t",i+1);
				printf("\n");
				for(i=0;i<provini->nelem;i++){
					printf("%s\t%s\t%d\t",provini->provini[i].nome,provini->provini[i].file,provini->provini[i].durata);
					for(j=0;j<K;j++)
						printf("%s\t",provini->provini[i].parole[j].parola);
				}
				printf("\n");
			}
		}
		else if(strcmp(buff,"B")==0 )	{
			//seconda funzione
			printf("Inserire nome provino: ");
			gets(buff);
			strcpy(aggiungi.nome,buff);
			printf("Inserire parola: ");
			gets(buff);
			strcpy(aggiungi.parola.parola,buff);
			result = aggiungi_parola_1(&aggiungi,cl);
			if(result == NULL)	{
				clnt_perror(cl, host);
				exit(1);
			}
			if(*result==0)
				printf("RPC_Client: Inserimento andato a buon fine\n");
			else
				printf("RPC_Client: Errore, parola gia presente o tabella delle parole piena o nome non trovato\n");
		} else
			printf("Errore!\n");
		printf("%s",cycleMessage);
	}
	printf("RPC_Client: termino...");
	// Libero le risorse, distruggendo il gestore di trasporto
	clnt_destroy (cl);
	exit (0);

}
