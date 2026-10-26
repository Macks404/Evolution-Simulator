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
    private int newPlantSpawnRate = 10;
    private int plantCap = 5000;
    private int plantCount = 0;

    // spawn rate of new creature from premade genome pool
    private int newCreatureSpawnRate = 30;
    private int minCreatures = 100;
    private int creatureCount = 0;

    private final Genome[] premadeGenomes = {
        new Genome(4, 7, 0.05, 120, Math.toRadians(120), 1, 1, 0, 0.25), //yellow small herbivore
        new Genome(5, 5, 0.6, 120, Math.toRadians(120), 1, 1, 1, 0.25), //white avg omnivore
        new Genome(5, 6, 0.95, 120, Math.toRadians(120), 1, 0, 1, 0.25) //purple large carnivore
    };
    
    World(int width, int height, int startingCreatureCount, int startingPlantCount) {
        this.width = width;
        this.height = height;
        plantCount = startingPlantCount;
        creatureCount = startingCreatureCount;

        for(Genome genome : premadeGenomes) {
            for(int i = 0; i < startingCreatureCount/premadeGenomes.length; i++) {
                entities.add(new Creature(random.nextDouble()*width, random.nextDouble()*height, random.nextDouble()*2*Math.PI, genome.mutate(random), new Network(Creature.BRAIN_SIZES, random), new Network(Creature.ASSESSOR_SIZES, random)));
            }
        }

        for(int i = 0; i < startingPlantCount; i++) {
            entities.add(new Plant(random.nextDouble()*width, random.nextDouble()*height, 5, 0x23c243));
        }
    }

    public void tick() {
        for(Entity entity : entities) {
            entity.update(this);
        }

        if(tick%newPlantSpawnRate == 0) {
            addEntity(new Plant(random.nextInt(0,width), random.nextInt(0,height), 5, 0x23c243));
        }

        if(creatureCount < minCreatures) {
            Creature parent = randomCreature();
            int attempts = 0;
            while(parent.childrenCount < 1 && attempts < 5) {
                parent = randomCreature();
                attempts++;
            }
            double x = random.nextDouble()*width, y = random.nextDouble()*height, h = random.nextDouble()*2*Math.PI;

            if(parent != null && random.nextBoolean() && parent.childrenCount > 0) {
                // 50%: a mutated copy of someone alive, so good genes stick around
                double rate = parent.getGenome().mutationRate();
                addEntity(new Creature(x, y, h, parent.getGenome().mutate(random),
                        parent.getBrain().mutate(random, rate), parent.getAssessor().mutate(random, rate)));
            } else {
                // 50%: a fresh random one, for new ideas
                addEntity(new Creature(x, y, h, premadeGenomes[random.nextInt(0,premadeGenomes.length)].mutate(random),
                        new Network(Creature.BRAIN_SIZES, random), new Network(Creature.ASSESSOR_SIZES, random)));
            }
        }
        else if(tick%newCreatureSpawnRate==0) {
            Creature c = randomCreature();
            double x = c.getX(), y = c.getY(), h = random.nextDouble()*2*Math.PI;
            Genome genome = premadeGenomes[2];

            addEntity(new Creature(x, y, h, genome.mutate(random),
                        new Network(Creature.BRAIN_SIZES, random), new Network(Creature.ASSESSOR_SIZES, random)));
        }

        entities.removeIf(entity -> {
            if(entity.getActive()) return false; // still alive, keep it

            if(entity instanceof Plant) plantCount--;
            if(entity instanceof Creature c) {
                creatureCount--;
                addEntity(new Corpse(entity.getX(), entity.getY(), entity.getRadius(), 0xFF0000, entity.getEnergy() + c.lostToAttack));
            }
            return true; // remove it
        });
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
        if(entity instanceof Creature) {
            creatureCount++;
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

    private Creature randomCreature() {
        List<Creature> alive = new ArrayList<>();
        for(Entity e : entities) {
            if(e instanceof Creature c && e.getActive()) alive.add(c);
        }
        if(alive.isEmpty()) return null;
        return alive.get(random.nextInt(alive.size()));
    }
}
