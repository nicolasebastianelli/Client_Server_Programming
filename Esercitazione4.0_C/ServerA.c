//Nicola Sebastianelli 0000722894 Esercitazione 4

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <signal.h>
#include <errno.h>
#include <fcntl.h>
#include <dirent.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>

#define STRINGA_RICEZIONE_SERVER 20

typedef struct
{ char stringa[20];
int intero;
char carattere;
} struttura1;

typedef struct
{ char* stringa;
int intero;
char carattere;
} struttura2;

/********************************************************/
void gestore(int signo){
  int stato;
  printf("esecuzione gestore di SIGCHLD\n");
  wait(&stato);
}
/********************************************************/

int main(int argc, char **argv)
{
	int  listen_sd, conn_sd;
	int port, len,nread,i,num;
	char c,buff[STRINGA_RICEZIONE_SERVER],buff2[STRINGA_RICEZIONE_SERVER];
	const int on = 1;
	struct sockaddr_in cliaddr, servaddr;
	struct hostent *host;
	struttura1 str1;
	struttura2 str2;

	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if(argc!=2){
		printf("Error: %s port\n", argv[0]);
		exit(1);
	}
	else{
		num=0;
		while( argv[1][num]!= '\0' ){
			if( (argv[1][num] < '0') || (argv[1][num] > '9') ){
				printf("Secondo argomento non intero\n");
				exit(2);
			}
			num++;
		}
		port = atoi(argv[1]);
		if (port < 1024 || port > 65535){
			printf("Error: %s port\n", argv[0]);
			printf("1024 <= port <= 65535\n");
			exit(2);
		}

	}

	/* INIZIALIZZAZIONE INDIRIZZO SERVER ----------------------------------------- */
	memset ((char *)&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = INADDR_ANY;
	servaddr.sin_port = htons(port);

	/* CREAZIONE E SETTAGGI SOCKET D'ASCOLTO --------------------------------------- */
	listen_sd=socket(AF_INET, SOCK_STREAM, 0);
	if(listen_sd <0)
	{perror("creazione socket "); exit(1);}
	printf("Server: creata la socket d'ascolto per le richieste di ordinamento, fd=%d\n", listen_sd);

	if(setsockopt(listen_sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on))<0)
	{perror("set opzioni socket d'ascolto"); exit(1);}
	printf("Server: set opzioni socket d'ascolto ok\n");

	if(bind(listen_sd,(struct sockaddr *) &servaddr, sizeof(servaddr))<0)
	{perror("bind socket d'ascolto"); exit(1);}
	printf("Server: bind socket d'ascolto ok\n");

	if (listen(listen_sd, 5)<0) //creazione coda d'ascolto
	{perror("listen"); exit(1);}
	printf("Server: listen ok\n");

	/* AGGANCIO GESTORE PER EVITARE FIGLI ZOMBIE,
	* Quali altre primitive potrei usare? E' portabile su tutti i sistemi?
	* Pregi/Difetti?
	* Alcune risposte le potete trovare nel materiale aggiuntivo!
	*/
	signal(SIGCHLD, gestore);

	/* CICLO DI RICEZIONE RICHIESTE --------------------------------------------- */
	for(;;){
	  	len=sizeof(cliaddr);
		if((conn_sd=accept(listen_sd,(struct sockaddr *)&cliaddr,&len))<0){
			if (errno==EINTR){
				perror("Forzo la continuazione della accept");
				continue;
			}
			else exit(1);
		}

			if (fork()==0){ // figlio
						close(listen_sd);
						host=gethostbyaddr( (char *) &cliaddr.sin_addr, sizeof(cliaddr.sin_addr), AF_INET);
									if (host == NULL){
										printf("client host information not found\n"); continue;
									}
									else printf("Server (figlio): host client e' %s \n", host->h_name);
						read(conn_sd,&str1,sizeof(str1));
						read(conn_sd,&str2.carattere,sizeof(char));
						read(conn_sd,&str2.intero,sizeof(int));
						read(conn_sd,&len,sizeof(int));
						str2.stringa=(char*)malloc(len*sizeof(char));
						read(conn_sd,str2.stringa,len);
						printf("Ricevuta struttura1 con %c %d %s\n",str1.carattere,str1.intero,str1.stringa);
						printf("Ricevuta struttura2 con %c %d %s\n",str2.carattere,str2.intero,str2.stringa);
						free(str2.stringa);
						printf("------------Inizio parte 2:------------\n");
						sleep(10);
						while((nread=read(conn_sd,buff,sizeof(char)*STRINGA_RICEZIONE_SERVER))>0){
							printf("Figlio per %s, ricezione stringhe:\n",host->h_name);
							
							for( i =0 ; i<nread;i++){
								printf("%c",buff[i]);
								if(buff[i]=='\0'){
									printf("\n");
									
								}
							}
							sleep(10);
						}
						printf("Server (figlio:%s): termino\n",host->h_name);
						exit(1);
			}

		}
		close(conn_sd);  // padre chiude socket di connessione non di scolto
	} // ciclo for infinito

