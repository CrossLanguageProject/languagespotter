package it.polito.clj.languagespotter;

import java.util.List;
import java.util.LinkedList;

import org.parboiled.*;

import org.parboiled.errors.ParseError;
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
class GenericProgrammingLanguageTokenizer {

    private List<String> rulesToIgnore = new LinkedList<>();

    public GenericProgrammingLanguageTokenizer(){
        rulesToIgnore.add("EOI");
        rulesToIgnore.add("OneOrMore");
        rulesToIgnore.add("Anything");
        rulesToIgnore.add("ListOfTokens");
        rulesToIgnore.add("EOI");
    }

    private void analyze(Node node,List<Token> tokens,String code){
        if (!rulesToIgnore.contains(node.getLabel())){
            String source = code.substring(node.getStartIndex(),node.getEndIndex());
            tokens.add(new Token(node.getLabel(),source));
        } else {
            for (Object child : node.getChildren()){
                analyze((Node)child,tokens,code);
            }
        }
    }

    public List<Token> parse(String code) {
        GenericProgrammingLanguageFlatParser pp = Parboiled.createParser(GenericProgrammingLanguageFlatParser.class);
        ReportingParseRunner rpr = new ReportingParseRunner<Object>(pp.ListOfTokens());
        ParsingResult pr = rpr.run(code);
        for (Object pe : pr.parseErrors){
            System.out.println("Parsing error: "+printParseError((ParseError)pe));
        }
        List<Token> tokens = new LinkedList<Token>();
        if (pr.parseTreeRoot!=null){
            Node listOfTokens = pr.parseTreeRoot;
            analyze(listOfTokens,tokens,code);
        } else {
            throw new ParsingException();
        }
        return tokens;
    }

}
