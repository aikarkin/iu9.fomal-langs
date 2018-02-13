package ru.bmstu.iu9.javascript.lexer;


import java.util.ArrayList;

public class Lexer {
    private ArrayList<Token> tokens;
    private String source;

    public Lexer(String source) {
        this.source = source;
        this.tokens = null;
    }

    public TokenIterator getTokenIterator() {
        return new TokenIterator(source);
    }

    ArrayList<Token> getTokens() {
        if(tokens == null) {
            tokens = new ArrayList<>();
            for(Token tok : this.getTokenIterator()) {
                tokens.add(tok);
            }
        }

        return tokens;
    }

    public String normalizeSpaces() {
        return source.replaceAll("[ \t\f]+", " ");
    }

    public ArrayList<Token> getIdentifiers() {
        ArrayList<Token> identifiers = new ArrayList<>();

        for(Token tok : getTokens()) {
            if(tok.getType() == TokenType.IDENTIFIER) {
                identifiers.add(tok);
            }
        }

        return identifiers;
    }

    public String removeComments() {
        String clearedSource = this.source;
        for(Token tok : getTokens()) {
            if(tok.getType() == TokenType.COMMENT && tok.getToken().startsWith("/*")) {
                clearedSource = this.source.substring(0, tok.start()) + this.source.substring(tok.end());
            }
        }

        return clearedSource;
    }

    public ArrayList<Token> getErrors() {
        ArrayList<Token> errors = new ArrayList<>();

        if(tokens == null) {
            tokens = getTokens();
        }

        for(Token tok : tokens) {
            if(tok.getType() == TokenType.UNKNOWN)
            errors.add(tok);
        }

        return errors;
    }
}
