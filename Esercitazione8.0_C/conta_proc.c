//Nicola Sebastianelli 0000722894 Esercitazione 8

#include <stdio.h>
#include <fcntl.h>
#include <dirent.h>
#include <rpc/rpc.h>
#include "conta.h"
#include <sys/stat.h>

Result * contaocc_1_svc(Contaocc *conta, struct svc_req *rp){
	static Result ris;
	int fd_sorg,nread,i=0;
	char c,cpre;
	ris.caratteri=0;
	ris.linee=0;
	ris.parole=0;
	ris.parolaspec=0;
	printf("Ricevuto file: %s e parola %s\n", conta->nomeFile, conta->parola);
	printf("Conteggio sul file: %s\n",conta->nomeFile);
	if((fd_sorg=open(conta->nomeFile, O_RDONLY))<0){
		perror("File non esistente");
		ris.caratteri=-1;
		ris.linee=-1;
		ris.parole=-1;
		ris.parolaspec=-1;
	}
	else{
		while((nread=read(fd_sorg, &c, sizeof(char)))>0){
			if(c=='\n'){
				ris.linee++;
			}
			if((c==' '||c=='\t'||c=='.'||c==':'||c==';'||c==','||c=='\n')&&(cpre!=' '&&cpre!='\n'&&cpre!='\t'&&cpre!='.'&&cpre!=':'&&cpre!=';'&&cpre!=','))
				ris.parole++;
			if(c==conta->parola[i]){
				i++;
				if(i==strlen(conta->parola))
					ris.parolaspec++;
			}
			else
				i=0;			
			cpre=c;
			ris.caratteri++;
		}
	}
	printf("Invio risultato: [%d,%d,%d,%d]\n", ris.caratteri,ris.parole,ris.linee,ris.parolaspec);
	return (&ris);
}

int * contafile_1_svc(Contafile *conta, struct svc_req *rp){
	static int ris=0,err=-1;
	DIR *dirp;
	int size;
	struct stat st;
	struct dirent * entry;
	ris=0;
	printf("Ricevuto direttorio: %s, prefisso %s e dimensione %d\n", conta->dir, conta->pref,conta->dim);
	if((dirp= opendir(conta->dir))==NULL)
	{
		perror("Directory non esistente");
		return &err;
	}
	else{
		while((entry = readdir(dirp))!= NULL){
			stat(entry->d_name,&st);
			size=st.st_size;
			printf("Lunghezza del file %s: %d byte",entry->d_name,size);
			if((strncmp(conta->pref,entry->d_name,strlen(conta->pref))==0)&&size>=conta->dim){
				ris++;
				printf("\t\t\tOK\n");
			}
			else
				printf("\n");
		}
		closedir(dirp);
		printf("Invio risultato: %d\n", ris);
		return (&ris);
	}

}
