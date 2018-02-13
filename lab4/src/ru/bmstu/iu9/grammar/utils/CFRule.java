package ru.bmstu.iu9.grammar.utils;

import java.util.LinkedList;

public class CFRule {
    public RuleToken left;
    public LinkedList<RuleTokenSequence> alternatives;

    public CFRule(RuleToken leftTerm, LinkedList<RuleTokenSequence> rightRules) {
        this.left = leftTerm;
        this.alternatives = rightRules;
    }
}
