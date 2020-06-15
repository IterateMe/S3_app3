import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {

    protected DatagramSocket socket = null;
    public Vector<String> receptionBuffer = new Vector<>();
    String fileName = "";

    public ServerThread() throws IOException {
        socket = new DatagramSocket(25101);
    }
    public void run(){

        boolean keepListening = true;
        while(keepListening){

            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                receptionBuffer.add(received);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        socket.close();
    }
}