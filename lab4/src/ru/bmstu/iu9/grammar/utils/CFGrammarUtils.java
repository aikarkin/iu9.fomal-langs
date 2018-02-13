package ru.bmstu.iu9.grammar.utils;

import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CFGrammarUtils {
    public static LinkedHashMap<String, LinkedList<RuleTokenSequence>> parseGrammar(String grammarSource) throws ParseException {
        Pattern emptyLine = Pattern.compile("\\s*");
        LinkedHashMap<String, LinkedList<RuleTokenSequence>> grammarRules = new LinkedHashMap<>();
        String[] lines = grammarSource.split(System.lineSeparator());
        int lc = 0;

        for(String line : lines) {
            Matcher elineMatch = emptyLine.matcher(line);
            if(!elineMatch.matches()) {
                CFRule parsedRule = parseRule(line);
                if(parsedRule != null && parsedRule.left != null && !grammarRules.containsKey(parsedRule.left.value)) {
                    grammarRules.put(parsedRule.left.value, new LinkedList<>());
                }
                assert parsedRule != null;
                assert parsedRule.left != null;
                grammarRules.get(parsedRule.left.value).addAll(parsedRule.alternatives);
            }
        }

        return grammarRules;
    }

    private static CFRule parseRule(String line) throws ParseException {
        Pattern rulePattern = Pattern.compile("(<.*?>)\\s*:=\\s*(.*$)");
        Matcher ruleMatcher = rulePattern.matcher(line);

        Pattern nonTermPattern = Pattern.compile("<(.*?)>");
        Pattern termPattern = Pattern.compile("\"(.*?)\"|[\\w_]+");

        if(!ruleMatcher.lookingAt() || ruleMatcher.groupCount() < 2) {
            throw new ParseException("Cannot matchRule line: \"" + line + "\"", 0);
        }

        String nonTerm = ruleMatcher.group(1);
        String rightSide = ruleMatcher.group(2);
        String[] tokenSequences = rightSide.split("(?<=[\\w_\">])\\s+\\|\\s+(?=[\\w_\"<])");
        LinkedList<RuleTokenSequence> sequencesList = new LinkedList<>();

        for(String tokSeqStr : tokenSequences) {
            String[] tokenStrings = tokSeqStr.split("\\s+");

            RuleTokenSequence tokSeq = new RuleTokenSequence();

            for(String tokStr : tokenStrings) {
                Matcher nontermMatch = nonTermPattern.matcher(tokStr);
                Matcher termMatch = termPattern.matcher(tokStr);

                if (nontermMatch.lookingAt()) {
                    tokSeq.add(new RuleToken(tokStr, RuleToken.Type.NON_TERM));
                } else if (termMatch.lookingAt()) {
                    tokSeq.add(new RuleToken(tokStr, RuleToken.Type.TERM));
                } else {
                    return null;
                }
            }

            sequencesList.add(tokSeq);
        }

        return new CFRule(new RuleToken(nonTerm, RuleToken.Type.NON_TERM), sequencesList);
    }

    public static Map<String, LinkedList<RuleTokenSequence>> greibah(Map<String, LinkedList<RuleTokenSequence>> grammar) {
        final Map<String, Integer> nontermIdx = new HashMap<>();
        int idx = 0, lsIdx, rsIdx;

        for (String nonterm : grammar.keySet()) {
            nontermIdx.put(nonterm, idx++);
        }

        for (int i = 0; i < grammar.keySet().size() - 1; i++) {
            for (String lsNonterm : grammar.keySet()) {
                lsIdx = nontermIdx.get(lsNonterm);
                LinkedList<RuleTokenSequence> rmRules = new LinkedList<>();
                LinkedList<RuleTokenSequence> addedRules = new LinkedList<>();

                for(RuleTokenSequence rsTokSeq : grammar.get(lsNonterm)) {
                    if(rsTokSeq.get(0).type == RuleToken.Type.NON_TERM) {
                        rsIdx = nontermIdx.get(rsTokSeq.get(0).value);
                        if(lsIdx < rsIdx) {
                            addedRules.addAll(greibahProduceNewRules(grammar, rsTokSeq));
                            rmRules.add(rsTokSeq);
                        }
                    }
                }

                grammar.get(lsNonterm).removeAll(rmRules);
                grammar.get(lsNonterm).addAll(addedRules);
//                System.out.println(grammar.getState(lsNonterm));
            }
            System.out.println("tick");
        }
        return grammar;
    }

    private static ArrayList<RuleTokenSequence> greibahProduceNewRules(Map<String, LinkedList<RuleTokenSequence>> grammar, RuleTokenSequence tokenSequence) {
        ArrayList<RuleTokenSequence> retSequences = new ArrayList<>();
        RuleToken fntToken = tokenSequence.get(0);

        for(RuleTokenSequence fntTokSeq : grammar.get(fntToken.value)) {
            RuleTokenSequence newTokSeq = new RuleTokenSequence();
            newTokSeq.add(fntToken);
            System.out.println("To: " + fntToken);
            System.out.println();
            newTokSeq.addAll(fntTokSeq);
            retSequences.add(newTokSeq);
        }

//        System.out.println("Produced getAll: ");
//
//        for(RuleTokenSequence tokSeq : retSequences) {
//            for(RuleToken tok : tokSeq) {
//                System.out.print(tok.value + " ");
//            }
//            System.out.print(" | ");
//        }
//        System.out.println();

        return retSequences;
    }

}
