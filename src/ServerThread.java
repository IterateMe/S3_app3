import headers.AppliLayer;
import headers.DataLinkLayer;
import headers.TransportLayer;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {

    protected DatagramSocket socket = null;
    public Vector<String> receptionBuffer = new Vector<>();
    String fileName = "";

    AppliLayer application;
    TransportLayer transport;
    DataLinkLayer dataLink;

    String[] args;

    public ServerThread(String[] args) throws IOException {
        socket = new DatagramSocket(25800);
        this.args = args;

        application =  new AppliLayer(args[0]);
        transport = new TransportLayer(args[0]);
        dataLink = new DataLinkLayer();

        System.out.println("RUN");
        run();
    }

    public void run(){
        boolean keepListening = true;

        while(keepListening){

            byte[] buf = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());

                transport.readHeader(received);
                if(transport.transactionCompleted){
                    application.getRemoteContent(transport.getContentForAppliLayer());
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }
}