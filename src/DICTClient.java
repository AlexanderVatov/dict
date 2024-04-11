import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class DICTClient {
    public interface Backend {
        String query(String query) throws IOException;
    }
    protected final Backend backend;

    public DICTClient(Backend backend){
        this.backend = backend;
    }

    public record Definition(String headword, String databaseID, String databaseDescription, String text) {}
    public record Database(String databaseID, String databaseDescription){}

    public List<Definition> define(String databaseID, String query) throws IOException {
        // The format of the command and the response are described in section 3.2 of RFC 2229:
        // https://www.rfc-editor.org/rfc/rfc2229.html#section-3.2
        String response = backend.query(STR."DEFINE \{databaseID} \{query}");
        if(response == null || response.isBlank()) {
            throw new IOException("Backend gave no response or empty response!");
        }
        Scanner scanner = new Scanner(response.strip());

        int code = scanner.nextInt();
        if(code == 220) {
            scanner.nextLine();
            code = scanner.nextInt();
        }
        List<Definition> definitionList = new ArrayList<>();

        //Possible codes and syntax: https://www.rfc-editor.org/rfc/rfc2229.html#section-3.2.2
        int numberOfDefinitionsExpected = 0;
        if(code == 150) numberOfDefinitionsExpected = scanner.nextInt();
        else if(code == 552) {
            //No match; return empty list
            return definitionList;
        } else if (code == 550) {
            //Invalid database
            throw new IllegalArgumentException(String.format("No such database ID: \"%s\"",databaseID));
        }
        scanner.nextLine();

        //Start parsing definitions
        Pattern defaultDelimiter = scanner.delimiter();
        Pattern endOfDefinition = Pattern.compile("\\R[.]\\R(?=[0-9]{3})");
        Pattern quotedString = Pattern.compile("\".+?\"");

        for(int i = 0; i < numberOfDefinitionsExpected; ++i) {
            if(scanner.nextInt() != 151) System.err.println("Warning: Code 151 expected at the start of a definition!");
            String headword = scanner.next(quotedString).replace("\"","");
            String readDatabaseID = scanner.next();
            String databaseName = scanner.nextLine().strip().replace("\"","");

            // Per section 2.4.3 of RFC 2229, each definition ends with a line containing
            // a single period (\n.\n):
            // https://www.rfc-editor.org/rfc/rfc2229.html#section-2.4.3
            scanner.useDelimiter(endOfDefinition);
            String definitionText = scanner.next();
            scanner.useDelimiter(defaultDelimiter);
            // Get rid of the final dot
            scanner.next();

            Definition definition = new Definition(headword, readDatabaseID, databaseName, definitionText);
            definitionList.add(definition);
        }
        return definitionList;
    }
}
