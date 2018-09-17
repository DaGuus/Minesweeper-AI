package minesweeper.game;

import minesweeper.game.gui.MainGUI;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Game game = new Game();

        MainGUI gui = new MainGUI(game);
        gui.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gui.setVisible(true);
    }
}
