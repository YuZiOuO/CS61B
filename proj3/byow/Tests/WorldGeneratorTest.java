package byow.Tests;

import byow.Config;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import byow.WorldGenerator.Entity;
import byow.WorldGenerator.WorldGenerator;
import org.junit.Test;

import java.util.Set;

public class WorldGeneratorTest {
    public static void main() {
        TERenderer renderer = new TERenderer();
        renderer.initialize(Config.CANVAS_WIDTH, Config.CANVAS_HEIGHT);

        WorldGenerator wg = new WorldGenerator(Config.CANVAS_WIDTH, Config.CANVAS_HEIGHT);
        Set<Entity> s = wg.generate(114514);

        TETile[][] world = new TETile[Config.CANVAS_WIDTH][Config.CANVAS_HEIGHT];
        for (int x = 0; x < Config.CANVAS_WIDTH; x += 1) {
            for (int y = 0; y < Config.CANVAS_HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        WorldGenerator.render(world, s);
        renderer.renderFrame(world);
    }

    @Test
    public void testWorldGenerator() {
        WorldGenerator wg = new WorldGenerator(60, 50);
        Set<Entity> s = wg.generate(114514);
    }
}
