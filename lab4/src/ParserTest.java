import ru.bmstu.iu9.javascript.lexer.Lexer;
import ru.bmstu.iu9.javascript.parser.Parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

public class ParserTest {
    public static void main(String[] args) {
        String file = "";
        try {
            file = args[0];
            byte[] readBytes = Files.readAllBytes(Paths.get(file));
            String program = new String(readBytes, StandardCharsets.UTF_8);
            file = args[1];
            readBytes = Files.readAllBytes(Paths.get(file));
            String grammar = new String(readBytes, StandardCharsets.UTF_8);
            Lexer lexer = new Lexer(program);
            Parser parser = new Parser(lexer.getTokenIterator());
            parser.buildFSM(grammar);
            parser.parse();
        } catch (IOException e) {
            System.out.println("#ERROR: Cannot read file " + file);
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("#ERROR: Parse exception");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
