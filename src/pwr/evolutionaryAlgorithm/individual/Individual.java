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

    public Evaluation getEvaluation() {
        return getEvaluation(0);
    }

    public Evaluation getEvaluation(int cl) {
        if (cl < evaluations.size() && evaluations.get(cl) != null) {
            return evaluations.get(cl);
        } else {
            return null;
        }
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

    public abstract int diversityMeasure(final Individual I);

    protected abstract int getGenesInIndividual();

    @Override
    public abstract String toString();

    // Convinience delegates
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
