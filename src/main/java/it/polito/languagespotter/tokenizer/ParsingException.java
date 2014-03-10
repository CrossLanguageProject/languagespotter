package it.polito.languagespotter.tokenizer;

import java.util.List;

public class ParsingException extends RuntimeException {

    private List<String> parsingProblems;
    private String code;

    public ParsingException(List<String> parsingProblems, String code){
        this.parsingProblems = parsingProblems;
        this.code = code;
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
        sb.append("CODE <<<START>>>\n"+code+"\n<<<END>>>");
        return sb.toString();
    }
}
