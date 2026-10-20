package sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    private int width, height;
    private List<Entity> entities = new ArrayList<>();
    private List<Entity> toAdd = new ArrayList<>();
    private Random random = new Random(10);

    private int maxPlants = 800;
    private double plantSpawnChance = 0.2;

    private long tick = 0;
    private final Stats stats = new Stats("stats.csv", 1000);

    
    World(int width, int height, int startingCreatureCount, int startingPlantCount) {
        /* 
        When a world is created: 
        - Create omnivores of the same species scattered throughout the map
        - Create plants scattered throughout the map
        */
        this.width = width;
        this.height = height;

        Genome starter = new Genome(10, 0.5, 0.5, 100, Math.toRadians(120), 0.99, 0.73, 0.01, 0.25);
        for(int i = 0; i < startingCreatureCount; i++) {
            entities.add(new Creature(random.nextDouble()*width, random.nextDouble()*height, random.nextDouble()*2*Math.PI, starter.mutate(random), new Network(Creature.BRAIN_SIZES, random)));
        }

        for(int i = 0; i < startingPlantCount; i++) {
            entities.add(new Plant(random.nextDouble()*width, random.nextDouble()*height, 5, 0x23c243));
        }
    }

    public void tick() {
        int plantCount = 0;
        for(Entity entity : entities) {
            entity.update(this);

            if(entity instanceof Plant) plantCount++;

            if(!entity.getActive() && entity instanceof Creature) {
                toAdd.add(new Corpse(entity.getX(), entity.getY(), entity.getRadius(), 0xFF0000, entity.getEnergy()));
            }
        }
        if(plantCount < maxPlants && random.nextDouble() < plantSpawnChance) {
            toAdd.add(new Plant(random.nextDouble()*width, random.nextDouble()*height, 5, 0x23c243));
        }

        entities.removeIf(entity -> !entity.getActive());
        entities.addAll(toAdd);
        toAdd.clear();

        tick++;
        stats.record(tick, entities);
    }

    public void addEntity(Entity entity) {
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
