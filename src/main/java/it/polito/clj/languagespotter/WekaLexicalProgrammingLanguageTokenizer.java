package it.polito.clj.languagespotter;

public class WekaLexicalProgrammingLanguageTokenizer extends AbstactWekaProgrammingLanguageTokenizer {

    protected String adapt(Token token){
        return token.getSource();
    }

}
