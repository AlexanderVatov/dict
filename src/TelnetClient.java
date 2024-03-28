import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TelnetClient {
    private Socket socket;
    private PrintWriter outbound;
    private BufferedReader inbound;

    TelnetClient(String hostname, int port) throws IOException {
        socket = new Socket(hostname, port);
        outbound = new PrintWriter(socket.getOutputStream());
        inbound = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String readLine() throws IOException {
        String result;
        //Get rid of any initial blank lines
        while (true){
            result = inbound.readLine();
            if(result != null && result.isEmpty())
                continue;
            else
                return result;
        }
    }

    public boolean ready() throws IOException {
        return inbound.ready();
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
