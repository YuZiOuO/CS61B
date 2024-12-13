package byow.WorldGenerator;

import byow.TileEngine.TETile;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class WorldGenerator {
    public static final int MAX_ROOM = 10;

    public static final double MIN_UTILITY_RATE = 0.4;
    public static final double MAX_UTILITY_RATE = 0.8;
    /* Probability to generate an entity when utility rate reaches minimum */
    public static final double GENERATION_PROBABILITY = 0.9;

    public final int resX, resY;
    public final int resolution;
    public int usedTiles;

    public WorldGenerator(int resolutionX, int resolutionY) {
        this.resX = resolutionX;
        this.resY = resolutionY;
        this.resolution = resolutionX * resolutionY;
        usedTiles = 0;
    }

    public static TETile[][] render(TETile[][] tiles, Set<Entity> entities) {
        for (Entity e : entities) {
            e.render(tiles);
        }
        return tiles;
    }

    public Set<Entity> generate(int seed) {
        Random rng = new Random(seed);
        HashSet<Entity> entities = new HashSet<>();
        while (continueGeneration(rng)) {
            Room r = generateRoom(rng);
            entities.add(r);
            usedTiles += r.usedTiles();
        }
        return entities;
    }

    private boolean continueGeneration(Random rng) {
        double rate = (double) usedTiles / (double) resolution;
        if (rate < MIN_UTILITY_RATE) {
            return true;
        } else if (rate > MAX_UTILITY_RATE) {
            return false;
        } else {
            return rng.nextDouble() < GENERATION_PROBABILITY;
        }
    }

    private Room generateRoom(Random rng) {
        int x = rng.nextInt(Corridor.MIN_HEIGHT, resX - Room.MAX_EDGE_LENGTH - Corridor.MIN_HEIGHT);
        int y = rng.nextInt(Corridor.MIN_HEIGHT, resY - Room.MAX_EDGE_LENGTH - Corridor.MIN_HEIGHT);
        int width = rng.nextInt(Room.MIN_EDGE_LENGTH, Room.MAX_EDGE_LENGTH);
        int height = rng.nextInt(Room.MIN_EDGE_LENGTH, Room.MAX_EDGE_LENGTH);
        Direction dir = Direction.random(rng);
        int entryPos = (dir == Direction.EAST || dir == Direction.WEST) ?
                rng.nextInt(height) : rng.nextInt(width);
        return new Room(x, y, width, height, entryPos, dir);
    }

    private boolean validateRoom(Room room) {
        return false;
    }
}
