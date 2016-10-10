#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>

#define MAX_STRING_LENGTH 256

int main(int argc, char* argv[]){

	char *file_in, read_char, buf[MAX_STRING_LENGTH];
    int nread, fd;
	
	file_in = argv[1];
	
	fd = open(file_in, O_RDONLY); // controllare  if fd non errore
	while(read(fd, &read_char, sizeof(char))) /* Fino ad EOF*/{
		if (nread < 0){
			sprintf(buf,"(PID %d) impossibile leggere dal file %s", getpid(), file_in);
			perror(buf);
			exit(EXIT_FAILURE);
		}
		else{
			write(1,&read_char,1); //controllo out?
		}
	}
	close(fd);
}