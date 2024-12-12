package byow.WorldGenerator;

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

    public WorldGenerator(int resolutionX,int resolutionY) {
        this.resX = resolutionX;
        this.resY = resolutionY;
        this.resolution = resolutionX * resolutionY;
        usedTiles = 0;
    }

    public Set<Entity> generate(int seed){
        Random rand = new Random(seed);
        HashSet<Entity> entities = new HashSet<>();
        while(continueGeneration()){
            Room r = generateRoom(rand);
            entities.add(r);
            usedTiles += r.utilityTiles();
        }
        return entities;
    }

    private boolean continueGeneration(){
        double rate = (double) usedTiles / (double)resolution;
        if(rate < MIN_UTILITY_RATE){
            return true;
        }else if(rate > MAX_UTILITY_RATE){
            return false;
        }else{
            Random rand = new Random();
            return rand.nextDouble() < GENERATION_PROBABILITY;
        }
    }

    private Room generateRoom(Random rand){
        int x = rand.nextInt(0, resX-Room.MAX_EDGE_LENGTH);
        int y = rand.nextInt(0, resY-Room.MAX_EDGE_LENGTH);
        int width = rand.nextInt(Room.MIN_EDGE_LENGTH, Room.MAX_EDGE_LENGTH);
        int height = rand.nextInt(Room.MIN_EDGE_LENGTH, Room.MAX_EDGE_LENGTH);
        return new Room(x,y,width,height);
    }
}
