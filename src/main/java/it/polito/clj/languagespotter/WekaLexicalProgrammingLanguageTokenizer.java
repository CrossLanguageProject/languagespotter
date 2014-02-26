package it.polito.clj.languagespotter;

import weka.core.tokenizers.WordTokenizer;

import java.util.List;

/**
 * Created by federico on 2/26/14.
 */
public class WekaLexicalProgrammingLanguageTokenizer extends AbstactWekaProgrammingLanguageTokenizer {

    protected String adapt(Token token){
        return token.getSource();
    }

}
