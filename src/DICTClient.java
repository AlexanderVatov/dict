import java.io.IOException;
import java.util.List;

public class DICTClient {
    public interface Backend {
        String query(String query) throws IOException;
    }
    public record Definition(String headword, String dictionaryName, String text) {}

    public static List<Definition> define(String dictionaryID, String query) {
        return null;
    }
}
