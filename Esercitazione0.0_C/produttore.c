#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>

#define MAX_STRING_LENGTH 256

int main(int argc, char* argv[]){
	int fd, bytes_to_write, written, righe, i, c;
	char *file_out, riga[MAX_STRING_LENGTH], buffer[MAX_STRING_LENGTH];
	
	file_out = argv[1];	
	printf("Quante righe vuoi inserire?\n");
    while(scanf("%i", &righe)!= 1){
		do{
			c=getchar(); printf("%c ", c);
		} while (c!= '\n');
		printf("Inserisci valore intero");
	} // si esce con intero
	gets(buffer);
	fd = open(file_out, O_WRONLY|O_CREAT|O_TRUNC, 00640);
    if (fd < 0){
        perror("P0: Impossibile creare/aprire il file");
        exit(EXIT_FAILURE);
    }
	
	for(i=0;i<righe;++i){
        printf("Inserisci la nuova riga\n");
		gets(riga); //gets lascia il fine stringa
		strcat(riga, "\n"); //il fine linea ricopre il fine stringa
		bytes_to_write = strlen(riga);
		if (write(fd, riga, bytes_to_write) < 0){
			perror("P0: errore nella scrittura sul file");
			exit(EXIT_FAILURE);
		}
    }	
	close(fd);
}