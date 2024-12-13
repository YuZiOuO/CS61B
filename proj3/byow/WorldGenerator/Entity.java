package byow.WorldGenerator;

import byow.TileEngine.TETile;

public interface Entity {
    public int X();

    public int Y();

    public int usedTiles();

    public void render(TETile[][] world);
}
