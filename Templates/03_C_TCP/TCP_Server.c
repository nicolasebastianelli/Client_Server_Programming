/**
 * Nicola	Sebastianelli
 *
 * 0000722894
 *
 * TCP_Server.c
 */

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

#define DIM_BUFF 256

/* ----------- Dichiarazione eventuali funzioni ----------- */
void gestore(int signo) {
	int stato;
	printf("Esecuzione gestore di SIGCHLD\n");
	wait(&stato);
	printf("Stato figlio: %d\n" , stato>>8);
}

int main(int argc, char **argv) {
	//Variabili per la comunicazione
	struct sockaddr_in cliaddr, servaddr;
	struct hostent *host;
	const int on = 1;
	int  listen_sd, conn_sd;
	int port, len, nread, nwrite;
	int defaultPort = 54321;
	char buff[DIM_BUFF];

	//Variabili per la logica del programma
	char c,temp;
    int readRes,i,k,size;

	// Controllo del numero degli argomenti
	if(argc == 1)	{
		port = defaultPort;
	} else if(argc == 2) {
		port = atoi(argv[1]);
		if (port < 1024 || port > 65535)  {
			printf("Porta fuori range...");
			port = defaultPort;
		}
	}
	printf("TCP_Server: Porta usata: %d\n", port);

	// Inizializzazione indirizzo Server
	memset ((char *)&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = INADDR_ANY;
	servaddr.sin_port = htons(port);


	// Creazione Socket
	listen_sd=socket(AF_INET, SOCK_STREAM, 0);
	if(listen_sd <0)  {
		perror("Creazione socket ");
		exit(1);
	}
	printf("TCP_Server: creata la socket, fd=%d\n", listen_sd);

	// Opzioni Socket
	if(setsockopt(listen_sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on))<0)  {
		perror("Set opzioni socket d'ascolto");
		exit(1);
	}
	printf("TCP_Server: set opzioni socket d'ascolto ok\n");

	// Bind Socket
	if(bind(listen_sd,(struct sockaddr *) &servaddr, sizeof(servaddr))<0) {
		perror("bind socket d'ascolto");
		exit(1);
	}
	printf("TCP_Server: bind socket d'ascolto ok\n");

	// Listen
	if (listen(listen_sd, 5)<0) {
		perror("listen");
		exit(1);
	}
	printf("TCP_Server: listen ok\n");

	signal(SIGCHLD, gestore); // Aggancio gestore per evitare figli zombie

	// Ciclo di ricezione Richieste
	printf("TCP_Server: Avviato!\n\n");
	for(;;) {
		len=sizeof(cliaddr);
		if((conn_sd=accept(listen_sd,(struct sockaddr *)&cliaddr,&len))<0)  {
			if (errno==EINTR) {
				perror("Forzo la continuazione della accept");
				continue;
			} else
				exit(1);
		}

		if (fork()==0)  {

			// Figlio
			close(listen_sd);
			host=gethostbyaddr((char *) &cliaddr.sin_addr, sizeof(cliaddr.sin_addr), AF_INET);
			if (host == NULL) {
				printf("TCP_ServerChild: informazioni su host del client non trovate\n");
				continue;
			} else
				printf("TCP_ServerChild %i: host client: %s\n", getpid(), host->h_name);

			//BODY SERVER

			for (;;)  {

				//TODO ELABORAZIONE RICHIESTA
				if((readRes=read(conn_sd, buff, sizeof(buff)))<0) {
					perror("Read");
					break;
				} else if(readRes==0) {  // Raggiunto EOF
					printf("TCP_ServerChild %i: ricevuto EOF\n",getpid());
					break;
				}
				printf("TCP_ServerChild %i: ricevuto: %s\n", getpid(),buff);
				
                
				printf("TCP_ServerChild %i: inviando: %s\n", getpid(),buff);
				if((i=write(conn_sd, buff, strlen(buff)+1))<0) {
					perror("write");
					break;
				}
			}

			// Libero risorse CHILD
			close(conn_sd);
			exit(0);
		}

		// Libero risorse PADRE
		close(conn_sd);
	}

	printf("\nServer: termino...\n");
	exit(0);
}
