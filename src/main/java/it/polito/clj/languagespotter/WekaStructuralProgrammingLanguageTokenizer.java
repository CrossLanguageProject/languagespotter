package it.polito.clj.languagespotter;

public class WekaStructuralProgrammingLanguageTokenizer extends AbstactWekaProgrammingLanguageTokenizer {

    protected String adapt(Token token){
        if (token.getType().equals("Operator")){
            return token.getType()+"_"+token.getSource();
        }
        return token.getType();
    }

}
