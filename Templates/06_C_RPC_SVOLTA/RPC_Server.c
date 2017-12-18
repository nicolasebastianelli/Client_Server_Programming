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

#define TABSIZE 5

static Prenotazione table[TABSIZE];
static int inizializzato=0;

void inizializza()	{
	int i;

	if( inizializzato==1 )
		return;

	//TODO Inizializzazione struttura dati
	for(i=0;i<TABSIZE;i++){
		table[i] = (Prenotazione) {"L","L",-1,-1,"L",-1};
	}
	table[1] = (Prenotazione) {"AAAAA","11111",16,45,"Mario Rossi",12};
	table[2] = (Prenotazione) {"BBBBB","11111",16,45,"Luigi Bianchi",12};
	table[4] = (Prenotazione) {"CCCCC","22222",7,50,"Alessandro Franca",14};

	printf("Id\tVolo\tOra\tNome\tGate\n");
	for(i=0;i<TABSIZE;i++){
		printf("%s\t%s\t%d:%d\t%s\t%d\n",table[i].id,table[i].volo,table[i].ora,table[i].min,table[i].nome,table[i].gate);
	}
	inizializzato = 1;
	printf("RPC_Server: Terminata inizializzazione struttura dati!\n");
}


//TODO IMPLEMENTAZIONE FUNZIONI
Prenotazione * visualizza_dati_1_svc(char **id, struct svc_req *rqstp)		{
	int i;
	static Prenotazione result;
	inizializza();
	printf("\nRPC_Server: Ricevuta richiesta visualizzazione dati con id: %s\n",*id);
	result = (Prenotazione) {"L","L",-1,-1,"L",-1};

	for(i=0;i<TABSIZE;i++){
		if(!strcmp(table[i].id,*id)){
			result=table[i];
			break;
		}
	}
	printf("RPC_Server: Invio risultato:\n");

	printf("Id\tVolo\tOra\tNome\tGate\n");
	printf("%s\t%s\t%d:%d\t%s\t%d\n",result.id,result.volo,result.ora,result.min,result.nome,result.gate);
	return (&result);
}

int * elimina_volo_1_svc(char **volo, struct svc_req *rqstp)	{
	inizializza();
	int i;
	static int result;
	printf("\nRPC_Server: Ricevuta richiesta di elimina per volo %s\n",*volo);
	result=-1;
	for(i = 0; i < TABSIZE; i++)
	{
		if(!strcmp(*volo,table[i].volo)){
			table[i] = (Prenotazione) {"L","L",-1,-1,"L",-1};
			result=0;
		}
	}
	printf("RPC_Server: Invio risultato %d\n",result);
	printf("Id\tVolo\tOra\tNome\tGate\n");
	for(i=0;i<TABSIZE;i++){
		printf("%s\t%s\t%d:%d\t%s\t%d\n",table[i].id,table[i].volo,table[i].ora,table[i].min,table[i].nome,table[i].gate);
	}
	return (&result);
}


