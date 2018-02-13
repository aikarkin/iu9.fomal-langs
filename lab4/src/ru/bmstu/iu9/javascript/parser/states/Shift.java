package ru.bmstu.iu9.javascript.parser.states;


public class Shift implements IState {
    public int state;

    public Shift(int state) {
        this.state = state;
    }

    @Override
    public StateType getType() {
        return StateType.SHIFT;
    }

    @Override
    public String toString() {
        return "shift(" + state + ")";
    }
}
