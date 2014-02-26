package it.polito.clj.languagespotter;

import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class ParsingSamplesOfJavaFile {

    private static String slurp(final InputStream is, final int bufferSize)
    {
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try {
            final Reader in = new InputStreamReader(is, "UTF-8");
            try {
                for (;;) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0)
                        break;
                    out.append(buffer, 0, rsz);
                }
            }
            finally {
                in.close();
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return out.toString();
    }
    private void assertParseWithoutErrors(String code){
        WekaLexicalProgrammingLanguageTokenizer t = new WekaLexicalProgrammingLanguageTokenizer();
        t.tokenize(code);
    }

    private String readSample(String name){
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("java/sample_1");
        if (is==null){
            throw new RuntimeException("Resource not found");
        }
        String code = slurp(is,65536);
        return code;
    }

    @Test
    public void parseJavaSample1() throws Exception {
        assertParseWithoutErrors(readSample("java/sample_1"));
    }

    @Test
    public void parseJavaSample2() throws Exception {
        assertParseWithoutErrors(readSample("java/sample_2"));
    }

    @Test
    public void parseJavaSample3() throws Exception {
        assertParseWithoutErrors(readSample("java/sample_3"));
    }

}
