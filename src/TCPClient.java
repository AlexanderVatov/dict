import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * Allows text communication over a TCP/IP connection
 *
 * All messages are assumed to be encoded with UTF-8 and end with a line separator.
 */
public class TCPClient {
    private Socket socket;
    private PrintWriter outbound;
    private BufferedReader inbound;

    TCPClient(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        socket.setKeepAlive(true);
        // Prevent read operations for blocking for longer than two seconds
        socket.setSoTimeout(2000);
        outbound = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        inbound = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
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
    }

    public void close() throws IOException {
        outbound.close();
        inbound.close();
        socket.close();
    }

}
