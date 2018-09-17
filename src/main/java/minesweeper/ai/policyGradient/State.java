package minesweeper.ai.policyGradient;

import minesweeper.game.Tile;

import java.util.Arrays;

public class State {

    private int[] surroundings;
    private boolean[] flags;

    public State(Tile[] tiles) {
        surroundings = new int[tiles.length];
        flags = new boolean[tiles.length];

        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i].isClicked()) {
                surroundings[i] = tiles[i].getSurroundingMines();
            } else {
                surroundings[i] = -1;
            }
            flags[i] = tiles[i].hasFlag();
        }
    }

    public int[] getSurroundings() {
        return surroundings;
    }

    public boolean[] getFlags() {
        return flags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return Arrays.equals(surroundings, state.surroundings) &&
                Arrays.equals(flags, state.flags);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(surroundings);
        result = 31 * result + Arrays.hashCode(flags);
        return result;
    }
}
