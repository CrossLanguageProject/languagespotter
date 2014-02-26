package it.polito.clj.languagespotter;

import org.parboiled.BaseParser;
import org.parboiled.MatcherContext;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.annotations.MemoMismatches;
import org.parboiled.annotations.SuppressNode;
import org.parboiled.annotations.SuppressSubnodes;
import org.parboiled.matchers.CustomMatcher;

/**
 * Most of the code is just stolen from one example of the Parboiled project.
 */
@SuppressWarnings({"InfiniteRecursion"})
@BuildParseTree
class ParboiledParser extends BaseParser<Object> {

    public abstract class AbstractJavaCharacterMatcher extends CustomMatcher {

        protected AbstractJavaCharacterMatcher(String label) {
            super(label);
        }

        @Override
        public final boolean isSingleCharMatcher() {
            return true;
        }

        @Override
        public final boolean canMatchEmpty() {
            return false;
        }

        @Override
        public boolean isStarterChar(char c) {
            return acceptChar(c);
        }

        @Override
        public final char getStarterChar() {
            return 'a';
        }

        public final <V> boolean match(MatcherContext<V> context) {
            if (!acceptChar(context.getCurrentChar())) {
                return false;
            }
            context.advanceIndex(1);
            context.createNode();
            return true;
        }

        protected abstract boolean acceptChar(char c);
    }

    public class JavaLetterMatcher extends AbstractJavaCharacterMatcher {

        public JavaLetterMatcher() {
            super("Letter");
        }

        @Override
        protected boolean acceptChar(char c) {
            return Character.isJavaIdentifierStart(c);
        }
    }

    public class JavaLetterOrDigitMatcher extends AbstractJavaCharacterMatcher {

        public JavaLetterOrDigitMatcher() {
            super("LetterOrDigit");
        }

        @Override
        protected boolean acceptChar(char c) {
            return Character.isJavaIdentifierPart(c);
        }
    }

    Rule ListOfTokens(){
        return Sequence(OneOrMore(Anything()), EOI);
    }

    Rule Anything(){
        return FirstOf(Spacing(),IntegerLiteral(),Keyword(),Identifier(),StringLiteral());
    }

    @SuppressSubnodes
    @MemoMismatches
    Rule Identifier() {
        return Sequence(TestNot(Keyword()), Letter(), ZeroOrMore(LetterOrDigit()));
    }

    // JLS defines letters and digits as Unicode characters recognized
    // as such by special Java procedures.

    Rule Letter() {
        // switch to this "reduced" character space version for a ~10% parser performance speedup
        //return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), '_', '$');
        return FirstOf(Sequence('\\', UnicodeEscape()), new JavaLetterMatcher());
    }

    Rule StringLiteral() {
        return Sequence(
                '"',
                ZeroOrMore(
                        FirstOf(
                                Escape(),
                                Sequence(TestNot(AnyOf("\r\n\"\\")), ANY)
                        )
                ).suppressSubnodes(),
                '"'
        );
    }
    @SuppressSubnodes
    Rule DecimalFloat() {
        return FirstOf(
                Sequence(OneOrMore(Digit()), '.', ZeroOrMore(Digit()), Optional(Exponent()), Optional(AnyOf("fFdD"))),
                Sequence('.', OneOrMore(Digit()), Optional(Exponent()), Optional(AnyOf("fFdD"))),
                Sequence(OneOrMore(Digit()), Exponent(), Optional(AnyOf("fFdD"))),
                Sequence(OneOrMore(Digit()), Optional(Exponent()), AnyOf("fFdD"))
        );
    }

    Rule Exponent() {
        return Sequence(AnyOf("eE"), Optional(AnyOf("+-")), OneOrMore(Digit()));
    }

    Rule Digit() {
        return CharRange('0', '9');
    }

    @SuppressSubnodes
    Rule HexFloat() {
        return Sequence(HexSignificant(), BinaryExponent(), Optional(AnyOf("fFdD")));
    }

    Rule HexSignificant() {
        return FirstOf(
                Sequence(FirstOf("0x", "0X"), ZeroOrMore(HexDigit()), '.', OneOrMore(HexDigit())),
                Sequence(HexNumeral(), Optional('.'))
        );
    }

    Rule BinaryExponent() {
        return Sequence(AnyOf("pP"), Optional(AnyOf("+-")), OneOrMore(Digit()));
    }

    Rule CharLiteral() {
        return Sequence(
                '\'',
                FirstOf(Escape(), Sequence(TestNot(AnyOf("'\\")), ANY)).suppressSubnodes(),
                '\''
        );
    }

    @SuppressSubnodes
    Rule IntegerLiteral() {
        return Sequence(FirstOf(HexNumeral(), /*OctalNumeral(),*/ DecimalNumeral()), Optional(AnyOf("lL")));
    }

    @SuppressSubnodes
    Rule DecimalNumeral() {
        return FirstOf('0', Sequence(CharRange('1', '9'), ZeroOrMore(Digit())));
    }

    @SuppressSubnodes

    @MemoMismatches
    Rule HexNumeral() {
        return Sequence('0', IgnoreCase('x'), OneOrMore(HexDigit()));
    }

    Rule HexDigit() {
        return FirstOf(CharRange('a', 'f'), CharRange('A', 'F'), CharRange('0', '9'));
    }


    Rule Escape() {
        return Sequence('\\', FirstOf(AnyOf("btnfr\"\'\\"), OctalEscape(), UnicodeEscape()));
    }

    Rule OctalEscape() {
        return FirstOf(
                Sequence(CharRange('0', '3'), CharRange('0', '7'), CharRange('0', '7')),
                Sequence(CharRange('0', '7'), CharRange('0', '7')),
                CharRange('0', '7')
        );
    }

    Rule UnicodeEscape() {
        return Sequence(OneOrMore('u'), HexDigit(), HexDigit(), HexDigit(), HexDigit());
    }

    @MemoMismatches
    Rule LetterOrDigit() {
        // switch to this "reduced" character space version for a ~10% parser performance speedup
        //return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'), CharRange('0', '9'), '_', '$');
        return FirstOf(Sequence('\\', UnicodeEscape()), new JavaLetterOrDigitMatcher());
    }



    @MemoMismatches
    Rule Keyword() {
        return Sequence(
                FirstOf("assert", "break", "case", "catch", "class", "const", "continue", "default", "do", "else",
                        "enum", "extends", "finally", "final", "for", "goto", "if", "implements", "import", "interface",
                        "instanceof", "new", "package", "return", "static", "super", "switch", "synchronized", "this",
                        "throws", "throw", "try", "void", "while"),
                TestNot(LetterOrDigit())
        );
    }

    @SuppressNode
    Rule Spacing() {
        return OneOrMore(FirstOf(

                // whitespace
                OneOrMore(AnyOf(" \t\r\n\f").label("Whitespace")),

                // traditional comment
                Sequence("/*", ZeroOrMore(TestNot("*/"), ANY), "*/"),

                // end of line comment
                Sequence(
                        "//",
                        ZeroOrMore(TestNot(AnyOf("\r\n")), ANY),
                        FirstOf("\r\n", '\r', '\n', EOI)
                )
        ));
    }
}
