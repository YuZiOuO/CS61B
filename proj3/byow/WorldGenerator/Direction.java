package byow.WorldGenerator;

import java.util.Random;

public enum Direction {
    EAST, WEST, NORTH, SOUTH;

    public static Direction random(Random r) {
        int random = r.nextInt(4);
        return switch (random) {
            case 1 -> WEST;
            case 2 -> NORTH;
            case 3 -> SOUTH;
            default -> EAST;
        };
    }
}
