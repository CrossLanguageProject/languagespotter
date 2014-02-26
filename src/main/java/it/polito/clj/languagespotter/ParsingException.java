package it.polito.clj.languagespotter;

import java.util.List;

public class ParsingException extends RuntimeException {

    private List<String> parsingProblems;

    public ParsingException(List<String> parsingProblems){
        this.parsingProblems = parsingProblems;
    }

    public List<String> getParsingProblems(){
        return this.parsingProblems;
    }

    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append("ParsingErrors:");
        for (String pe : parsingProblems){
            sb.append("\n - "+pe);
        }
        return sb.toString();
    }
}
