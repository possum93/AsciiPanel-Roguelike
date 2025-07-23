package rltut.screens;

import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;
import rltut.World;
import rltut.WorldBuilder;

public class PlayScreen implements Screen {

    private World world;
    private int centerX; // centerX and centerY are effectively the players position. I would change
    private int centerY; // these variable names, but I don't want to stray from the tutorial too much.
    private int screenWidth;
    private int screenHeight;

    public PlayScreen() {
        screenWidth = 80;
        screenHeight = 21;
        createWorld();
    }

    private void createWorld() {
        world = new WorldBuilder(90, 31).makeCaves().build();
    }

    public int getScrollX() {
        // Tells displayTiles() where the left edge of the screen should start and when to scroll the "camera".
        // Returns zero if the player is not more than halfway across the cave.
        // (world.width() - screenWidth) prevents left from being updated when the screen is
        //      already showing the right-most edge of the world.
        return Math.max(0, Math.min(centerX - screenWidth / 2, world.width() - screenWidth));
    }

    public int getScrollY() {
        // Tells displayTiles() where the top edge of the screen should start.
        return Math.max(0, Math.min(centerY - screenHeight / 2, world.height() - screenHeight));
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++){
                int wx = x + left;
                int wy = y + top;

                // Takes the position of the current terminal tile at (x,y) and
                // uses the left and top as offsets to place the correct glyph.
                // wx and wy can be thought of as "world.x and world.y" whereas
                // x and y are screen coordinates.
                terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
            }
        }
    }

    public void scrollBy(int mx, int my) {
        // mx and my are given values when a movement key is pressed.
        // Updates the players coordinates while preventing them from going out of bounds.
        centerX = Math.max(0, Math.min(centerX + mx, world.width() - 1));
        centerY = Math.max(0, Math.min(centerY + my, world.height() - 1));
    }

    public void displayOutput(AsciiPanel terminal) {
        int left = getScrollX();
        int top = getScrollY();
        displayTiles(terminal, left, top);

        // Keeps the players position accurate when the camera moves
        terminal.write('@', centerX - left, centerY - top);

        // Just debug text to help me understand this file
        terminal.writeCenter("(centerX, centerY) : (" + centerX + ", " + centerY + ")", 22);
        terminal.writeCenter("(left, top) : (" + left + ", " + top + ")", 23);
    }

    public Screen respondToUserInput(KeyEvent key) {
        switch (key.getKeyCode()) {
            case KeyEvent.VK_ESCAPE: return new LoseScreen();
            case KeyEvent.VK_ENTER: return new WinScreen();
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_H: scrollBy(-1, 0); break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_L: scrollBy( 1, 0); break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_K: scrollBy( 0,-1); break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_J: scrollBy( 0, 1); break;
            case KeyEvent.VK_Y: scrollBy(-1,-1); break;
            case KeyEvent.VK_U: scrollBy( 1,-1); break;
            case KeyEvent.VK_B: scrollBy(-1, 1); break;
            case KeyEvent.VK_N: scrollBy( 1, 1); break;
        }

        return this;
    }
}
