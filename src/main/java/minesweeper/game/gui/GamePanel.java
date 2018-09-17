package minesweeper.game.gui;

import minesweeper.game.Game;
import minesweeper.game.Tile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel implements MouseListener {

    private final Game game;

    private BufferedImage sprite_normal_tile;
    private BufferedImage sprite_normal_tile_clicked;
    private BufferedImage sprite_normal_tile_flagged;
    private BufferedImage sprite_bomb;
    private BufferedImage sprite_bomb_red;
    private BufferedImage sprite_bomb_red_crossed;
    private BufferedImage sunglasses;

    private BufferedImage[] numSprites;

    public static final int TILE_SIZE = 16;

    public GamePanel(Game game) {
        this.game = game;

        try {
            loadSprites();
        } catch (IOException e) {
            e.printStackTrace();
        }

        repaint();

        addMouseListener(this);
    }

    private void loadSprites() throws IOException {
        sprite_normal_tile = ImageIO.read(new File("res/tile_normal.png"));
        sprite_normal_tile_clicked = ImageIO.read(new File("res/tile_clicked.png"));
        sprite_normal_tile_flagged = ImageIO.read(new File("res/tile_flagged.png"));
        sprite_bomb = ImageIO.read(new File("res/tile_bomb.png"));
        sprite_bomb_red = ImageIO.read(new File("res/tile_bomb_red.png"));
        sprite_bomb_red_crossed = ImageIO.read(new File("res/tile_bomb_crossed.png"));
        sunglasses = ImageIO.read(new File("res/sunglasses.png"));

        numSprites = new BufferedImage[8];
        for (int i = 0; i < numSprites.length; i++) {
            numSprites[i] = ImageIO.read(new File("res/num_" + (i + 1) + ".png"));
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 500, 500);

        for (int x = 0; x < Game.WIDTH; x++) {
            for (int y = 0; y < Game.HEIGHT; y++) {
                Tile tile = game.getTile(x, y);
                if (tile == null) continue;

                if (tile.hasMine()) {
                    if (game.isGameover()) {
                        if (tile.isClicked()) {
                            g.drawImage(sprite_bomb_red_crossed, x * TILE_SIZE, y * TILE_SIZE, null);
                        } else {
                            if (tile.hasFlag()) {
                                g.drawImage(sprite_bomb, x * TILE_SIZE, y * TILE_SIZE, null);
                            } else {
                                g.drawImage(sprite_bomb_red, x * TILE_SIZE, y * TILE_SIZE, null);
                            }
                        }
                    } else if (game.hasWon()) {
                        g.drawImage(sunglasses, x * TILE_SIZE, y * TILE_SIZE, null);
                    } else {
                        if (tile.hasFlag()) {
                            g.drawImage(sprite_normal_tile_flagged, x * TILE_SIZE, y * TILE_SIZE, null);
                        } else {
                            g.drawImage(sprite_normal_tile, x * TILE_SIZE, y * TILE_SIZE, null);
                        }
                    }
                } else if (tile.isClicked()) {
                    if (tile.getSurroundingMines() == 0) {
                        g.drawImage(sprite_normal_tile_clicked, x * TILE_SIZE, y * TILE_SIZE, null);
                    } else {
                        g.drawImage(numSprites[tile.getSurroundingMines() - 1], x * TILE_SIZE, y * TILE_SIZE, null);
                    }
                } else {
                    if (tile.hasFlag()) {
                        g.drawImage(sprite_normal_tile_flagged, x * TILE_SIZE, y * TILE_SIZE, null);
                    } else {
                        g.drawImage(sprite_normal_tile, x * TILE_SIZE, y * TILE_SIZE, null);
                    }
                }
            }
        }
    }

    public void click(int x, int y) {
        if (!game.isGameover()) {
            game.firstClick(x, y);
        }
        repaint();
    }

    public void mark(int x, int y) {
        if (!game.isGameover()) {
            game.mark(x, y);
        }
        repaint();
    }

    public void mouseClicked(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {
        int clickX = e.getX();
        int clickY = e.getY();

        int convertedX = clickX / TILE_SIZE;
        int convertedY = clickY / TILE_SIZE;

        if (e.getButton() == 1) {
            click(convertedX, convertedY);
        } else if (e.getButton() == 3) {
            mark(convertedX, convertedY);
        }
    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
}
