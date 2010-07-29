/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.eurekin.util;

import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.Configuration.CrossoverType;
import pwr.evolutionaryAlgorithm.Configuration.MutationType;

/**
 *
 * @author Rekin
 */
public class Configurator {

    private static final String DEF_COMMENT = "built with builder";

    private void assertProb(float probability) {
        if (probability < 0f || probability > 1.0f) {
            throw new IllegalArgumentException("mutation has to be between 0 and 1");
        }
    }

    public Configurator rules(int rules) {
        this.rules = rules;
        return this;
    }

    public enum Dataset {

        IRIS,
        WINE,
        GLASS,
        DIABETES,
        IRIS_CRIPPLED;

        @Override
        public String toString() {

            return EnumHelper.getHumanReadableName(this);
        }
    };
    private Dataset dataset = Dataset.IRIS;
    private MutationType mutType = MutationType.SM;
    private float mutationProb = 0.01f;
    private CrossoverType cxType = CrossoverType.SCX;
    private float evoCrossoverProb = 0.8f;
    private int generations = 100;
    private int populationSize = 20;
    private float coevolutionMut = 0.1f;
    private int selection = 2;
    private boolean echo = false;
    private int folds = 2;
    private int repetitions = 1;
    private int rules = 5;

    public Configurator crossvalidation(int folds, int repetitions) {
        this.folds = folds;
        this.repetitions = repetitions;
        return this;
    }

    public Configurator verbose() {
        echo = true;
        return this;
    }

    public Configurator tournamentSel(int tournamentSize) {
        if (tournamentSize < 2) {
            throw new IllegalArgumentException("Tournament has to be bigger than 2");
        }
        selection = tournamentSize;
        return this;
    }

    public Configurator randomSel() {
        selection = 0;
        return this;
    }

    public Configurator rouletteSel() {
        this.selection = 1;
        return this;
    }

    public Configurator coevolutionMut(float coevolutionMut) {
        assertProb(evoCrossoverProb);
        this.coevolutionMut = coevolutionMut;
        return this;
    }

    public Configurator populationSize(int populationSize) {
        this.populationSize = populationSize;
        return this;
    }

    public Configurator generations(int generations) {
        this.generations = generations;
        return this;
    }

    public Configurator mutationSimple(float probability) {
        mutType = MutationType.SM;
        mutationProb = probability;
        return this;
    }

    public Configurator mutationFAided(float probability) {
        mutType = MutationType.FAM;
        mutationProb = probability;
        return this;
    }

    public Configurator crossoverSimple(float probability) {
        cxType = CrossoverType.SCX;
        evoCrossoverProb = probability;
        return this;
    }

    public Configurator crossoverBest(float probability) {
        cxType = CrossoverType.BCX;
        evoCrossoverProb = probability;
        return this;
    }

    public Configurator dataset(Dataset dataset) {
        this.dataset = dataset;
        return this;
    }

    public Configuration build() {
        final String dName = dataset.name().toLowerCase();
        Configuration.newConfiguration(getDefaultConfigName(dName), DEF_COMMENT);
        Configuration c = Configuration.getConfiguration();

        c.setTrainDataFileName("data/" + dName + "/" + dName + ".csv");
        c.setCROSSOVER(cxType);
        c.setMUTATION(mutType);
        c.setPcrossover(evoCrossoverProb);
        c.setPmutation(mutationProb);
        c.setPopSize(populationSize);
        c.setStopGeneration(generations);
        c.setCoevSelMutationProb(coevolutionMut);
        c.setSelection(selection);
        c.setECHO(echo);
        c.setCoevClsEcho(echo);
        c.setCoevSubEcho(echo);
        c.setCv(repetitions, folds);
        c.setDATASET_RULES(rules);

        return c;
    }

    public static String getDefaultConfigName(String dName) {
        return "default_config_for_" + dName;
    }
}
