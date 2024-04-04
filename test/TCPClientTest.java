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

    @Test
    void testQuery() throws IOException {
        TCPClient client = new TCPClient("dict.org", 2628);
        String response = client.query("define gcide hello").strip();
        assertTrue(response.startsWith("220 "));
        int beginningOfLastLine = response.lastIndexOf("\n") + 1;
        assertNotEquals(-1, beginningOfLastLine, "Response should contain at least two lines!");
        assertEquals("250 ok", response.substring(beginningOfLastLine, beginningOfLastLine + 6));
        assertTrue(response.endsWith("s]"));
        client.close();
    }
}