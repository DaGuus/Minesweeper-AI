package minesweeper.game.gui;

import minesweeper.game.Game;

import javax.swing.*;
import java.awt.*;

public class MainGUI extends JFrame {

    private static final int SCREEN_WIDTH = Game.WIDTH * GamePanel.TILE_SIZE + 5;
    private static final int SCREEN_HEIGHT = Game.HEIGHT * GamePanel.TILE_SIZE + 30;

    private GamePanel gamePanel;

    public MainGUI(Game game) {
        super("MineSweeper AI - User Side");

        setSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setLayout(new BorderLayout());

        gamePanel = new GamePanel(game);
        add(gamePanel, BorderLayout.CENTER);

        validate();
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
