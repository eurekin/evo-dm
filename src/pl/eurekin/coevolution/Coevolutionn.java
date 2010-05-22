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
    private BestIndividualSelector<RuleSet> bestClsOfRun;
    private Population<Selector> selectingPopulation;
    private Population<RuleSet> classifyingPopulation;
    private BestIndividualSelector<Selector> bestSelOfRun;

    public void initPopulation() {
        selectingPopulation =
                new Population<Selector>(new Selector());
        classifyingPopulation =
                new Population<RuleSet>(new RuleSet());

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
        bestClsOfRun = new BestIndividualSelector<RuleSet>();
        bestSelOfRun = new BestIndividualSelector<Selector>();
        // MAIN Evolutionary Algorithm
        while (!stop) {
            /*new generation*/
            classifyingPopulation = classifyingPopulation.recombinate();
            generation++;
            evaluatePopulations(DataLoader.getTrainData());
            //the best individual
            bestClsOfRun.rememberBestFrom(classifyingPopulation);
            bestSelOfRun.rememberBestFrom(selectingPopulation);
            reportGenerationEnd();
            stop = config.getStopEval() <= classifyingPopulation.getBestFitness();
            stop |= config.getStopGeneration() == generation;
        }
        //END: EA works
    }

    private void reportGenerationEnd() {
        if (config.isEcho() || true) {
            report.reportAfterOneGeneration(bestClsOfRun.getBest(),
                    classifyingPopulation, generation);
            report.reportAfterOneGeneration(bestSelOfRun.getBest(),
                    selectingPopulation, generation);
        }
    }

    /**
     * testing constructor
     */
    public Coevolutionn() {
        generation = 0;
        config = null;
        report = null;
        classifyingPopulation = new Population<RuleSet>(new RuleSet());
        classifyingPopulation.init();
    }

    public static void main(String[] args) {
        Configuration.newConfiguration("_iris", "IRIS with CX");
        Configuration config = Configuration.getConfiguration();
        config.setPcrossover(0.4f);
        Coevolutionn coev = new Coevolutionn(config);
        coev.start();
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
        String prompt = config.getPrompt();
        report.evoAlgInitStart(prompt);
        DataLoader.getDataLoader(config);
        report.evoAlgInitStop(prompt, DataLoader.FileSummary());
    }
    private BestIndividualSelector<RuleSet> bestOfFold;

    public void evaluateAndReport(Clock clock) {
        final float trainFsc, trainAcc, testFsc, testAcc;
        final RuleSet best = bestClsOfRun.getBest();

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
     *
     * <p>Z implementacyjnego punktu widzenia ta metoda realizuje
     * strategię łączenia osobników wybierających z klasyfikującymi.
     * Łączy osobników z dwóch populacji w pary, lub ujmując bardziej
     * symbolicznie: 1-1 (jeden do jednego).
     *
     * <p>Istnieje możliwość obrania innej strategii łączenia. W
     * ramach badań możnaby wypróbować: <ol>
     * <li> 1-1 </li>
     * <li> 1-n </li>
     * <li> n-n </li>
     * </ol>
     */
    private void evaluatePopulations(DataSource dSrc) {
        assert selectingPopulation.size() == classifyingPopulation.size() :
                "Co-evolving populations must be the same size";

        Iterator<Selector> si = selectingPopulation.iterator();
        Iterator<RuleSet> ci = classifyingPopulation.iterator();
        Selector s;
        RuleSet c;
        while (si.hasNext() && ci.hasNext()) {
            s = si.next();
            c = ci.next();

            // evaluation
            c.evaluate(dSrc, s);
            s.evaluateUsingClassifier(c);
        }

        classifyingPopulation.updateStatistics();
        selectingPopulation.updateStatistics();

    }
}
