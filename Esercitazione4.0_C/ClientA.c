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

#define DIM_MAX_STRINGA_CLIENT 5

typedef struct
{ char stringa[20];
int intero;
char carattere;
} struttura1;

typedef struct
{ char* stringa;
int intero;
char carattere;
} struttura2;

int main(int argc, char *argv[])
{
	int sd, port, fd_sorg, fd_dest, nread, len;
	char buff[DIM_MAX_STRINGA_CLIENT],car,c;
	struct hostent *host;
	struct sockaddr_in servaddr;
	struttura1 str1;
	struttura2 str2;


	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if(argc!=3){
		printf("Error:%s serverAddress serverPort\n", argv[0]);
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

		/* CREAZIONE SOCKET ------------------------------------ */
		sd=socket(AF_INET, SOCK_STREAM, 0);
		if(sd<0) {perror("apertura socket"); exit(1);}
		printf("Client: creata la socket sd=%d\n", sd);

		/* Operazione di BIND implicita nella connect */
		if(connect(sd,(struct sockaddr *) &servaddr, sizeof(struct sockaddr))<0)
		{perror("connect"); exit(1);}
		printf("Client: connect ok\n");

		/*INVIO File*/
		str1.carattere='a';
		str1.intero=1;
		strcpy(buff,"prova1");
		strcpy(str1.stringa,buff);
		str2.carattere='b';
		str2.intero=2;
		strcpy(buff,"prova2");
		len=strlen(buff) +1;
		str2.stringa=(char*)malloc(sizeof(char)*len);
		strcpy(str2.stringa,buff);

		printf("Client: invio struttura1\n");
		write(sd,&str1,sizeof(str1));

		printf("Client: invio struttura2\n");
		write(sd,&str2.carattere,sizeof(char));
		write(sd,&str2.intero,sizeof(int));
		write(sd,&len,sizeof(int));
		write(sd,str2.stringa,len);
		free(str2.stringa);
		printf("Inserire stringa, EOF per terminare: \n");
		while (fgets(buff,DIM_MAX_STRINGA_CLIENT,stdin)){
			while(getchar() != '\n'){}
			buff[strcspn(buff,"\n")]= '\0';
			printf("Client: invio stringa %s\n",buff);
			write(sd,buff,sizeof(buff));
			printf("Inserire stringa, EOF per terminare: \n");
		}
	//while
	printf("\nClient: termino...\n");
	exit(0);
}



