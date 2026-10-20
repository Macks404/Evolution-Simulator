package sim;

import java.util.ArrayList;
import java.util.List;

public class Creature extends Entity {
    private final Genome genome;
    private final double maxEnergy;

    // movement variables
    private double heading;
    private double currentSpeed;
    private double accelerate = 1;
    private double turn = 0.2;
    private double maxTurnRate = 0.1;

    record Sighting(Entity entity, double dist, double rel) {}

    private Sighting nearestPlant;
    private Sighting nearestCorpse;
    private final List<Sighting> visibleCreatures = new ArrayList<>();

    // info for brain
    private boolean seesPlant;
    private double plantAngle;
    private double plantDistance;
    
    private boolean seesCorpse;
    private double corpseAngle;
    private double corpseDistance;

    public Creature(double x, double y, double heading, Genome genome) {
        this.x = x;
        this.y = y;
        this.heading = heading;
        this.genome = genome;
        radius = genome.radius();
        maxEnergy = radius * radius;
        color = ((int)(genome.red() * 255) << 16)
                | ((int)(genome.green() * 255) << 8)
                |  (int)(genome.blue() * 255);
        energy = maxEnergy;
    }

    @Override 
    protected void tick(World world) {
        look(world);

        // apply movement
        heading += turn * maxTurnRate;
        currentSpeed = accelerate * genome.maxSpeed();
        x += Math.cos(heading) * currentSpeed;
        y += Math.sin(heading) * currentSpeed;
        // decrease energy with movement
        energy -= currentSpeed * currentSpeed / 100;

        // passive decrease corrolated to size
        energy = Math.clamp(energy-(radius/500), 0, maxEnergy);
    }

    private void look(World world) {
        // reset vision
        nearestCorpse = null;
        nearestPlant = null;
        visibleCreatures.clear();

        for(Entity e : world.getEntities()) {
            if(e == this) continue;

            // vector from this to target
            double dx = e.getX() - x;
            double dy = e.getY() - y;
            double dist = Math.sqrt(dx*dx + dy*dy);
            //if its in my view distance
            if(dist > genome.viewRange()) continue;

            // angle to the target relative to where I'm facing, wrapped into -PI -> PI
            double rel = Math.atan2(dy, dx) - heading;
            rel = Math.atan2(Math.sin(rel), Math.cos(rel));
            // if its in my vision cone
            if(Math.abs(rel) > genome.fieldOfView() / 2) continue;

            if(e instanceof Plant) {
                if(nearestPlant == null || nearestPlant.dist > dist) {
                    nearestPlant = new Sighting(e, dist, rel);
                }
            }
            else if(e instanceof Corpse) {
                if(nearestCorpse == null || nearestCorpse.dist > dist) {
                    nearestCorpse = new Sighting(e, dist, rel);
                }
            }
            else if(e instanceof Creature) {
                visibleCreatures.add(new Sighting(e, dist, rel));
            }
        }

        // fill in values that neural network can use, vals must be normalised between 0 and 1
        if(nearestPlant != null) {
            seesPlant = true;
            plantAngle = nearestPlant.rel() / (genome.fieldOfView() / 2);
            plantDistance = nearestPlant.dist() / genome.viewRange();
        }
        else {
            seesPlant = false;
            plantAngle = 0;
            plantDistance = 1;
        }
        if(nearestCorpse != null) {
            seesCorpse = true;
            corpseAngle = nearestCorpse.rel() / (genome.fieldOfView() / 2);;
            corpseDistance = nearestCorpse.dist() / genome.viewRange();;
        }
        else {
            seesCorpse = false;
            corpseAngle = 0;
            corpseDistance = 1;
        }
    }

    double[] think(double[] inputs) {
        return null;
    }
}
