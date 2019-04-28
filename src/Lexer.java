import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.*;

public class Lexer {

    public class TokenPattern {

        public final String name;
        public final String pattern;

        TokenPattern(String name, String pattern) {
            this.name = name;
            this.pattern = pattern;
        }
    }
    static public ArrayList<TokenPattern> patterns = new ArrayList<TokenPattern>();

    private void fillPatterns(){
        patterns.add(new TokenPattern("COMMENT", "(?:#|//)[^\\r\\n]*|/\\*[\\s\\S]*?\\*/"));
        patterns.add(new TokenPattern("CONSTSTRING", "[a-zA-Z_\\x7f-\\xff][a-zA-Z0-9_\\x7f-\\xff]*"));
        patterns.add(new TokenPattern("INTERPOLATINSTRING", "\"([^\"\\\\]*(?:\\\\.[^\"\\\\]*)*)\""));
        patterns.add(new TokenPattern("STRING", "\'([^\'\\\\]*(?:\\\\.[^\'\\\\]*)*)\'"));
        patterns.add(new TokenPattern("EVAL", "eval\b"));
        patterns.add(new TokenPattern("REQUIRE", "require\b"));
        patterns.add(new TokenPattern("REQUIREONCE", "require_once\b"));
        patterns.add(new TokenPattern("OPENTAGWITHECHO", "<(\\?|%)="));
        patterns.add(new TokenPattern("OPENTAG", "(<\\?php|<\\?|<%)\\s?"));
        patterns.add(new TokenPattern("CLOSETAG", "\\?>|%>"));
        patterns.add(new TokenPattern("CAST", "\\((real|double|float|bool|boolean|array|string|unset|object|int)\\)"));
        patterns.add(new TokenPattern("HEXNUMBER", "0x[0-9a-fA-F]+(?:[eE][\\\\+\\\\-]?[0-9]+)?"));
        patterns.add(new TokenPattern("OCTNUMBER", "0[0-9]+(?:[eE][\\+\\-]?[0-9]+)?"));
        patterns.add(new TokenPattern("FLOATNUMBER", "([0-9]*\\.[0-9]+|[0-9]+\\.[0-9]*)(?:[eE][\\+\\-]?[0-9]+)?"));
        patterns.add(new TokenPattern("INTNUMBER", "(?:0|[1-9][0-9]*)(?:[eE][\\+\\-]?[0-9]+)?"));
        patterns.add(new TokenPattern("VARIABLE", "\\$[a-zA-Z_\\x7f-\\xff][a-zA-Z0-9_\\x7f-\\xff]*\\b"));
        patterns.add(new TokenPattern("NS", "\\\\"));
        patterns.add(new TokenPattern("OBJECTOPERATOR", "\\->"));
        patterns.add(new TokenPattern("DOUBLEARROW", "=>"));
        patterns.add(new TokenPattern("INCREMENT", "\\+\\+"));
        patterns.add(new TokenPattern("DECREMENT", "\\-\\-"));
        patterns.add(new TokenPattern("COALESCE", "\\?\\?"));
        patterns.add(new TokenPattern("POW", "\\*\\*"));
        patterns.add(new TokenPattern("AT", "@"));
        patterns.add(new TokenPattern("PLUSEQUAL", "\\+="));
        patterns.add(new TokenPattern("MINUSEQUAL", "\\-="));
        patterns.add(new TokenPattern("MULEQUAL", "\\*="));
        patterns.add(new TokenPattern("DIVEQUAL", "/="));
        patterns.add(new TokenPattern("CONCATEQUAL", "\\.="));
        patterns.add(new TokenPattern("MODEQUAL", "%="));
        patterns.add(new TokenPattern("ANDEQUAL", "&="));
        patterns.add(new TokenPattern("OREQUAL", "\\|="));
        patterns.add(new TokenPattern("XOREQUAL", "\\^="));
        patterns.add(new TokenPattern("SLEQUAL", "<<="));
        patterns.add(new TokenPattern("SREQUAL", ">>="));
        patterns.add(new TokenPattern("IDENTICAL", "==="));
        patterns.add(new TokenPattern("NOTIDENTICAL", "!=="));
        patterns.add(new TokenPattern("EQUAL", "=="));
        patterns.add(new TokenPattern("ASSIGN", "="));
        patterns.add(new TokenPattern("NOTEQUAL", "!="));
        patterns.add(new TokenPattern("NOT", "!"));
        patterns.add(new TokenPattern("BOOLEANAND", "&&"));
        patterns.add(new TokenPattern("BOOLEANOR", "\\|\\|"));
        patterns.add(new TokenPattern("BINAND", "&"));
        patterns.add(new TokenPattern("BINOR", "\\|"));
        patterns.add(new TokenPattern("BINNOT", "~"));
        patterns.add(new TokenPattern("ELLIPSIS", "\\.\\.\\."));
        patterns.add(new TokenPattern("MINUS", "\\-"));
        patterns.add(new TokenPattern("PLUS", "\\+"));
        patterns.add(new TokenPattern("MUL", "\\*"));
        patterns.add(new TokenPattern("CONCAT", "\\."));
        patterns.add(new TokenPattern("DIV", "/"));
        patterns.add(new TokenPattern("MOD", "%"));
        patterns.add(new TokenPattern("XOR", "\\^"));
        patterns.add(new TokenPattern("SPACESHIP", "<=>"));
        patterns.add(new TokenPattern("SL", "<<"));
        patterns.add(new TokenPattern("SR", ">>"));
        patterns.add(new TokenPattern("GREATEROREQUAL", ">="));
        patterns.add(new TokenPattern("GREATER", ">"));
        patterns.add(new TokenPattern("LESSOREQUAL", "<="));
        patterns.add(new TokenPattern("LESS", "<"));
        patterns.add(new TokenPattern("SEMICOLON", ";"));
        patterns.add(new TokenPattern("DOUBLECOLON", "::"));
        patterns.add(new TokenPattern("COLON", ":"));
        patterns.add(new TokenPattern("QMARK", "\\?"));
        patterns.add(new TokenPattern("COMMA", ","));
        patterns.add(new TokenPattern("LOGICALOR", "or\b"));
        patterns.add(new TokenPattern("LOGICALAND", "and\b"));
        patterns.add(new TokenPattern("LOGICALXOR", "xor\b"));
        patterns.add(new TokenPattern("PARENTHESISOPEN", "\\("));
        patterns.add(new TokenPattern("PARENTHESISCLOSE", "\\)"));
        patterns.add(new TokenPattern("BRACKETOPEN", "\\["));
        patterns.add(new TokenPattern("BRACKETCLOSE", "\\]"));
        patterns.add(new TokenPattern("BRACEOPEN", "\\{"));
        patterns.add(new TokenPattern("BRACECLOSE", "\\}"));
        patterns.add(new TokenPattern("NEW", "new\b"));
        patterns.add(new TokenPattern("CLONE", "clone\b"));
        patterns.add(new TokenPattern("EXIT", "exit\b"));
        patterns.add(new TokenPattern("IF", "if\b"));
        patterns.add(new TokenPattern("ELSEIF", "elseif\b"));
        patterns.add(new TokenPattern("ELSE", "else\b"));
        patterns.add(new TokenPattern("ENDIF", "endif\b"));
        patterns.add(new TokenPattern("ECHO", "echo\b"));
        patterns.add(new TokenPattern("DO", "do\b"));
        patterns.add(new TokenPattern("WHILE", "while\b"));
        patterns.add(new TokenPattern("ENDWHILE", "endwhile\b"));
        patterns.add(new TokenPattern("FOR", "for\b"));
        patterns.add(new TokenPattern("ENDFOR", "endfor\b"));
        patterns.add(new TokenPattern("FOREACH", "foreach\b"));
        patterns.add(new TokenPattern("ENDFOREACH", "endforeach\b"));
        patterns.add(new TokenPattern("DECLARE", "declare\b"));
        patterns.add(new TokenPattern("ENDDECLARE", "enddeclare\b"));
        patterns.add(new TokenPattern("AS", "as\b"));
        patterns.add(new TokenPattern("SWITCH", "switch\b"));
        patterns.add(new TokenPattern("ENDSWITCH", "endswitch\b"));
        patterns.add(new TokenPattern("CASE", "case\b"));
        patterns.add(new TokenPattern("DEFAULT", "default\b"));
        patterns.add(new TokenPattern("BREAK", "break\b"));
        patterns.add(new TokenPattern("CONTINUE", "continue\b"));
        patterns.add(new TokenPattern("GOTO", "goto\b"));
        patterns.add(new TokenPattern("FUNCTION", "function\b"));
        patterns.add(new TokenPattern("CONST", "const\b"));
        patterns.add(new TokenPattern("RETURN", "return\b"));
        patterns.add(new TokenPattern("TRY", "try\b"));
        patterns.add(new TokenPattern("CATCH", "catch\b"));
        patterns.add(new TokenPattern("FINALLY", "finally\b"));
        patterns.add(new TokenPattern("THROW", "throw\b"));
        patterns.add(new TokenPattern("USE", "use\b"));
        patterns.add(new TokenPattern("INSTEADOF", "insteadof\b"));
        patterns.add(new TokenPattern("GLOBAL", "global\b"));
        patterns.add(new TokenPattern("STATIC", "static\b"));
        patterns.add(new TokenPattern("ABSTRACT", "abstract\b"));
        patterns.add(new TokenPattern("FINAL", "final\b"));
        patterns.add(new TokenPattern("PRIVATE", "private\b"));
        patterns.add(new TokenPattern("PROTECTED", "protected\b"));
        patterns.add(new TokenPattern("PUBLIC", "public\b"));
        patterns.add(new TokenPattern("VAR", "var\b"));
        patterns.add(new TokenPattern("UNSET", "unset\b"));
        patterns.add(new TokenPattern("ISSET", "isset\b"));
        patterns.add(new TokenPattern("EMPTY", "empty\b"));
        patterns.add(new TokenPattern("HALTCOMPILER", "__halt_compiler\b"));
        patterns.add(new TokenPattern("CLASS", "class\b"));
        patterns.add(new TokenPattern("TRAIT", "trait\b"));
        patterns.add(new TokenPattern("INTERFACE", "interface\b"));
        patterns.add(new TokenPattern("EXTENDS", "extends\b"));
        patterns.add(new TokenPattern("IMPLEMENTS", "implements\b"));
        patterns.add(new TokenPattern("LIST", "list\b"));
        patterns.add(new TokenPattern("ARRAY", "array\b"));
        patterns.add(new TokenPattern("CALLABLE", "callable\b"));
        patterns.add(new TokenPattern("LINE", "__LINE__\b"));
        patterns.add(new TokenPattern("FILE", "__FILE__\b"));
        patterns.add(new TokenPattern("DIR", "__DIR__\b"));
        patterns.add(new TokenPattern("CLASSC", "__CLASS__\b"));
        patterns.add(new TokenPattern("TRAITC", "__TRAIT__\b"));
        patterns.add(new TokenPattern("METHODC", "__METHOD__\b"));
        patterns.add(new TokenPattern("FUNCC", "__FUNCTION__\b"));
        patterns.add(new TokenPattern("NSC", "__NAMESPACE__\b"));
        patterns.add(new TokenPattern("TRUE", "true\b"));
        patterns.add(new TokenPattern("FALSE", "false\b"));
        patterns.add(new TokenPattern("NULL", "null\b"));
        patterns.add(new TokenPattern("NAMESPACE", "namespace\b"));
        patterns.add(new TokenPattern("SELF", "self\b"));
        patterns.add(new TokenPattern("INSTANCEOF", "instanceof\b"));
        patterns.add(new TokenPattern("PARENT", "parent\b"));
        patterns.add(new TokenPattern("YIELDFROM", "yield\\s+from\b"));
        patterns.add(new TokenPattern("YIELD", "yield\b"));
        patterns.add(new TokenPattern("DIE", "die\b"));
        patterns.add(new TokenPattern("PRINT", "print\b"));
        patterns.add(new TokenPattern("INCLUDE", "include\b"));
        patterns.add(new TokenPattern("INCLUDEONCE", "include_once\b"));
        patterns.add(new TokenPattern("WHITESPACE", "\\s+"));

    }


    private CharSequence input;
    private int where = 0;
    private final int[] lineStartOffsets;


    private Location locationOf(int offset) {
        for (int ln = 0; ln < lineStartOffsets.length; ++ln) {
            int curr = lineStartOffsets[ln];
            if (curr == offset)
                return new Location(ln, offset - curr);

            if (curr > offset) {
                int col0 = lineStartOffsets[ln - 1];
                return new Location(ln - 1, offset - col0);
            }
        }

        assert false;
        return null;
    }



    public Lexer(String s) {
        fillPatterns();
        input = s;

        Vector<Integer> ints = new Vector<Integer>();
        ints.add(0);

        CharSequence cs = input;
        for (int offset = 0; offset < cs.length(); ++offset) {
            char c = cs.charAt(offset);
            if (c != '\n')
                continue;

            ints.add(offset + 1);
        }

        ints.add(cs.length());

        this.lineStartOffsets = new int[ints.size()];
        int ln = -1;
        for (int curr : ints) {
            ++ln;
            lineStartOffsets[ln] = curr;
        }
    }

    public Lexer(InputStream is) throws IOException {
        this(new InputStreamReader(is));
    }

    private static String makeStr(Reader r) throws IOException {
        StringBuilder sb = new StringBuilder();

        while (true) {
            int n = r.read();
            if (n < 0)
                break;

            char c = (char) n;
            sb.append(c);
        }

        return sb.toString();
    }

    public Lexer(Reader r) throws IOException {
        this(makeStr(r));
    }

    public Token lex() {
        for (TokenPattern p: patterns) {
            Token t = next(p.pattern, p.name);
            if (t != null) {
                return t;
            }
        }
        return null;
    }

    public Token next(Pattern p, String name) {
        Matcher m = p.matcher(input);
        boolean b = m.find();
        if (!b)
            return null;

        MatchResult mr = m.toMatchResult();
        if (m.start() != 0)
            return null;

        String s = input.subSequence(mr.start(), mr.end()).toString();
        Token result = new Token(s, name, locationOf(where + mr.start()),
                locationOf(where + mr.end()));

        input = input.subSequence(mr.end(), input.length());
        where += mr.end();

        return result;
    }
    public boolean finished() {
        return input.length() == 0;
    }

    public Token next(String regexp, String name) {
        return next(Pattern.compile(regexp), name);
    }

    public Token next(TokenPattern tp) {
        return next(Pattern.compile(tp.pattern), tp.name);
    }
}