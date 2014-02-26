package it.polito.clj.languagespotter;

import weka.core.tokenizers.WordTokenizer;

import java.util.List;

/**
 * Adapter from GenericProgrammingLanguageTokenizer to WordTokenizer.
 */
abstract class AbstactWekaProgrammingLanguageTokenizer extends WordTokenizer {

    private List<Token> tokens = null;
    private int indexOfNextElement = -1;

    @Override
    public boolean hasMoreElements() {
        return tokens!=null&&indexOfNextElement<tokens.size();
    }

    @Override
    public Object nextElement() {
        if (!hasMoreElements()){
            throw new IllegalStateException();
        }
        indexOfNextElement+=1;
        return adapt(tokens.get(indexOfNextElement-1));
    }

    @Override
    public void tokenize(String code) {
        GenericProgrammingLanguageTokenizer t = new GenericProgrammingLanguageTokenizer();
        this.tokens = t.parse(code);
        indexOfNextElement = 0;
    }

    protected abstract String adapt(Token token);

}
