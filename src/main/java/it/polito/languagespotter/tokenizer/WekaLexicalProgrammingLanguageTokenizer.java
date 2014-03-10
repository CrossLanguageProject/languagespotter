package it.polito.languagespotter.tokenizer;

public class WekaLexicalProgrammingLanguageTokenizer extends AbstactWekaProgrammingLanguageTokenizer {

    protected String adapt(Token token){
        return token.getSource();
    }

}
