import ru.bmstu.iu9.grammar.utils.CFGrammarUtils;
import ru.bmstu.iu9.grammar.utils.RuleToken;
import ru.bmstu.iu9.grammar.utils.RuleTokenSequence;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

public class GenerateRules {
    public static void main(String[] args) {
        try {
            byte[] readBytes = Files.readAllBytes(Paths.get(args[0]));
            String source = new String(readBytes, StandardCharsets.UTF_8);
            Map<String, LinkedList<RuleTokenSequence>> grammar = CFGrammarUtils.parseGrammar(source);

            System.out.println(
                /*"public final class Rules {" +*/
                "Rule[] rules = new Rule[] {"
            );

            for(String nonterm : grammar.keySet()) {
                for (RuleTokenSequence rsTokSeq : grammar.get(nonterm)) {
                    ArrayList<String> ruleTokensInits = new ArrayList<>();

                    for(RuleToken tok : rsTokSeq) {
                        String valuleLiteral = tok.value, typeStr = "StateType.";
                        if (tok.type == RuleToken.Type.NON_TERM) {
                            valuleLiteral = "\"" + valuleLiteral + "\"";
                            typeStr += "NON_TERM";
                        } else {
                            typeStr += "TERM";
                            if(!tok.value.startsWith("\"")) {
                                valuleLiteral = "\"@" + valuleLiteral + "\"";
                            }
                        }


                        ruleTokensInits.add(String.format("new RuleToken(%s, %s)", valuleLiteral, typeStr));
                    }

                    System.out.printf("new Rule(\n\"%s\", \nnew RuleToken[] {%s}\n),\n", nonterm, String.join(", ", ruleTokensInits));
                }
            }

            System.out.println("};" /*+ "}"*/);
        } catch (IOException e) {
            System.out.println("#ERROR: Cannot read file: " + args[0]);
            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println("#ERROR: Cannot parse grammar.");
            e.printStackTrace();
        }
    }
}
