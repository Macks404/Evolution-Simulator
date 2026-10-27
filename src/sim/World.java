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

    private int newPlantSpawnRate = 5;
    private int plantCap = 7500;
    private int plantCount = 0;
    private int newCreatureSpawnRate = 30;
    private int minCreatures = 100;
    private int creatureCount = 0;
    private int carnivoreCount = 0;
    private int minCarnivores = 20;
    private Creature lastCarnivoreParent = null;

    private final Genome[] premadeGenomes = {
        new Genome(8, 5, 0.05, 100, Math.toRadians(120), 1, 1, 0, 0.25, 0.3), //yellow herbivore
        new Genome(5, 5, 0.35, 120, Math.toRadians(120), 1, 1, 1, 0.25, 0.3), //white omnivore
        new Genome(10, 5, 0.65, 120, Math.toRadians(120), 0, 1, 1, 0.25, 0.3), //large cyan omnivore
        new Genome(5, 6, 0.95, 120, Math.toRadians(120), 1, 0, 1, 0.25, 0.3) //purple carnivore
    };
    
    World(int width, int height, int startingCreatureCount, int startingPlantCount) {
        this.width = width;
        this.height = height;
        plantCount = startingPlantCount;
        creatureCount = startingCreatureCount;

        for(int i = 0; i < startingCreatureCount; i++) {
            Creature c = new Creature(random.nextDouble()*width, random.nextDouble()*height, random.nextDouble()*2*Math.PI, premadeGenomes[0].mutate(random), new Network(Creature.BRAIN_SIZES, random), new Network(Creature.ASSESSOR_SIZES, random));
            c.makeAdult();
            entities.add(c);
        }

        for(int i = 0; i < startingPlantCount; i++) {
            entities.add(new Plant(random.nextDouble()*width, random.nextDouble()*height, 5, 0x23c243));
        }
    }

    public void tick() {
        carnivoreCount = 0;
        for(Entity entity : entities) {
            entity.update(this);

            if(entity instanceof Creature c && c.getActive() && c.getGenome().diet() > 0.65) carnivoreCount++;
        }

        if(tick%newPlantSpawnRate == 0) {
            addEntity(new Plant(random.nextInt(0,width), random.nextInt(0,height), 5, 0x23c243));
        }

        if(creatureCount < minCreatures) {
            Creature parent = randomCreature();
            int attempts = 0;
            while(parent != null && parent.childrenCount < 1 && attempts < 5) {
                parent = randomCreature();
                attempts++;
            }
            double x = random.nextDouble()*width, y = random.nextDouble()*height, h = random.nextDouble()*2*Math.PI;

            if(parent != null && random.nextBoolean() && parent.childrenCount > 0) {
                double rate = parent.getGenome().mutationRate();
                stats.immigrants++;
                Creature c = new Creature(x, y, h, parent.getGenome().mutate(random),
                        parent.getBrain().mutate(random, rate), parent.getAssessor().mutate(random, rate));
                c.makeAdult();
                addEntity(c);
            } else {
                stats.immigrants++;
                Creature c = new Creature(x, y, h, premadeGenomes[0].mutate(random),
                        new Network(Creature.BRAIN_SIZES, random), new Network(Creature.ASSESSOR_SIZES, random));
                c.makeAdult();
                addEntity(c);
            }
        }
        else if(tick%newCreatureSpawnRate==0 && carnivoreCount < minCarnivores) {
            Creature c = randomCreature();
            if(c != null) {
                double x = c.getX(), y = c.getY(), h = random.nextDouble()*2*Math.PI;
                Creature carnivore;
                if(lastCarnivoreParent != null && random.nextBoolean()) {
                    Creature p = lastCarnivoreParent;
                    double rate = p.getGenome().mutationRate();
                    carnivore = new Creature(x, y, h, p.getGenome().mutate(random),
                            p.getBrain().mutate(random, rate), p.getAssessor().mutate(random, rate));
                } else {
                    carnivore = new Creature(x, y, h, premadeGenomes[3].mutate(random),
                            new Network(Creature.BRAIN_SIZES, random), new Network(Creature.ASSESSOR_SIZES, random));
                }
                carnivore.makeAdult();
                stats.immigrants++;
                addEntity(carnivore);
            }
        }

        entities.removeIf(entity -> {
            if(entity.getActive()) return false; // still alive, keep it

            if(entity instanceof Plant) plantCount--;
            if(entity instanceof Creature c) {
                if(c.killedByAttack) stats.killed++;
                creatureCount--;
                stats.deaths++;
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

    public record SimTime(long ticks) {
        public static final long SECONDS_PER_TICK = 324;

        public long totalSeconds() { return ticks * SECONDS_PER_TICK; }
        public long days()    { return totalSeconds() / 86400; }
        public long hours()   { return (totalSeconds() % 86400) / 3600; }
        public long minutes() { return (totalSeconds() % 3600) / 60; }

        @Override
        public String toString() {
            return String.format("Day %d  %02d:%02d", days() + 1, hours(), minutes());
        }
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

    public void carnivoreBred(Creature c) {
        lastCarnivoreParent = c;
    }

    private Creature randomCreature() {
        List<Creature> alive = new ArrayList<>();
        for(Entity e : entities) {
            if(e instanceof Creature c && e.getActive()) alive.add(c);
        }
        if(alive.isEmpty()) return null;
        return alive.get(random.nextInt(alive.size()));
    }

    public Stats getStats() {
        return stats;
    }
}
