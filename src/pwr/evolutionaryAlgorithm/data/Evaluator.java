package pwr.evolutionaryAlgorithm.data;

import java.util.ArrayList;
import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.individual.Individual;
import pwr.evolutionaryAlgorithm.individual.Rule;
import pwr.evolutionaryAlgorithm.individual.RuleSet;

/**
 * Dlaczego singleton, a nie klasa z metodami i polami statycznymi?
 *
 * Singleton class
 */
public class Evaluator {

    /**
     * Używane tylko przez BB - best breed (?)
     */
    private ArrayList<DataSet> data = null;
    private int individualPointer = 0;
    private final Configuration config = Configuration.getConfiguration();
    private final int numberOfAttributes = config.getNumberOfAttributes();
    private final boolean oneClassActive = config.isOneClassActive();
    private final int activeClass = config.getActiveClass();
    private final int classNo = DataLoader.getClassNumber();
    private final int popSize = config.getPopSize();
    static private Evaluator e = null;

    static public Evaluator getEvaluator() {
        if (e == null) {
            e = new Evaluator();
        }
        return e;
    }

    /**
     * private constructor
     */
    private Evaluator() {
    }

    public void evaluate(DataSource DSc, Individual I) {
        if (I instanceof RuleSet) {
            RuleSet ruleSet = (RuleSet) I;
            ruleSet.evaluate(DSc);
        } else if (I instanceof Rule) {
            Rule rule = (Rule) I;
            rule.evaluate(DSc);
        }
    }

    /**
     *
     * @param dSrc zbiór danych, jak zbiór testowy albo treningowy
     * @param rule reguła dla której ma być to wszystko przeliczone
     * @return
     */
    private DataSet evaluateRule(DataSource dSrc, Rule rule) {
        // Algorytm potrafi budować klasyfikator dla pojedynczej klasy,
        // zarówno jak i dla wszystkich na raz. Tutaj jest to uwzględniane.
        int activeCls = oneClassActive ? activeClass : rule.getClassID();

        // Tutaj wykorzystywany jest indeks i przeszukiwanie binarne.
        // Wybierane są te rekordy, których dotyczy reguła rule.
        DataSet ds = rule.getCoveredDataSet(dSrc);

        // Ok, czyli mamy rekordy, których dotyczy reguła rule.
        // Teraz oceniany jest zbiór danych (tak, dokładnie: DataSet),
        // wiedząc, że pochodzi ze źródła dSrc (co pozwala wyznaczyć
        // ile jest wszystkich rekordów, a nie tylko ze zbioru DataSet
        // itp.).
        Evaluation evl = ds.evaluate(dSrc, activeCls);

        // Po tej całej przeprawie mamy DataSet, który posiada pełne
        // statystyki dla pojedynczej klasy. Zapisujemy te statystyki
        // w regule:
        rule.setEvaluation(evl);

        // Zwróć oceniony DataSet -- tylko po co?
        return ds;
    }

    /**
     * <p>Mamy regułę
     * <pre>
     * długość IN <1,2> and szerokość IN<2,3>
     * </pre>
     *
     * <p>Dla niej ta metoda zwróci {@code DataSet }zawierający wszystkie
     * {@code Record }, które mogą zostać rozpatrywane przez tą regułę.
     *
     * <p>Dla podanego przykładu zwróci te rekordy które mają odpowiednią
     * długość i szerokość (jednocześnie).
     *
     * @param dSrc
     * @param r
     * @return
     */
    public DataSet getCoveredDataSet(DataSource dSrc, Rule r) {
        DataSet dSet = new DataSet();
        int att = 0;
        //for first review -> search for first enabled attribute
        // Reguła może mieć wyłączone warunki, więc szukamy pierwszego
        // włączonego
        for (att = 0; att < numberOfAttributes; att++) {
            if (r.isCondition(att)) {
                // Tutaj dzieje się magia rodem z opowieści o bazach danych.
                // Ta metoda zwraca DataSet spełniający warunek c używając
                // do tego przeszukiwania binarnego.
                dSet = dSrc.getDataSet(r.getCondition(att));
                break;
            }
        }

        if (att != numberOfAttributes && dSet != null) {
            // Znaleziono pierwszy aktywny Condition o numerze att.
            // Warunek jest spełniony dla rekordów zawartych w cnd.
            // Pozostaje uwzględnić resztę warunków z reguły r.
            do {
                if (r.isCondition(att)) {
                    dSet.filter(r.getCondition(att));
                }
                att++;
            } while (att < numberOfAttributes && !dSet.empty());
        }

        //  / A co gdy pierwszy włączony warunek zwraca pusty zbiór?
        // / Wtedy wiemy, że żaden rekord nie spełnia pierwszego warunku,
        /// a w związku z tym, że reguła jest _koniunkcją_ warunków, to
        // dalsze sprawdzenia są niepotrzebne.
        return dSet;
    }

    /**
     * Best breed implementation
     */
    public void clearBB() {
        if (data == null) {
            data = new ArrayList<DataSet>(popSize);
        }
        individualPointer = 0;
    }

    public void evaluateBB(DataSource DSc, Individual I) {
        // DataSet DSgenerated = EvaluateRuleSet(DSc, (RuleSet) I);
        // DataSet DScorrect = getAllCorrectClassified(DSgenerated, (RuleSet) I);
        // data.set(individualPointer++, DScorrect);
    }

    /**
     * returns classification report for selected rule
     *
     * @param dSrc datasource
     * @param ds dataset of generated data
     * @param r Selected rule
     * @return
     * @see FullClassificationReport
     */
    protected String ClassificationReport(DataSource dSrc, DataSet ds, Rule r) {
        final StringBuilder sb = new StringBuilder();
        final int clazz = r.getClassID();
        final long correct = ds.getCorrectCount(clazz);
        int cl = oneClassActive ? activeClass : clazz;
        final long expected = dSrc.getExpected(cl);
        final long generated = ds.size();

        sb.append("\n").append(r.toString()).append("\n");
        sb.append(" Generated=").append(generated);
        sb.append(" Correct=").append(correct);
        sb.append(" Expected=").append(expected);
        sb.append(" UnCorrect=").append(generated - correct);

        for (Record record : ds) {
            if (!record.hasClass(clazz)) {
                sb.append("\n---").append(record.toString());
            }
        }
        return sb.toString();
    }
}
