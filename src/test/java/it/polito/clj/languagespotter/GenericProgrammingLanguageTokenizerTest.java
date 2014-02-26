package it.polito.clj.languagespotter;

import org.junit.Test;
import static org.junit.Assert.*;

public class GenericProgrammingLanguageTokenizerTest {

    private void assertParse(String code, Token... tokens){
        GenericProgrammingLanguageTokenizer t = new GenericProgrammingLanguageTokenizer();
        assertArrayEquals(tokens, t.parse(code).toArray());
    }

    @Test
    public void parseSingleIdentifier() throws Exception {
        assertParse("ciao",new Token("Identifier", "ciao"));
    }

    @Test
    public void parseIgnoreSpacing() throws Exception {
        assertParse(" \r \n  \r\n    ciao \t",new Token("Identifier", "ciao"));
    }

    @Test
    public void parseIntegerLiteral() throws Exception {
        assertParse("123",new Token("IntegerLiteral", "123"));
    }

    @Test
    public void parseIgnoreBlockComment() throws Exception {
        assertParse("/*pre*/\"abc\"/*post*/",new Token("StringLiteral", "\"abc\""));
    }

    @Test
    public void parseIgnoreLineComment() throws Exception {
        assertParse("1//comment\n2",new Token("IntegerLiteral", "1"),new Token("IntegerLiteral", "2"));
    }

    @Test
    public void parseStringLiteral() throws Exception {
        assertParse("\"abc\"",new Token("StringLiteral", "\"abc\""));
    }
}
