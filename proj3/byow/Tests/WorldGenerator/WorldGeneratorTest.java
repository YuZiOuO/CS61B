package byow.Tests.WorldGenerator;

import byow.WorldGenerator.Entity;
import byow.WorldGenerator.WorldGenerator;
import org.junit.Test;

import java.util.Set;

public class WorldGeneratorTest {
    @Test
    public void testWorldGenerator() {
        WorldGenerator wg = new WorldGenerator(60,50);
        Set<Entity> s = wg.generate(114514);
    }
}
