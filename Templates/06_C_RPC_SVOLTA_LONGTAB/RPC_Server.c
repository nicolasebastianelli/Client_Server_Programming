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

#define TABSIZE 8
#define K 5

static Provini table;
static int inizializzato=0;

void inizializza()	{
	int i,j;

	if( inizializzato==1 )
		return;

	//TODO Inizializzazione struttura dati
	for(i=0;i<TABSIZE;i++){
		strcpy(table.provini[i].nome,"L");
		table.provini[i].durata=-1;
		strcpy(table.provini[i].file,"L");
		for(j=0;j<K;j++){
			strcpy(table.provini[i].parole[j].parola,"L");
		}
	}

	strcpy(table.provini[1].nome,"14061978_Lele_Jane");
	table.provini[1].durata=180;
	strcpy(table.provini[1].file,"1.avi");
	strcpy(table.provini[1].parole[3].parola,"basso");
	strcpy(table.provini[1].parole[2].parola,"andante");


	strcpy(table.provini[3].nome,"21091988_Jappi_Maria");
	table.provini[3].durata=345;
	strcpy(table.provini[3].file,"2.avi");
	strcpy(table.provini[3].parole[2].parola,"andante");

	strcpy(table.provini[4].nome,"01111983_Cecco_Elvon");
	table.provini[4].durata=830;
	strcpy(table.provini[4].file,"3.avi");
	strcpy(table.provini[4].parole[3].parola,"lento");
	strcpy(table.provini[4].parole[2].parola,"acuto");
	strcpy(table.provini[4].parole[1].parola,"dolce");

	printf("Nome\tFile\tDurata\t");
	for(i=0;i<K;i++)
		printf("Parola %d\t",i+1);
	printf("\n");
	for(i=0;i<TABSIZE;i++){
		printf("%s\t%s\t%d\t",table.provini[i].nome,table.provini[i].file,table.provini[i].durata);
		for(j=0;j<K;j++)
			printf("%s\t",table.provini[i].parole[j].parola);
		printf("\n");
	}

	inizializzato = 1;
	printf("RPC_Server: Terminata inizializzazione struttura dati!\n");
}


//TODO IMPLEMENTAZIONE FUNZIONI
Provini * ricerca_provini_1_svc(Istanti *input, struct svc_req *rqstp)		{
	int i,j,count;
	static Provini result;
	inizializza();
	count=0;
	result.nelem=0;
	for(i=0;i<TABSIZE;i++){
		strcpy(result.provini[i].nome,"L");
		result.provini[i].durata=-1;
		strcpy(result.provini[i].file,"L");
		for(j=0;j<K;j++){
			strcpy(result.provini[i].parole[j].parola,"L");
		}
	}
	printf("\nRPC_Server: Ricevuta richiesta ricerca provini con istanti compresi tra %d-%d\n",input->inizio,input->fine);

	for(i=0; i<TABSIZE && count < 8;i++){
		if(table.provini[i].durata > input->inizio && table.provini[i].durata < input->fine){
			/*
			strcpy(result.provini[count].file,table.provini[i].file);
			strcpy(result.provini[count].nome,table.provini[i].nome);
			result.provini[count].durata=table.provini[i].durata;
			for(j=0;j<K;j++)
				strcpy(result.provini[count].parole[j].parola,table.provini[i].parole[j].parola);*/

			memcpy(&(result.provini[count]), &(table.provini[i]), sizeof(Provino));
			count++;
		}
	}
	result.nelem=count;

	if (count==0) {
		printf("\nRPC_Server: Nessun provino trovato\n");
		return &result;
	}

	printf("Nome\tFile\tDurata\t");
	for(i=0;i<K;i++)
		printf("Parola %d\t",i+1);
	printf("\n");
	for(i=0;i<TABSIZE;i++){
		printf("%s\t%s\t%d\t",result.provini[i].nome,result.provini[i].file,result.provini[i].durata);
		for(j=0;j<K;j++)
			printf("%s\t",result.provini[i].parole[j].parola);
		printf("\n");
	}
	printf("%p\n", &result);

	printf("RPC_Server: Invio %d risultati\n",result.nelem);
	return (&result);
}

int * aggiungi_parola_1_svc(AggiungiP *input, struct svc_req *rqstp)	{
	inizializza();
	int i,j;
	static int result;
	printf("\nRPC_Server: Ricevuta richiesta di aggiunta parola: %s per provino %s\n",input->nome,input->parola.parola);
	result=-1;
	for(i = 0; i < TABSIZE; i++)
	{
		if(!strcmp(table.provini[i].nome,input->nome)){
			for(j=0;j<K;j++){
				if(!strcmp(input->parola.parola,table.provini[i].parole[j].parola))
				{
					printf("RPC_Server: Parola gia presente\n");
					return &result;
				}
				if(!strcmp("L",table.provini[i].parole[j].parola))
				{
					strcpy(table.provini[i].parole[j].parola,input->parola.parola);
					printf("RPC_Server: Parola inserita\n");
					result=0;
					break;
				}
			}

		}
	}
	printf("RPC_Server: Invio risultato %d\n",result);
	printf("Nome\tFile\tDurata\t");
	for(i=0;i<K;i++)
		printf("Parola %d\t",i+1);
	printf("\n");
	for(i=0;i<TABSIZE;i++){
		printf("%s\t%s\t%d\t",table.provini[i].nome,table.provini[i].file,table.provini[i].durata);
		for(j=0;j<K;j++)
			printf("%s\t",table.provini[i].parole[j].parola);
		printf("\n");
	}

	return (&result);
}


