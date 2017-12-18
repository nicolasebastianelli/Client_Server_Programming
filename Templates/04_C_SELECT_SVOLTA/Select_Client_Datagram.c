/**
 * Nicola	Sebastianelli
 *
 * 0000722894
 *
 * Select_Client_Datagram.c
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>
#include <dirent.h>
#include <fcntl.h>
#include <stdbool.h>

#define DIM_BUFF 256

typedef struct
{
	char id[DIM_BUFF];
	char numpers[DIM_BUFF];
}Aggiorna;

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

int main(int argc, char **argv) { // main client datagram
	struct hostent *host;
	struct sockaddr_in clientaddr, servaddr;
	int  sd, len, port;

	//Variabili per la logica del programma
	int num_file;
	char buff[DIM_BUFF];

	// Controllo del numero di argomenti
	if(argc!=3) {
		printf("Usage: %s serverAddress serverPort\n", argv[0]);
		exit(1);
	}

	// Controllo della porta inserita
	if(!checkPortVal(argv[2]))  {
		printf("Usage: %s serverAddress serverPort\n", argv[0]);
		perror("Secondo valore deve essere intero\n");
		exit(2);
	} else  {
		port = atoi(argv[2]);
	}

	// Inizializzazione indirizzo client
	memset((char *)&clientaddr, 0, sizeof(struct sockaddr_in));
	clientaddr.sin_family = AF_INET;
	clientaddr.sin_addr.s_addr = INADDR_ANY;
	clientaddr.sin_port = 0;

	// Inizializzazione indirizzo server
	memset((char *)&servaddr, 0, sizeof(struct sockaddr_in));
	servaddr.sin_family = AF_INET;
	host = gethostbyname(argv[1]);
	if (host == NULL) {
		printf("%s non trovato in /etc/hosts\n", argv[1]);
		exit(2);
	} else  {
		servaddr.sin_addr.s_addr = ((struct in_addr *) (host->h_addr))->s_addr;
		servaddr.sin_port = htons(port);
	}

	printf("Select_Client_Datagram: avviato!\n");


	// Creazione socket
	sd=socket(AF_INET, SOCK_DGRAM, 0);
	if(sd<0)  {
		perror("opening socket");
		exit(1);
	}
	printf("Select_Client_Datagram: creata la socket sd: %d\n", sd);

	// BIND della Socket a una porta scelta dal sistema
	if(bind(sd,(struct sockaddr *) &clientaddr, sizeof(clientaddr))<0)  {
		perror("bind socket");
		exit(1);
	}
	printf("Select_Client_Datagram: bind socket done, port: %i\n", clientaddr.sin_port);

	// BODY CLIENT UDP
	char cycleMessage[] ="\t----------\nImmettere ID , EOF per terminare: ";
	Aggiorna aggiorna;
	int result;
    printf("%s",cycleMessage);
	while (gets(buff))  {
		len=sizeof(servaddr);
		if(!strcmp(buff,"L")){
			printf("Select_Client_Datagram: Immissione non valida");
			printf("%s",cycleMessage);
			continue;
		}
		strcpy(aggiorna.id,buff);
		printf("Immettere numero di persone aggiornato: ");
		gets(buff);
		if(!checkPortVal(buff)){
			printf("Select_Client_Datagram: Immissione non valida, non è un numero\n");
			printf("%s",cycleMessage);
			continue;
		}
		strcpy(aggiorna.numpers,buff);
		//TODO INTERAZIONE CON UTENTE
		if(sendto(sd, &aggiorna, sizeof(aggiorna), 0, (struct sockaddr *)&servaddr, len)<0) {
			perror("sendto");
			continue;
		}
		printf("Select_Client_Datagram: Inviato %s ,%s\n",aggiorna.id,aggiorna.numpers);

		if (recvfrom(sd, &result, sizeof(int), 0, (struct sockaddr *)&servaddr, &len)<0) {
			perror("recvfrom");
			printf("\n--------\nFolder name: ");
			continue;
		}
		if(result<0)
			printf("Select_Client_Datagram: ID non presente\n");
		else
			printf("Select_Client_Datagram: Aggiornamento eseguito con successo");
		printf("%s",cycleMessage);

	}

	/* ----------- libero le risorse ----------- */
	close(sd);

	printf("\nClient Datagram terminate...\n");
	exit(0);
}
