import headers.AppliLayer;
import headers.DataLinkLayer;
import headers.TransmissionErrorException;
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
                socket.setSoTimeout(1000);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                ///////////////////////////////////////////
                // LAYER MANAGEMENT
                ///////////////////////////////////////////
                dataLink.readHeader(received);
                if( ! dataLink.getPayloads().lastElement().equals("CRC_ERROR") ) {
                    transport.readHeader(dataLink.getPayloads().lastElement());
                    application.transmissionError = transport.transmissionError;

                    String ack = transport.createAcknowledgement(transport.getPayloads().lastElement());
                    sendFeedback(ack, dataLink, args[1], socket);
                }

                if(transport.transactionCompleted){
                    application.getRemoteContent(transport.getContentForAppliLayer());
                    application.setFileName(transport.getFileName());
                    application.writeFile();
                    resetServer();
                }
            } catch (IOException | TransmissionErrorException e) {
                e.printStackTrace();
            }
        }
        socket.close();
    }

    public void sendFeedback(String fb, DataLinkLayer dataLink, String dest, DatagramSocket socket){
        String crcHeader = Long.toString(dataLink.CRC32Generator(fb));
        crcHeader = dataLink.setFixedLengthString(12, crcHeader);
        fb = crcHeader+fb;
        try {
            byte[] buf = new byte[1024];
            buf = fb.getBytes();
            InetAddress address = InetAddress.getByName(dest);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 25800);
            socket.send(packet);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resetServer(){
        application =  new AppliLayer(args[0]);
        transport = new TransportLayer(args[0]);
        dataLink = new DataLinkLayer();
    }
}
