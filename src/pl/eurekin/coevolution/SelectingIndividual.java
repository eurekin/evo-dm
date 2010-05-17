package pl.eurekin.coevolution;

import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.individual.Individual;

/**
 *
 * @author Rekin
 */
public class SelectingIndividual extends Individual {

    @Override
    public void init() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Individual mutate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void evaluate(DataSource dSrc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Individual crossoverWith(Individual Indv1) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int diversityMeasure(Individual I) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected int getGenesInIndividual() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    void evaluateUsingClassifier(ClassifyingIndividual c) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


}
