import java.io.*;
import java.util.Vector;
import java.util.regex.*;

public class Lexer {
    static String PATTERNS = ""
            + "(?<COMMENT>(?:#|//)[^\\r\\n]*|/\\*[\\s\\S]*?\\*/)|"
            + "(?<CONSTSTRING>[a-zA-Z_\\x7f-\\xff][a-zA-Z0-9_\\x7f-\\xff]*)|"
            + "(?<EVAL>eval\b)|"
            + "(?<INCLUDE>include\b)|"
            + "(?<INCLUDEONCE>include_once\b)|"
            + "(?<REQUIRE>require\b)|"
            + "(?<REQUIREONCE>require_once\b)|"
            + "(?<OPENTAGWITHECHO><(\\?|%)=)|"
            + "(?<OPENTAG>(<\\?php|<\\?|<%)\\s?)|"
            + "(?<CLOSETAG>\\?>|%>)|"
            +"(?<CAST>\\((real|double|float|bool|boolean|array|string|unset|object|int)\\))|"


            + "(?<HEXNUMBER>0x[0-9a-fA-F]+(?:[eE][\\\\+\\\\-]?[0-9]+)?)|"
            +"(?<OCTNUMBER>0[0-9]+(?:[eE][\\+\\-]?[0-9]+)?)|"
            + "(?<FLOATNUMBER>([0-9]*\\.[0-9]+|[0-9]+\\.[0-9]*)(?:[eE][\\+\\-]?[0-9]+)?)|"
            + "(?<INTNUMBER>(?:0|[1-9][0-9]*)(?:[eE][\\+\\-]?[0-9]+)?)|"
            + "(?<VARIABLE>\\$[a-zA-Z_\\x7f-\\xff][a-zA-Z0-9_\\x7f-\\xff]*\\b)|"

            +"(?<NS>\\\\)|"
            +"(?<OBJECTOPERATOR>\\->)|"
            +"(?<DOUBLEARROW>=>)|"
            +"(?<INCREMENT>\\+\\+)|"
            +"(?<DECREMENT>\\-\\-)|"
            +"(?<COALESCE>\\?\\?)|"
            +"(?<POW>\\*\\*)|"
            +"(?<AT>@)|"

            +"(?<PLUSEQUAL>\\+=)|"
            +"(?<MINUSEQUAL>\\-=)|"
            +"(?<MULEQUAL>\\*=)|"
            +"(?<DIVEQUAL>/=)|"
            +"(?<CONCATEQUAL>\\.=)|"
            +"(?<MODEQUAL>%=)|"
            +"(?<ANDEQUAL>&=)|"
            +"(?<OREQUAL>\\|=)|"
            +"(?<XOREQUAL>\\^=)|"
            +"(?<SLEQUAL><<=)|"
            +"(?<SREQUAL>>>=)|"
            +"(?<IDENTICAL>===)|"
            +"(?<NOTIDENTICAL>!==)|"


            +"(?<EQUAL>==)|"
            +"(?<ASSIGN>=)|"
            +"(?<NOTEQUAL>!=)|"
            +"(?<NOT>!)|"
            +"(?<BOOLEANAND>&&)|"
            +"(?<BOOLEANOR>\\|\\|)|"
            +"(?<BINAND>&)|"
            +"(?<BINOR>\\|)|"


            + "(?<WHITESPACE>\\s+)"

            + "";



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

    public Token next() {
        return next(PATTERNS);
    }

    public Token next(Pattern p) {
        Matcher m = p.matcher(input);
        boolean b = m.find();
        if (!b)
            return null;

        MatchResult mr = m.toMatchResult();
        if (m.start() != 0)
            return null;

        String s = input.subSequence(mr.start(), mr.end()).toString();
        Token result = new Token(s, 0, locationOf(where + mr.start()),
                locationOf(where + mr.end()));

        input = input.subSequence(mr.end(), input.length());
        where += mr.end();

        return result;
    }
    public boolean finished() {
        return input.length() == 0;
    }

    public Token next(String regexp) {
        return next(Pattern.compile(regexp));
    }
}