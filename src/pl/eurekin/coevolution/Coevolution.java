/*
 * 1. ograniczyć maksymalną liczbę wybieranych rekordów
 *    (określić ile procent jest optymalne).
 * 
 * 2. wizualizacja 3d, 2d zbiory uczące + osobnik: GLASS, WINE
 *
 * 3. Rozpisać to na CUDA.
 *
 * 
 */
package pl.eurekin.coevolution;

import java.util.Iterator;
import pl.eurekin.util.BestIndividualSelector;
import pl.eurekin.util.Configurator;
import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.Population;
import pwr.evolutionaryAlgorithm.Report;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.individual.RuleSet;
import pwr.evolutionaryAlgorithm.utils.Clock;
import static pl.eurekin.util.Configurator.Dataset.*;

/**
 *
 * @author Rekin
 */
public class Coevolution {

    private long generation;
    private final Configuration config;
    private final Report report;
    private BestIndividualSelector<RuleSet> bestClsOfRun;
    private Population<Selector> selectingPopulation;
    private Population<RuleSet> classifyingPopulation;
    private BestIndividualSelector<Selector> bestSelOfRun;

    public void initPopulation() {
        selectingPopulation = new Population<Selector>(new Selector());
        classifyingPopulation = new Population<RuleSet>(new RuleSet());

        selectingPopulation.evaluate(DataLoader.getTrainData());
        classifyingPopulation.evaluate(DataLoader.getTrainData());
    }

    public RuleSet start() {
        final int testNo = report.getTestNumber();
        final int crossvalidationNo = config.getCrossvalidationValue();
        final Clock totalTimeClock = new Clock();
        final Clock myClock = new Clock();
        BestIndividualSelector<RuleSet> simplyTheBest = new BestIndividualSelector<RuleSet>();
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
        simplyTheBest.rememberIfBest(bestOfFold.getBest());
        report.reportAllToFile(config, bestOfFold.getBest(), totalTimeClock);
        return simplyTheBest.getBest();
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
        // ehhhh, nie chcem, ale muszem
        final float evPCx = config.getCrossoverValue();
        final float cvPCx = config.getCoevSelCrossoverProb();
        // MAIN Evolutionary Algorithm
        while (!stop) {
            /*new generation*/
            classifyingPopulation = classifyingPopulation.recombinate(evPCx);
            selectingPopulation = selectingPopulation.recombinate(cvPCx);
            generation++;
            evaluatePopulations(DataLoader.getTrainData());
            //the best individual
            bestClsOfRun.rememberBestFrom(classifyingPopulation);
            bestSelOfRun.rememberBestFrom(selectingPopulation);
            reportGenerationEnd();
            // stop = config.getStopEval() <= classifyingPopulation.getBestFitness();
            stop = config.getStopGeneration() == generation;
        }
        System.out.println("Generations = " + generation);
        //END: EA works
    }

    private void reportGenerationEnd() {
        boolean rep = false;
        StringBuilder sb = new StringBuilder();
        if (config.isEcho()) {
            sb.append(report.genReport(classifyingPopulation.getBestIndividual(),
                    classifyingPopulation, generation));
            sb.append(Report.DLM);
            rep = true;
        }
        if (config.isCoevSubEcho()) {
            sb.append(report.genReport(selectingPopulation.getBestIndividual(),
                    selectingPopulation, generation));
            sb.append(Report.DLM);
            rep = true;
        }
        if (config.isCoevClsEcho()) {
            classifyingPopulation.evaluate(DataLoader.getTrainData());
            // Have to evaluate on whole dataset
            sb.append(report.genReport(classifyingPopulation.getBestIndividual(),
                    classifyingPopulation, generation));
            rep = true;
        }
        sb.append("\n");
        if (rep) {
            report.consoleReport(sb.toString());
        }
    }

    /**
     * testing constructor
     */
    public Coevolution() {
        generation = 0;
        config = null;
        report = null;
        classifyingPopulation = new Population<RuleSet>(new RuleSet());
        classifyingPopulation.init();
    }

    public static void main(String[] args) {
        Configuration.setConfiguration(new Configurator() //
                // CONFIGURATION BEGIN
                .dataset(IRIS) //
                .mutationSimple(0.003f) //
                .crossoverSimple(0f) //
                .generations(2000) //
                .populationSize(200) //
                .tournamentSel(2) //
                .crossvalidation(10, 1) //
                // CONFIGURATION END
                .build());//

        Coevolution coev = new Coevolution(Configuration.getConfiguration());
        coev.start();
    }

    public Coevolution(String ConfigFileName, String ResearchComment) {
        this(setConfiguration(ConfigFileName, ResearchComment));
    }

    private static Configuration setConfiguration(String configFileName,
            String researchComment) {
        Configuration.newConfiguration(configFileName, researchComment);
        return Configuration.getConfiguration();
    }

    public Coevolution(Configuration config) {
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
