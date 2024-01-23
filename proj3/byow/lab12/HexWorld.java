package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    public static void addHexagon(TETile[][] world, int px, int py, int s) {
        for (int y = 0; y < s; y++) {
            for (int x = -y; x < s+y; x++) {
                world[px + x][py + y] = Tileset.AVATAR;
                world[px + x][py + 2 * s - y - 1] = Tileset.AVATAR;
            }
        }
    }

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        // add hexagon
        addHexagon(world, 10, 10, 5);
        // arrange amount of hexagons
        ter.renderFrame(world);
    }
}
