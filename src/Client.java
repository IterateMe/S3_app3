import headers.AppliLayer;
import headers.TransportLayer;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    public static Vector<Integer> acknowledgementBuffer = new Vector<Integer>();

    public static void main(String[] args) throws IOException {

        DatagramSocket socket = new DatagramSocket();

        AppliLayer application =  new AppliLayer(args[0]);
        application.readFile();
        TransportLayer transport = new TransportLayer(args[0]);
        transport.getContent(application.getLocalContent());
        transport.writeHeader();
        //transport.printPayloads();

        setAckowledgementBuffer(  transport.getPayloads().size()  );     // Mettre la derniere couche en reference

        for(int i = 0; i<acknowledgementBuffer.size(); i++){
            //Send packet
            System.out.println(i);
            try {
                String dataGramme = transport.getPayloads().get(i);
                byte[] buf = new byte[1024];
                buf = dataGramme.getBytes();
                InetAddress address = InetAddress.getByName(args[1]);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 25800);
                socket.send(packet);

            } catch (IOException e) {
                e.printStackTrace();
            }

            //Receive and treat acknowledgement
            //byte[] buf = new byte[256];
            //DatagramPacket packet = new DatagramPacket(buf, buf.length);
            //socket.receive(packet);
            //String received = new String(packet.getData(), 0, packet.getLength());
        }

        socket.close();
    }
    public static void setAckowledgementBuffer(Integer count){
        for(int i=0; i<count; i++){
            acknowledgementBuffer.add(0);
        }
    }
}