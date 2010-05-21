package pl.eurekin.coevolution;

import pwr.evolutionaryAlgorithm.data.DataSet;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.data.Evaluation;
import pwr.evolutionaryAlgorithm.individual.RuleSet;

/**
 * Potrzebuje podmienić wyłącznie ewaluację
 *
 * <p>Najlepiej byłoby, jakby ta klasa korzystała z już dostępnego API
 * do ewaluacji.
 *
 * <p><b>UWAGA 1.</b>Trzeba dobrze rozpatrzyć ewaluację na zbiorach
 * uczących i testowych. W przypadku nadklasy - RuleSet - ewaluacja
 * może się odbywać po dwóch zbiorach: testowym i treningowym. Ta
 * klasa przeznaczona jest do ewaluacji na konkretnym podzbiorze
 * zbioru treningowego. Problem: Jak oceniać tego osobnika na zbiorze
 * testowym? Otóż, jedyne sensowne rozwiązanie to na całym zbiorze.
 * Podobnie, na etapie wyłącznie OCENY tego osobnika, należałoby
 * rozróżnić wszystkie sposoby oceniania.
 *
 * <p>Ogólnie każdy RuleSet można oceniać na:<ol>
 * <li>Zbiorze trenującym</li>
 * <li>Zbiorze testowym</li>
 * <li>Dowolnym <em>podzbiorze</em></li>
 * </ol>
 *
 * @author Rekin
 */
public class ClassifyingIndividual extends RuleSet {

    private SelectingIndividual selector;


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

    public void setSelector(SelectingIndividual selector) {
        this.selector = selector;
    }
}
