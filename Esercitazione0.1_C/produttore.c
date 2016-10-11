//Nicola Sebastianelli 0000722894 esercitazione 0
#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#define MAX_STRING_LENGTH 256

int main(int argc, char* argv[]){
	int fd, bytes_to_write, written, i=0, c;
	char *file_out,car,buffer[MAX_STRING_LENGTH];
	file_out = argv[1];
	printf("Inserisci il testo\n");
	fd = open(file_out, O_WRONLY|O_CREAT|O_TRUNC, 00640);
    if (fd < 0){
        perror("P0: Impossibile creare/aprire il file");
        exit(EXIT_FAILURE);
    }
    while(scanf("%c", &car)!= EOF){
			if (write(fd, &car,sizeof(char)) < 0){
				perror("P0: errore nella scrittura sul file");
				exit(EXIT_FAILURE);
			}
    }	
	close(fd);
}
