import java.io.*;
import java.net.*;
import java.util.*;

public class ServerThread extends Thread {

    protected DatagramSocket socket = null;
    protected BufferedReader in = null;

    public ServerThread() throws IOException {
        socket = new DatagramSocket(25101);
    }
}