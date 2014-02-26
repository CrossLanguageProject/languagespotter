package it.polito.clj.languagespotter;

/**
 * Created by federico on 2/26/14.
 */
public class Token {
    private String type;
    private String source;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Token(String type, String source){
        this.type = type;
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Token token = (Token) o;

        if (source != null ? !source.equals(token.source) : token.source != null) return false;
        if (type != null ? !type.equals(token.type) : token.type != null) return false;

        return true;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type='" + type + '\'' +
                ", source='" + source + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (source != null ? source.hashCode() : 0);
        return result;
    }
}
