public class Token {


    public Token(String image, int id, Location begin, Location end) {
        this.id = id;
        this.image = image;
        this.begin = begin;
        this.end = end;
    }

    public final int id;
    public final String image;
    public final Location begin;
    public final Location end;

    public String toString() {
        return "Token['" + image + "' id=" + id + " " + begin + ".." + end + "]";
    }
}
