package ru.bmstu.iu9.javascript.lexer;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TokenIterator extends Throwable implements Iterator<Token>, Iterable<Token> {
    private static final Set<String> keywords = new HashSet<>(Arrays.asList(
            "break",    "do",       "instanceof",
            "typeof",       "case",     "else",
            "var",          "catch",    "finally",
            "void",         "continue", "for",
            "switch",       "while",    "debugger",
            "function",     "this",     "with",
            "default",      "if",       "throw",
            "delete",       "in",       "try",
            "new",          "return"
    ));

    private static final ArrayList<Pattern> toksPtrs = new ArrayList<Pattern>() {{
        add(Pattern.compile("(/\\*.*?\\*/)|(//.*?\r?\n)", Pattern.DOTALL)); // comments
        add(Pattern.compile("\r?\n",  Pattern.MULTILINE)); // newline
        add(Pattern.compile("(?<!\\\\)\".*?(?<!\\\\)\"")); // string literal
        add(Pattern.compile("null")); // null literal
        add(Pattern.compile("true|false")); // boolean literal
        add(Pattern.compile("(0[xb][a-fA-F0-9]+)|([0-9]*((\\.[0-9]+)([eE][+\\-]?[0-9]+)?)?)")); // numeric literal
        add(Pattern.compile("[/].*?[/][gimy]*")); // regexp literal
        add(Pattern.compile("[a-zA-F_$][\\w$]*")); // identifier
        // punctuator
        add(Pattern.compile("(>>>=)|(===)|(!==)|(>>>)|(<<=)|(>>=)|(<=)|(>=)|(==)|(!=)|(\\+{2})|(--)|(<<)|(>>)|(&&)|([|]{2})|(\\+=)|(-=)|(\\*=)|(%=)|(&=)|([|]=)|(\\^=)|[.;,<>+\\-*%&|^!~?:={}()\\[\\]]"));
    }};
    private String source;
    private int raw, col;
    private int pos;

    public TokenIterator(String source) {
        this.pos = 0;
        this.source = source;
        this.raw = 0;
        this.col = 0;
    }

    @Override
    public boolean hasNext() {
        return this.pos < source.length();
    }

    @Override
    public Token next() {
        String tokStr = "";
        int rawOffset = 0, colOffset = 0;
        String remStr = source.substring(pos);

        // skip whitespaces
        Matcher whiteSpaces = Pattern.compile("[ \t\f]+").matcher(remStr);
        if(whiteSpaces.lookingAt() && !whiteSpaces.group().isEmpty()) {
            colOffset +=  whiteSpaces.group().length();
            pos += colOffset;
            remStr = source.substring(pos);
        }

        int pos0 = pos;

        // check for multiline comments
        Matcher m = toksPtrs.get(0).matcher(remStr);
        if(m.lookingAt() && m.group(1) != null && !m.group(1).isEmpty()) {
            tokStr = m.group(1);
            Token tok = new Token(tokStr, TokenType.COMMENT);
            String[] lines = tokStr.split(System.lineSeparator());
            int n = lines.length - 1;

            tok.setPosition(raw, col, pos0);

            rawOffset += n;
            colOffset += lines[n - 1].length();

            pos += tokStr.length();

            raw += rawOffset;
            col += colOffset;


            return tok;
        }

        Token tok = null;

        // check another regexps
        for (int i = 0; i < toksPtrs.size(); i++) {
            m = toksPtrs.get(i).matcher(remStr);

            if(m.lookingAt() && !m.group().isEmpty()) {
                tokStr = m.group();
                TokenType type = TokenType.values()[i];
                tok = new Token(tokStr, type);

                int n = tokStr.length();
                pos += n;
                colOffset += n;

                if(type == TokenType.NEWLINE) {
                    tok.setPosition(raw, col, pos0);
                    raw += rawOffset + 1;
                    col = 0;

                    return tok;
                }

                break;
            }
        }


        if(tok == null) {
            tok = new Token("" + this.source.charAt(pos), TokenType.UNKNOWN);
            pos++;
        }


        if(tok.getType() == TokenType.IDENTIFIER && keywords.contains(tok.getToken())) {
            tok = new Token(tokStr, TokenType.KEYWORD);
        }

        tok.setPosition(raw, col, pos0);

        raw += rawOffset;
        col += colOffset;

        return tok;
    }

    @Override
    public Iterator<Token> iterator() {
        return new TokenIterator(source);
    }
}
