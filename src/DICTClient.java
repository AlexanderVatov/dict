import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class DICTClient {
    public interface Backend {
        String query(String query) throws IOException;
    }
    private Backend backend;

    public DICTClient(Backend backend){
        this.backend = backend;
    }
    public record Definition(String headword, String dictionaryID, String dictionaryName, String text) {}

    public List<Definition> define(String dictionaryID, String query) throws IOException {
        String response = backend.query(STR."define \{dictionaryID} \{query}");
        Scanner scanner = new Scanner(response);


        int code = scanner.nextInt();
        if(code == 220) {
            scanner.nextLine();
            code = scanner.nextInt();
        }
        List<Definition> definitionList = new ArrayList<>();
        int numberOfDefinitionsExpected = 0;
        if(code == 150) numberOfDefinitionsExpected = scanner.nextInt();
        else if(code == 552) {
            //No match; return empty list
            return definitionList;
        } else if (code == 550) {
            //Invalid database
            throw new IllegalArgumentException(String.format("No such dictionary ID: \"%s\"",dictionaryID));
        }
        scanner.nextLine();

        //Start parsing definitions
        Pattern defaultDelimiter = scanner.delimiter();
        Pattern endOfDefinition = Pattern.compile("\\R(?=[0-9]{3})");
        Pattern quotedString = Pattern.compile("\".+?\"");

        for(int i = 0; i < numberOfDefinitionsExpected; ++i) {
            if(scanner.nextInt() != 151) System.err.println("Warning: Code 151 expected at the start of a definition!");
            String headword = scanner.next(quotedString).replace("\"","");
            String readDictionaryID = scanner.next();
            String dictionaryName = scanner.nextLine().replace("\"","").strip();

            scanner.useDelimiter(endOfDefinition);
            String definitionText = scanner.next();
            scanner.useDelimiter(defaultDelimiter);

            definitionList.add(new Definition(headword,readDictionaryID,dictionaryName,definitionText));
        }
        return definitionList;
    }
}
