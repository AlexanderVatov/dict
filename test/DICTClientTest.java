import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DICTClientTest {
    private static class StubBackend implements DICTClient.Backend {
        private final Map<String, String> cannedResponses = Map.of(
                "","",
                "DEFINE gcide hello", "220 dict.dict.org dictd 1.12.1/rf on Linux 4.19.0-10-amd64 <auth.mime> <291799443.23651.1712262604@dict.dict.org>\n150 1 definitions retrieved\n151 \"Hello\" gcide \"The Collaborative International Dictionary of English v.0.48\"\nHello \\Hel*lo\"\\, interj. & n.\n   An exclamation used as a greeting, to call attention, as an\n   exclamation of surprise, or to encourage one. This variant of\n   {Halloo} and {Holloo} has become the dominant form. In the\n   United States, it is the most common greeting used in\n   answering a telephone.\n   [1913 Webster +PJC]\n.\n250 ok [d/m/c = 1/0/16; 0.000r 0.000u 0.000s]\n",
                "DEFINE * hello", "\n150 4 definitions retrieved\n151 \"Hello\" gcide \"The Collaborative International Dictionary of English v.0.48\"\nHello \\\\Hel*lo\"\\\\, interj. & n.\n   An exclamation used as a greeting, to call attention, as an\n   exclamation of surprise, or to encourage one. This variant of\n   {Halloo} and {Holloo} has become the dominant form. In the\n   United States, it is the most common greeting used in\n   answering a telephone.\n   [1913 Webster +PJC]\n.\n151 \"hello\" wn \"WordNet (r) 3.0 (2006)\"\nhello\n    n 1: an expression of greeting; \"every morning they exchanged\n         polite hellos\" [syn: {hello}, {hullo}, {hi}, {howdy}, {how-\n         do-you-do}]\n.\n151 \"hello\" moby-thesaurus \"Moby Thesaurus II by Grady Ward, 1.0\"\n19 Moby Thesaurus words for \"hello\":\n   accost, address, bob, bow, curtsy, embrace, greeting, hail,\n   hand-clasp, handshake, how-do-you-do, hug, kiss, nod, salutation,\n   salute, smile, smile of recognition, wave\n\n\n\n.\n151 \"hello\" foldoc \"The Free On-line Dictionary of Computing (30 December 2018)\"\nhello, world\nhello\n\n   <programming> The canonical, minimal, first program that a\n   programmer writes in a new {programming language} or {development\n   environment}.  The program just prints \"hello, world\" to {standard\n   output} in order to verify that the programmer can successfully\n   edit, compile and run a simple program before embarking on\n   anything more challenging.\n\n   Hello, world is the first example program in the {C} programming\n   book, {K&R}, and the tradition has spread from there to pretty\n   much every other language and many of their textbooks.\n\n   Environments that generate an unreasonably large executable\n   for this trivial test or which require a {hairy}\n   compiler-linker invocation to generate it are considered bad.\n\n   {Hello, World in over 400 programming languages\n   (http://www.roesler-ac.de/wolfram/hello.htm)}.\n\n   (2013-10-27)\n\n.\n250 ok [d/m/c = 4/0/139; 0.000r 0.000u 0.000s]\n"
        );

        @Override
        public String query(String query) {
            return cannedResponses.get(query);
        }
    }

    @Test
    void define() throws IOException {
        DICTClient.Backend backend = new StubBackend();
        DICTClient c = new DICTClient(backend);
        List<DICTClient.Definition> definitionList = c.define("gcide", "hello");
        assertEquals(1, definitionList.size());
        DICTClient.Definition def = definitionList.getFirst();
        assertEquals("Hello", def.headword());
        assertEquals("gcide", def.databaseID());

        definitionList = c.define("*", "hello");
        assertEquals(4, definitionList.size());
    }
}