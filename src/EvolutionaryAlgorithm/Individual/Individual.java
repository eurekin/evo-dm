package evolutionaryAlgorithm.individual;

import java.util.ArrayList;
import data.Evaluation;

public abstract class Individual {

    protected ArrayList<Evaluation> Evaluations;

    public void clearEvaluations() {
        for (int i = 0; i < this.Evaluations.size(); i++) {
            this.Evaluations.get(i).clear();
        }
    }

    /**
     * XXX only for class number ZERO?!?
     *
     * @return
     */
    public void setEvaluation(Evaluation E) {
        setEvaluation(0, E);
    }

    /**
     * XXX only for class number ZERO?!?
     *
     * @return
     */
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

    public abstract void Initialise();

    public abstract Individual Mutation();

    public abstract Individual Crossover(final Individual Indv1, Individual Indv2) throws Exception;

    public abstract int diversityMeasure(final Individual I) throws Exception;

    protected abstract int getGenesInIndividual() throws Exception;

    @Override
    public abstract String toString();
}

