package ru.bmstu.iu9.javascript.parser;
import ru.bmstu.iu9.grammar.utils.RuleToken;
import ru.bmstu.iu9.grammar.utils.RuleTokenSequence;
import ru.bmstu.iu9.javascript.parser.states.*;

import java.text.ParseException;
import java.util.*;

public class StateTable {
    private final static RuleToken EOF = new RuleToken("$", RuleToken.Type.TERM);
    private final static RuleToken START = new RuleToken("__START__", RuleToken.Type.NON_TERM);
    private HashMap<RuleToken, Integer> symbols;
    private ArrayList<Rule> rules;
    private ArrayList<ArrayList<IState>> table;
    private ArrayList<HashMap<Integer, Integer>> transitions;
    private int statesCount;


    public StateTable(LinkedHashMap<String, LinkedList<RuleTokenSequence>> grammar) throws Exception {
        this.symbols = new HashMap<>();
        this.rules = new ArrayList<>();
        this.transitions = new ArrayList<>();
        this.statesCount = 0;

        int symbolNum = 0;
        int rulesNum = 0;
        Rule curRule;

        symbols.put(START, symbolNum++);

        for (String nonterm : grammar.keySet()) {
            RuleToken left = new RuleToken(nonterm, RuleToken.Type.NON_TERM);

            if (symbolNum == 1) {
                RuleTokenSequence ftRight = new RuleTokenSequence();
                ftRight.add(new RuleToken(nonterm, RuleToken.Type.NON_TERM));
                curRule = new Rule(START, ftRight);
                curRule.index = rulesNum++;
                rules.add(curRule);
            }

            if (!symbols.containsKey(left)) {
                symbols.put(left, symbolNum++);
            }

            for (RuleTokenSequence tokSeq : grammar.get(nonterm)) {
                curRule = new Rule(left, tokSeq);
                curRule.index = rulesNum++;
                rules.add(curRule);

                for (RuleToken tok : tokSeq) {
                    if (!symbols.containsKey(tok)) {
                        symbols.put(tok, symbolNum++);
                    }
                }
            }
        }

        symbols.put(EOF, symbolNum);

        buildLR1Table();
    }

    private ArrayList<RuleToken> firstTerms(RuleToken tok) {
        ArrayList<RuleToken> retTerms = new ArrayList<>();
        if(tok.type == RuleToken.Type.TERM) {
            retTerms.add(tok);
            return retTerms;
        }

        for(Rule rule : rules) {
            if(rule.left.equals(tok)) {
                RuleToken rft = rule.right.get(0);
                if(rft.type == RuleToken.Type.TERM) {
                    retTerms.add(rft);
                } else {
                    retTerms.addAll(firstTerms(rft));
                }
            }
        }

        return retTerms;
    }

    private void addTransition(int state, RuleToken symbol, int nextState) {
        if(state >= transitions.size()) {
            for (int i = transitions.size(); i <= state; i++) {
                transitions.add(new HashMap<>());
            }
        }
        transitions.get(state).put(symbols.get(symbol), nextState);
    }

    public int getTransition(int state, RuleToken symbol) throws Exception {
        if(!symbols.containsKey(symbol)) {
            throw new Exception("An error occurred while building LR1 transition table - unknown symbol \"" + symbol.value + "\"");
        }

        int idx = symbols.get(symbol);
        if(state >= transitions.size() || !transitions.get(state).containsKey(idx)) {
            return -1;
        }

        return transitions.get(state).get(idx);
    }

    private RuleToken tokenForFirst(Item item) {
        RuleToken nextMarked = item.marker + 1 < item.rule.right.size() ? item.rule.right.get(item.marker + 1) : null;

        if(nextMarked == null) {
            RuleToken beforeMarked = item.marker - 1 >= 0 ? item.rule.right.get(item.marker - 1) : null;
            if(beforeMarked == null) {
                return EOF;
            } else {
                return beforeMarked;
            }
        } else {
            return nextMarked;
        }
    }

    private ArrayList<Item> closure(ArrayList<Item> items) {
        ArrayList<Item> retItems = new ArrayList<>();
        retItems.addAll(items);
        if(retItems.size() == 0) {
            return retItems;
        }
        boolean changed = false;

        while(!changed) {
            changed = false;
            for(Item item : items) {
                RuleToken marked = item.markedToken();

                if(marked != null && marked.type == RuleToken.Type.NON_TERM) {
                    for(Rule rule : rules) {
                        if(rule.left.equals(marked)) {
                            RuleToken forFirst = tokenForFirst(item);
                            for (RuleToken term : firstTerms(forFirst)) {
                                Item newItem = new Item(rule, 0);
                                newItem.symbol = term;
                                retItems.add(newItem);
                                changed = true;
                            }
                        }
                    }
                }
            }
        }


        return retItems;
    }

    private ArrayList<Item> shiftItems(ArrayList<Item> items, RuleToken symbol) {
        ArrayList<Item> shiftItems = new ArrayList<>();

        for (Item item : items) {
            RuleToken marked = item.markedToken();
            if (marked != null && item.markedToken().equals(symbol)) {
                Item newItem = new Item(item.rule, item.marker + 1);
                newItem.symbol = item.symbol;
                shiftItems.add(newItem);
            }
        }

        return closure(shiftItems);
    }

    private ArrayList<ArrayList<Item>> items() {
        ArrayList<ArrayList<Item>> retFamilies = new ArrayList<>();
        boolean changed = true;
        int i = 0;

        ArrayList<Item> startItems = new ArrayList<>();
        Item startItem = new Item(rules.get(0), 0);
        startItem.symbol = EOF;
        startItems.addAll(closure(new ArrayList<>(Collections.singletonList(startItem))));
        retFamilies.add(startItems);

        while(changed) {
            changed = false;
            for (int k = i; k < retFamilies.size();  k++) {
                ArrayList<Item> items = retFamilies.get(k);
                for(RuleToken symbol : symbols()) {
                    ArrayList<Item> shifted = shiftItems(items, symbol);
                    if(shifted.size() > 0) {
                        addTransition(k, symbol, statesCount);
                        retFamilies.add(shifted);
                        statesCount++;
                        changed = true;
                    }
                }
            }
            i++;
        }

        return retFamilies;
    }

    private void buildLR1Table() throws Exception {
        ArrayList<ArrayList<Item>> itemFamilies = items();
        table = new ArrayList<>(statesCount);
        int symbolsCount = symbols.size();

        // init lr1-table by ERROR state
        for (int i = 0; i < statesCount; i++) {
            ArrayList<IState> rows = new ArrayList<>(symbolsCount);
            for (int j = 0; j < symbolsCount; j++) {
                rows.add(new SimpleState(StateType.ERROR));
            }
        }

        //for(ArrayList<Item> items : itemFamilies) {
        for (int i = 0; i < itemFamilies.size(); i++) {
            ArrayList<Item> items = itemFamilies.get(i);

            for(Item item : items) {
                RuleToken marked = item.markedToken();
                if(marked == null && !item.rule.left.equals(START)) {
                    setState(i, item.symbol, new Reduce(item.rule));
                } else if(marked != null && marked.type == RuleToken.Type.TERM) {
                    setState(i, marked, new Shift(getTransition(i, marked)));
                } else if(item.rule.left.equals(START) && item.symbol.equals(EOF)) {
                    setState(i, EOF, new SimpleState(StateType.ACCEPTED));
                }
            }
        }
    }

    public int statesCount() {
        return statesCount;
    }

    public Set<RuleToken> symbols() {
        return symbols.keySet();
    }

    public Rule getRule(int index) {
        return this.rules.get(index);
    }

    public IState getState(int stateIdx, RuleToken token) throws ParseException, IndexOutOfBoundsException {
        if (!symbols.containsKey(token)) {
            throw new ParseException("Unexpected token " + token.value, 0);
        }
        if (stateIdx < statesCount) {
            throw new IndexOutOfBoundsException("State index out of bond");
        }

        return table.get(symbols.get(token)).get(stateIdx);
    }

    private void setState(int stateIdx, RuleToken token, IState state) {
        int symbolsStates = table.get(symbols.get(token)).size();
        if (stateIdx >= symbolsStates) {
            for (int i = symbolsStates; i <= stateIdx; i++) {
                table.get(symbols.get(token)).add(new SimpleState(StateType.ERROR));
            }
        }

        this.table.get(symbols.get(token)).set(stateIdx, state);
    }

    public class Rule {
        RuleToken left;
        RuleTokenSequence right;
        int index;

        Rule(RuleToken left, RuleTokenSequence right) {
            this.left = left;
            this.right = right;
            this.index = 0;
        }

        @Override
        public String toString() {
            ArrayList<String> tokStrings = new ArrayList<>();

            for(RuleToken tok : right) {
                tokStrings.add(tok.value);
            }

            return left.value + " -> " + String.join(" ", tokStrings);
        }
    }

    private class Item {
        int marker;
        Rule rule;
        RuleToken symbol;

        Item(Rule rule, int marker) {
            this.rule = rule;
            this.marker = marker;
        }

        RuleToken markedToken() {
            return (marker < rule.right.size()) ? rule.right.get(marker) : null;
        }
    }

    @Override
    public String toString() {
        ArrayList<String> symbolStrings = new ArrayList<>();
        StringBuilder strBuilder = new StringBuilder();
        for(RuleToken symbol : symbols()) {
            symbolStrings.add("\"" + symbol.value + "\"");
        }
        strBuilder.append(String.join(";", symbolStrings)).append("\n");
        ArrayList<String> states;
        for (int i = 0; i < statesCount; i++) {
            states = new ArrayList<>();
            for(RuleToken symbol : symbols()) {
                try {
                    states.add("\"" + getState(i, symbol).toString() + "\"");
                } catch (ParseException e) {
                    states.add("\"error\"");
                }
            }
            strBuilder.append(String.join(";", states)).append("\n");
        }

        return strBuilder.toString();
    }
}
