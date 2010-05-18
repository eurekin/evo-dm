package pl.eurekin.coevolution;

import java.util.Iterator;
import org.apache.log4j.Logger;
import pwr.evolutionaryAlgorithm.Report;
import pwr.evolutionaryAlgorithm.Population;
import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.utils.Clock;
import pwr.evolutionaryAlgorithm.data.Evaluator;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.individual.RuleSet;
import pwr.evolutionaryAlgorithm.individual.Individual;

/**
 *
 * <p><b>Oceny<b>Każda z populacji wymaga osobnego podejścia do funkcji
 * oceny. Osobnik klasyfikujący dodatkowo może być oceniany na dwa różne
 * sposoby: <ol>
 * <li>Na całym zbiorze</li>
 * <li>Na wybranym podzbiorze</li>
 * </ol>
 *
 * @author Rekin
 */
public class Coevolution {

    public static final Logger LOG = Logger.getLogger(Coevolution.class);
    private Population<SelectingIndividual> selectingPopulation;
    private Population<ClassifyingIndividual> classifyingPopulation;

    /**
     * Creates & initializes both populations
     */
    private void createPopulations() {
        selectingPopulation =
                new Population<SelectingIndividual>(
                new SelectingIndividual());
        classifyingPopulation =
                new Population<ClassifyingIndividual>(
                new ClassifyingIndividual());
    }

    public static void main(String... args) {
        LOG.trace("Starting main");

        Configuration.newConfiguration("_iris", "coTest");
        Configuration conf = Configuration.getConfiguration();
        Coevolution c = new Coevolution(conf);

        c.start();

        LOG.trace("Ending main");
    }
    private Clock myClock;
    private long generation;
    private Clock totalTimeClock;
    private Individual theBestInd;
    private DataLoader dataLoader;

    private void evolve(final Configuration config, final Report report) {
        LOG.trace("Starting evolution.");
        // warunek stopu
        boolean stopEval = false;
        generation = 0;
        final long stopGeneration = config.getStopGeneration();
        // MAIN Evolutionary Algorithm
        while (stopEval == false && stopGeneration != generation) {
            /*new generation*/
            classifyingPopulation = classifyingPopulation.recombinate();
            generation++;
            classifyingPopulation.evaluate(DataLoader.getTrainData());
            //the best individual
            if (classifyingPopulation.getBestFitness()
                    > theBestInd.getFitness()) {
                theBestInd = new RuleSet((RuleSet) classifyingPopulation.getBestIndividual());
            }
            if (config.getStopEval() <= classifyingPopulation.getBestFitness()) {
                stopEval = true;
                break;
            }
            if (config.isEcho()) {
                report.reportAfterOneGeneration(theBestInd, classifyingPopulation, generation);
            }
        }
        //END: EA works
    }

    private void updateTheBestIndividual(Individual I) {
        theBestInd = new RuleSet((RuleSet) (I));
    }

    public DataLoader getDataLoader() {
        return dataLoader;
    }

    /**
     * testing constructor
     */
    public Coevolution() {
        generation = 0;
        myClock.Reset();
        myClock = new Clock();
        theBestInd = new RuleSet();
        classifyingPopulation.init();
        totalTimeClock = new Clock();
        dataLoader = new DataLoader(null, null);
        createPopulations();
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
        Report report = config.getReport();
        String prompt = config.getPrompt();
        report.evoAlgInitStart(prompt);
        dataLoader = DataLoader.getDataLoader(config);
        myClock = new Clock();
        theBestInd = new RuleSet();
        report.evoAlgInitStop(prompt, DataLoader.FileSummary());
    }

    public void start() {
        /**
         * Można wykorzystać ewaluator z podstawowej wersji, bo służy
         * właśnie do oceny na bazie całego zbioru. I w ten sposób
         * można obiektywnie porównywać osiągi koewolucji z ewolucją.
         */
        Evaluator eval = Evaluator.getEvaluator();
        ClassifyingIndividual theBestOfTheBest = null;
        totalTimeClock = new Clock();
        totalTimeClock.Reset();
        final Configuration config = Configuration.getConfiguration();
        final Report report = config.getReport();
        final int testNo = report.getTestNumber();
        int crossvalidationNo = config.getCrossvalidationValue();

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

                // tworzenie nowej populacji
                createPopulations();
                theBestInd = null;

                // evolutionary version
                // classifyingPopulation.evaluate();
                // replaced by:
                evaluatePopulations(DataLoader.getTrainData());

                /**
                 * Unimplemented
                 */
                //evaluatePopulations();
                /**
                 * Unimplemented
                 */
                //updateTheBestIndividual(classifyingPopulation.getBestIndividual());
                // EA START
                /**
                 * Unimplemented
                 */
                //evolve(config, report);
                // EA DONE
                myClock.Pause();
                totalTimeClock.Pause();
                /**
                 * Unimplemented
                 */
                //evaluateAndReport(eval, report, config);
                /**
                 * Unimplemented
                 */
                //theBestOfTheBest = getNewBestOfTheBestIndividual(theBestOfTheBest, eval, config);
            }
            DataLoader.doCrossvalidationNext();
        }//END: CROSSVALIDATION CV TIMES
        /**
         * Unimplemented
         */
        //report.reportAllToFile(config, eval, theBestOfTheBest, totalTimeClock);
    }

    /**
     * Pewne wątpliwości budzi obecność tej metody w klasie. W jaki
     * sposób właściwie ta informacja miałaby być pobierana? Główny
     * algorytm jest blokujący, więc w grę wchodzi jedynie inny wątek.
     * To z kolei wymagałoby użycia mechanizmu synchronizacj.
     * 
     * @return aktualna liczba pokoleń
     */
    public long getGeneration() {
        return generation;
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
