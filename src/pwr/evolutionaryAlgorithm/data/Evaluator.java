package pwr.evolutionaryAlgorithm.data;

import java.util.ArrayList;
import pl.eurekin.coevolution.ClassifyingIndividual;
import pl.eurekin.coevolution.SelectingIndividual;

import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.individual.*;

/**
 * Dlaczego singleton, a nie klasa z metodami i polami statycznymi?
 *
 * Singleton class
 */
public class Evaluator {

    static private Evaluator e = null;
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
     * @param DSc datasource (traint/test)
     * @param RuleSet
     * @return DataSet of all covered data
     */
    private DataSet EvaluateRuleSet(DataSource DSc, RuleSet RS) {

        DataSet DSPart = new DataSet();
        DataSet DSResult = new DataSet();

        ///////////////////////////////////////////
        /// only one class active!
        if (Configuration.getConfiguration().isOneClassActive()) {
            DSResult.clear();
            RS.clearEvaluations();

            /// for each rule....
            for (int r = 0; r < RS.rulesNo(); r++) {
                DSPart.clear();
                //if rule is active and returns such class
                if (RS.getRule(r).isActive()) {

                    // Co to w ogóle zwraca?
                    DSPart = evaluateRule(DSc, RS.getRule(r));
                    DSResult = DataSet.operatorPlus(DSResult, DSPart);

                }
            } ///// end: for each rule
            /////// CLASS Summary ///////////////
//            evaluateDataSet(DSc, DSResult, Configuration.getConfiguration().getActiveClass());
            DSResult.evaluate(DSc, Configuration.getConfiguration().getActiveClass());
            ///
            Evaluation E = new Evaluation(DSResult);
            RS.setEvaluation(E);
        } // all classes are active
        //for each class....
        else {
            RS.clearEvaluations();

            ////////////////// CLASSESS ////////////////////////////////////////
            for (int c = 0; c < DataLoader.getClassNumber(); c++) {
                DSPart.clear();
                DSResult.clear();

                //////////////////RULES ////////////////////////////////////////
                for (int r = 0; r < RS.rulesNo(); r++) {
                    //if rule is activa and returns such class
                    if (RS.getRule(r).isActive() && RS.getRule(r).getClassID() == c) {
                        DSPart = evaluateRule(DSc, RS.getRule(r));
                        DSResult = DataSet.operatorPlus(DSResult, DSPart);
                    }
                }
                ////////////////END: RULES /////////////////////////////////////

                /////// CLASS Summary ///////////////
                DSResult.evaluate(DSc, c);
                Evaluation evl = new Evaluation(DSResult);
                RS.setEvaluation(c, evl);
            }//////////////////END:CLASSESS ////////////////////////////////////
        }
        RS.doCountTotalEvaluation(DataLoader.getClassNumber());
        return DSResult;
    }

    /**
     * evaluates RuleSet using specified subset of DataSource
     * @param ds datasource (traint/test)
     * @param RuleSet
     * @return DataSet of all covered data
     */
    public DataSet evaluateRuleSetUsingSelector(
            DataSource ds, SelectingIndividual sl, ClassifyingIndividual ci) {

        DataSet DSPart = new DataSet();
        DataSet DSResult = new DataSet();

        ci.clearEvaluations();
        ///////////////////////////////////////////
        /// only one class active!
        if (Configuration.getConfiguration().isOneClassActive()) {
            DSResult.clear();

            /// for each rule....
            for (Rule rule : ci.getRules()) {
                DSPart.clear();
                //if rule is active and returns such class
                if (rule.isActive()) {
                    DSPart = evaluateRule(ds, rule);
                    DSResult = DataSet.operatorPlus(DSResult, DSPart);
                }
            } ///// end: for each rule
            /////// CLASS Summary ///////////////
            final int activeClass = Configuration.getConfiguration().getActiveClass();
            DSResult.evaluate(ds, activeClass);
            ci.setEvaluation(new Evaluation(DSResult));
        } // all classes are active
        //for each class....
        else {
            ////////////////// CLASSESS ////////////////////////////////////////
            for (int c = 0; c < DataLoader.getClassNumber(); c++) {
                DSPart.clear();
                DSResult.clear();


                // Z punktu widzenia implementacji koewolucji najważniejszy
                // jest moment kiedy mogę odfiltrować rekordy. Zostawić tylko
                // te, które wybrane są przez osobnika wybierającego. Problem
                // w tym, żeby zachować poprawność oceniania populacji. Gdzieś
                // trzeba zmodyfikować ocenę, gdyż w koewolucji osobnik klasy-
                // fikujący widzi tylko podzbiór danych.

                // Fill DSResult with appropriate rules
                for (Rule rule : ci.getRules()) {
                    //if rule is active and returns such class
                    if (rule.isActive() && rule.getClassID() == c) {
                        DSPart = evaluateRule(ds, rule);
                        DSResult = DataSet.operatorPlus(DSResult, DSPart);
                    }
                }
                ////////////////END: RULES /////////////////////////////////////

                /////// CLASS Summary ///////////////
                DSResult.evaluate(ds, c);
                Evaluation E = new Evaluation(DSResult);
                ci.setEvaluation(c, E);
            }//////////////////END:CLASSESS ////////////////////////////////////
        }
        ci.doCountTotalEvaluation(DataLoader.getClassNumber());
        return DSResult;
    }

    private DataSet evaluateRule(DataSource dSrc, Rule rule) {

        Configuration cfg = Configuration.getConfiguration();
        final int activeClass = cfg.isOneClassActive() ? cfg.getActiveClass() : rule.getClassID();

        // Tutaj wykorzystywany jest indeks i przeszukiwanie binarne.
        // Wybierane są tutaj te rekordy, których dotyczy reguła rule.
        DataSet ds = getCoveredDataSet(dSrc, rule);  //get DataSet covered by rule

        //
        ds.evaluate(dSrc, activeClass);
        rule.setEvaluation(new Evaluation(ds));
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
        final int numberOfAttributes = Configuration.getConfiguration().getNumberOfAttributes();
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
        int pop_size = Configuration.getConfiguration().getPopSize();
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
     * Method that looks for DataSource ang gives information about DataSet
     * (acc, prec, rec and Fsc) in given class.
     *
     * As a side effect {@code DataSet ds}'s evaluation is updated to
     * reflect computated values. Thus it's best candidate for a method
     * of DataSet class.
     */
    protected void evaluateDataSet(DataSource dSrc, DataSet ds, int classId) {
        final float rcl, prc, pPt, rPt, eSc, fSc, out, acc;
        final float alpha, expected, correct, generated;

        // get input data
        alpha = 0.5f;
        generated = ds.size();
        expected = dSrc.getExpected(classId);
        correct = dSrc.getCorrect(ds, classId).size();

        // recall & precision - corrected to handle division by zero
        rcl = expected == 0 ? 0 : correct / expected;
        prc = generated == 0 ? 0 : correct / generated;

        // E score
        pPt = prc == 0 ? 0 : alpha / prc;
        rPt = rcl == 0 ? 0 : (1 - alpha) / rcl;
        eSc = pPt + rPt == 0 ? 1 : 1 - (1 / (pPt + rPt));

        // F Score
        fSc = 1 - eSc;

        // Accuracy
        out = dSrc.size() - expected - generated + 2 * correct;
        acc = prc == 0 || rcl == 0.0 ? 0 : out / dSrc.size();

        // return
        ds.setEvaluation(prc, rcl, acc, fSc);
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
        if (Configuration.getConfiguration().isOneClassActive() == true) {
            expected = dSrc.getExpected(Configuration.getConfiguration().getActiveClass());
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
        if (Configuration.getConfiguration().isOneClassActive()) {
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
            final int activeClass = Configuration.getConfiguration().getActiveClass();
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
