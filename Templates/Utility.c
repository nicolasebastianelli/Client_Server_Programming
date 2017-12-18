// Definizione Struttura
typedef struct
{
    char id[DIM_ID];
    char tipoPala[DIM_TIPO];
    int numero;
    char file[DIM_NOME];
}Struttura;


// Stampa Struttura
printf("\nLotti: \n");
for(i=0; i<DIM_TAB; i++)
{
    printf("%s\t%s\t%d\t%s\n", tab[i].id, tab[i].tipoPala, tab[i].numero, tab[i].file);
}


// Apertura file || O_TRUNC (sovrascrittura) O_CREATE (se il file non esiste lo crea) O_APPEND(scrive da EOF)
fd=open(nomefile,O_WRONLY|O_CREAT|O_TRUNC, 0666);
if(fd<0)
{
    ok=-1;
    perror("open");
    break;
}
close(fd);


// scrittura su file descriptor
ok=write(connfd, &count, sizeof(int));
if(ok<0) {perror("write"); break;}


// Lettura e scrittura file su socket
while((ok=read(fd, buff, sizeof(buff)))>0)
{
    ok=write(connfd, buff, ok);
    if(ok<0) {perror("write"); break;}
}


//ricavare dimensione file e riposizionarci a inizio file
dim=lseek(fd, 0, SEEK_END);
lseek(fd, 0, 0);


//Lista dei file in una directory
#include <dirent.h>

DIR *dir;
struct dirent *ent;
if ((dir = opendir (nomedir)) != NULL) {
    /* print all the files and directories within directory */
    while ((ent = readdir (dir)) != NULL) {
        printf ("%s\n", ent->d_name);
    }
    closedir (dir);
} else {
    perror ("Directory non presente");
    return EXIT_FAILURE;
}

//Verifica se un path è una directory
struct stat s;
if( stat(path,&s) == 0 )
{
    if( s.st_mode & S_IFDIR )
    {
        //it's a directory
    }
    else if( s.st_mode & S_IFREG )
    {
        //it's a file
    }
    else
    {
        //something else
    }
}
else
{
    //error
}

//Lista dei file in una directory e subdirectory
void listdir(const char *name, int level)
{
    DIR *dir;
    struct dirent *entry;
    
    if (!(dir = opendir(name)))
        return;
    if (!(entry = readdir(dir)))
        return;
    
    do {
        if (entry->d_type == DT_DIR) { //dir
            char path[1024];
            int len = snprintf(path, sizeof(path)-1, "%s/%s", name, entry->d_name);
            path[len] = 0;
            if (strcmp(entry->d_name, ".") == 0 || strcmp(entry->d_name, "..") == 0)
                continue;
            printf("%*s[%s]\n", level*2, "", entry->d_name);
            listdir(path, level + 1);
        }
        else
            printf("%*s- %s\n", level*2, "", entry->d_name); //file
    } while (entry = readdir(dir));
    closedir(dir);
}
listdir(nomedir, 0);//usage

//Cerca Parola in una line-file
void FindWord(char *word , char *file){
    char *line = NULL;
    size_t n = 0;
    FILE *f = fopen(file, "r") ;
    while (getline(&line_buffer, &n, f) != -1)
    {
        if (strstr(line , word )!= NULL)
        {
            printf("%s",line);
        }
    }
    fclose(f);
    free(line);
}

//Cambiare carattere da upcase a lowcase e vice
char c;
if(c>='a'&&c<='z')//toUpCase
    c=c-32;
if(c>='A'&&c<='Z')//toLowCase
    c=c+32;


//Controllo che una stringa contenga solo interi
int checkVal(char str[]){
    int i = 0;
    while( str[i]!= '\0' )  {
        if((str[i] < '0') || (str[i] > '9'))  {
            return -1;
        }
        i++;
    }
    return 1;
}

//Conversione intero a stringa
int aInt = 368;
char str[15];
sprintf(str, "%d", aInt);

//RPC: funzioni con passaggio stringhe
char str[256];
int *result;
gets(str);
char *prt = &(str[0]);
result = funzione_1(&prt, cl);

//Assunzione ora e minuti
printf("Immettere ora (0-23): ");
gets(buff);
if(!checkVal(buff))
{
    printf("Select_Client_Datagram: Immissione non numerica");
    printf("%s",cycleMessage);
    continue;
}
table.ora=atoi(buff);
if(table.ora>23){
    printf("Select_Client_Datagram: Immissione non valida");
    printf("%s",cycleMessage);
    continue;
}
printf("Immettere minuti (0-59): ");
gets(buff);
if(!checkVal(buff))
{
    printf("Select_Client_Datagram: Immissione non numerica");
    printf("%s",cycleMessage);
    continue;
}
table.minuti=atoi(buff);
if(table.minuti>59){
    printf("Select_Client_Datagram: Immissione non valida");
    printf("%s",cycleMessage);
    continue;
}

//Assunzione Data: giorno mese anno
printf("Immettere giorno (1-30): ");
gets(buff);
if(!checkVal(buff))
{
    printf("Select_Client_Datagram: Immissione non numerica");
    printf("%s",cycleMessage);
    continue;
}
table.giorno=atoi(buff);
if(table.giorno>30||table.giorno<1){
    printf("Select_Client_Datagram: Immissione non valida");
    printf("%s",cycleMessage);
    continue;
}
printf("Immettere mese (1-12): ");
gets(buff);
if(!checkVal(buff))
{
    printf("Select_Client_Datagram: Immissione non numerica");
    printf("%s",cycleMessage);
    continue;
}
table.mese=atoi(buff);
if(table.mese>12||table.mese<1){
    printf("Select_Client_Datagram: Immissione non valida");
    printf("%s",cycleMessage);
    continue;
}
printf("Immettere anno: ");
gets(buff);
if(!checkVal(buff))
{
    printf("Select_Client_Datagram: Immissione non numerica");
    printf("%s",cycleMessage);
    continue;
}
table.anno=atoi(buff);








