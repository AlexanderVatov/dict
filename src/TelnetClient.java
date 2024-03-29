import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


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
        return inbound.readLine();
    }

    public String readLine(int timeoutMilliseconds) throws IOException {
        if (timeoutMilliseconds < 0) return readLine();

        long timerStart = System.currentTimeMillis();
        String response;
        while(System.currentTimeMillis() - timerStart <= timeoutMilliseconds) {
            if(inbound.ready()) {
                response = inbound.readLine();
                if(response != null) {
                    return response;
                }
            }
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException ignored) {}
        }
        return null;
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

    public static void main(String[] args) throws IOException {
        String hostname = null;
        int port = 23;
        try {
            hostname = args[0];
            if(args.length >= 2) port = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
            System.out.println("Usage: java TelnetClient HOSTNAME [PORT]");
            System.exit(1);
        }
        TelnetClient client = new TelnetClient(hostname, port);
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        String server_message, user_response;

        // REPL loop
        while(true) {
            // Read from Telnet
            while(client.ready()) {
                server_message = client.readLine();
                if (server_message == null) {
                    break;
                } else {
                    System.out.println(server_message);
                }
            }

            // Read and transmit user response
            if(scanner.ready()){
                user_response = scanner.readLine();
                if (Objects.equals(user_response, "exit")) break;
                else {
                    System.err.println("Sending: " + user_response);
                    client.writeLine(user_response);
                }
            } else{
                // No new input from either server or user.
                // Sleep for a bit to reduce busy waiting, then check again.
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
            }

        }
        client.close();
    }
}
