package byow.WorldGenerator;

/**
 * The class representing a room instant.
 */
public class Room extends Entity{
    public static final int MAX_EDGE_LENGTH = 10;
    public static final int MIN_EDGE_LENGTH = 3;

    public final int width;
    public final int height;

    public Room(int x, int y, int width, int height) {
        super(x,y);
        this.width = width;
        this.height = height;
    }

    @Override
    public int utilityTiles() {
        return width * height;
    }
}
