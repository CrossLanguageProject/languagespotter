package it.polito.clj.languagespotter;

/**
 * Created by federico on 2/26/14.
 */
public class WekaStructuralProgrammingLanguageTokenizer extends AbstactWekaProgrammingLanguageTokenizer {

    protected String adapt(Token token){
        return token.getType();
    }

}
