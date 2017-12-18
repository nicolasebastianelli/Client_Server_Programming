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
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <rpc/rpc.h>
#include "RPC_xFile.h"



static Output state;
static int inizializzato=0;

void inizializza()	{
	int i, j;

	if( inizializzato==1 )
		return;

	//TODO Inizializzazione struttura dati

	//Termine inizializzazione
	inizializzato = 1;
	printf("RPC_Server: Terminata inizializzazione struttura dati!\n");
}


//TODO IMPLEMENTAZIONE FUNZIONI
Output * funzione_uno_1_svc(Input *input, struct svc_req *rqstp)		{
	int size,k,i;
		char temp, *buff;
		static Output result;
		inizializza();
        free(result);
		result.risultato=(char *)malloc(256);
		buff=(char *)malloc(256);
		strcpy(buff,input->richiesta);
		
		printf("RPC_Server: Ricevuto: %s\n",buff);
		
		strcpy(result.risultato,buff);
		printf("RPC_Server: Invio: %s\n",result.risultato);
		free(buff);
		return (&result);
}

Output * funzione_due_1_svc(void *in, struct svc_req *rqstp)	{
	inizializza();
	static Output result;
	printf("RPC_Server: Ricevuta richiesta dummy\n");
	result.risultato = "Questa funzione è dummy\n";
	return (&result);
}

