package pl.eurekin.coevolution;

import java.util.BitSet;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.DataSet;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.data.Record;
import pwr.evolutionaryAlgorithm.individual.Individual;
import pwr.evolutionaryAlgorithm.individual.RuleSet;

/**
 *
 * @author Rekin
 */
public class SelectingIndividual extends Individual {

    private final BitSet chromosome;

    public SelectingIndividual(int length) {
        this.chromosome = new BitSet(length);
    }

    public SelectingIndividual() {
        this(DataLoader.getTrainData().size());
    }

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
    public Individual crossoverWith(Individual ind) {
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

    void evaluateUsingClassifier(RuleSet c) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Individual getACopy() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Raison d'etre of this class
     *
     * @param toAccept
     * @return
     */
    public DataSet filter(DataSet toAccept) {
        DataSet result = new DataSet(toAccept.size());

        // TODO selecting implementation placeholder
        for (Record rec : result) {
            if (selected(rec)) {
                result.addRecord(rec);
            }
        }
        return result;
    }

    private boolean selected(Record rec) {
        // watch out for the NEGATION - it's temporary test fixture
        return !chromosome.get(rec.getId());
    }

    /**
     *
     * @param dSrc
     * @param c
     * @return
     */
    public int count(DataSource dSrc, int c) {
        return dSrc.getExpected(c);
    }
}
