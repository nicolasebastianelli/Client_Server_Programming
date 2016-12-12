/*
 * Nicola Sebastianelli
 * 0000722894
 * Esercitazione 9
 *
 */

#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <rpc/rpc.h>
#include "votasanremo.h"

#define LUNGTABLE 10

/* STATO SERVER */
static Canzone table[LUNGTABLE];
static int inizializzato=0;

void inizializza(){
	int i;

	if( inizializzato==1 ) return;

	// inizializzazione struttura dati
	for(i =0 ; i<LUNGTABLE ;i++){
		table[i].cantante=(char*) malloc(64);
		table[i].titolo=(char*) malloc(64);
		table[i].nomeFile=(char*) malloc(64);
		strcpy(table[i].cantante,"L");
		strcpy(table[i].titolo,"L");
		table[i].voti=-1;
		strcpy(table[i].nomeFile,"L");
	}
	strcpy(table[0].cantante,"Drake");
	strcpy(table[0].titolo,"One dance");
	table[0].voti=100;
	strcpy(table[0].nomeFile,"OD.txt");
	strcpy(table[1].cantante,"Coldplay");
	strcpy(table[1].titolo,"Hymn for the weekend");
	table[1].voti=99;
	strcpy(table[1].nomeFile,"HFTW.txt");
	strcpy(table[2].cantante,"Justin Bieber");
	strcpy(table[2].titolo,"Love yourself");
	table[2].voti=56;
	strcpy(table[2].nomeFile,"LY.txt");
	strcpy(table[3].cantante,"Rihanna");
	strcpy(table[3].titolo,"Work");
	table[3].voti=200;
	strcpy(table[3].nomeFile,"W.txt");
	strcpy(table[4].cantante,"Sia");
	strcpy(table[4].titolo,"Cheap Thrills");
	table[4].voti=10;
	strcpy(table[4].nomeFile,"CT.txt");
	strcpy(table[5].cantante,"Major Lazer");
	strcpy(table[5].titolo,"Cold water");
	table[5].voti=0;
	strcpy(table[5].nomeFile,"CW.txt");

	inizializzato = 1;
	printf("Server: Terminata inizializzazione struttura dati!\n");
}


Podio * visualizza_podio_1_svc(void *in, struct svc_req *rqstp) {
	static Podio result;
	inizializza();
	int curmax,curind,precmax=-1,i,j,k;
	Canzone canzpre;
	printf("\nServer: Ricevuta richiesta di visualizzazione podio\n");
	for(k =0 ; k<LUNGTABLE;k++)
		if((table[k].voti>precmax)){
			precmax=table[k].voti;
		}
	for(j=0; j<3 ;j++){
		curmax=-1;
		curind=-1;
		for(i=0;i<LUNGTABLE;i++){
			if((table[i].voti>curmax)&&(table[i].voti<=precmax)&&(strcmp(canzpre.cantante,table[i].cantante)!=0)){
				curmax=table[i].voti;
				curind=i;
			}
		}
		precmax=curmax;
		result.canzoni[j]=table[curind];
		canzpre=table[curind];
	}
	printf("Server: Podio\n\t--------\nCantante\tTitolo\tVoti\tNome File\n");
	for(k =0 ; k<3;k++){
		printf("%s\t\t%s\t%d\t%s\n",result.canzoni[k].cantante,result.canzoni[k].titolo,result.canzoni[k].voti,result.canzoni[k].nomeFile);
	}
	return (&result);
}


int * esprimi_voto_1_svc(char **in, struct svc_req *rqstp){
	static int result,i;
	inizializza();
	result = -1;
	printf("\nServer: Ricevuta richiesta di votazione\n");
	for(i=0; i<LUNGTABLE;i++){
		if(!strcmp(table[i].cantante,*in)||!strcmp(table[i].titolo,*in)){
			table[i].voti++;
			result=1;
			printf("Server: votazione effettuata per %s\n",*in);
			break;
		}
	}
	if(result == -1)
		printf("Server: Elemento non presente nella tabella\n");
	return (&result);
}
