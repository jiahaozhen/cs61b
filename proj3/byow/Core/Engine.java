package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        long seed = Long.parseLong(input.substring(1,input.length()-1));
        Random random = new Random(seed);
        /* fill with wall */
        fillWithWall(finalWorldFrame);
        /* the starter position */
        Position starter = getRandomPosition(random);
        finalWorldFrame[starter.getX()][starter.getY()] = Tileset.FLOOR;
        /* spread from the start */
        spreadFromStarter(finalWorldFrame, starter, random);
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(finalWorldFrame);
        return finalWorldFrame;
    }

    private Position getRandomPosition(Random random) {
        int px = RandomUtils.uniform(random, 0, WIDTH);
        int py = RandomUtils.uniform(random, 0, HEIGHT);
        return new Position(px, py);
    }

    private void spreadFromStarter(TETile[][] frame, Position starter, Random random) {
        for (Position neighbour : starter.getNeighbour()) {
            if (frame[neighbour.getX()][neighbour.getY()] != Tileset.WALL) {
                continue;
            }
            boolean flag = RandomUtils.bernoulli(random, 0.5);
            if (flag) {
                frame[neighbour.getX()][neighbour.getY()] = Tileset.FLOOR;
                spreadFromStarter(frame, neighbour, random);
            } else {
                frame[neighbour.getX()][neighbour.getY()] = Tileset.WALL;
            }
        }
    }

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.interactWithInputString(args[1]);
    }

    private void fillWithWall(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.WALL;
            }
        }
    }
}
