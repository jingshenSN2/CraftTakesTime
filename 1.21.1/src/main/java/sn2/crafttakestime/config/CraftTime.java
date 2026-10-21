package sn2.crafttakestime.config;

/** Pure duration policy, shared by server logic and regression tests. */
public final class CraftTime {
    private CraftTime() {}
    public static int ticks(double ingredients, double output, double global, double container, int level) {
        return ticks(ingredients, output, global, container, level, 1);
    }
    public static int ticks(double ingredients, double output, double global, double container, int level, double attributeSpeed) {
        double speed = (1 + 0.02 * Math.clamp(level, 0, 200)) * attributeSpeed;
        double duration = 20 * ingredients * output * global * container / speed;
        // A zero multiplier still completes on a server tick, never inside a click callback.
        return (int) Math.clamp(Math.ceil(duration), 1, Integer.MAX_VALUE);
    }
}
