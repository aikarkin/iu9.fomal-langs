package ru.bmstu.iu9.javascript.parser.states;

import ru.bmstu.iu9.javascript.parser.StateTable;

public class Reduce implements IState{
    public StateTable.Rule rule;

    @Override
    public StateType getType() {
        return StateType.ACCEPTED;
    }

    public Reduce(StateTable.Rule rule) {
        this.rule = rule;
    }

    @Override
    public String toString() {
        return "reduce("+ rule.toString() +")";
    }
}
