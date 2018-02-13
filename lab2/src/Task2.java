import ru.bmstu.iu9.js.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Task2 {
    public static void main(String[] args) {

        try {
            String source;
            List<String> lines;

            if(args.length == 0) {
                lines = new ArrayList<>();
                Scanner sc = new Scanner(System.in);
                while(sc.hasNext()) {
                    lines.add(sc.next());
                }
            } else {
                lines = Files.readAllLines(Paths.get(args[0]));
            }
            source = String.join(System.lineSeparator(), lines);

            System.out.println("SOURCE: ");
            System.out.println("####################\n");
            System.out.println(source);

            Lexer jsLexer = new Lexer(source);

            System.out.println("\n####################");
            System.out.println("TOKENS:\n");

            for(Token tok : jsLexer.getTokenIterator()) {
                System.out.println(tok);
            }

            System.out.println("\n####################");
            System.out.println("ERRORS:\n");

            for(Token error : jsLexer.getErrors()) {
                System.out.println(String.format("Error: Unknown lexeme %s at (%d, %d)", error.getToken(), error.getLine(), error.getColumn()));
            }

            System.out.println("\n####################");
            System.out.println("SOURCE WITH NORMALIZED SPACES:\n");

            System.out.println(jsLexer.normalizeSpaces());

            System.out.println("\n####################");
            System.out.println("SOURCE WITHOUT COMMENTS:\n");

            System.out.println(jsLexer.removeComments());


            System.out.println("\n####################");
            System.out.println("IDENTIFIERS:\n");

            for(Token tok : jsLexer.getIdentifiers()) {
                System.out.println(tok);
            }

        } catch (IOException e) {
            System.out.println("Error: Unable to read from file");
            e.printStackTrace();
        }
    }
}
