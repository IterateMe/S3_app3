package headers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

public class TransportLayer implements headerInterface{
    String file;
    String content = "";
    int headerLength = 7;
    int errorCount = 0;
    public boolean transactionCompleted = false;
    Vector<String> payloads = new Vector<>();

    public TransportLayer(String file){
        this.file = file;
    }
    public void printPayloads(){
        for(int i=0; i< payloads.size(); i++){
            System.out.println(payloads.get(i));
        }
    }

    public Vector<String> getPayloads(){
        return payloads;
    }

    @Override
    public String writeHeader() {
        // definir le premier layload comme etant la nom du fichier
        payloads.add(getFileName());
        //Tronquer le String en payloads de 200 octets
        chunk();
        addHeader();
        return null;
    }

    @Override
    public String readHeader(String packet) {
        //Calculer la taille du fichier
        //Premier packet sera le nom du fichier a ecrire dans le repertoire local du serveur
        //Declarer la connexion perdu si plus de 3 erreurs
        //Reinitialiser la couche transport
        //Transporter une erreur TransmissionErrorException

        String payload = packet.substring(7);
        payloads.add(payload);
        String header = packet.substring(0,7);
        Integer numPacket = Integer.valueOf(header.substring(0,3));
        Integer totPacket = Integer.valueOf(header.substring(4));
        Integer payloadSyze = payloads.size();

        if(numPacket != payloadSyze){
            errorCount +=1;
            /////////////////////////////////////////////////////
            //RESEND
            if(errorCount == 3){
                //////////////////////////////////////////////////////
                //throw TransmissionErrorException;
            }
            // This loop puts empty strings in the vector where the missing data is
            for(int i=0; i< numPacket-payloadSyze; i++){
                payloads.insertElementAt("", payloadSyze);
            }
        }
        if(numPacket == totPacket && errorCount == 0){
            for(int i=1; i< payloads.size(); i++){
                content += payloads.get(i);
            }
            transactionCompleted = true;
        };
        return null;
    }

    public String setFixedLengthString(int size, String origin){
        while(origin.length() != size){
            origin = "0" + origin;
        }
        return origin;
    }

    public String getFileName(){
        String result = "";
        Path path = Paths.get(file);
        Path fileName = path.getFileName();
        result += fileName.toString();
        return result;
    }
    //////////////////////////////////////////////////////////////////////////
    // Methods for the writeHeader() method
    //////////////////////////////////////////////////////////////////////////
    public void getContentFromAppliLayer(String content){
        this.content = content;
    }

    public void chunk(){
        int length = content.length();
        int lastPayload;
        if(length%200 > 0) {
            lastPayload = 1;
        }else{
            lastPayload = 0;
        }
        int numPayloads = length/200;

        for(int i=0; i<numPayloads; i++){
            String payload = "";
            for(int j=0; j<200; j++){
                payload += content.charAt((i*200)+j);
            }
            payloads.add(payload);
            //System.out.println(payload);
        }
        if(lastPayload == 1){
            String payload = "";
            for(int j=0; j<length%200; j++){
                payload += content.charAt((numPayloads*200)+j);
            }
            payloads.add(payload);
            //System.out.println(payload);
        }
    }

    public void addHeader(){
        Integer payloadLength = payloads.size();
        String payloadSize = setFixedLengthString(3, payloadLength.toString());
        for(Integer i=0; i<payloadLength; i++){
            String position = setFixedLengthString(3, Integer.toString(i+1));
            String header = position + "S" + payloadSize;
            payloads.set(i, header + payloads.get(i));
        }
    }

    public void receiveAcknowledgement(String ack){

    }
    ////////////////////////////////////////////////////////////
    //Methods for the readHeader() method
    ////////////////////////////////////////////////////////////
    public String createAcknowledgement(String pac){
        String ackNumber = pac.substring(0 , 6);
        String acknowledgementHeader = ackNumber + "|ACK";
        return acknowledgementHeader;
    }

    public void findFileToSave(){

    }

    public String getContentForAppliLayer(){
        return content;
    }
    ////////////////////////////////////////////////////////////////////////////
    // PAS DE FOOTER DANS LA COUCHE TRANSPORT DE CETTE VERSION DU PROTOCOLE
    @Override
    public String writeFooter() {
        return null;
    }

    @Override
    public String readFooter() {
        return null;
    }
}