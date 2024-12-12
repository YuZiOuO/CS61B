package byow.WorldGenerator;

public abstract class Entity {
    public final int x;
    public final int y;
    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public abstract int utilityTiles();
}
