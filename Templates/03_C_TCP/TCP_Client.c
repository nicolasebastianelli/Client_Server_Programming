/**
 * Nicola	Sebastianelli
 *
 * 0000722894
 *
 * TCP_Client.c
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

/* ----------- Dichiarazione eventuali funzioni ----------- */
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

int main(int argc, char *argv[])  {
	//Variabili per la comunicazione
	struct hostent *host;
	struct sockaddr_in servaddr;
	int port, sd;
	char buff[DIM_BUFF];
	int nwrite,nread;

	//Variabili per la logica del programma

	// Controllo del numero degli argomenti
	if(argc!=3) {
		printf("Usage: %s serverAddress serverPort\n", argv[0]);
		exit(1);
	}

	// Controllo della porta inserita
	if(!checkPortVal(argv[2]))  {
		printf("Secondo argomento non intero\n");
		printf("Usage: %s serverAddress serverPort\n", argv[0]);
		exit(2);
	} else  {
		port = atoi(argv[2]);
	}

	// Inizializzazione indirizzo Server
	memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
	servaddr.sin_family = AF_INET;
	host = gethostbyname(argv[1]);
	if(host == NULL)  {
		printf("TCP_Client: %s non trovato in /etc/hosts\n", argv[1]);
		exit(1);
	}
	servaddr.sin_addr.s_addr=((struct in_addr*) (host->h_addr))->s_addr;
	servaddr.sin_port = htons(atoi(argv[2]));

	// Creazione Socket Stream
	sd = socket(AF_INET, SOCK_STREAM, 0);
	if(sd<0)  {
		perror("Apertura socket");
		exit(1);
	}
	printf("TCP_Client: creata la socket sd: %d\n", sd);

	// BIND della Socket implicita nella connect
	if(connect(sd,(struct sockaddr *) &servaddr, sizeof(struct sockaddr))<0)  {
		perror("connect");
		exit(1);
	}
	printf("TCP_Client: connect ok\n");

	//BODY CLIENT
	printf("\t----------\nImmettere input , EOF per terminare: ");
	while (gets(buff)) {

		//TODO INTERAZIONE CON UTENTE
		nwrite =write(sd, buff, strlen(buff)+1);
		printf("TCP_Client: %s inviato\n",buff);
		nread = read(sd, buff, strlen(buff)+1);
		printf("TCP_Client: %s ricevuto\n",buff);
		printf("\t----------\nImmettere input , EOF per terminare: ");
	}
	shutdown(sd,1); // Chiusura socket in spedizione -> invio dell'EOF
	shutdown(sd, 0); // Chiusura socket in ricezione

	// Libero risorse
	close(sd);
	printf("\nTCP_Client: termino...\n");
	exit(0);
}

