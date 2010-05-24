package pl.eurekin.coevolution;

import java.util.Arrays;
import java.util.BitSet;
import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.DataSet;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.data.Evaluation;
import pwr.evolutionaryAlgorithm.data.Record;
import pwr.evolutionaryAlgorithm.individual.Individual;
import pwr.evolutionaryAlgorithm.individual.RuleSet;
import pwr.evolutionaryAlgorithm.utils.Rand;

/**
 *
 * @author Rekin
 */
public class Selector extends Individual {

    private final BitSet chromosome;
    private Evaluation evaluation;
    private final int[] classCount;
    private final float mutationProb;

    public Selector(Selector sel) {
        this.chromosome = sel.chromosome;
        this.evaluation = new Evaluation(sel.evaluation);
        this.mutationProb = sel.mutationProb;
        this.classCount = Arrays.copyOf(sel.classCount, sel.classCount.length);
    }

    public Selector(int length) {
        final Configuration config = Configuration.getConfiguration();
        this.evaluation = new Evaluation(1, 1, 1, 1);
        this.mutationProb = config.getCoevSelMutationProb();
        this.chromosome = getRandomBitset(length);
        this.classCount = calcClassCount();
    }

    private static BitSet getRandomBitset(int length) {
        BitSet result = new BitSet(length);
        for (int i = 0; i < result.size(); i++) {
            if (Rand.getRandomBoolean()) {
                result.flip(i);
            }
        }
        return result;
    }

    private int[] calcClassCount() {
        DataSource data = DataLoader.getTrainData();
        int[] result = new int[DataLoader.getClassNumber()];
        for (int i = 0; i < data.size(); i++) {
            Record rec = data.get(i);
            if (isSelected(rec)) {
                result[rec.getClassName()]++;
            }
        }
        return result;
    }

    public Selector() {
        this(DataLoader.getTrainData().size());
    }

    @Override
    public void init() {
        // it seeems the constructor is doing all
        // the necessary work
    }

    /**
     * Simple bit mutation.
     * 
     * @return
     */
    @Override
    public Individual mutate() {
        Selector result = new Selector(this);
        for (int i = 0; i < chromosome.size(); i++) {
            if (Rand.getRandomBooleanFlip(mutationProb)) {
                chromosome.flip(i);
            }
        }
        return result;
    }

    @Override
    public Individual crossoverWith(Individual ind) {
        Selector result = new Selector(this);
        Selector other = (Selector) ind;
        final int crossPoint = Rand.getRandomInt(chromosome.length());


        for (int i = 0; i < crossPoint; i++) {
            result.chromosome.set(i, this.chromosome.get(i));

        }
        for (int i = crossPoint; i < chromosome.length(); i++) {
            result.chromosome.set(i, other.chromosome.get(i));

        }
        return result;
    }

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
        
        // can't throw
        // have to be noop
        // throw new UnsupportedOperationException("use evaluateUsingClassifier()");
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
            if (isSelected(rec)) {
                result.addRecord(rec);
            }
        }
        return result;
    }

    private boolean isSelected(Record rec) {
        assert rec.getId() < chromosome.length();
        return chromosome.get(rec.getId());
    }

    /**
     *
     * @param dSrc
     * @param c
     * @return
     */
    public int count(DataSource dSrc, int c) {
        return classCount[c];
    }
}
