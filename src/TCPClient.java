import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class TCPClient {
    private Socket socket;
    private PrintWriter outbound;
    private BufferedReader inbound;

    TCPClient(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        socket.setKeepAlive(true);
        socket.setSoTimeout(2000);
        outbound = new PrintWriter(socket.getOutputStream());
        inbound = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Socket getSocket() {
        return socket;
    }

    public String readLine() throws IOException {
        try {
            return inbound.readLine();
        } catch (SocketTimeoutException ignored) {
            return null;
        }
    }

    public void writeLine(String line) {
        outbound.println(line);
        outbound.flush();
    }

    public void close() throws IOException {
        outbound.close();
        inbound.close();
        socket.close();
    }

}
