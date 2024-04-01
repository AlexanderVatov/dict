import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TCPShell {
    public static void main(String[] args) throws IOException {
        String hostname = null;
        int port = 23;
        try {
            hostname = args[0];
            if(args.length >= 2) port = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
            System.out.println("Usage: java TCPShell HOSTNAME [PORT]");
            System.exit(1);
        }
        TCPClient client = new TCPClient(hostname, port);

        // Stop read operations from blocking for longer than 0.25 s. Since this is a REPL shell, any
        // data not read within this timeframe will be read during the following loop iteration.
        client.getSocket().setSoTimeout(250);
        System.err.println("TCPShell: connected. Type ^D or \"exit\" to exit.");


        // Use a BufferedReader instead of the usual Scanner so we can use BufferedReader.ready
        BufferedReader user_input = new BufferedReader(new InputStreamReader(System.in));
        String server_message, user_response;

        // REPL loop
        while(true) {
            // Read from Telnet
            server_message = client.readLine();
            if(server_message != null) System.out.println(server_message);

            // Read and transmit user response
            if(user_input.ready()){
                // The statement below will block if the user has started typing, but not yet
                // typed an entire line. This is intentional (if opinionated) behaviour, as
                // printing further input while the user is still typing will not only confuse
                // the user, but also prevent them from seeing what they typed. Any data received
                // while the user was typing will be displayed when the user presses enter.
                // This has the drawback that the user might misinterpret that output as being
                // a response to what they just sent.
                user_response = user_input.readLine();
                if (user_response == null || user_response.equals("exit")){
                    // A null response would indicate user typed an EoF (^D) in the shell
                    break;
                }
                else {
                    client.writeLine(user_response);
                }
            }
        }
        System.out.println("TCPShell: exiting.");
        client.close();
    }
}
