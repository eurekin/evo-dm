package pwr.evolutionaryAlgorithm.data;

import pwr.evolutionaryAlgorithm.Configuration;

/**
 * RuleSet używa metod {@link #doAverage(int) } oraz {@link #update() } do
 * obliczania ocen.
 *
 * <p>Pytanie co to oznacza zmienna {@link #done } Ustawiana jest: <ol>
 * <li> na {@code true} przy ustawianiu jednej z ocen:
 * precision, recall, accuracy, fitness
 * ({@link #setPrecision(float)}, {@link #setRecall(float)},
 * {@link #setAccuracy(float)}, {@link #setFitness(float)}),</li>
 * <li> na {@code true} w konstruktorze kopiującym
 * ( {@link #Evaluation(Evaluation) } konstruktor ten NIE
 * kopiuje wartości zmiennej {@code done}),</li>
 * <li> na {@code true} w metodzie
 * {@link #set(float, float, float, float) },</li>
 * <li>na {@code true} w metodzie {@link #clear() },</li>
 * <li>na  {@code false} w metodzie {@link #update(Evaluation) },</li>
 * <li>na {@code true} w metodzie {@link #doAverage(int) }</li>.
 * </ol>
 *
 * <p>Wydaje się, że jest to flaga służąca rozpoznaniu czy wykonana
 * została średnia ocen po wszystkich klasach. {@code #RuleSet } używa
 * tych metod właśnie do tego celu.</p>
 * @author Rekin
 */
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

    public Evaluation(DataSet ds) {
        this(ds.getPrecision(), ds.getRecall(), ds.getFsc(), ds.getAccuracy());
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

    /**
     * Liczy średnią arytmetyczną statystyk.
     * @param classes
     */
    public void doAverage(int classes) {
        this.Precision = this.Precision / classes;
        this.Recall = this.Recall / classes;

        this.Fsc = this.Fsc / classes;
        this.Accuracy = this.Accuracy / classes;

        if (Configuration.getConfiguration().isFsc()) {
            this.Fitness = this.Fsc;
        } else {
            this.Fitness = this.Accuracy;
        }
        this.done = true;
    }

    public Evaluation() {
        Fsc = Configuration.getConfiguration().getFITNESS_DEFAULT();
        Recall = Configuration.getConfiguration().getFITNESS_DEFAULT();
        Fitness = Configuration.getConfiguration().getFITNESS_DEFAULT();
        Accuracy = Configuration.getConfiguration().getFITNESS_DEFAULT();
        Precision = Configuration.getConfiguration().getFITNESS_DEFAULT();
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

