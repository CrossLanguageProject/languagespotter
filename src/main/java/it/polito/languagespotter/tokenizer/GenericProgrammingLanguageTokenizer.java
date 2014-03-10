package it.polito.languagespotter.tokenizer;

import java.util.List;
import java.util.LinkedList;

import org.parboiled.*;

import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import static org.parboiled.errors.ErrorUtils.printParseError;

/**
 * It is a tokenizer working with the most common programming
 * languages operators and keywords.
 *
 * Basically it is build unifying all the tokens definitions from a plethora
 * of languages.
 */
public class GenericProgrammingLanguageTokenizer {

    private List<String> rulesToIgnore = new LinkedList<>();

    public GenericProgrammingLanguageTokenizer(){
        rulesToIgnore.add("EOI");
        rulesToIgnore.add("OneOrMore");
        rulesToIgnore.add("Anything");
        rulesToIgnore.add("ListOfTokens");
        rulesToIgnore.add("EOI");
    }

    private void analyze(Node node,List<Token> tokens,String code, ParsingResult pr){
        try{
        if (!rulesToIgnore.contains(node.getLabel())){
            String source = "";
            // EOF is placed AFTER the end of the code
            int start = pr.inputBuffer.getOriginalIndex(node.getStartIndex());
            int end   = pr.inputBuffer.getOriginalIndex(node.getEndIndex());
            /*if (node.getStartIndex()<pr.inputBuffer.getOriginalIndex()){
                /*if (end>code.length()){
                    // the recovering mechanism can plat weird jokes by introducing virtual chars...
                    end = code.length();
                }*/
            source = code.substring(start,end);
            //}
            tokens.add(new Token(node.getLabel(),source));
        } else {
            for (Object child : node.getChildren()){
                analyze((Node)child,tokens,code,pr);
            }
        }}catch(StringIndexOutOfBoundsException ex){
            throw new RuntimeException("Code length: "+code.length()+", Start "+node.getStartIndex()+", End "+node.getEndIndex());
        }
    }

    public List<Token> parse(String code) {
        GenericProgrammingLanguageFlatParser pp = Parboiled.createParser(GenericProgrammingLanguageFlatParser.class);
        RecoveringParseRunner rpr = new RecoveringParseRunner<Object>(pp.ListOfTokens());
        ParsingResult pr = rpr.run(code);
        List<String> errors = new LinkedList<>();
        for (Object pe : pr.parseErrors){
            errors.add(printParseError((ParseError)pe));
        }
        List<Token> tokens = new LinkedList<Token>();
        if (pr.parseTreeRoot!=null){
            Node listOfTokens = pr.parseTreeRoot;
            analyze(listOfTokens,tokens,code,pr);
        } else {
            throw new ParsingException(errors,code);
        }
        return tokens;
    }

}
