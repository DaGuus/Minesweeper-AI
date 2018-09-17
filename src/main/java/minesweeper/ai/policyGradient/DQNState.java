package minesweeper.ai.policyGradient;

import minesweeper.game.Game;
import minesweeper.game.Tile;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.awt.*;

public class DQNState {

    private INDArray input;

    private static final Color FLAG = new Color(0xFFFF00); // Tile is flagged
    private static final Color CLICKED = new Color(0x00FFFF); // Tile is clicked but empty          0
    private static final Color NUM_1 = new Color(0, 0, 255); // Tile has 1 surrounding         1
    private static final Color NUM_2 = new Color(0, 128, 0); // Tile has 1 surrounding         2
    private static final Color NUM_3 = new Color(255, 0, 0); // Tile has 1 surrounding         3
    private static final Color NUM_4 = new Color(0, 0, 128); // Tile has 1 surrounding         4
    private static final Color NUM_5 = new Color(128, 0, 0); // Tile has 1 surrounding         5
    private static final Color NUM_6 = new Color(0, 128, 128); // Tile has 1 surrounding       6
    private static final Color NUM_7 = new Color(255, 128, 128); // Tile has 1 surrounding     7
    private static final Color NUM_8 = new Color(128, 128, 128); // Tile has 1 surrounding     8
    private static final Color EMPTY = new Color(0x000000); // Nothing happened to the tile         9


    public DQNState(Tile[] tiles) {
        input = Nd4j.zeros(1, 10, Game.HEIGHT, Game.WIDTH);

        for (int x = 0; x < Game.WIDTH; x++) {
            for (int y = 0; y < Game.HEIGHT; y++) {
                if (tiles[x + y * Game.WIDTH].hasFlag()) {
                    putValue(input, x, y, 10);
                    throw new AssertionError("No flag should be present");
                } else if (!tiles[x + y * Game.WIDTH].isClicked()) {
                    putValue(input, x, y, 9);
                } else if (tiles[x + y * Game.WIDTH].isClicked()) {
                    putValue(input, x, y, tiles[x + y * Game.WIDTH].getSurroundingMines());
                }
            }
        }
    }

    private void putValue(INDArray array, int x, int y, int depth) {
        for (int d = 0; d < 10; d++) {
            array.putScalar(new int[]{0, d, y, x}, d == depth ? 1 : 0);
        }
    }

    public INDArray getInput() {
        return input;
    }
}
