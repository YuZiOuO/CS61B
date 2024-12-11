package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    public static final int WIDTH = 60;
    public static final int HEIGHT = 30;

    public static void main(String[] args) {
        TERenderer renderer = new TERenderer();
        renderer.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        addAHex(world, 10, 10, 4, Tileset.FLOOR);
        addAHex(world, 17, 14, 4, Tileset.FLOWER);
        renderer.renderFrame(world);
    }

    /**
     * Add a Hex with given size whose top left vertex is at (x,y)
     */
    private static void addAHex(TETile[][] tiles, int x, int y, int size, TETile tile) {
        double axis = y - size + 0.5;
        for (int i = 0; i < size; i++) {
            drawALine(tiles, x - i, y - i, size + 2 * i, tile, axis);
        }
    }

    private static void drawALine(TETile[][] tiles,
                                  int x, int y, int size, TETile tile,
                                  double axis) {
        for (int i = 0; i < size; i++) {
            tiles[x + i][y] = tile;
            axialSymmetry(tiles, x + i, y, axis);
        }
    }

    private static void lineSymmetry(TETile[][] tiles, int x, int y, double axis) {
        // symmetrify like this to avoid redundant computation for symmetric y.
    }

    private static void axialSymmetry(TETile[][] tiles,
                                      int x, int y,
                                      double axis) {
        double yDiff = Math.abs(y - axis);
        int symY = y > axis ? (int) (axis - yDiff) : (int) (axis + yDiff);
        tiles[x][symY] = tiles[x][y];
    }
}
