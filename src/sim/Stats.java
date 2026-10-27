package sim;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

public class Stats {
    private PrintWriter out;
    private final int interval; // record a row every this many ticks

    // event counters, World and Creature add to these. reset after every row,
    // so each row shows what happened since the last one
    public int births = 0;
    public int deaths = 0;
    public int killed = 0;   // deaths where the creature had been bitten by an attacker
    public int immigrants = 0;

    Stats(String fileName, int interval) {
        this.interval = interval;
        try {
            // false = start a fresh file each run instead of adding to the old one
            out = new PrintWriter(new FileWriter(fileName, false));
            out.println("tick,day,creatures,plants,corpses,"
                + "adults,juveniles,herbivores,omnivores,carnivores,"
                + "births,deaths,killed,immigrants,"
                + "radius,adultRadius,growth,birthScale,energyFull,"
                + "maxSpeed,diet,dietMin,dietMax,viewRange,fieldOfView,mutationRate,"
                + "plantEnergy,corpseEnergy");
        }
        catch(IOException e) {
            System.out.println("Stats disabled, couldn't open " + fileName + ": " + e);
        }
    }

    public void record(long tick, List<Entity> entities) {
        if(out == null || tick % interval != 0) return;

        int creatures = 0, plants = 0, corpses = 0;
        int adults = 0, herbivores = 0, omnivores = 0, carnivores = 0;
        double radius = 0, adultRadius = 0, growth = 0, birthScale = 0, energyFull = 0;
        double maxSpeed = 0, diet = 0, viewRange = 0, fieldOfView = 0, mutationRate = 0;
        double dietMin = 1, dietMax = 0;
        double plantEnergy = 0, corpseEnergy = 0;

        for(Entity e : entities) {
            if(e instanceof Plant) {
                plants++;
                plantEnergy += e.getEnergy();
            }
            else if(e instanceof Corpse) {
                corpses++;
                corpseEnergy += e.getEnergy();
            }
            else if(e instanceof Creature c) {
                creatures++;
                Genome g = c.getGenome();

                if(c.getGrowth() >= 1) adults++;
                if(g.diet() < 1.0/3) herbivores++;
                else if(g.diet() > 0.65) carnivores++;
                else omnivores++;

                radius += c.getRadius();         // size right now
                adultRadius += g.radius();       // size it will be when grown
                growth += c.getGrowth();
                birthScale += g.birthScale();
                energyFull += c.getEnergy() / c.getMaxEnergy();

                maxSpeed += g.maxSpeed();
                diet += g.diet();
                viewRange += g.viewRange();
                fieldOfView += g.fieldOfView();
                mutationRate += g.mutationRate();
                dietMin = Math.min(dietMin, g.diet());
                dietMax = Math.max(dietMax, g.diet());
            }
        }

        // averages (avoid dividing by 0 if everything is dead)
        int n = Math.max(creatures, 1);
        double day = new World.SimTime(tick).totalSeconds() / 86400.0;

        out.printf(Locale.ROOT,
            "%d,%.2f,%d,%d,%d,"
            + "%d,%d,%d,%d,%d,"
            + "%d,%d,%d,%d,"
            + "%.3f,%.3f,%.3f,%.3f,%.3f,"
            + "%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,"
            + "%.1f,%.1f%n",
            tick, day, creatures, plants, corpses,
            adults, creatures - adults, herbivores, omnivores, carnivores,
            births, deaths, killed, immigrants,
            radius / n, adultRadius / n, growth / n, birthScale / n, energyFull / n,
            maxSpeed / n, diet / n, dietMin, dietMax, viewRange / n, Math.toDegrees(fieldOfView / n), mutationRate / n,
            plantEnergy, corpseEnergy);
        // write to disk now, so the file is up to date even if the sim crashes
        out.flush();

        births = deaths = killed = immigrants = 0;
    }
}
