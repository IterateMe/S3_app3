
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerThread server = new ServerThread();
        server.start();
        server.run();
    }
}
