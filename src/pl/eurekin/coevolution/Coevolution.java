/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.eurekin.coevolution;

import EvolutionaryAlgorithm.Population;
import org.apache.log4j.Logger;

/**
 *
 * @author Rekin
 */
public class Coevolution {

    public static final Logger LOG = Logger.getLogger(Coevolution.class);
    private Population selectors;
    private Population classifiers;

    public Coevolution() {
        createPopulations();
    }

    public void evolve() {
        LOG.trace("Starting evolution.");
       
    }

    private void createPopulations() {
        selectors = new Population(new SelectingIndividual());
        classifiers = new Population(new ClassifyingIndividual());
    }

    public static void main(String... args) {
        LOG.trace("Starting main");

        LOG.trace("Ending main");
    }
}
