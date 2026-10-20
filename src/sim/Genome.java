package sim;

import java.util.Random;

public record Genome(
    double radius,
    double maxSpeed,
    double diet,
    double viewRange,
    double fieldOfView,
    double red, double green, double blue,   // 0 to 1 each
    double mutationRate
) {
    public Genome mutate(Random random) {
        return new Genome(
            clamp(nudge(radius,      1.0,  random), 2, 30),
            clamp(nudge(maxSpeed,    0.1,  random), 0.05, 5),
            clamp(nudge(diet,        0.05, random), 0, 1),
            clamp(nudge(viewRange,   5.0,  random), 10, 300),
            clamp(nudge(fieldOfView, 0.1,  random), 0.1, 2 * Math.PI),
            clamp(nudge(red,         0.05, random), 0, 1),
            clamp(nudge(green,       0.05, random), 0, 1),
            clamp(nudge(blue,        0.05, random), 0, 1),
            clamp(mutationRate * Math.exp(random.nextGaussian() * 0.1), 0.01, 5)
        );
    }

    public double distance(Genome other) {
        return Math.abs(radius - other.radius) / 28
            + Math.abs(maxSpeed - other.maxSpeed) / 4.95
            + Math.abs(diet - other.diet)
            + Math.abs(viewRange - other.viewRange) / 290
            + Math.abs(fieldOfView - other.fieldOfView) / (2 * Math.PI - 0.1)
            + (
                Math.abs(red - other.red)
                + Math.abs(green - other.green)
                + Math.abs(blue - other.blue)
            ) / 3;
    }

    private double nudge(double value, double step, Random random) {
        return value + random.nextGaussian() * step * mutationRate;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}