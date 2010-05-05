package pwr.evolutionaryAlgorithm.data;

import pwr.evolutionaryAlgorithm.Configuration;

public class Evaluation {

    protected float Precision;
    protected float Recall;
    protected float Fitness;
    protected float Fsc;
    protected float Accuracy;
    protected boolean done;

    public float getFsc() {
        return this.Fsc;
    }

    public void setPrecision(float f) {
        this.Precision = f;
        this.done = true;
    }

    public void setRecall(float f) {
        this.Recall = f;
        this.done = true;
    }

    public float getPrecision() {
        return this.Precision;
    }

    public float getRecall() {
        return this.Recall;
    }

    public float getAccuracy() {
        return this.Accuracy;
    }

    public void setAccuracy(float acc) {
        this.Accuracy = acc;
        this.done = true;
    }

    public void setFitness(float f) {
        this.Fitness = f;
        this.done = true;
    }

    public float getFitness() {
        return this.Fitness;
    }

    public void clear() {
        Precision = 0;
        Recall = 0;
        Fitness = 0;
        Fsc = 0;
        Accuracy = 0;
        this.done = false;
    }

    public Evaluation(final Evaluation E) {
        this.Precision = E.Precision;
        this.Recall = E.Recall;
        this.Fitness = E.Fitness;
        this.Fsc = E.Fsc;
        this.Accuracy = E.Accuracy;
        this.done = true;
    }

    public Evaluation(float P, float R, float Fsc, float Acc) {
        this.Precision = P;
        this.Recall = R;

        if (Configuration.getConfiguration().isFsc()) {
            this.Fitness = Fsc;
        } else {
            this.Fitness = Acc;
        }

        this.Fsc = Fsc;
        this.Accuracy = Acc;
        this.done = true;
    }

    public void set(float P, float R, float Fsc, float Acc) {
        this.Precision = P;
        this.Recall = R;

        if (Configuration.getConfiguration().isFsc()) {
            this.Fitness = Fsc;
        } else {
            this.Fitness = Acc;
        }

        this.Fsc = Fsc;
        this.Accuracy = Acc;
        this.done = true;
    }
//	------------------------------------------------------------------------------	

    public void update(final Evaluation E) {
        this.Precision = this.Precision + E.Precision;
        this.Recall = this.Recall + E.Recall;

        if (Configuration.getConfiguration().isFsc()) {
            this.Fitness = this.Fitness + E.Fsc;
        } else {
            this.Fitness = this.Fitness + E.Accuracy;
        }

        this.Fsc = this.Fsc + E.Fsc;
        this.Accuracy = this.Accuracy + E.Accuracy;
        this.done = false;
    }

    public void doAverage(int classes) {
        this.Precision = this.Precision / classes;
        this.Recall = this.Recall / classes;

        if (Configuration.getConfiguration().isFsc()) {
            this.Fitness = this.Fsc / classes;
        } else {
            this.Fitness = this.Accuracy / classes;
        }

        this.Fsc = this.Fsc / classes;
        this.Accuracy = this.Accuracy / classes;
        this.done = true;
    }

    public Evaluation() {
        Precision = Configuration.getConfiguration().getFINTESSDEFAULT();
        Recall = Configuration.getConfiguration().getFINTESSDEFAULT();
        Fitness = Configuration.getConfiguration().getFINTESSDEFAULT();
        Fsc = Configuration.getConfiguration().getFINTESSDEFAULT();
        Accuracy = Configuration.getConfiguration().getFINTESSDEFAULT();
        this.done = false;
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public String toString() {
        StringBuilder SB = new StringBuilder();

        if (this.done) {
            SB.append(" Rec=" + String.format("%.3f", this.Recall)
                    + " Prec=" + String.format("%.3f", this.Precision)
                    + " Acc=" + String.format("%.3f", this.Accuracy)
                    + " Fsc=" + String.format("%.3f", this.Fsc));
        } else {
            SB.append(" --- not used ---");
        }
        return SB.toString();
    }
}

