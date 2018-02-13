package ru.bmstu.iu9.javascript.parser;

import ru.bmstu.iu9.grammar.utils.CFGrammarUtils;
import ru.bmstu.iu9.grammar.utils.RuleToken;
import ru.bmstu.iu9.grammar.utils.RuleTokenSequence;
import ru.bmstu.iu9.javascript.lexer.Token;
import ru.bmstu.iu9.javascript.lexer.TokenIterator;
import ru.bmstu.iu9.javascript.parser.states.Reduce;

import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Stack;

public class Parser {
    private TokenIterator tokIter;
    private Token curToken;
    private StateTable fsm;
    private Stack<Integer> stack;
    private boolean isEof;

    private boolean hasToken() {
        return !tokIter.hasNext() && !isEof || tokIter.hasNext();
    }

    private void throwParseError() throws ParseException {
        String msg = String.format(
                "Syntax error: unexpected token %s at line %d, column %d",
                curToken.getToken(),
                curToken.getLine(),
                curToken.getColumn()
        );
        throw new ParseException(msg, curToken.start());
    }

    private RuleToken nextToken() {
        if(!tokIter.hasNext() && !isEof) {
            isEof = true;
            return new RuleToken("$", RuleToken.Type.TERM);
        }

        this.curToken = tokIter.next();

        return new RuleToken(curToken.getToken(), RuleToken.Type.TERM);
    }

    public Parser(TokenIterator tokens) {
        this.tokIter = tokens;
        this.stack = new Stack<>();
        this.fsm = null;
        this.isEof = false;
    }

    public void buildFSM(String grammarSource) throws Exception {
        LinkedHashMap<String, LinkedList<RuleTokenSequence>> grammar = CFGrammarUtils.parseGrammar(grammarSource);
        this.fsm = new StateTable(grammar);
    }

    public void parse() throws Exception {
        while(hasToken()) {
            Integer state = stack.peek();
            RuleToken tok = nextToken();

            switch(fsm.getState(state, tok).getType()) {
                case SHIFT:
                    stack.push(state);
                    break;
                case REDUCE:
                    Reduce rs = (Reduce)fsm.getState(state, tok);

                    for (int i = 0; i < rs.rule.right.size(); i++) {
                        stack.pop();
                    }

                    int s = stack.peek();
                    stack.push(fsm.getTransition(s, rs.rule.left));
                case ACCEPTED:
                    return;
                default:
                    throwParseError();
            }
        }
    }

    public void printFSM() {
        System.out.println(fsm.toString());
    }
}
