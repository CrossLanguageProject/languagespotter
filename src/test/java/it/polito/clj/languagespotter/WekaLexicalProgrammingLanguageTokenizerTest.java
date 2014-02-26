package it.polito.clj.languagespotter;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

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
    public void parseAssignement() throws Exception {
        assertParse("a *= 7 +(2- 3)","a","*=","7","+","(","2","-","3",")");
    }

    @Test
    public void parseASimpleMethod() {
        String code = "    @Test\n" +
                "    public void parseIgnoreBlockComment() throws Exception {\n" +
                "        assertParse(\"aString\",new Token(\"StringLiteral\", \"\\\"abc\\\"\"));\n" +
                "    }";
        assertParse(code,"@Test","public","void","parseIgnoreBlockComment","(",")","throws","Exception","{","assertParse",
                "(","\"aString\"",",","new","Token","(","\"StringLiteral\"",",","\"\\\"abc\\\"\"",")",")",";","}");
    }

}
