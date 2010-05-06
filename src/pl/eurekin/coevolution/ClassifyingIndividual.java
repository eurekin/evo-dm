/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.eurekin.coevolution;

import pwr.evolutionaryAlgorithm.individual.RuleSet;

/**
 * Potrzebuje podmienić wyłącznie ewaluację
 *
 * Najlepiej byłoby, jakby ta klasa korzystała z już dostępnego API
 * do ewaluacji.
 *
 * @author Rekin
 */
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

    void evaluateUsingSubset(SelectingIndividual s) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
