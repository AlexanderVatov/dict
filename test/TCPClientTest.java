import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TCPClientTest {
    @Test
    public void testOnline() throws IOException {
        TCPClient client = new TCPClient("india.colorado.edu", 13);
        String response = client.readLine() + "\n" + client.readLine();
        assertTrue(response.contains("UTC(NIST)"),"Unexpected response: " + response);
    }
}