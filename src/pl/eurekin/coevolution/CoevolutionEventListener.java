package pl.eurekin.coevolution;

import pwr.evolutionaryAlgorithm.Population;
import pwr.evolutionaryAlgorithm.individual.RuleSet;

/**
 *
 * @author Rekin
 */
public interface CoevolutionEventListener {

    void evolvedAGeneration(Population<Selector> sels, Population<RuleSet> cls);
}
