package it.polito.languagespotter.tokenizer;

import org.parboiled.common.Sink;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.TracingParseRunner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Let's debug the tokenizer...
 */
public class TokenizerWorkbench {



    public static void main(String[] args){
        String code = "...\n" +
                "\n" +
                "---\n" +
                "# In this example the &title anchor contains the list [\"Must be ...\", ...], not the map.";
        GenericProgrammingLanguageFlatParser p = new GenericProgrammingLanguageFlatParser();
        TracingParseRunner<String> tpr = new TracingParseRunner<>(p.ListOfTokens());
        tpr.run(code);
        Sink<String> sink = tpr.getLog();
        for (ParseError error : tpr.getParseErrors()){
            System.err.println("ERROR: "+error);
        }
    }
}
