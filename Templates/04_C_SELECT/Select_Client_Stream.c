/**
 * Nicola	Sebastianelli
 *
 * 0000722894
 *
 * Select_Client_Stream.c
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <stdbool.h>  

#define DIM_BUFF 256

// Dichiarazione eventuali funzioni
bool checkPortVal(char *a)  {
	int aux = 0;
	while( a[aux]!= '\0' )  {
		if((a[aux] < '0') || (a[aux] > '9'))  {
			return false;
		}
		aux++;
	}

	return true;
}

int main(int argc, char *argv[])  { // main client stream
	int sd, nread,nwrite, port;
	struct hostent *host;
	struct sockaddr_in servaddr;

	// Variabili per la logica del programma
	char buff[DIM_BUFF];

	// Controllo del numero di argomenti
	if(argc!=3) {
		printf("Usage: %s serverAddress serverPort\n", argv[0]);
		exit(1);
	}

	// Controllo della porta inserita
	if(!checkPortVal(argv[2]))  {
		printf("Usage: %s serverAddress serverPort\n", argv[0]);
		perror("Secondo argomento deve essere intero\n");
		exit(2);
	} else  {
		port = atoi(argv[2]);
	}

	// Inizializzazione indirizzo Server
	memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
	servaddr.sin_family = AF_INET;
	host = gethostbyname(argv[1]);
	if(host == NULL)  {
		printf("Select_Client_Stream: %s non trovato in /etc/hosts\n", argv[1]);
		exit(1);
	}
	servaddr.sin_addr.s_addr=((struct in_addr*) (host->h_addr))->s_addr;
	servaddr.sin_port = htons(atoi(argv[2]));

	printf("Select_Client_Stream: Avviato!\n");

	// Creazione Socket
	sd = socket(AF_INET, SOCK_STREAM, 0);
	if(sd<0)  {
		perror("opening socket");
		exit(1);
	}
	printf("Select_Client_Stream: creata la socket sd: %d\n", sd);

	// BIND della Socket implicita nella connect
	if(connect(sd,(struct sockaddr *) &servaddr, sizeof(struct sockaddr))<0)  {
		perror("connect");
		exit(1);
	}
	printf("Select_Client_Stream: bind socket done\n");


	//BODY CLIENT TCP
	char cycleMessage[] ="\t----------\nImmettere input , EOF per terminare: ";
    printf("%s",cycleMessage);
	while (gets(buff)) {

		//TODO INTERAZIONE CON UTENTE
		if ((nwrite=write(sd, buff, (strlen(buff)+1)))<0)  {
			perror("write");
			break;
		}
		printf("Select_Client_Stream: Invio %s\n", buff);
		if((nread=read(sd, buff, sizeof(buff)))<0) {
			perror("read");
			break;
		}
		printf("Select_Client_Stream: Ricevuto %s\n", buff);
		printf("%s",cycleMessage);
	}


/* ----------- Libero risorse ----------- */
close(sd);
printf("\nSelect_Client_Stream: termino...\n");
exit(0);

}

