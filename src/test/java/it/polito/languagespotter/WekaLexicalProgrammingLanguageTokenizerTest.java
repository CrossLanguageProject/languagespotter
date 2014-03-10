package it.polito.languagespotter;

import static org.junit.Assert.assertArrayEquals;
import it.polito.languagespotter.tokenizer.WekaLexicalProgrammingLanguageTokenizer;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class WekaLexicalProgrammingLanguageTokenizerTest {

    private void assertParse(String code, String... expectedTokens){
        WekaLexicalProgrammingLanguageTokenizer t = new WekaLexicalProgrammingLanguageTokenizer();
        List<String> actualTokens = new LinkedList<>();
        t.tokenize(code);
        while (t.hasMoreElements()){
            actualTokens.add((String)t.nextElement());
        }
        assertArrayEquals("Expected: "+ Arrays.toString(expectedTokens)+", \nActual: "+Arrays.toString(actualTokens.toArray()), expectedTokens, actualTokens.toArray());
    }

    @Test
    public void parseSingleIdentifier() throws Exception {
        assertParse("ciao","ciao");
    }

    @Test
    public void parseSimpleMathExp() throws Exception {
        assertParse("1*2","1","*","2");
    }

    @Test
    public void parseJavaAssignement() throws Exception {
        assertParse("a *= 7 +(2- 3)","a","*=","7","+","(","2","-","3",")");
    }

    @Test
    public void parseASimpleJavaMethod() {
        String code = "    @Test\n" +
                "    public void parseIgnoreBlockComment() throws Exception {\n" +
                "        assertParse(\"aString\",new Token(\"StringLiteral\", \"\\\"abc\\\"\"));\n" +
                "    }";
        assertParse(code,"@Test","public","void","parseIgnoreBlockComment","(",")","throws","Exception","{","assertParse",
                "(","\"aString\"",",","new","Token","(","\"StringLiteral\"",",","\"\\\"abc\\\"\"",")",")",";","}");
    }

    @Test
    public void parsePreambleOfJavaFile() {
        String code = "package it.polito.clj.languagespotter;\n"+
            "\n"+
            "import org.junit.Test;\n"+
            "import static org.junit.Assert.*;";
        assertParse(code,"package","it",".","polito",".","clj",".","languagespotter",";",
                "import","org",".","junit",".","Test",";",
                "import","static","org",".","junit",".","Assert",".","*",";");
    }

}
