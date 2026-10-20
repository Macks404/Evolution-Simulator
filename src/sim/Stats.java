package sim;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

public class Stats {
    private PrintWriter out;
    private final int interval; // record a row every this many ticks

    Stats(String fileName, int interval) {
        this.interval = interval;
        try {
            // false = start a fresh file each run instead of adding to the old one
            out = new PrintWriter(new FileWriter(fileName, false));
            out.println("tick,creatures,plants,corpses,"
                + "radius,maxSpeed,diet,dietMin,dietMax,viewRange,fieldOfView,mutationRate");
        }
        catch(IOException e) {
            System.out.println("Stats disabled, couldn't open " + fileName + ": " + e);
        }
    }

    public void record(long tick, List<Entity> entities) {
        if(out == null || tick % interval != 0) return;

        int creatures = 0, plants = 0, corpses = 0;
        double radius = 0, maxSpeed = 0, diet = 0, viewRange = 0, fieldOfView = 0, mutationRate = 0;
        double dietMin = 1, dietMax = 0;

        for(Entity e : entities) {
            if(e instanceof Plant) plants++;
            else if(e instanceof Corpse) corpses++;
            else if(e instanceof Creature c) {
                creatures++;
                Genome g = c.getGenome();
                radius += g.radius();
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
        out.printf(Locale.ROOT, "%d,%d,%d,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f%n",
            tick, creatures, plants, corpses,
            radius / n, maxSpeed / n, diet / n, dietMin, dietMax,
            viewRange / n, Math.toDegrees(fieldOfView / n), mutationRate / n);
        // write to disk now, so the file is up to date even if the sim crashes
        out.flush();
    }
}
