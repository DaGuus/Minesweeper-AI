package minesweeper.game;

public class Tile {

    private final int x;
    private final int y;
    private boolean hasMine;
    private boolean hasFlag;
    private boolean isClicked;
    private int surroundingMines;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;

        hasFlag = false;
        hasMine = false;
        isClicked = false;
        surroundingMines = -1;
    }

    public int getSurroundingMines() {
        return surroundingMines;
    }

    public void setSurroundingMines(int surroundingMines) {
        this.surroundingMines = surroundingMines;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean hasMine() {
        return hasMine;
    }

    public void setHasMine(boolean hasMine) {
        this.hasMine = hasMine;
    }

    public boolean hasFlag() {
        return hasFlag;
    }

    public void setHasFlag(boolean hasFlag) {
        this.hasFlag = hasFlag;
    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }
}
