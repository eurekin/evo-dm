/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.eurekin.coevolution;

import java.util.Iterator;
import pl.eurekin.util.BestIndividualSelector;
import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.Population;
import pwr.evolutionaryAlgorithm.Report;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.individual.RuleSet;
import pwr.evolutionaryAlgorithm.utils.Clock;

/**
 *
 * @author Rekin
 */
public class Coevolutionn {

    private long generation;
    private final Configuration config;
    private final Report report;
    private Population<RuleSet> rulePopulation;
    private BestIndividualSelector<RuleSet> bestOfRun;
    private Population<SelectingIndividual> selectingPopulation;
    private Population<ClassifyingIndividual> classifyingPopulation;

    public void initPopulation() {
        rulePopulation = new Population<RuleSet>(new RuleSet());
        rulePopulation.evaluate(DataLoader.getTrainData());

        selectingPopulation =
                new Population<SelectingIndividual>(new SelectingIndividual());
        classifyingPopulation =
                new Population<ClassifyingIndividual>(new ClassifyingIndividual());

        selectingPopulation.evaluate(DataLoader.getTrainData());
        classifyingPopulation.evaluate(DataLoader.getTrainData());
    }

    public void start() {
        final int testNo = report.getTestNumber();
        final int crossvalidationNo = config.getCrossvalidationValue();
        final Clock totalTimeClock = new Clock();
        final Clock myClock = new Clock();
        bestOfFold = new BestIndividualSelector<RuleSet>();
        //CROSSVALIDATION CV TIMES
        DataLoader.doCrossvalidation();
        for (int cv = 0; cv < crossvalidationNo; cv++) {
            report.indicateCrossvalidationFold(cv);
            //RUN N-times EA....
            for (int run = 0; run < testNo; run++) {
                // czas jednego powtórzenia kroswalidacji
                myClock.Reset();
                myClock.Start();
                totalTimeClock.Start();
                initPopulation();
                // EA START
                evolve();
                // EA DONE
                myClock.Pause();
                totalTimeClock.Pause();
                evaluateAndReport(myClock);
            }
            DataLoader.doCrossvalidationNext();
        }
        //END: CROSSVALIDATION CV TIMES
        report.reportAllToFile(config, bestOfFold.getBest(), totalTimeClock);
    }

    /**
     * Evolutionary Algorithm - core implementation
     *
     * @param config supplying parameters
     * @param report object to send messages to
     */
    private void evolve() {
        // warunek stopu
        boolean stop = false;
        generation = 0;
        bestOfRun = new BestIndividualSelector<RuleSet>();
        // MAIN Evolutionary Algorithm
        while (!stop) {
            /*new generation*/
            rulePopulation = rulePopulation.recombinate();
            generation++;
            evaluatePopulations(DataLoader.getTestData());
            //the best individual
            bestOfRun.rememberBestFrom(rulePopulation);
            reportGenerationEnd();
            stop = config.getStopEval() <= rulePopulation.getBestFitness();
            stop |= config.getStopGeneration() == generation;
        }
        //END: EA works
    }

    private void reportGenerationEnd() {
        if (config.isEcho()) {
            report.reportAfterOneGeneration(bestOfRun.getBest(),
                    rulePopulation, generation);
        }
    }

    /**
     * testing constructor
     */
    public Coevolutionn() {
        generation = 0;
        config = null;
        report = null;
        rulePopulation = new Population<RuleSet>(new RuleSet());
        rulePopulation.init();
    }

    public Coevolutionn(String ConfigFileName, String ResearchComment) {
        this(setConfiguration(ConfigFileName, ResearchComment));
    }

    private static Configuration setConfiguration(String configFileName,
            String researchComment) {
        Configuration.newConfiguration(configFileName, researchComment);
        return Configuration.getConfiguration();
    }

    public Coevolutionn(Configuration config) {
        this.config = config;
        report = config.getReport();
        DataLoader.getDataLoader(config);
        String prompt = config.getPrompt();
        report.evoAlgInitStart(prompt);
        report.evoAlgInitStop(prompt, DataLoader.FileSummary());
    }
    private BestIndividualSelector<RuleSet> bestOfFold;

    public void evaluateAndReport(Clock clock) {
        final float trainFsc, trainAcc, testFsc, testAcc;
        final RuleSet best = bestOfRun.getBest();

        best.evaluate(DataLoader.getTrainData());
        trainFsc = best.getFsc();
        trainAcc = best.getAccuracy();

        best.evaluate(DataLoader.getTestData());
        testFsc = best.getFsc();
        testAcc = best.getAccuracy();

        report.report(generation, trainFsc, trainAcc, testFsc, testAcc, clock.GetTotalTime());
        report.extendedReport(config, best);

        bestOfFold.rememberIfBest(best);
        if (bestOfFold.isBetterThanLastOne()) {
            report.reportBestInd(best);
        }
    }

    /**
     * <p>Ocenia osobniki w populacjach używając podejścia 1-1: jednemu
     * osobnikowi klasyfikującemu przypada jeden wybierający.
     *
     * <p>Potrzebujemy dwóch ewaluatorów:
     *
     * <p>Jednego do oceniania osobników klasyfikujących. Można skorzystać
     * z istniejącego kodu. Należy zmodyfikować ocenę tak, aby uwzględnione
     * zostały odpowiednie przykładu -- wyselekcjonowane przez drugą
     * populację.
     *
     * <p>Drugi ewaluator jest kwestią otwartą. Można zaimplemenentować
     * ocenę osobnika wybierającego na podstawie tego, jak bardzo
     * utrudnił osobnikowi oceniającemu. W ten sposób wyselekcjonują
     * najcięższe przypadki - przynajmniej w zamierzeniu...
     *
     * <p>Ocena osobników wybierających zależy od oceny osobników
     * klasyfikujących i dlatego należy zadbać o poprawną kolejność
     * wykonywania.
     */
    private void evaluatePopulations(DataSource dSrc) {
        // boilerplate
        Iterator<SelectingIndividual> si = selectingPopulation.iterator();
        Iterator<ClassifyingIndividual> ci = classifyingPopulation.iterator();
        SelectingIndividual s;
        ClassifyingIndividual c;
        while (si.hasNext() && ci.hasNext()) {
            s = si.next();
            c = ci.next();

            // evaluation
            c.evaluateUsingSubset(s, dSrc);
            s.evaluateUsingClassifier(c);
        }

        // health & safety
        assert !si.hasNext() && !ci.hasNext() :
                "Co-evolving populations differ in size";
    }
}
