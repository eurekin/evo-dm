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
 * <li>na {@code false} w metodzie {@link #update(Evaluation) },</li>
 * <li>na {@code true} w metodzie {@link #doAverage(int) }</li>.
 * </ol>
 *
 * <p>Wydaje się, że jest to flaga służąca rozpoznaniu czy wykonana
 * została średnia ocen po wszystkich klasach. {@code #RuleSet } używa
 * tych metod właśnie do tego celu.</p>
 * @author Rekin
 */
public class Evaluation {

    private final Configuration config = Configuration.getConfiguration();
    private float precision;
    private float recall;
    private float fitness;
    private float Fsc;
    private float accuracy;
    private boolean done;

    public float getFsc() {
        return this.Fsc;
    }

    public void setPrecision(float f) {
        this.precision = f;
        this.done = true;
    }

    public void setRecall(float f) {
        this.recall = f;
        this.done = true;
    }

    public float getPrecision() {
        return this.precision;
    }

    public float getRecall() {
        return this.recall;
    }

    public float getAccuracy() {
        return this.accuracy;
    }

    public void setAccuracy(float acc) {
        this.accuracy = acc;
        this.done = true;
    }

    public void setFitness(float f) {
        this.fitness = f;
        this.done = true;
    }

    public float getFitness() {
        return this.fitness;
    }

    public void clear() {
        Fsc = 0;
        recall = 0;
        fitness = 0;
        accuracy = 0;
        precision = 0;
        this.done = false;
    }

    public Evaluation(final Evaluation E) {
        this.Fsc = E.Fsc;
        this.recall = E.recall;
        this.fitness = E.fitness;
        this.accuracy = E.accuracy;
        this.precision = E.precision;
        this.done = true;
    }

    public Evaluation(DataSet ds) {
        this(ds.getPrecision(), ds.getRecall(), ds.getFsc(), ds.getAccuracy());
    }

    public Evaluation(float P, float R, float Fsc, float Acc) {
        this.fitness = config.isFsc() ? Fsc : Acc;
        this.accuracy = Acc;
        this.precision = P;
        this.recall = R;
        this.Fsc = Fsc;
        this.done = true;
    }

    public void set(float P, float R, float Fsc, float Acc) {
        this.fitness = config.isFsc() ? Fsc : Acc;
        this.accuracy = Acc;
        this.precision = P;
        this.recall = R;
        this.Fsc = Fsc;
        this.done = true;
    }

    public void update(final Evaluation E) {
        this.fitness += config.isFsc() ? E.Fsc : E.accuracy;
        precision += E.precision;
        accuracy += E.accuracy;
        recall += E.recall;
        Fsc += E.Fsc;
        done = false;
    }

    /**
     * Liczy średnią arytmetyczną statystyk.
     * @param classes
     */
    public void doAverage(int classes) {
        setPrecision(getPrecision() / classes);
        recall /= classes;
        Fsc /= classes;
        accuracy /= classes;
        fitness = config.isFsc() ? Fsc : accuracy;
        done = true;
    }

    public Evaluation() {
        final float fitDef = config.getFITNESS_DEFAULT();
        Fsc = recall = fitness = accuracy = precision = fitDef;
        this.done = false;
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public String toString() {
        if (!done) {
            return " --- not used ---";
        }
        StringBuilder sb = new StringBuilder();
        final String format = "%.3f";
        sb.append(" Rec=").append(String.format(format, recall));
        sb.append(" Prec=").append(String.format(format, precision));
        sb.append(" Acc=").append(String.format(format, accuracy));
        sb.append(" Fsc=").append(String.format(format, Fsc));
        return sb.toString();
    }
}
