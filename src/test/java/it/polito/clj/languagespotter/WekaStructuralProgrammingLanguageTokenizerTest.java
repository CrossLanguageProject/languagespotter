package it.polito.clj.languagespotter;


import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class WekaStructuralProgrammingLanguageTokenizerTest {

    private void assertParse(String code, String... expectedTokens){
        WekaStructuralProgrammingLanguageTokenizer t = new WekaStructuralProgrammingLanguageTokenizer();
        List<String> actualTokens = new LinkedList<>();
        t.tokenize(code);
        while (t.hasMoreElements()){
            actualTokens.add((String)t.nextElement());
        }
        assertArrayEquals(expectedTokens, actualTokens.toArray());
    }

    @Test
    public void parseSingleIdentifier() throws Exception {
        assertParse("ciao","Identifier");
    }

    @Test
    public void parseSimpleMathExp() throws Exception {
        assertParse("1*2","IntegerLiteral","Operator_*","IntegerLiteral");
    }

}
