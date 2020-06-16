package headers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class AppliLayer implements headerInterface{
    private String content = "";
    public String file;
    public boolean transmissionError = false;

    public AppliLayer(String file){
        this.file = file;
    }

    public void setFileName(String fileName){
        this.file = fileName;
    }

    public void readFile(){
        try {
            File myFile = new File(file);
            Scanner myReader = new Scanner(myFile);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                content += data + "\n";
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("The specified file couldn't be found");
            e.printStackTrace();
        }
        //System.out.println("CONTENT: \n" + content);
    }

    public void writeFile(){
        boolean alreadyexists = false;
        file = "received_"+file;
        try {
            File myObj = new File(file);
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                alreadyexists = true;
            }
        } catch (IOException e) {
            System.out.println("An error occurred while trying to write on file.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter(file, false);  // false means it will override the original content
            myWriter.write(content);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
            if(alreadyexists){System.out.println("The file already existed");}
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void getRemoteContent(String content){
        this.content = content;
    }

    public String getLocalContent() {
        return content;
    }

    //CES FONCTIONS NE SONT PAS UTILISEES A CE NIVEAU
    @Override
    public String writeHeader() {
        return null;
    }

    @Override
    public String writeFooter() {
        return null;
    }

    @Override
    public String readHeader(String packet) {
        return null;
    }

    @Override
    public String readFooter() {
        return null;
    }
}
