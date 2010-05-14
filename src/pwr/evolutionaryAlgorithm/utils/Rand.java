package pwr.evolutionaryAlgorithm.utils;

import org.uncommons.maths.random.XORShiftRNG;

/**
 *
 * @author pawelm
 */
public class Rand {

//    private static final Random rnd = new Random();
    private static final XORShiftRNG rnd = new XORShiftRNG(new byte[]{
                116, -47, -76, -32, -73, -118, -127, 120, 122, -4, -65, 23, -47, -58, 55, 13, 64, 120, 86, 124
            });

    public static int getRandomInt(int maxVal) {
        return rnd.nextInt(maxVal);
    }

    public static float GetRandomFloat() {
        return rnd.nextFloat();
    }

    public static boolean getRandomBoolean() {
        return rnd.nextBoolean();
    }

    public static boolean getRandomBooleanFlip(float border) {
        return rnd.nextFloat() <= border;
    }

    private Rand() {
    }
}
