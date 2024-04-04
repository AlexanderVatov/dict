import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DICTClientTest {

    @Test
    void define() throws IOException {
        DICTClient c = new DICTClient(new TCPClient("dict.org", 2628));
        List<DICTClient.Definition> definitionList = c.define("gcide", "hello");
        assertEquals(1, definitionList.size());
        DICTClient.Definition def = definitionList.getFirst();
        assertEquals("Hello", def.headword());
        assertEquals("gcide", def.dictionaryID());
    }
}