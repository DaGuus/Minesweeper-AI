package minesweeper.ai.policyGradient;

import java.util.Objects;

public class Action {

    private final int x;
    private final int y;
    private final Type type;

    public Action(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Action{" +
                "x=" + x +
                ", y=" + y +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return x == action.x &&
                y == action.y &&
                type == action.type;
    }

    @Override
    public int hashCode() {

        return Objects.hash(x, y, type);
    }
}

enum Type {
    FLAG, TEST
}
