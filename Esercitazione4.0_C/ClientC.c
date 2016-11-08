//Nicola Sebastianelli 0000722894 Esercitazione 4

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <sys/time.h>


int main(int argc, char *argv[])
{
	int sd, port, fd_sorg, nread, len,T1,T2;
	char car,c,nomefile[256];
	struct hostent *host;
	struct sockaddr_in servaddr;
	struct timeval start;
	struct timeval end;

	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if(argc!=4){
		printf("Error:%s serverAddress serverPort buff_sizes\n", argv[0]);
		exit(1);
	}

	/* INIZIALIZZAZIONE INDIRIZZO SERVER -------------------------- */
	memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
	servaddr.sin_family = AF_INET;
	host = gethostbyname(argv[1]);

	/*VERIFICA INTERO*/
	nread=0;
	while( argv[2][nread]!= '\0' ){
		if( (argv[2][nread] < '0') || (argv[2][nread] > '9') ){
			printf("Secondo argomento non intero\n");
			exit(2);
		}
		nread++;
	}
	port = atoi(argv[2]);

	/* VERIFICA PORT e HOST */
	if (port < 1024 || port > 65535){
		printf("%s = porta scorretta...\n", argv[2]);
		exit(2);
	}
	if (host == NULL){
		printf("%s not found in /etc/hosts\n", argv[1]);
		exit(2);
	}else{
		servaddr.sin_addr.s_addr=((struct in_addr *)(host->h_addr))->s_addr;
		servaddr.sin_port = htons(port);
	}

	/* CORPO DEL CLIENT:
	ciclo di accettazione di richieste da utente ------- */
		int bsize=atoi(argv[3]);
		char buff[bsize];
		/* CREAZIONE SOCKET ------------------------------------ */
		sd=socket(AF_INET, SOCK_STREAM, 0);
		if(sd<0) {perror("apertura socket"); exit(1);}
		printf("Client: creata la socket sd=%d\n", sd);

		/* Operazione di BIND implicita nella connect */
		if(connect(sd,(struct sockaddr *) &servaddr, sizeof(struct sockaddr))<0)
		{perror("connect"); exit(1);}
		printf("Client: connect ok\n");
		printf("Inserire nome file, EOF per terminare: \n");
		while(gets(nomefile)){
			
			printf("File da aprire: __%s__\n", nomefile);

					
					if((fd_sorg=open(nomefile, O_RDONLY))<0){
						perror("open file sorgente"); 
						printf("Inserire nome file, EOF per terminare: \n");
						continue;
					}
					printf("Invio file: \n");
					gettimeofday(&start, NULL);
					while((nread=read(fd_sorg, buff, bsize))>0){
			//stampa
								write(sd,buff,nread);	//invio
							}
					// secondi e microsecondi
					gettimeofday(&end, NULL);
					printf("Tempo di trasferimento: %ld microsecondi\n",(end.tv_usec-start.tv_usec));
					printf("\nClient: termino...\n");
						exit(0);
		}
	//while
	printf("\nClient: termino...\n");
	exit(0);
}



