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
#include <sys/time.h>

#define STRINGA_RICEZIONE_SERVER 20


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
	int port, len,nread,num,ntot;
	const int on = 1;
	struct sockaddr_in cliaddr, servaddr;
	struct hostent *host;
	struct timeval start;
	struct timeval end;



	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if(argc!=3){
		printf("Error: %s port buff_size\n", argv[0]);
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
	int bsize=atoi(argv[2]);
	char buff[bsize];

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
			ntot=0;
			host=gethostbyaddr( (char *) &cliaddr.sin_addr, sizeof(cliaddr.sin_addr), AF_INET);
			if (host == NULL){
				printf("client host information not found\n"); continue;
			}
			else printf("Server (figlio): host client e' %s \n", host->h_name);
			sleep(5);
			gettimeofday(&start, NULL);
			while((nread=read(conn_sd, buff, bsize))>0){
				ntot+=nread;
			}
			gettimeofday(&end, NULL);
			printf("Tempo di trasferimento: %ld secondi e %ld millisecondi\n",(end.tv_sec-start.tv_sec),(end.tv_usec-start.tv_usec)/1000);
			printf("Dimensione del buffer: %d byte\n",bsize);
			printf("Dimensione del file: %d byte\n",ntot);

			printf("Server (figlio:%s): termino\n",host->h_name);
			exit(1);
		}

	}
	close(conn_sd);  // padre chiude socket di connessione non di scolto
} // ciclo for infinito

