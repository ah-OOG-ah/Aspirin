package klaxon.klaxon.aspirin;

import java.util.*;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class Timer {

    // The time from which we log. Init at 0, because it's used at the very start
    public static long START = 0;

    // The master list of times. The first key is the index, the pair is the phase name and the time.
    public static Map<Integer, Pair<String, Long>> TIMES = new HashMap<>();

    // This is the internal index of phases; the user-facing index is shifted by however much is needed to make
    // Constructing Mods phase 0
    public static int INDEX = 0;
    // This must be set later, as internal indexes are dynamic
    public static int SHIFT = -1;

    // Logs a phase. What else did you expect?
    public static long logPhase(String name) {

        // Get the time delta from t0 to now
        long delta = System.nanoTime() - Timer.START;
        Timer.TIMES.put(INDEX, new ImmutablePair<>(name, delta));
        ++INDEX;

        // Technically not needed, just a convenience for reporting
        return delta;
    }
}
