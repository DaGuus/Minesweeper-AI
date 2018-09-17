package minesweeper.game;

import minesweeper.ai.qLearn.State;

import java.util.Random;

public class Game {

    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;
    public static final int MINES = 30;

    private Tile[] tiles;
    private boolean gameover;
    private boolean hasWon;
    private int moves;

    private int clickedTiles;

    public Game() {
        gameover = false;
        hasWon = false;
        moves = 0;
        clickedTiles = 0;

        initBoard();
    }

    public void reset() {
        gameover = false;
        hasWon = false;
        moves = 0;
        clickedTiles = 0;
        initBoard();
    }

    public int getMoves() {
        return moves;
    }

    public Tile[] getTiles() {
        return tiles;
    }

    public State getState() {
        return new State(tiles);
    }

    private void initBoard() {
        // Init Tiles
        tiles = new Tile[WIDTH * HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                tiles[x + y * WIDTH] = new Tile(x, y);
            }
        }

        // Spawn mines
        Random random = new Random();
        int mineCount = 0;
        while (mineCount < MINES) {
            Tile tile = getTile(random.nextInt(WIDTH), random.nextInt(HEIGHT));

            if (!tile.hasMine()) {
                tile.setHasMine(true);

                mineCount++;
            }
        }

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                int surroundingMines = 0;
                if (x > 0 && getTile(x - 1, y).hasMine()) surroundingMines++;
                if (x > 0 && y > 0 && getTile(x - 1, y - 1).hasMine()) surroundingMines++;
                if (x < WIDTH - 1 && getTile(x + 1, y).hasMine()) surroundingMines++;
                if (x < WIDTH - 1 && y > 0 && getTile(x + 1, y - 1).hasMine()) surroundingMines++;
                if (y > 0 && getTile(x, y - 1).hasMine()) surroundingMines++;
                if (y < HEIGHT - 1 && getTile(x, y + 1).hasMine()) surroundingMines++;
                if (y < HEIGHT - 1 && x > 0 && getTile(x - 1, y + 1).hasMine()) surroundingMines++;
                if (y < HEIGHT - 1 && x < WIDTH - 1 && getTile(x + 1, y + 1).hasMine()) surroundingMines++;

                getTile(x, y).setSurroundingMines(surroundingMines);
            }
        }
    }

    public void mark(int x, int y) {
        if (x < 0 || x > WIDTH - 1 || y < 0 || y > HEIGHT - 1) return;
        if (getTile(x, y).isClicked()) return;

        getTile(x, y).setHasFlag(!getTile(x, y).hasFlag());

        if (correctFlags() == MINES && incorrectFlags() == 0) {
            // WON!!!
            hasWon = true;
        }
    }

    public boolean hasWon() {
        if (hasWon) return true;

        return clickedTiles + MINES == WIDTH * HEIGHT;

    }

    public int incorrectFlags() {
        int incorrectFlags = 0;
        for (Tile tile : tiles) {
            if (tile.hasFlag() && !tile.hasMine()) incorrectFlags++;
        }

        return incorrectFlags;
    }

    public int correctFlags() {
        int correctFlags = 0;
        for (Tile tile : tiles) {
            if (tile.hasFlag() && tile.hasMine()) correctFlags++;
        }

        return correctFlags;
    }

    public void firstClick(int x, int y) {
        if (x < 0 || x > WIDTH - 1 || y < 0 || y > HEIGHT - 1) return;
        if (getTile(x, y).isClicked() || getTile(x, y).hasFlag()) return;

        moves++;
        click(x, y);
    }

    public void click(int x, int y) {
        if (x < 0 || x > WIDTH - 1 || y < 0 || y > HEIGHT - 1) return;
        if (getTile(x, y).isClicked()) return;

        if (getTile(x, y).hasMine()) {
            // Gameover
            getTile(x, y).setClicked(true);
            gameover = true;
        } else {
            getTile(x, y).setClicked(true);
            clickedTiles++;

            if (getTile(x, y).getSurroundingMines() == 0) {
                // Click neighbours
                if (x > 0) click(x - 1, y);
                if (x > 0 && y > 0) click(x - 1, y - 1);
                if (x < WIDTH - 1) click(x + 1, y);
                if (x < WIDTH - 1 && y > 0) click(x + 1, y - 1);
                if (y > 0) click(x, y - 1);
                if (y < HEIGHT - 1) click(x, y + 1);
                if (y < HEIGHT - 1 && x > 0) click(x - 1, y + 1);
                if (y < HEIGHT - 1 && x < WIDTH - 1) click(x + 1, y + 1);
            }
        }
    }

    public void addMove() {
        moves++;
    }

    public Tile getTile(int x, int y) {
        if (x < 0 || x > WIDTH - 1 || y < 0 || y > HEIGHT - 1) return null;
        return tiles[x + y * WIDTH];
    }

    public boolean isGameover() {
        return gameover;
    }
}
