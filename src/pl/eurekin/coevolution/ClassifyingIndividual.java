package pl.eurekin.coevolution;

import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.Population;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.DataSet;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.data.Evaluation;
import pwr.evolutionaryAlgorithm.data.Evaluator;
import pwr.evolutionaryAlgorithm.individual.Rule;
import pwr.evolutionaryAlgorithm.individual.RuleSet;

/**
 * Potrzebuje podmienić wyłącznie ewaluację
 *
 * Najlepiej byłoby, jakby ta klasa korzystała z już dostępnego API
 * do ewaluacji.
 *
 * @author Rekin
 * @deprecated needs to be synchronized with new Evaluation API -- fast
 */
@Deprecated
public class ClassifyingIndividual extends RuleSet {

    /**
     * Noop. Wymagane przez createPopulations do identyfikacji konstruowanego
     * typu.
     */
    public ClassifyingIndividual() {
    }

    /**
     * Konstruktor kopiujący.
     *
     * <p>Polega na nim metoda
     * {@link Coevolution#getNewBestOfTheBestIndividual(
     * ClassifyingIndividual, Evaluator, Configuration) }.
     * @param classifyingIndividual
     */
    ClassifyingIndividual(ClassifyingIndividual classifyingIndividual) {
        // short circuit XXX write real
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Ewolucyjny odpowiednik:
     * {@link Population#evaluate(DataSource) }
     *
     * Odpowiadająca metoda ocenia... I ROBI statystyki -- trzeba to
     * uwzględnić w koewolucji.
     *
     * @param s osobnik wybierający podzbiór danych
     * @param dSrc to przykładowo dane treningowe
     */
    void evaluateUsingSubset(SelectingIndividual s, DataSource dSrc) {
        evaluateRuleSetUsingSelector(dSrc, s, this);
    }

    /**
     * evaluates RuleSet using specified subset of DataSource
     * @param dSrc datasource (train/test)
     * @param sl
     * @param ci
     * @return DataSet of all covered data
     */
    private DataSet evaluateRuleSetUsingSelector(
            DataSource dSrc,
            SelectingIndividual si,
            ClassifyingIndividual ci) {

        DataSet DSetResult = new DataSet(), DSetPart;
        Configuration config = Configuration.getConfiguration();
        Evaluator evl = Evaluator.getEvaluator();

        ci.clearEvaluations();
        ///////////////////////////////////////////
        /// only one class active!
        if (config.isOneClassActive()) {
            DSetResult.clear();

            /// for each rule....
            for (Rule rule : ci.getRules()) {
                //if rule is active and returns such class
                if (rule.isActive()) {
                    DSetPart = evl.getCoveredDataSet(dSrc, rule);
                    DSetResult = DataSet.operatorPlus(DSetResult, DSetPart);
                }
            } ///// end: for each rule
            /////// CLASS Summary ///////////////
            final int activeClass = config.getActiveClass();
            Evaluation evl2 = DSetResult.evaluate(dSrc, activeClass);
            ci.setEvaluation(evl2);
        } else {
            // all classes are active
            //for each class....
            ////////////////// CLASSESS ////////////////////////////////////////
            for (int c = 0; c < DataLoader.getClassNumber(); c++) {
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
                        DSetPart = evl.getCoveredDataSet(dSrc, rule);
                        DSetResult = DataSet.operatorPlus(DSetResult, DSetPart);
                    }
                }
                ////////////////END: RULES /////////////////////////////////////

                // Po poprzednich krokach posiadamy DataSet (DSetResult),
                // który zawiera rekordy zebrane dla klasy c.

                // Należy odfiltrować te, których nie wybrał osobnik
                // wybierający.
                DSetResult = filterUsingSelectingIndividual(si, DSetResult);

                Evaluation evl2 = DSetResult.evaluate(dSrc, c);
                ci.setEvaluation(c, evl2);
            }//////////////////END:CLASSESS ////////////////////////////////////
        }

        // do average value of all classes -> without unused (or not tested)
        // class. Pozostało jedynie policzenie średniej wartości, ze
        // wszystkich klas.
        ci.doCountTotalEvaluation(DataLoader.getClassNumber());
        return DSetResult;
    }

    private DataSet filterUsingSelectingIndividual(SelectingIndividual si,
            DataSet set) {
        DataSet result = new DataSet(set.size());

        // TODO selecting implementation placeholder
        result = new DataSet(set);
        //

        return result;
    }
}
