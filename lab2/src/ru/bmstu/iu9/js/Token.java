package ru.bmstu.iu9.js;


public class Token {
    private TokenType type;
    private String value;
    private int raw;
    private int col;
    private int pos;

    public Token(String value, TokenType type) {
        this.type = type;
        this.value = value;
    }

    public void setPosition(int lineNumber, int columnNumber, int pos) {
        this.raw = lineNumber + 1;
        this.col = columnNumber + 1;
        this.pos = pos;
    }

    public TokenType getType() {
        return type;
    }

    public String getToken() {
        return value;
    }

    public int getLine() {
        return this.raw;
    }

    public int getColumn() {
        return this.col;
    }

    public int start() {
        return pos;
    }

    public int end() {
        return pos + value.length();
    }

    public String toString() {
        return String.format(
                "{token: \"%s\", type: %s, position: %d:%d}",
                value.equals(System.lineSeparator()) ? "\\n" : value.replaceAll("\"", "\\\\\""),
                type,
                raw,
                col
        );
    }
}
