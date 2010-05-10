package pwr.evolutionaryAlgorithm.data;

import java.util.ArrayList;
import pl.eurekin.coevolution.ClassifyingIndividual;
import pl.eurekin.coevolution.SelectingIndividual;
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

    static private Evaluator e = null;
    private final Configuration config = Configuration.getConfiguration();
    /**
     * Używane tylko przez BB - best breed (?)
     */
    private ArrayList<DataSet> data = null;
    private int individualPointer = 0;

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
            EvaluateRuleSet(DSc, (RuleSet) I);
        } else if (I instanceof Rule) {
            evaluateRule(DSc, (Rule) I);
        }
    }

    /**
     * evaluates RuleSet
     * @param DSrc datasource (traint/test)
     * @param RuleSet
     * @return DataSet of all covered data
     */
    private DataSet EvaluateRuleSet(DataSource DSrc, RuleSet rSet) {

        DataSet DSPart = new DataSet();
        DataSet DSResult = new DataSet();

        ///////////////////////////////////////////
        /// only one class active!
        if (config.isOneClassActive()) {
            DSResult.clear();
            rSet.clearEvaluations();

            /// for each rule....
            for (int r = 0; r < rSet.rulesNo(); r++) {
                DSPart.clear();
                //if rule is active and returns such class
                if (rSet.getRule(r).isActive()) {

                    // Co to w ogóle zwraca?
                    DSPart = getCoveredDataSet(DSrc, rSet.getRule(r));
                    DSResult = DataSet.operatorPlus(DSResult, DSPart);

                }
            } ///// end: for each rule
            
            /////// CLASS Summary ///////////////
            DSResult.evaluate(DSrc, config.getActiveClass());
            rSet.setEvaluation(new Evaluation(DSResult));
        } // all classes are active
        //for each class....
        else {
            rSet.clearEvaluations();

            ////////////////// CLASSESS ////////////////////////////////////////
            for (int c = 0; c < DataLoader.getClassNumber(); c++) {
                DSPart.clear();
                DSResult.clear();

                //////////////////RULES ////////////////////////////////////////
                Rule rule;
                for (int r = 0; r < rSet.rulesNo(); r++) {
                    rule = rSet.getRule(r);
                    //if rule is active and returns such class
                    if (rule.isActive() && rule.getClassID() == c) {
                        DSPart = getCoveredDataSet(DSrc, rule);
                        DSResult = DataSet.operatorPlus(DSResult, DSPart);
                    }
                }
                ////////////////END: RULES /////////////////////////////////////

                /////// CLASS Summary ///////////////
                DSResult.evaluate(DSrc, c);
                rSet.setEvaluation(c, new Evaluation(DSResult));
            }//////////////////END:CLASSESS ////////////////////////////////////
        }
        rSet.doCountTotalEvaluation(DataLoader.getClassNumber());
        return DSResult;
    }

    /**
     * evaluates RuleSet using specified subset of DataSource
     * @param dSrc datasource (train/test)
     * @param RuleSet
     * @return DataSet of all covered data
     */
    public DataSet evaluateRuleSetUsingSelector(
            DataSource dSrc, SelectingIndividual sl, ClassifyingIndividual ci) {

        DataSet DSetPart = new DataSet();
        DataSet DSetResult = new DataSet();

        ci.clearEvaluations();
        ///////////////////////////////////////////
        /// only one class active!
        if (config.isOneClassActive()) {
            DSetResult.clear();

            /// for each rule....
            for (Rule rule : ci.getRules()) {
                DSetPart.clear();
                //if rule is active and returns such class
                if (rule.isActive()) {
                    DSetPart = getCoveredDataSet(dSrc, rule);
                    DSetResult = DataSet.operatorPlus(DSetResult, DSetPart);
                }
            } ///// end: for each rule
            /////// CLASS Summary ///////////////
            final int activeClass = config.getActiveClass();
            DSetResult.evaluate(dSrc, activeClass);
            ci.setEvaluation(new Evaluation(DSetResult));
        } // all classes are active
        //for each class....
        else {
            ////////////////// CLASSESS ////////////////////////////////////////
            for (int c = 0; c < DataLoader.getClassNumber(); c++) {
                DSetPart.clear();
                DSetResult.clear();

                // Z punktu widzenia implementacji koewolucji najważniejszy
                // jest moment kiedy mogę odfiltrować rekordy. Zostawić tylko
                // te, które wybrane są przez osobnika wybierającego. Problem
                // w tym, żeby zachować poprawność oceniania populacji. Gdzieś
                // trzeba zmodyfikować ocenę, gdyż w koewolucji osobnik klasy-
                // fikujący widzi tylko podzbiór danych.

                // Fill DSetResult with appropriate racords related to class c
                for (Rule rule : ci.getRules()) {
                    //if rule is active and returns such class
                    if (rule.isActive() && rule.getClassID() == c) {
                        // niejawnie (poprzez regułę) zawarta jest
                        // też informacja o wybranej klasie (c)
                        DSetPart = evaluateRule(dSrc, rule);

                        // Powyższe wywołanie budzi we mnie pewne wątpliwości.
                        // Mianowicie, tamta metoda jest przystosowana do
                        // ewaluacji pojedynczej reguły. Czy uruchamiając
                        // ją w pętli, dla każdego osobnika, oraz dla każdej
                        // reguły w osobniku, nie wykonuje ona kilka razy
                        // tej samej pracy?

                        // Gromadzenie statystyk dla wszystkich reguł.
                        // Khem, khem, khem. Tak naprawdę, statystyki są
                        // tutaj pomijane...
                        // Faktyczne statystyki wyliczane są po wyjściu z tej
                        // pętli. Tutaj potrzebne jest jedynie gromadzenie
                        // wybranych rekordów.
                        DSetResult = DataSet.operatorPlus(DSetResult, DSetPart);
                    }
                }
                ////////////////END: RULES /////////////////////////////////////

                // Po poprzednich krokach posiadamy DataSet (DSetResult),
                // który zawiera statystyki (oceny) zebrane dla klasy c.
                DSetResult.evaluate(dSrc, c);
                ci.setEvaluation(c, new Evaluation(DSetResult));
            }//////////////////END:CLASSESS ////////////////////////////////////
        }

        // do average value of all classes -> without unused (or not tested) class
        // Pozostało jedynie policzenie średniej wartości, ze wszystkich klas.
        ci.doCountTotalEvaluation(DataLoader.getClassNumber());
        return DSetResult;
    }

    /**
     *
     * @param dSrc zbiór danych, jak zbiór testowy albo treningowy
     * @param rule reguła dla której ma być to wszystko przeliczone
     * @return
     */
    private DataSet evaluateRule(DataSource dSrc, Rule rule) {
        /** Algorytm potrafi budować klasyfikator dla pojedynczej klasy,
        /* zarówno jak i dla wszystkich na raz. Tutaj jest to uwzględniane.
         */
        int activeClass = config.isOneClassActive()
                ? config.getActiveClass() : rule.getClassID();

        // Tutaj wykorzystywany jest indeks i przeszukiwanie binarne.
        // Wybierane są te rekordy, których dotyczy reguła rule.
        DataSet ds = getCoveredDataSet(dSrc, rule);

        // Ok, czyli mamy rekordy, których dotyczy reguła rule.
        // Teraz oceniany jest zbiór danych (tak, dokładnie: DataSet),
        // wiedząc, że pochodzi ze źródła dSrc (co pozwala wyznaczyć
        // ile jest wszystkich rekordów, a nie tylko ze zbioru DataSet
        // itp.).
        ds.evaluate(dSrc, activeClass);

        // Po tej całej przeprawie mamy DataSet, który posiada pełne
        // statystyki dla pojedynczej klasy. Zapisujemy te statystyki
        // w regule:
        rule.setEvaluation(new Evaluation(ds));

        // Zwróć oceniony DataSet
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
    protected DataSet getCoveredDataSet(DataSource dSrc, Rule r) {
        Condition c = null;
        DataSet cnd = new DataSet();
        int att = 0;
        final int numberOfAttributes = config.getNumberOfAttributes();
        //for first review -> search for first enabled attribute
        // Reguła może mieć wyłączone warunki, więc szukamy pierwszego
        // włączonego
        for (att = 0; att < numberOfAttributes; att++) {
            if (r.isCondition(att)) {
                c = r.getCondition(att);

                // Tutaj dzieje się magia rodem z opowieści o bazach danych.
                // Ta metoda zwraca DataSet spełniający warunek c używając
                // do tego przeszukiwania binarnego.
                cnd = dSrc.getDataSet(c);
                break;
            }
        }

        if (att != numberOfAttributes && cnd != null) {
            // Znaleziono pierwszy aktywny Condition o numerze att.
            // Warunek jest spełniony dla rekordów zawartych w cnd.
            // Pozostaje uwzględnić resztę warunków z reguły r.
            do {
                if (r.isCondition(att)) {
                    c = r.getCondition(att);
                    cnd = dSrc.getDataSet(cnd, c);
                }
                att++;
            } while (att < numberOfAttributes && !cnd.empty());
        }

        // A co gdy pierwszy włączony warunek zwraca pusty zbiór?
        // Wtedy wiemy, że żaden rekord nie spełnia pierwszego warunku,
        // a w związku z tym, że reguła jest _koniunkcją_ warunków, to
        // dalsze sprawdzenia są niepotrzebne.
        return cnd;
    }

    private DataSet getAllCorrectClassified(DataSource DSc,
            DataSet DSgenerated, RuleSet RS) {
        DataSet DSpart, DScorrect = new DataSet();

        int rules = RS.rulesNo();

        for (int r = 0; r < rules; r++) {
            DSpart = DSc.getCorrect(DSgenerated, RS.getRule(r).getClassID());
            DScorrect = DataSet.operatorPlus(DScorrect, DSpart);
        }
        return DScorrect;
    }

    /**
     * Best breed implementation
     */
    public void clearBB() {
        int pop_size = config.getPopSize();
        if (data == null) {
            data = new ArrayList<DataSet>(pop_size);
        }
        individualPointer = 0;
    }

    public void evaluateBB(DataSource DSc, Individual I) {
        DataSet DSgenerated = EvaluateRuleSet(DSc, (RuleSet) I);
        DataSet DScorrect = getAllCorrectClassified(DSc, DSgenerated, (RuleSet) I);
        data.set(individualPointer++, DScorrect);
    }

    /**
     * returns classification report for selected rule
     *
     * @param dSrc datasource
     * @param ds dataset of generated data
     * @param r Selected rule
     * @see FullClassificationReport
     */
    protected String ClassificationReport(DataSource dSrc, DataSet ds, Rule r) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n" + r.toString());

        long correct = dSrc.getCorrect(ds, r.getClassID()).size();
        long expected = 0;
        if (config.isOneClassActive() == true) {
            expected = dSrc.getExpected(config.getActiveClass());
        } else {
            expected = dSrc.getExpected(r.getClassID());
        }
        long generated = ds.size();

        sb.append("\n Generated=" + generated);
        sb.append(" Correct=" + correct);
        sb.append(" Expected=" + expected);
        sb.append(" InCorrect=" + (generated - correct));

        long elements = ds.size();
        for (int iter = 0; iter < elements; iter++) {
            if (!ds.getRecord(iter).hasClass(r.getClassID())) {
                sb.append("\n---" + ds.getRecord(iter).toString());
            }
        }
        return sb.toString();
    }

    /**
     * Return as string Report of classification of selected RuleSet
     * @param DS dataSource (Train or Test) as dataScurce
     * @return string for report
     */
    public String FullClassificationReport(DataSource dSrc, RuleSet rSet, String text) {

        StringBuilder SB = new StringBuilder();

        DataSet DSPart = new DataSet();
        DataSet DSResult = new DataSet();

        ///////////////////////////////////////////
        /// only one class active!
        if (config.isOneClassActive()) {
            DSResult.clear();
            rSet.clearEvaluations();
            /// for each rule....
            for (int r = 0; r < rSet.rulesNo(); r++) {
                DSPart.clear();
                //if rule is active and returns such class
                if (rSet.getRule(r).isActive()) {
                    DSPart = evaluateRule(dSrc, rSet.getRule(r));
                    SB.append(this.ClassificationReport(dSrc, DSPart, rSet.getRule(r)));
                    DSResult = DataSet.operatorPlus(DSResult, DSPart);
                }
            } ///// end: for each rule
            /////// CLASS Summary ///////////////
            final int activeClass = config.getActiveClass();
            DSResult.evaluate(dSrc, activeClass);
            ///
            Evaluation E = new Evaluation(DSResult);
            rSet.setEvaluation(E);
        } // all classes are active
        //for each class....
        else {
            rSet.clearEvaluations();
            ////////////////// CLASSESS ////////////////////////////////////////
            for (int c = 0; c < DataLoader.getClassNumber(); c++) {
                DSPart.clear();
                DSResult.clear();

                if (dSrc.getExpected(c) == 0) {
                    SB.append("=> NO INSTANCES");
                }

                //////////////////RULES ////////////////////////////////////////
                for (int r = 0; r < rSet.rulesNo(); r++) {
                    //if rule is activa and returns such class
                    if (rSet.getRule(r).isActive() && rSet.getRule(r).getClassID() == c) {
                        DSPart = evaluateRule(dSrc, rSet.getRule(r));

                        SB.append(this.ClassificationReport(dSrc, DSPart, rSet.getRule(r)));

                        DSResult = DataSet.operatorPlus(DSResult, DSPart);
                    }
                }
                ////////////////END: RULES /////////////////////////////////////

                /////// CLASS Summary ///////////////
                DSResult.evaluate(dSrc, c);
                Evaluation E = new Evaluation(DSResult);
                rSet.setEvaluation(c, E);
            }//////////////////END:CLASSESS ////////////////////////////////////
        }
        rSet.doCountTotalEvaluation(DataLoader.getClassNumber());

        SB.append("\n############################ " + text + "############################\n");
        SB.append("\n" + rSet.toString());
        SB.append("\n\n" + text + "_DATASOURCE  " + dSrc.toString());
        SB.append("\n##################################################################\n");

        return SB.toString();
    }
}
