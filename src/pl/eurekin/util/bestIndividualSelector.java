/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.eurekin.util;

import pwr.evolutionaryAlgorithm.individual.Individual;

/**
 *
 * @param <I>
 * @author Rekin
 */
public class bestIndividualSelector<I extends Individual> {

    private I best;

    public void rememberIfBest(I ind) {
        if (best == null) {
            best = ind;
            return;
        }

        if (ind.getFitness() > best.getFitness()) {
            best = ind;
        }
    }

    public I getBest() {
        return best;
    }
}
