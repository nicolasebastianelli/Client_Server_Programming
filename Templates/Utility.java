



//Conta numero di caratteri parole e linee contenute in un file
int[] contaCharWordFile(String nomeFile){
    int []result = new int[3];
    result[0]=0;
    result[1]=0;
    result[2]=0;
    String buff;
    BufferedReader in = null;
    try {
        in = new BufferedReader(new FileReader(nomeFile));
        System.out.println("Server: File aperto: " + nomeFile);
        while((buff=in.readLine()) != null){
            result[0]+=buff.length()+1;
            result[1]+=buff.split(" ").length;
            result[2]+=1;
        }
        in.close();
    } catch (Exception e) {
            result[0]=-1;
            result[1]=-1;
            result[2]=-1;
        }
    return result;
}


//Controllo esistenza di un file
String fileExist(String nomeFile){
String result;
    File curFile = new File(nomeFile);
    if (curFile.exists()) {
        result = "S";
    } else result = "N";
    return result;
}

//Lista dei file in una directory
File[] listDir(String nomeDir){
    File folder = new File(nomeDir);
    File[] listOfFiles =null;
    if(folder.isDirectory()){
        listOfFiles = folder.listFiles();
    }
}

//Cambiare caratteri di un file
public void replaceInFile(File file) throws IOException {

File tempFile = File.createTempFile("buffer", ".tmp");
FileWriter fw = new FileWriter(tempFile);

Reader fr = new FileReader(file);
BufferedReader br = new BufferedReader(fr);

while(br.ready()) {
fw.write(br.readLine().replaceAll("a", "o") + "\n"); // cambio caratteri
}
fw.close();
br.close();
fr.close();
tempFile.renameTo(file); //replace original file
}

//Trasferimento file binario su Stream, usare sia su client che server
//USAGE example: trasferisci_a_byte_file_binario(new DataInputStream(new FileInputStream(nomeFile)),outSock,File.length);
static void trasferisci_N_byte_file_binario(DataInputStream src, DataOutputStream dest, long daTrasferire) throws IOException
{
    int cont=0;
    int buffer=0;
    try
    {
        while (cont < daTrasferire)
        {
            buffer=src.read();
            dest.write(buffer);
            cont++;
        }
        dest.flush();
        System.out.println("Byte trasferiti: " + cont);
    }
    catch (EOFException e)
    {
        System.out.println("Problemi, i seguenti: ");
        e.printStackTrace();
    }
}

//scrittura da socket a file
File a = new File(nomeFile);
FileOutputStream fout = new FileOutputStream(a);
DataOutputStream dout = new DataOutputStream(fout);
long l = inSock.readLong();
FileUtility.trasferisci_N_byte_file_binario(inSock, dout, l);
dout.close();
fout.close();


//scrittura da file a socket
File a = new File(audio);
FileInputStream fin = new FileInputStream(a);
DataInputStream din = new DataInputStream(fin);
FileUtility.trasferisci_N_byte_file_binario(din, outSock, a.length());
din.close();
fin.close();

//Conversione da intero a stringa
String.valueOf(intero)

//Assunzione ore minuti
System.out.print("Inserire ora (0-23): ");
buff=stdIn.readLine();
try{ora=Integer.parseInt(buff);}
catch(Exception e){
System.out.println("Inserimento non numerico");
System.out.print(cycleMessage);
continue;
}
if(ora<0||ora>23)
{
System.out.println("Inserimento non compreso tra 0 e 23");
System.out.print(cycleMessage);
continue;
}
System.out.print("Inserire minuti (0-59): ");
buff=stdIn.readLine();
try{min=Integer.parseInt(buff);}
catch(Exception e){
System.out.println("Inserimento non numerico");
System.out.print(cycleMessage);
continue;
}
if(min<0||min>59)
{
System.out.println("Inserimento non compreso tra 0 e 59");
System.out.print(cycleMessage);
continue;
}
oramin=ora+":"+min;

//Assunzione Data(giorno,mese,anno);
System.out.print("Inserire giorno (1-30): ");
buff=stdIn.readLine();
try{giorno=Integer.parseInt(buff);}
catch(Exception e){
System.out.println("Inserimento non numerico");
System.out.print(cycleMessage);
continue;
}
if(giorno<1||giorno>30)
{
System.out.println("Inserimento non compreso tra 1 e 30");
System.out.print(cycleMessage);
continue;
}
System.out.print("Inserire mese (1-12): ");
buff=stdIn.readLine();
try{mese=Integer.parseInt(buff);}
catch(Exception e){
System.out.println("Inserimento non numerico");
System.out.print(cycleMessage);
continue;
}
if(mese<1||mese>12)
{
System.out.println("Inserimento non compreso tra 1 e 12");
System.out.print(cycleMessage);
continue;
}
System.out.print("Inserire anno: ");
buff=stdIn.readLine();
try{anno=Integer.parseInt(buff);}
catch(Exception e){
System.out.println("Inserimento non numerico");
System.out.print(cycleMessage);
continue;
}

data=giorno+"/"+mese+"/"+anno;

//Gestione ore minuti
LocalTime time;
time=LocalTime.of(ora,min);
time.isBefore(LocalTime time2);

//Gestione date
Date dateIT;
SimpleDateFormat formatterIT = new SimpleDateFormat("dd/MM/yyyy");
dateIT = formatterIT.parse("05/07/2012");
date.after(DATE when)






