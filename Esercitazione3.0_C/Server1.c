//Nicola Sebastianelli 0000722894 Esercitazione 3.0

#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>

#define LINE_LENGTH 256
/*Struttura di una richiesta*/
/********************************************************/
typedef struct{
	int caratteri;
	int parole;
	int linee;
}Request;
/********************************************************/

int main(int argc, char **argv){
	int sd, port, len, num1,fd_sorg,nread;
	const int on = 1;
	char ris[LINE_LENGTH],c,cpre=' ';
	struct sockaddr_in cliaddr, servaddr;
	struct hostent *clienthost;
	Request req;
	/* CONTROLLO ARGOMENTI ---------------------------------- */
	if(argc!=2){
		printf("Error: %s port\n", argv[0]);
		exit(1);
	}
	else{
		num1=0;
		while( argv[1][num1]!= '\0' ){
			if((argv[1][num1] < '0') || (argv[1][num1] > '9')){
				printf("Secondo argomento non intero\n");
				printf("Error: %s port\n", argv[0]);
				exit(2);
			}
			num1++;
		}
	  	port = atoi(argv[1]);
  		if (port < 1024 || port > 65535){
		      printf("Error: %s port\n", argv[0]);
		      printf("1024 <= port <= 65535\n");
		      exit(2);
  		}
	}

	/* INIZIALIZZAZIONE INDIRIZZO SERVER ---------------------------------- */
	memset ((char *)&servaddr, 0, sizeof(servaddr));
	servaddr.sin_family = AF_INET;
	servaddr.sin_addr.s_addr = INADDR_ANY;
	servaddr.sin_port = htons(port);

	/* CREAZIONE, SETAGGIO OPZIONI E CONNESSIONE SOCKET -------------------- */
	sd=socket(AF_INET, SOCK_DGRAM, 0);
	if(sd <0){perror("creazione socket "); exit(1);}
	printf("Server: creata la socket, sd=%d\n", sd);

	if(setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, &on, sizeof(on))<0)
	{perror("set opzioni socket "); exit(1);}
	printf("Server: set opzioni socket ok\n");

	if(bind(sd,(struct sockaddr *) &servaddr, sizeof(servaddr))<0)
	{perror("bind socket "); exit(1);}
	printf("Server: bind socket ok\n");
	/* CICLO DI RICEZIONE RICHIESTE ------------------------------------------ */
	for(;;){
		req.caratteri=0;
			req.linee=0;
			req.parole=0;
		len=sizeof(struct sockaddr_in);
				if (recvfrom(sd, &ris, sizeof(ris), 0, (struct sockaddr *)&cliaddr, &len)<0)
				{perror("Errore nella recvfrom"); continue;}
		/* trattiamo le conversioni possibili */

		printf("Conteggio sul file: %s\n",ris);
		if((fd_sorg=open(ris, O_RDONLY))<0){
					perror("File non esistente");
					req.caratteri=-1;
					req.linee=-1;
					req.parole=-1;
				}
		else{
			while((nread=read(fd_sorg, &c, sizeof(char)))>0){
					if(c=='\n'){
						req.linee++;
					}
					if((c==' '||c=='\t'||c=='.'||c==':'||c==';'||c==','||c=='\n')&&(cpre!=' '&&cpre!='\n'&&cpre!='\t'&&cpre!='.'&&cpre!=':'&&cpre!=';'&&cpre!=','))
						req.parole++;
					cpre=c;
					req.caratteri++;
			}
			req.caratteri--;
		}
		printf("Esito della richiesta, caratteri: %i, linee: %i, parole: %i\n", req.caratteri,req.linee,req.parole);
		clienthost=gethostbyaddr( (char *) &cliaddr.sin_addr, sizeof(cliaddr.sin_addr), AF_INET);
		if (clienthost == NULL) printf("client host information not found\n");
		else printf("Operazione richiesta da: %s %i\n", clienthost->h_name,(unsigned)ntohs(cliaddr.sin_port));
		req.caratteri=htonl(req.caratteri);
		req.linee=htonl(req.linee);
		req.parole=htonl(req.parole);
		if (sendto(sd, &req, sizeof(Request), 0, (struct sockaddr *)&cliaddr, len)<0)
				{perror("Errore nella sendto ");
				continue;}

	} //for
}
