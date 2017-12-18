/**
 * Nicola	Sebastianelli
 *
 * 0000722894
 *
 * Select_Server.c
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
#include <stdbool.h>

#define DIM_BUFF 256
#define max(a,b) ((a) > (b) ? (a) : (b))


// Dichiarazione eventuali funzioni

// Dichiarazione eventuali funzioni

void gestore(int signo) {
	int stato;
	printf("Lanciato SIGCHLD handler\n");
	wait(&stato);
}

// MAIN

int main(int argc, char **argv) {
	struct sockaddr_in cliaddr, servaddr;
	int  listenfd, connfd, udpfd, fd_file, nready, maxfdp1;
	int defaultPort = 54321;
	const int on = 1;
	fd_set rset; //maschera
	int len, nread, nwrite, port,size;

	//Variabili per la logica del programma
	char zero=0, buff[DIM_BUFF];
	int readRes,i,k;
	char temp;

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
	printf("Porta usata: %d\n", port);

	//Creazione socket TCP
	listenfd = socket(AF_INET, SOCK_STREAM, 0);
	if (listenfd <0)  {
		perror("Apertura socket TCP");
		exit(1);
	}
	printf("Select_Server: creato stream listen socket correttamente, fd: %d\n", listenfd);

	//Inizializzazione indirizzo server e bind
	memset ((char *)&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = INADDR_ANY;
	servaddr.sin_port = htons(port);

	if(setsockopt(listenfd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on))<0) {
		perror("set opzioni socket TCP");
		exit(2);
	}
	printf("Select_Server: Opzioni TCP socket settata correttamente\n");

	if (bind(listenfd,(struct sockaddr *) &servaddr, sizeof(servaddr))<0) {
		perror("bind socket TCP");
		exit(3);
	}
	printf("Select_Server: bind TCP fatto correttamente\n");

	if (listen(listenfd, 5)<0)  {
		perror("listen");
		exit(4);
	}
	printf("Select_Server: listen TCP settata correttamente\n");


	//Creazione socket UDP
	udpfd=socket(AF_INET, SOCK_DGRAM, 0);
	if(udpfd <0)  {
		perror("opening UDP socket");
		exit(5);
	}
	printf("Select_Server: socket UDP creata correttamente, fd=%d\n", udpfd);

	//Inizializzazione indirizzo server e bind
	memset ((char *)&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = INADDR_ANY;
	servaddr.sin_port = htons(port);

	if(setsockopt(udpfd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on))<0)  {
		perror("set opzioni socket UDP");
		exit(6);
	}
	printf("Select_Server: opzioni UDP impostate correttamente\n");

	if(bind(udpfd,(struct sockaddr *) &servaddr, sizeof(servaddr))<0) {
		perror("bind socket UDP");
		exit(7);
	}
	printf("Select_Server: bind UDP fatto correttamente\n");

	//Aggancio gestore per evitare figli zombie
	signal(SIGCHLD, gestore);
	printf("Server_Server: SIGCHILD handler agganciato\n");

	//Pulizia e settaggio maschera dei file descriptor
	FD_ZERO(&rset);
	maxfdp1=max(listenfd, udpfd)+1;
	printf("Server_Server: filedescriptor mask settata\n");
	printf("Select_Server: Avviato!\n\n");

	//Ciclo ricezione eventi della select
	for(;;) {
		FD_SET(listenfd, &rset);
		FD_SET(udpfd, &rset);

		if ((nready=select(maxfdp1, &rset, NULL, NULL, NULL))<0)  {
			if (errno==EINTR)
				continue;
			else {
				perror("select");
				exit(8);
			}
		}


		// GESTIONE RICHIESTE TCP
		if (FD_ISSET(listenfd, &rset))  {
			printf("Select_Server: Ricevuta richiesta TCP.\n");

			len = sizeof(struct sockaddr_in);
			if((connfd = accept(listenfd,(struct sockaddr *)&cliaddr,&len))<0)  {
				if (errno==EINTR)
					continue;
				else  {
					perror("accept");
					exit(9);
				}
			}

			// Processo figlio che serve la richiesta di operazione
			if (fork()==0)  {
				close(listenfd);
				printf("Select_Server: CHILD - pid=%i\n\n", getpid());

				//BODY SERVER TCP
				for (;;)  {

					// TODO ELABORAZIONE RICHIESTA
					if((nread=read(connfd, buff, sizeof(buff)))<0) {
						perror("read");
						break;
					} else if(nread==0) {
						printf("Ricevuto EOF\n");
						break;
					}
					printf("Select_ServerChild %i: - ricevuto: %s\n", getpid(),buff);
					
					printf("Select_ServerChild %i: - inviando: %s\n\n", getpid(),buff);
					if((nwrite=write(connfd, buff, strlen(buff)+1))<0) {
						perror("write");
						break;
					}
				}
				printf("Select_ServerChild %i: chiudo connessione e termino\n", getpid());
				// Libero risorse CHILD
				close(connfd);
				exit(0);
			}
			// Libero risorse PADRE
			close(connfd);
		}


		// GESTIONE RICHIESTE UDP
		if (FD_ISSET(udpfd, &rset)) {
			printf("Select_Server - Ricevuta richiesta UDP.\n");
			len=sizeof(struct sockaddr_in);

			// TODO ELABORAZIONE RICHIESTA
			if ((nread=recvfrom(udpfd, buff, sizeof(buff), 0, (struct sockaddr *)&cliaddr, &len))<0) {
				perror("recvfrom");
				continue;
			}
			printf("Select_Server - ricevuto: %s\n", buff);

			if ((nwrite=sendto(udpfd, buff, strlen(buff)+1, 0, (struct sockaddr *)&cliaddr, len))<0)  {
				perror("sendto");
				continue;
			}
			printf("Select_Server - inviato: %s\n\n", buff);
		}
	}

	printf("Select_Server - terminate...");
	exit(1);

}

