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
#define TABDIM 10
#define max(a,b) ((a) > (b) ? (a) : (b))

typedef struct
{
	char nome[64];
	char categoria[64];
	int voto;
	char file[64];
}Cantante;

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
	Cantante table[10];
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

	//Inizializzazione struttura
	for(i=0;i<TABDIM;i++){
		strcpy(table[i].nome,"L");
		strcpy(table[i].categoria,"L");
		table[i].voto=-1;
		strcpy(table[i].file,"L");
	}
	strcpy(table[1].nome,"Mandarino");
	strcpy(table[1].categoria,"Campioni");
	table[1].voto=1500;
	strcpy(table[1].file,"vicino.avi");
	strcpy(table[4].nome,"AmaraBianca");
	strcpy(table[4].categoria,"Campioni");
	table[4].voto=2000;
	strcpy(table[4].file,"immobilismo.avi");
	strcpy(table[5].nome,"Zucchero");
	strcpy(table[5].categoria,"NuoveProposte");
	table[5].voto=550;
	strcpy(table[5].file,"ascolto.avi");
	strcpy(table[9].nome,"Amari");
	strcpy(table[9].categoria,"NuoveProposte");
	table[9].voto=800;
	strcpy(table[9].file,"cosaE.avi");
	printf("Nome\tCategoria\tVoto\tFile\n");
	for(i=0;i<TABDIM;i++){
		printf("%s\t%s\t%d\t%s\n",table[i].nome,table[i].categoria,table[i].voto,table[i].file);
	}

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
				int nfile,ok=1,fd,i,size;
				char categoria[64];
				close(listenfd);
				printf("Select_Server: CHILD - pid=%i\n\n", getpid());
				//BODY SERVER TCP
				while((ok=read(connfd, &size, sizeof(int)))>0) {
					nfile=0;
					ok=1;
					if((read(connfd, buff, size))<0) {
						perror("read");
						break;
					}
					if((read(connfd, &i, sizeof(int)))<0) {
						perror("read");
						break;
					}
					strcpy(categoria,buff);
					printf("Select_ServerChild %i: - ricevuto: %s e %d\n", getpid(),categoria,i);
					for(k=0;k<TABDIM;k++)
					{
						if(!strcmp(table[k].categoria,categoria)&&table[k].voto>i)
						{
							nfile++;
						}
					}
					if((nwrite=write(connfd, &nfile, sizeof(int)))<0) {
						perror("write");
						break;
					}
					printf("Select_ServerChild %i: - inviando: %i file \n", getpid(),nfile);
					for(k=0;k<TABDIM;k++)
					{
						if(!strcmp(table[k].categoria,categoria)&&table[k].voto>i)
						{
							printf("Select_ServerChild %i: inizio invio file: %s\n", getpid(),table[k].file);
							size=strlen(table[k].file)+1;
							if((write(connfd,&size, sizeof(int))<0)) {
								perror("write");
								ok=-1;
								break;
							}
							if((write(connfd,table[k].file , strlen(table[k].file)+1))<0) {
								perror("write");
								ok=-1;
								break;
							}
							fd=open(table[k].file,O_RDONLY);
							if(fd<0)
							{
								perror("open");
								ok=-1;
								break;
							}
							size=lseek(fd,0,SEEK_END);
							lseek(fd,0,0);
							if((write(connfd,&size , sizeof(int)))<0) {
								perror("write");
								ok=-1;
								break;
							}
							printf("Invio dimensione file: %d\n",size);
							while((nread=read(fd, buff, sizeof(buff)))>0)
							{
								if ((nwrite=write(connfd, &nread, sizeof(int)))<0)
								{perror("write"); ok=-1;break;}
								if ((nwrite=write(connfd, buff, nread))<0)
								{perror("write"); ok=-1;break;}
							}
							if(ok<0) {close(fd);break;}
							write(fd,&zero,sizeof(char));
							printf("Select_ServerChild %i: fine invio file: %s\n\n", getpid(),table[k].file);
							close(fd);
						}
					}
					if(ok<0)
						break;
				}
				if(ok==0){printf("Ricevuto EOF!\n");}
				else perror("Problem: ");
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
			k=0;
			if ((nread=recvfrom(udpfd, buff, sizeof(buff), 0, (struct sockaddr *)&cliaddr, &len))<0) {
				perror("recvfrom");
				continue;
			}
			printf("Select_Server - ricevuto: %s\n", buff);

			for (i=0; i<TABDIM;i++){
				if(!strcmp(table[i].nome,buff)&&strcmp(table[i].nome,"L")!=0){
					table[i].voto++;
					k=1;
					break;
				}
			}
			if(k==1){
				k=table[i].voto;
			}
			else
				k=-1;
			if ((nwrite=sendto(udpfd, &k, sizeof(int), 0, (struct sockaddr *)&cliaddr, len))<0)  {
				perror("sendto");
				continue;
			}
			printf("Select_Server - inviato: %d\n\n", k);
		}
	}

	printf("Select_Server - terminate...");
	exit(1);

}

