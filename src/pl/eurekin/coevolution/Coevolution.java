package pl.eurekin.coevolution;

import pwr.evolutionaryAlgorithm.Population;
import org.apache.log4j.Logger;
import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.Report;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.Evaluator;
import pwr.evolutionaryAlgorithm.individual.Individual;
import pwr.evolutionaryAlgorithm.individual.RuleSet;
import pwr.evolutionaryAlgorithm.utils.Clock;

/**
 *
 * @author Rekin
 */
public class Coevolution {

    public static final Logger LOG = Logger.getLogger(Coevolution.class);
    private Population selectors;
    private Population classifiers;
    private Population selectingPopulation;

    private void createPopulations() {
        selectors = new Population(new SelectingIndividual());
        classifiers = new Population(new ClassifyingIndividual());
    }

    public static void main(String... args) {
        LOG.trace("Starting main");

        LOG.trace("Ending main");
    }
    private Clock myClock;
    private long generation;
    private Clock totalTimeClock;
    private Individual theBestInd;
    private DataLoader dataLoader;
    private Population classifyingPopulation;

    private void evolve(final Configuration config, final Report report) {
        LOG.trace("Starting evolution.");
        // warunek stopu
        boolean stopEval = false;
        generation = 0;
        // MAIN Evolutionary Algorithm
        while (stopEval == false && config.getStopGeneration() != generation) {
            /*new generation*/
            classifyingPopulation = classifyingPopulation.recombinate();
            generation++;
            classifyingPopulation.evaluate(DataLoader.getTrainData());
            //the best individual
            if (classifyingPopulation.getBestFitness() > theBestInd.getEvaluation().getFitness()) {
                theBestInd = new RuleSet((RuleSet) (classifyingPopulation.getBestIndividual()));
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
        classifyingPopulation = new Population(new RuleSet());
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
         * Potrzebujemy dwóch ewaluatorów.
         *
         * Jednego do oceniania osobników klasyfikujących. Można skorzystać
         * z istniejącego kodu. Należy zmodyfikować ocenę tak, aby uwzględnione
         * zostały odpowiednie przykładu -- wyselekcjonowane przez drugą
         * populację.
         *
         * Drugi ewaluator jest kwestią otwartą. Można zaimplemenentować
         * ocenę osobnika wybierającego na podstawie tego, jak bardzo
         * utrudnił osobnikowi oceniającemu. W ten sposób wyselekcjonują
         * najcięższe przypadki - przynajmniej w zamierzeniu...
         *
         * Ocena osobników wybierających zależy od oceny osobników
         * klasyfikujących i dlatego należy zadbać o poprawną kolejność
         * wykonywania.
         */
        Evaluator eval = Evaluator.getEvaluator();
        Individual theBestOfTheBest = null;
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
                classifyingPopulation = new Population(new ClassifyingIndividual());
                selectingPopulation = new Population(new SelectingIndividual());
                classifyingPopulation.init();
                theBestInd = null;

                classifyingPopulation.evaluate(DataLoader.getTrainData());
                // Stubs -
                evaluateClassifyingPopulation(classifyingPopulation, selectingPopulation);
                evaluateSelectingPopulation(selectingPopulation, classifyingPopulation);

                updateTheBestIndividual(classifyingPopulation.getBestIndividual());

                // EA START
                evolve(config, report);
                // EA DONE

                myClock.Pause();
                totalTimeClock.Pause();
                evaluateAndReport(eval, report, config);
                theBestOfTheBest = getNewBestOfTheBestIndividual(theBestOfTheBest, eval, config);
            }
            DataLoader.doCrossvalidationNext();
        }//END: CROSSVALIDATION CV TIMES
        report.reportAllToFile(config, eval, theBestOfTheBest, totalTimeClock);
    }

    public void evaluateAndReport(Evaluator eval, Report report, Configuration config) {
        eval.evaluate(DataLoader.getTrainData(), theBestInd);
        float train_fsc = theBestInd.getEvaluation().getFsc();
        float train_acc = theBestInd.getEvaluation().getAccuracy();

        eval.evaluate(DataLoader.getTestData(), theBestInd);
        float test_fsc = theBestInd.getEvaluation().getFsc();
        float test_acc = theBestInd.getEvaluation().getAccuracy();

        report.report(generation, train_fsc, train_acc, test_fsc, test_acc, myClock.GetTotalTime());
        report.extendedReport(config, eval, (RuleSet) theBestInd);
    }

    public Individual getNewBestOfTheBestIndividual(Individual bestInd,
            Evaluator eval, Configuration config) {
        if (bestInd == null) {
            bestInd = new RuleSet((RuleSet) (theBestInd));
            eval.evaluate(DataLoader.getTestData(), bestInd);
        }
        if (bestInd.getEvaluation().getAccuracy() < theBestInd.getEvaluation().getAccuracy()) {
            bestInd = new RuleSet((RuleSet) (theBestInd));
            eval.evaluate(DataLoader.getTestData(), bestInd);
            config.getReport().reportBestInd(bestInd);
        }
        return bestInd;
    }

    public long getGeneration() {
        return this.generation;
    }

    private void evaluateSelectingPopulation(Population selectingPopulation, Population classifyingPopulation) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void evaluateClassifyingPopulation(Population classifyingPopulation, Population selectingPopulation) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
