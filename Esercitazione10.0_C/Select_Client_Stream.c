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
	char buff[DIM_BUFF], buff2[DIM_BUFF];

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
	int i = 0;
	int j,fd,ok,size,btrasf,bread;
	char nomefile[64],c;
	printf("\t----------\nInserire nome della categoria, EOF per terminare: ");
	while (gets(buff)) {
		ok=1;
		//TODO INTERAZIONE CON UTENTE
		printf("Inserire numero di voti: ");
		gets(buff2);
		while( buff2[i]!= '\0' )  {
			if((buff2[i] < '0') || (buff2[i] > '9'))  {
				printf ("Stringa non composta da numeri");
				ok=-1;
				break;
			}
			i++;
		}
		if(ok<0) continue;
		i=atoi(buff2);
		size=strlen(buff)+1;
		if ((write(sd, &size, sizeof(int)))<0)  {
			perror("write");
			break;
		}
		if ((write(sd, buff, (strlen(buff)+1)))<0)  {
			perror("write");
			break;
		}
		printf("Select_Client_Stream: Invio %s\n", buff);
		if ((write(sd, &i,sizeof(int)))<0)  {
			perror("write");
			break;
		}
		printf("Select_Client_Stream: Invio %d\n", i);
		if((nread=read(sd, &i, sizeof(int)))<0) {
			perror("read");
			break;
		}
		if(i==0){
			printf("Non ci sono file per questi parametri\n");
			printf("\t----------\nInserire nome della categoria, EOF per terminare: ");
			continue;
		}
		for(j=0;j<i;j++)
		{
			if((read(sd, &size, sizeof(int)))<0) {
				perror("read");
				ok=-1;
				break;
			}
			if((read(sd, buff, size))<0) {
				perror("read");
				ok=-1;
				break;
			}
			strcpy(nomefile,buff);
			printf("Select_Client_Stream: Ricevuto nome file:%s\n", nomefile);
			fd=open(nomefile,O_WRONLY|O_CREAT|O_TRUNC, 0666);
			if(fd<0)
			{
				ok=-1;
				break;
			}
			if((read(sd, &size, sizeof(int)))<0) {
				perror("read");
				ok=-1;
				break;
			}
			printf("Select_Client_Stream: Ricevuto dimensione file: %d\n", size);
			btrasf=0;
			while(btrasf!=size)
			{
				if((nread=(read(sd, &bread,sizeof(int) )))<0) {
					perror("read");
					ok=-1;
					break;
				}
				if((nread=(read(sd, buff,bread)))<0) {
					perror("read");
					ok=-1;
					break;
				}
				printf("%d",nread);
				if ((write(fd, buff, nread))<0)  {
					perror("write");
					ok=-1;
					break;
				}
				btrasf=btrasf+nread;
			}
			close(fd);
			if(ok<0) break;
			printf("Select_Client_Stream: Fine trasferimento file :%s\n", nomefile);
		}
		if(ok<0) break;
		printf("\t----------\nInserire nome della categoria, EOF per terminare: ");
	}


	/* ----------- Libero risorse ----------- */
	close(sd);
	printf("\nSelect_Client_Stream: termino...\n");
	exit(0);

}

