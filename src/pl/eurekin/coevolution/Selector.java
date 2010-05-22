package pl.eurekin.coevolution;

import java.util.BitSet;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.DataSet;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.data.Evaluation;
import pwr.evolutionaryAlgorithm.data.Record;
import pwr.evolutionaryAlgorithm.individual.Individual;
import pwr.evolutionaryAlgorithm.individual.RuleSet;

/**
 *
 * @author Rekin
 */
public class Selector extends Individual {

    private final BitSet chromosome;
    private Evaluation evaluation;

    public Selector(Selector sel) {
        this.chromosome = sel.chromosome;
        this.evaluation = sel.evaluation;
    }

    public Selector(int length) {
        this.chromosome = new BitSet(length);
        this.evaluation = new Evaluation(1, 1, 1, 1);
    }

    public Selector() {
        this(DataLoader.getTrainData().size());
    }

    @Override
    public void init() {
        // it seeems the constructor is doing all
        // the necessary work
    }

    @Override
    public Individual mutate() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Have to override specific behavior of superclass
    // (Wasn't the specific classes for this stuff?)
    @Override
    public Evaluation getEvaluation() {
        return evaluation;
    }

    @Override
    public Evaluation getEvaluation(int cl) {
        return evaluation;
    }

    @Override
    public void evaluate(DataSource dSrc) {
        // Evaluate on dSrc? Doesn't work this way...
        // Selector has to be evaluated on corresponding
        // classiffying individual - RuleSet to be exact.
    }

    public void evaluateUsingClassifier(RuleSet c) {
        // TODO: use other strategies
        evaluation = new Evaluation(
                1 - c.getPrecision(),
                1 - c.getRecall(),
                1 - c.getFsc(),
                1 - c.getAccuracy());
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

    @Override
    public Individual getACopy() {
        return new Selector(this);
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
        for (Record rec : toAccept) {
            if (selected(rec)) {
                result.addRecord(rec);
            }
        }
        return result;
    }

    private boolean selected(Record rec) {
        // watch out for the NEGATION - it's temporary test fixture
        return  !chromosome.get(rec.getId());
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
