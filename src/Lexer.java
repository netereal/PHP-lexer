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
        patterns.add(new TokenPattern("OPENTAG", "(<\\?php|<\\?|<%)\\s?"));
        patterns.add(new TokenPattern("CLOSETAG", "\\?>|%>"));
        patterns.add(new TokenPattern("OPENTAGWITHECHO", "<(\\?|%)="));


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