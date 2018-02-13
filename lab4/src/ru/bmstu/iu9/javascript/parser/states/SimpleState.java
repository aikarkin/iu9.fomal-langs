package ru.bmstu.iu9.javascript.parser.states;

public class SimpleState implements IState {
    private StateType type;

    public SimpleState(StateType type) {
        this.type = type;
    }

    @Override
    public StateType getType() {
        return type;
    }

    @Override
    public String toString() {
        switch(type) {
            case ACCEPTED:
                return "accepted";
            case SHIFT:
                return "shift";
            case REDUCE:
                return "reduce";
            default:
                return "error";
        }
    }
}
