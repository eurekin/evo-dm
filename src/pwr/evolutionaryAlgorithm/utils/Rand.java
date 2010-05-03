/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pwr.evolutionaryAlgorithm.utils;

import java.util.Random;

/**
 *
 * @author pawelm
 */
public class Rand {

    private static Random rnd = new Random();
    // private static final MersenneTwisterFast rnd = new MersenneTwisterFast();
//  private static final MersenneTwister rnd= new MersenneTwister();

    private static void initialise() {
        //if (rnd==null) rnd = new Random();
        // if(rnd==null) rnd = new MersenneTwisterFast();
    }

    public static int GetRandomInt(int maxVal) {
        if (rnd == null) {
            initialise();
        }
        return rnd.nextInt(maxVal);
    }

    public static float GetRandomFloat() {
        if (rnd == null) {
            initialise();
        }
        return rnd.nextFloat();
    }

    public static boolean GetRandomBoolean() {
        if (rnd == null) {
            initialise();
        }
        return rnd.nextBoolean();
    }

    public static boolean GetRandomBooleanFlip(float border) {
        if (rnd == null) {
            initialise();
        }
        if (Rand.GetRandomFloat() <= border) {
            return true;
        } else {
            return false;
        }
    }
}
