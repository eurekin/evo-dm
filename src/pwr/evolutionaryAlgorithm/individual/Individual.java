package pwr.evolutionaryAlgorithm.individual;

import java.util.ArrayList;
import pwr.evolutionaryAlgorithm.data.Evaluation;

public abstract class Individual {

    protected ArrayList<Evaluation> Evaluations;

    public void clearEvaluations() {
        for (int i = 0; i < this.Evaluations.size(); i++) {
            this.Evaluations.get(i).clear();
        }
    }

    public void setEvaluation(Evaluation E) {
        setEvaluation(0, E);
    }

    public Evaluation getEvaluation() {
        return getEvaluation(0);
    }

    public Evaluation getEvaluation(int cl) {
        if (cl < Evaluations.size() && Evaluations.get(cl) != null) {
            return Evaluations.get(cl);
        } else {
            return null;
        }
    }

    public void setEvaluation(int cl, Evaluation E) {
        Evaluations.add(cl, E);
    }

    public abstract void Initialize();

    public abstract Individual Mutation();

    public abstract Individual crossoverWith(final Individual Indv1);

    public abstract int diversityMeasure(final Individual I);

    protected abstract int getGenesInIndividual();

    @Override
    public abstract String toString();
}

