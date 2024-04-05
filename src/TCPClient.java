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
public class TCPClient implements DICTClient.Backend{
    private Socket socket;
    private PrintWriter outbound;
    private BufferedReader inbound;

    TCPClient(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        socket.setKeepAlive(true);
        // Prevent read operations for blocking for longer than one second
        socket.setSoTimeout(5000);
        outbound = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
        inbound = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public String query(String query) throws IOException {
        writeLine(query);

        StringBuilder builder = new StringBuilder();
        String read;
        int linesRead = 0;
        socket.setSoTimeout(10000);

        while(true) {
            read = readLine();
            if(read == null) break;
            else {
                System.out.println(read);
                builder.append(read);
                builder.append('\n');
                linesRead += 1;
                socket.setSoTimeout(1000);
            }
        }
        if(linesRead > 0) return builder.toString();
        else throw new SocketTimeoutException("The connection timed out before any data was received!");
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
