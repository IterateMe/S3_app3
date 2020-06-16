import headers.AppliLayer;
import headers.DataLinkLayer;
import headers.TransportLayer;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static Vector<String> acknowledgementBuffer = new Vector<String>();

    public static void main(String[] args) throws IOException {

        DatagramSocket socket = new DatagramSocket();

        AppliLayer application =  new AppliLayer(args[0]);
        application.readFile();

        TransportLayer transport = new TransportLayer(args[0]);
        transport.getContentFromAppliLayer(application.getLocalContent());
        transport.writeHeader();

        DataLinkLayer dataLink = new DataLinkLayer();
        dataLink.getPayloadsFromTransport(transport.getPayloads());
        dataLink.writeHeader();


        setAckowledgementBuffer(  dataLink.getPayloads().size()  );     // Mettre la derniere couche en reference

        for(int i = 0; i<acknowledgementBuffer.size(); i++){
            //Send packet
            sendPacket(i,dataLink, args[1],socket);

            //Receive and treat acknowledgement
            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.setSoTimeout(1000);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            if(checkReception(received)){saveAckowledgement(received);}
            else{
                Integer index = Integer.valueOf(received.substring(0, 4));
                sendPacket(index,dataLink, args[1],socket);
            }

        }

        socket.close();
    }
    public static void setAckowledgementBuffer(Integer count){
        for(int i=0; i<count; i++){
            acknowledgementBuffer.add("");
        }
    }

    public static void sendPacket(int i, DataLinkLayer dataLink, String dest, DatagramSocket socket){
        try {
            String dataGramme = dataLink.getPayloads().get(i);
            byte[] buf = new byte[1024];
            buf = dataGramme.getBytes();
            InetAddress address = InetAddress.getByName(dest);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 25800);
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkReception(String received){
        String checker = received.substring(4,7);
        if(checker.equals("ACK")){return true;} // If the received packet is an acknowledgement
        else{return false;}                        // if the received packet is a resend request
    }

    public static void saveAckowledgement(String ack){
        Integer ackNum = Integer.valueOf(ack.substring(0,4)) - 1;
        acknowledgementBuffer.set(ackNum, ack);
    }
}