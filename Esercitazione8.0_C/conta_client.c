//Nicola Sebastianelli 0000722894 Esercitazione 8

#include <stdio.h>
#include <rpc/rpc.h>
#include "conta.h"
#define DIM 64

int main(int argc, char *argv[]){
	CLIENT *cl;
	Result *ris1;
	int *ris2;
	char *server;
	Contaocc c1;
	Contafile c2;
	char msg[DIM];
	char c;
	if (argc != 2) {
		fprintf(stderr, "uso: %s host\n", argv[0]);
		exit(1);
	}
	server = argv[1];
	cl = clnt_create(server, CONTAPROG,CONTAVERS,"udp");
	if (cl == NULL) {
		clnt_pcreateerror(server);
		exit(1);
	}
	printf("Inserire 'o' per contare in numero di occorrenze , 'f' per contare il numero di file, EOF per terminare\n");
	while ((c = getchar())!=EOF){
		while ((getchar())!='\n');
		if (c!= 'o' && c != 'f'){
			fprintf(stderr, "Carattere non riconosciuto\n");
			printf("Inserire 'o' per contare in numero di occorrenze , 'f' per contare il numero di file, EOF per terminare\n");
			continue;
		}
		if( c == 'o' )
		{
			char nomefile[64];
			char parola[64];
			printf("Inserire nome file:\n");
			gets(nomefile);
			c1.nomeFile=nomefile;
			printf("Inserire parola da cercare:\n");
			gets(parola);
			c1.parola=parola;
			ris1 = contaocc_1(&c1,cl);
			if (ris1 == NULL) {
				clnt_perror(cl, server);
				printf("Inserire 'o' per contare in numero di occorrenze , 'f' per contare il numero di file, EOF per terminare\n");
				continue;
			}
			if(ris1->caratteri==-1&&ris1->linee==-1&&ris1->parolaspec==-1&&ris1->parole==-1)
				printf("File inesistente\n");
			else
				printf("Risultato, caratteri: %d, parole: %d , righe: %d, occorrenze:%d\n",ris1->caratteri,ris1->parole,ris1->linee,ris1->parolaspec);
		}
		if( c == 'f' ) {
			char dir[256];
			char pref[64];
			printf("Inserire nome directory:\n");
			gets(dir);
			c2.dir=dir;
			printf("Inserire prefisso:\n");
			gets(pref);
			c2.pref=pref;
			printf("Inserire dimensione minima(Byte):\n");
			gets(msg);
			if((c2.dim=atoi(msg))==0){
				printf("Input non valido\n");
				printf("Inserire 'o' per contare in numero di occorrenze , 'f' per contare il numero di file, EOF per terminare\n");
				continue;
			}
			ris2 = contafile_1(&c2,cl);
			if (ris2==NULL) {
				clnt_perror(cl, server);
				printf("Inserire 'o' per contare in numero di occorrenze , 'f' per contare il numero di file, EOF per terminare\n");
				continue;
			}
			if(*ris2==-1)
				printf("Directory non esistente\n");
			else
				printf("Risultato, numero di file che iniziano per %s e di dimensione minima %d : %d\n",c2.pref,c2.dim,*ris2);
		}
		printf("Inserire 'o' per contare in numero di occorrenze , 'f' per contare il numero di file, EOF per terminare\n");
	}
	clnt_destroy(cl);
	printf("Termino...\n");
	exit(0);
}
