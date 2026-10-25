package sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    private int width, height;
    private List<Entity> entities = new ArrayList<>();
    private List<Entity> toAdd = new ArrayList<>();
    private Random random = new Random(10);

    private long tick = 0;
    private final Stats stats = new Stats("stats.csv", 1000);

    // spawn rate of plants made through no reproduction in ticks
    private int newPlantSpawnRate = 250;
    private int plantCap = 2000;
    private int plantCount = 0;
    
    World(int width, int height, int startingCreatureCount, int startingPlantCount) {
        this.width = width;
        this.height = height;
        plantCount = startingPlantCount;

        Genome starter = new Genome(10, 0.5, 0.5, 100, Math.toRadians(120), 0.99, 0.73, 0.01, 0.25);
        for(int i = 0; i < startingCreatureCount; i++) {
            entities.add(new Creature(random.nextDouble()*width, random.nextDouble()*height, random.nextDouble()*2*Math.PI, starter.mutate(random), new Network(Creature.BRAIN_SIZES, random), new Network(Creature.ASSESSOR_SIZES, random)));
        }

        for(int i = 0; i < startingPlantCount; i++) {
            entities.add(new Plant(random.nextDouble()*width, random.nextDouble()*height, 5, 0x23c243));
        }
    }

    public void tick() {
        for(Entity entity : entities) {
            entity.update(this);

            if(!entity.getActive() && entity instanceof Creature) {
                addEntity(new Corpse(entity.getX(), entity.getY(), entity.getRadius(), 0xFF0000, entity.getEnergy()));
            }
            if(!entity.getActive() && entity instanceof Plant) {
                plantCount--;
            }
        }
        if(tick%newPlantSpawnRate == 0) {
            addEntity(new Plant(random.nextInt(0,width), random.nextInt(0,height), 5, 0x23c243));
        }

        entities.removeIf(entity -> !entity.getActive());
        entities.addAll(toAdd);
        toAdd.clear();

        tick++;
        stats.record(tick, entities);
    }

    public void addEntity(Entity entity) {
        if(entity instanceof Plant) {
            if(plantCount >= plantCap) return;
            plantCount++;
        }
        toAdd.add(entity);
    }


    public long getTick() {
        return tick;
    }

    public Random getRandom() {
        return random;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Entity> getEntities() {
        return entities;
    }
}
