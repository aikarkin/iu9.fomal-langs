import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import ru.bmstu.iu9.javascript.antrl.JavaScriptLexer;
import ru.bmstu.iu9.javascript.antrl.JavaScriptParser;
import ru.bmstu.iu9.javascript.lexer.Lexer;
import ru.bmstu.iu9.javascript.parser.Parser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ParserTest1 {
    public static void main(String[] args) {
        String file = "";
        try {
            file = args[0];
            byte[] readBytes = Files.readAllBytes(Paths.get(file));
            String program = new String(readBytes, StandardCharsets.UTF_8);
            ANTLRStringStream tokStream = new ANTLRStringStream(program);
            JavaScriptLexer lexer = new JavaScriptLexer(tokStream);
            JavaScriptParser parser = new JavaScriptParser(new CommonTokenStream(lexer));
            System.out.println("Parsing has executed successfully");
        } catch (IOException e) {
            System.out.println("#ERROR: Cannot read file " + file);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
