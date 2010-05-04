package pwr.evolutionaryAlgorithm.utils;

import pl.eurekin.util.MersenneTwisterFast;

/**
 *
 * @author pawelm
 */
public class Rand {

    // private static final Random rnd = new Random();
    private static final MersenneTwisterFast rnd = new MersenneTwisterFast();
//  private static final MersenneTwister rnd= new MersenneTwister();

    public static int getRandomInt(int maxVal) {
        return rnd.nextInt(maxVal);
    }

    public static float GetRandomFloat() {
        return rnd.nextFloat();
    }

    public static boolean GetRandomBoolean() {
        return rnd.nextBoolean();
    }

    public static boolean getRandomBooleanFlip(float border) {
        return rnd.nextFloat() <= border;
    }

    public static void main (String ... args) {
        System.out.println(
        Long.toHexString(Double.doubleToLongBits(Math.random())));
    }
}
