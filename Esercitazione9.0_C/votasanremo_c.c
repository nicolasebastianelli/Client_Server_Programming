/*
 * Nicola Sebastianelli
 * 0000722894
 * Esercitazione 9
 *
 */

#include <stdio.h>
#include <rpc/rpc.h>
#include "votasanremo.h"

#define LUNGHFILA 7
#define NUMFILE 10

int main (int argc, char *argv[]){
	char *host;
	CLIENT *cl;
	int *ris;
	int k;
	void * in;
	Podio *podio;
	char  ok[256];

	if (argc != 2){
		printf ("usage: %s server_host\n", argv[0]);
		exit (1);
	}
	host = argv[1];

	cl = clnt_create (host, VOTASANREMO, VOTASANREMOVERS, "udp");
	if (cl == NULL){
		clnt_pcreateerror (host);
		exit (1);
	}

	printf("Inserire:\nV) per esprimere voto\tP) per visualizzare podio\t^D per terminare: ");

	while (gets (ok)){

		if( strcmp(ok,"V")==0 ){
			printf("Client: Inserire cantante o titolo: \n");
			gets(ok);
			char* s= ok;
			ris = esprimi_voto_1(&s, cl);
			if(ris == NULL) { clnt_perror(cl, host); exit(1); } 
			if(*ris<0)
				printf("Client: cantante o titolo inesistente\n");
			else printf("Client: votazione effettuata con successo\n");
		}

		else if( strcmp(ok,"P")==0 ){
			podio = visualizza_podio_1(in,cl);
			if(podio == NULL) { clnt_perror(cl, host); exit(1); }
			printf("\nClient: \tPodio Canzoni\n\t--------\nCantante\tTitolo\tVoti\tNome File\n");
				for(k =0 ; k<3;k++){
					printf("%s\t\t%s\t%d\t%s\n",podio->canzoni[k].cantante,podio->canzoni[k].titolo,podio->canzoni[k].voti,podio->canzoni[k].nomeFile);
				}
		}

		else printf("Argomento di ingresso errato!!\n");
		printf("\nInserire:\nV) per esprimere voto\tP) per visualizzare podio\t^D per terminare: ");
	} // while

	// Libero le risorse, distruggendo il gestore di trasporto
	clnt_destroy (cl);
	exit (0);
}
