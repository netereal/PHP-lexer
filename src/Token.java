public class Token {


    public Token(String image, String id, Location begin, Location end) {
        this.id = id;
        this.data = image;
        this.begin = begin;
        this.end = end;
    }

    public final String id;
    public final String data;
    public final Location begin;
    public final Location end;

    public String toString() {
        return "Token[ " + "id=" + id + " " + "token " + data +  " " + begin + ".." + end + "]";
    }
}
