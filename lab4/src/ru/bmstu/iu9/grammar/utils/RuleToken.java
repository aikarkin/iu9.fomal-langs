package ru.bmstu.iu9.grammar.utils;

public class RuleToken {
    public String value;
    public Type type;

    public RuleToken(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    public enum Type {
        TERM,
        NON_TERM
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof RuleToken))
            return false;
        RuleToken other = (RuleToken)obj;
        return (type == other.type) && (value.equals(other.value));
    }

    @Override
    public String toString() {
        return value;
    }
}
