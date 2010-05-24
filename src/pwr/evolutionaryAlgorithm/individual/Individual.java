package pwr.evolutionaryAlgorithm.individual;

import java.util.ArrayList;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.data.Evaluation;

public abstract class Individual {

    protected ArrayList<Evaluation> evaluations;

    public void clearEvaluations() {
        for (Evaluation evl : evaluations) {
            evl.clear();
        }
    }

    public void setEvaluation(Evaluation E) {
        setEvaluation(0, E);
    }

    public abstract Individual getACopy();

    public abstract Evaluation getEvaluation();

    public Evaluation getEvaluation(int cl) {
        return evaluations.get(cl);
    }

    public void setEvaluation(int cl, Evaluation evl) {
        evaluations.set(cl, evl);
    }

    public abstract void init();

    public abstract Individual mutate();

    /**
     *
     * @param dSrc
     */
    public abstract void evaluate(DataSource dSrc);

    public abstract Individual crossoverWith(final Individual Indv1);

    public int diversityMeasure(final Individual I) {
        throw new UnsupportedOperationException("Phi...");
    }

    protected int getGenesInIndividual() {
        return -1;
    }

    @Override
    public abstract String toString();

    // Convenience delegates
    public float getFsc() {
        return getEvaluation().getFsc();
    }

    public float getFitness() {
        return getEvaluation().getFitness();
    }

    public float getRecall() {
        return getEvaluation().getRecall();
    }

    public float getAccuracy() {
        return getEvaluation().getAccuracy();
    }

    public float getPrecision() {
        return getEvaluation().getPrecision();
    }
}
