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
#define SIZETAB 5
#define max(a,b) ((a) > (b) ? (a) : (b))

typedef struct
{
	char id[DIM_BUFF];
	char numpers[DIM_BUFF];
	char tipologia[DIM_BUFF];
	char veicolo[DIM_BUFF];
	char targa[DIM_BUFF];
	char file[DIM_BUFF];
}Struttura;

typedef struct
{
	char id[DIM_BUFF];
	char numpers[DIM_BUFF];
}Aggiorna;

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
	Struttura table[SIZETAB];

	//Variabili per la logica del programma
	char zero=0;
	int readRes,i;

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

	//inizzializzazione struttura
	for(i=0;i<SIZETAB;i++){
		table[i] = (Struttura) {"L","L","L","L","L","L"};
	}
	table[0] = (Struttura) {"HGFD89","3","mezza piazzola","niente","L","prova1.jpg"};
	table[2] = (Struttura) {"WERT26","5","mezza piazzola","camper","AA567AA","prova2.jpg"};
	table[4] = (Struttura) {"QLJC33","4","piazzola","auto","BB444BB","prova3.jpg"};

	printf("ID\tNumero Pers.\tTipologia\tVeicolo\tTarga\tFile\n");
	for(i=0;i<SIZETAB;i++){
		printf("%s\t%s\t%s\t%s\t%s\t%s\n",table[i].id,table[i].numpers,table[i].tipologia,table[i].veicolo,table[i].targa,table[i].file);
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
			char richiesta[DIM_BUFF], buff[DIM_BUFF];
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
				int ok,nfile,fd;
				printf("Select_Server: CHILD - pid=%i\n\n", getpid());
				//BODY SERVER TCP
				while((ok=read(connfd, &size, sizeof(int)))>0) {
					nfile=0;
					ok=1;
					if((read(connfd, richiesta, size))<0) {
						perror("read");
						break;
					}
					printf("Select_ServerChild %i: - ricevuto: %s\n", getpid(),richiesta);
					for(i=0;i<SIZETAB;i++)
					{
						if(!strcmp(table[i].tipologia,richiesta))//TODO condizione trasferimento file
						{
							nfile++;
						}
					}
					if((nwrite=write(connfd, &nfile, sizeof(int)))<0) {
						perror("write");
						break;
					}
					printf("Select_ServerChild %i: - inviando: %i file \n", getpid(),nfile);
					for(i=0;i<SIZETAB;i++)
					{
						if(!strcmp(table[i].tipologia,richiesta))//TODO condizione trasferimento file
						{
							printf("Select_ServerChild %i: inizio invio file: %s\n", getpid(),table[i].file);
							size=strlen(table[i].file)+1;
							if((write(connfd,&size, sizeof(int))<0)) {
								perror("write");
								ok=-1;
								break;
							}
							if((write(connfd,table[i].file , strlen(table[i].file)+1))<0) {
								perror("write");
								ok=-1;
								break;
							}
							fd=open(table[i].file,O_RDONLY);
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
							printf("Select_ServerChild %i: Invio dimensione file: %d\n",getpid(),size);
							while((nread=read(fd, buff, sizeof(buff)))>0)
							{
								if ((nwrite=write(connfd, &nread, sizeof(int)))<0)
								{perror("write"); ok=-1;break;}
								if ((nwrite=write(connfd, buff, nread))<0)
								{perror("write"); ok=-1;break;}
							}
							if(ok<0) {close(fd);break;}
							write(fd,&zero,sizeof(char));
							printf("Select_ServerChild %i: fine invio file: %s\n\n", getpid(),table[i].file);
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
			Aggiorna aggiorna;
			int result=-1;
			printf("Select_Server - Ricevuta richiesta UDP.\n");
			len=sizeof(struct sockaddr_in);

			// TODO ELABORAZIONE RICHIESTA
			if ((nread=recvfrom(udpfd, &aggiorna, sizeof(aggiorna), 0, (struct sockaddr *)&cliaddr, &len))<0) {
				perror("recvfrom");
				continue;
			}
			printf("Select_Server - ricevuto: %s, %s\n", aggiorna.id,aggiorna.numpers);
			for(i=0;i<SIZETAB;i++){
				if(!strcmp(table[i].id,aggiorna.id)){
					result=1;
					strcpy(table[i].numpers,aggiorna.numpers);
				}
			}

			if ((nwrite=sendto(udpfd, &result,sizeof(int), 0, (struct sockaddr *)&cliaddr, len))<0)  {
				perror("sendto");
				continue;
			}
			printf("Select_Server - inviato: %d\n\n", result);
			printf("ID\tNumero Pers.\tTipologia\tVeicolo\tTarga\tFile\n");
				for(i=0;i<SIZETAB;i++){
					printf("%s\t%s\t%s\t%s\t%s\t%s\n",table[i].id,table[i].numpers,table[i].tipologia,table[i].veicolo,table[i].targa,table[i].file);
				}
		}
	}

	printf("Select_Server - terminate...");
	exit(1);

}

