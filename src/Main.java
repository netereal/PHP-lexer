import java.io.File;
import java.io.FileInputStream;

public class Main {

    public static void main(String[] args) throws Exception {
        File file = new File("data.php");
        FileInputStream in = new FileInputStream(file);

        Lexer l = new Lexer(in);
        while (true) {
            Token x = l.next();
            if (x ==null) {
                System.out.println("null");

                break;
            }
            System.out.println(x);

        }
        // error handling
        System.out.println("Error");

    }
}
