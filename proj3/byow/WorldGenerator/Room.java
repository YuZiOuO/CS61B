package byow.WorldGenerator;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * The class representing a room instant.
 */
public class Room implements Entity {
    public static final int MAX_EDGE_LENGTH = 10;
    public static final int MIN_EDGE_LENGTH = 3;

    public final int x;
    public final int y;
    public final int width;
    public final int height;

    /**
     * the position of the entry of the room is defined by Pos & Dir.
     * Dir tells which edge the entry is on,
     * and Pos shows exactly where it is, staring from the bottom or left.<br>
     * e.g:<br>
     * *********<br>
     * *       * ←entry: Dir:EAST Pos:2<br>
     * *       *<br>
     * *********<br>
     * ↑<br>
     * (x,y) = (x0,y0)<br>
     * no matter where the datum is,Pos is counted from the bottom edge.<br>
     */
    public final int entryPos;
    public final Direction entryDir;

    public Room(int x, int y, int width, int height, int entryPos, Direction entryDir) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.entryPos = entryPos;
        this.entryDir = entryDir;
    }

    // TODO:可以把随机生成方法重构到这.

    @Override
    public int X() {
        return x;
    }

    @Override
    public int Y() {
        return y;
    }

    @Override
    public int usedTiles() {
        return width * height;
    }

    @Override
    public void render(TETile[][] world) {
        for (int i = x; i < width; i++) {
            world[i][y] = Tileset.WALL;
        }
        for (int j = y; j < height; j++) {
            world[x][j] = Tileset.WALL;
        }
        world[entryAtTopOrBottom() ? x + entryPos : x]
                [entryAtTopOrBottom() ? y : y + entryPos] = Tileset.UNLOCKED_DOOR;
    }

    public boolean entryAtTopOrBottom() {
        return entryDir == Direction.NORTH || entryDir == Direction.SOUTH;
    }
}
