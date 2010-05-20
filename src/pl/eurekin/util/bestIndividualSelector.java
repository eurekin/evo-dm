/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.eurekin.util;

import pwr.evolutionaryAlgorithm.Population;
import pwr.evolutionaryAlgorithm.individual.Individual;

/**
 *
 * @param <I>
 * @author Rekin
 */
public class BestIndividualSelector<I extends Individual> {

    private I best;
    private boolean betterThanLastOne;

    /**
     * 
     * @param individual to copy if better than last one kept
     */
    @SuppressWarnings("unchecked")
    public void rememberIfBest(I individual) {
        if (best == null) {
            best = (I) individual.getACopy();
            betterThanLastOne = false;
            return;
        }

        if (individual.getFitness() > best.getFitness()) {
            best = (I) individual.getACopy();
            betterThanLastOne = true;
        } else {
            betterThanLastOne = false;
        }
    }

    public void rememberBestFrom(Population<I> pop) {
        rememberIfBest(pop.getBestIndividual());
    }

    public boolean isBetterThanLastOne() {
        return betterThanLastOne;
    }

    public I getBest() {
        return best;
    }

}
