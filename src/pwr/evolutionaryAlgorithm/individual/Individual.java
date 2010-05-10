package pwr.evolutionaryAlgorithm.individual;

import java.util.ArrayList;
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

    public abstract Individual crossoverWith(final Individual Indv1);

    public abstract int diversityMeasure(final Individual I);

    protected abstract int getGenesInIndividual();

    @Override
    public abstract String toString();
}

