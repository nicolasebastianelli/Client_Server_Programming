//Nicola Sebastianelli 0000722894 esercitazione 0#include <stdio.h>
#include <stdio.h>
#include <fcntl.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define MAX_STRING_LENGTH 256

int main(int argc, char* argv[]){

	char *file_in, read_char, buf[MAX_STRING_LENGTH];
    int index,nread, fd,nline=1,i=strlen(argv[argc-1]),j=0,ok=1;
	if(argc==3){
		file_in = argv[1];
		fd = open(file_in, O_RDONLY);
	}
	else if(argc==2)
		fd = STDIN_FILENO;
	while(read(fd, &read_char, sizeof(char))) /* Fino ad EOF*/{
		if (nread < 0){
			sprintf(buf,"(PID %d) impossibile leggere dal file %s", getpid(), file_in);
			perror(buf);
			exit(EXIT_FAILURE);
		}
		else{
			if(j<i)
				if(read_char!=argv[argc-1][j])
				{
					ok=0;
				}
			if(j==i-1 && ok ==1){
				write(1,argv[argc-1],i);
			}
			if(j>=i && ok==1){
				write(1,&read_char,sizeof(char));
			}
			j++;
			if(read_char=='\n')
			{
				j=0;
				ok=1;
			}
		}
	}
	close(fd);
}
