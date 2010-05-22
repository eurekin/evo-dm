package pwr.evolutionaryAlgorithm;

import java.util.ArrayList;
import java.util.Iterator;
import pl.eurekin.coevolution.ClassifyingIndividual;
import pl.eurekin.coevolution.Selector;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.utils.Rand;
import pwr.evolutionaryAlgorithm.individual.Individual;
import pwr.evolutionaryAlgorithm.individual.Rule;
import pwr.evolutionaryAlgorithm.individual.RuleSet;

public class Population<I extends Individual> implements Iterable<I> {

    private ArrayList<I> individuals;
    private float fitnessBest = 0.0f;
    private float fitnessWorst = 0.0f;
    private float fitnessAvg = 0.0f;
    private float fitnessSum = 0.0f;
    private final int popSize = Configuration.getConfiguration().getPopSize();

    public int size() {
        return individuals.size();
    }

    /**
     * Proper OO
     *
     * Krzyżowanie i mutacja
     * @return new population (new object)
     */
    @SuppressWarnings("unchecked")
    public Population<I> recombinate() {
        Population<I> tmp = new Population<I>();
        int i = 0; //individuals counter
        final Configuration configuration = Configuration.getConfiguration();
        final int selection = configuration.getSelection();
        final float crossoverValue = configuration.getCrossoverValue();
        I p1, p2;
        do {
            p1 = select(selection);
            p2 = select(selection);

            //crossover?
            if (Rand.getRandomBooleanFlip(crossoverValue)) {
                tmp.addInividual((I) p1.crossoverWith(p2).mutate());
                i++;
                //System.out.print("\n X ("+p1.Fitness+" "+p2.Fitness+") => "+o.Fitness+" ");
            } else {
                //there is no crossover -> p1 and p2 into new population
                tmp.addInividual((I) p1.mutate());
                i++;
                if (i < popSize) //there is place?
                {
                    tmp.addInividual((I) p2.mutate());
                    i++;
                }
            }
        } while (i < popSize);
        return tmp;
    }

    /**
     * Evalutation of population
     * @version Rule evaluation, scaling
     * @param DSc dataSource needed to evaluate individuals
     */
    public void evaluate(DataSource DSc) {
        for (Individual ind : individuals) {
            ind.evaluate(DSc);
        }
        updateStatistics();
    }

    public void updateStatistics() {
        float fitness = 0.0f;
        fitnessSum = 0;
        fitnessBest = Float.NEGATIVE_INFINITY;
        fitnessWorst = Float.POSITIVE_INFINITY;
        for (Individual ind : individuals) {
            fitness = ind.getEvaluation().getFitness();
            fitnessSum += fitness;
            if (fitness > fitnessBest) {
                fitnessBest = fitness;
            }
            if (fitness < fitnessWorst) {
                fitnessWorst = fitness;
            }
        }
        fitnessAvg = fitnessSum / popSize;
    }

    public final void init() {
        for (Individual ind : individuals) {
            ind.init();
        }
    }

    public Population() {
        individuals = new ArrayList<I>(popSize);
    }

    @SuppressWarnings("unchecked")
    public Population(Individual type) {
        individuals = new ArrayList<I>(popSize);

        /* Extension friendly version
         * unused for performance reasons
        Class c = type.getClass();
        try {
        for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
        Individual ind;
        ind = (Individual) c.newInstance();
        this.Individuals.add(ind);
        }
        } catch (InstantiationException ex) {
        ex.printStackTrace();
        } catch (IllegalAccessException ex) {
        ex.printStackTrace();
        } */


        /**
         * Fragile implementation...
         * It has to be in this specific order to work.
         *
         * Subtypes goes first.
         */
        Individual r;
        if (type instanceof ClassifyingIndividual) {
            for (int i = 0; i < popSize; i++) {
                r = new ClassifyingIndividual();
                individuals.add((I) r);
            }
        } else if (type instanceof Selector) {
            for (int i = 0; i < popSize; i++) {
                r = new Selector();
                individuals.add((I) r);
            }
        } else if (type instanceof RuleSet) {
            for (int i = 0; i < popSize; i++) {
                r = new RuleSet();
                individuals.add((I) r);
            }
        } else if (type instanceof Rule) {
            for (int i = 0; i < popSize; i++) {
                r = new Rule();
                individuals.add((I) r);
            }
        } else {
            throw new RuntimeException(
                    "Oooops... Unsupported Individual type");
        }
        init();
    }

    /**
     * Selects individual for population
     * @param selection 0-roullette, 1-random, 1+tournament selection
     * @return individual
     */
    private I select(int selection) {
        // IF-ELSE class hierarchy struggling to be free XXX

        int indv = 0;

        if (selection > 1) { ////////////////// tournament selection with repeat
            int bestID = 0, candID = 0;
            float bestFitness = 0, candFitness = 0;
            for (int i = 0; i < selection; i++) {
                candID = Rand.getRandomInt(popSize);
                candFitness = individuals.get(candID).getFitness();
                if (i == 0 || (i != 0 & bestFitness < candFitness)) {
                    bestID = candID;
                    bestFitness = candFitness;
                }
            }
            indv = bestID;
        } else if (selection == 1) { ////////////////////////// random selection
            indv = Rand.getRandomInt(popSize);
        } else if (selection == 0) {        //////////////////// roullette wheel
            float rToken = Rand.GetRandomFloat() * fitnessSum;
            float partSum = 0.0f;
            int i = -1;
            do {
                i++;
                partSum += individuals.get(i).getFitness();
            } while (partSum <= rToken && i < (popSize - 1));
            indv = i;
        } else {
            throw new RuntimeException("Wrong selection: " + selection);
        }

        // if (Configuration.isEcho()) System.out.print(" s("+selection+")->"+indv+" \n");
        return individuals.get(indv);
    }

    public void addInividual(I ind) {
        individuals.add(ind);
    }

    public float getAvgFitness() {
        return fitnessAvg;
    }

    public float getWorstFitness() {
        return fitnessWorst;
    }

    public float getBestFitness() {
        return fitnessBest;
    }

    /**
     * Gets staticsits about population
     * @return [bestFitness; worstFitness;avgFitness;;;;]
     */
    public String report() {
        return String.format("%.3f;%.3f;%.3f;;;;\n", fitnessBest, fitnessAvg, fitnessWorst);
    }

    public I getBestIndividual() {
        I best = individuals.get(0);
        for (I ind : individuals) {
            if (ind.getFitness() > best.getFitness()) {
                best = ind;
            }
        }
        return best;
    }

    public String getBest() {
        StringBuilder s = new StringBuilder();
        float f = 0;
        int fi = 0;

        for (int i = 0; i < popSize; i++) {
            final float fitness = individuals.get(i).getEvaluation().getFitness();
            if (f < fitness) {
                f = fitness;
                fi = i;
            }
        }

        s.append("BEST ").append(individuals.get(fi).toString()).append("\n");
        return s.toString();
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < popSize; i++) {
            s.append(individuals.get(i).toString()).append("\n");
        }
        return s.toString();
    }

    @Override
    public Iterator<I> iterator() {
        return individuals.listIterator();
    }

    public class diversity {

        public int diff;
        public int clones;

        diversity() {
            diff = 0;
            clones = 0;
        }
    }

    public diversity getDiversity() {
        diversity d = new diversity();
        int tmp = 0;
        for (int i = 0; i < popSize; i++) {
            for (int j = i + 1; j < popSize; j++) {
                tmp = individuals.get(i).diversityMeasure(individuals.get(j));

                if (j != i) {
                    if (tmp == 0) {
                        d.clones++;
                    }
                    d.diff += tmp;
                }
            }
        }
        return d;
    }
}
