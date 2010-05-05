package pwr.evolutionaryAlgorithm;

import java.util.ArrayList;
import java.util.Iterator;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.data.Evaluator;
import pwr.evolutionaryAlgorithm.utils.Rand;
import pwr.evolutionaryAlgorithm.individual.Individual;
import pwr.evolutionaryAlgorithm.individual.Rule;
import pwr.evolutionaryAlgorithm.individual.RuleSet;

public class Population<I extends Individual> implements Iterable<I>{


    private ArrayList<I> individuals;
    private float fitnessBest = 0.0f;
    private float fitnessWorst = 0.0f;
    private float fitnessAvg = 0.0f;
    private float fitnessSum = 0.0f;

    /**
     * Proper OO
     *
     * Krzyżowanie i mutacja
     * @param selection selection method 0-roullete, 1-random, 1+tournament (0..N)
     * @param Px probability of crossover (>=0)
     * @param Pm probability of mutation(>=0)
     * @return new population (new object)
     */
    public Population<I> recombinate() {
        Population<I> tmp = new Population<I>();
        int i = 0; //individuals counter
        final Configuration configuration = Configuration.getConfiguration();
        final int selection = configuration.getSelection();
        final float crossoverValue = configuration.getCrossoverValue();
        final int popSize = configuration.getPopSize();
        I p1, p2;
        do {
            p1 = select(selection);
            p2 = select(selection);

            //crossover?
            if (Rand.getRandomBooleanFlip(crossoverValue)) {
                tmp.addInividual((I) p1.crossoverWith(p2).Mutation());
                i++;
                //System.out.print("\n X ("+p1.Fitness+" "+p2.Fitness+") => "+o.Fitness+" ");
            } else { //there is no crossover -> p1 and p2 into new population
                tmp.addInividual((I) p1.Mutation());
                i++;
                if (i < popSize) //there is place?
                {
                    tmp.addInividual((I) p2.Mutation());
                    i++;
                }
            }
        } while (i < popSize);
        return tmp;
    }

    /**
     * Evalutation of population
     * @version Rule evaluation, scaling
     * @param DS dataSource needed to evaluate inidvluals
     */
    public void evaluate(DataSource DSc) {

        int popSize = Configuration.getConfiguration().getPopSize();
        Evaluator E = Evaluator.getEvaluator();

        for (int i = 0; i < popSize; i++) {
            E.evaluate(DSc, this.individuals.get(i));
        }

        /////// end EVALUATION

        ////////// STATISTICS
        float fitness = 0.0f;
        fitnessSum = 0;
        for (int i = 0; i < popSize; i++) {
            fitness = this.individuals.get(i).getEvaluation().getFitness();
            fitnessSum = fitnessSum + fitness;
            if (i == 0) {
                this.fitnessBest = this.fitnessWorst = fitness;
            }
            if (fitness > this.fitnessBest) {
                this.fitnessBest = fitness;
            }
            if (fitness < this.fitnessWorst) {
                this.fitnessWorst = fitness;
            }
        }
        this.fitnessAvg = this.fitnessSum / Configuration.getConfiguration().getPopSize();
        ////////// END: STATISTICS
    }

    public void init() {
        //System.out.println("X"+this.PopSize+"_"+this.Individuals.size()+"_");
        for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
            this.individuals.get(i).init();
        }

    }

    public Population() {
        this.individuals = new ArrayList<I>(Configuration.getConfiguration().getPopSize());
    }

    public Population(Individual type) {
        this.individuals = new ArrayList<I>(Configuration.getConfiguration().getPopSize());

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


        if (type instanceof RuleSet) {
            for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
                RuleSet rs = new RuleSet();
                individuals.add((I) rs);
            }
        } else if (type instanceof Rule) {
            for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
                Rule r = new Rule();
                individuals.add((I) r);
            }
        } else {
            throw new RuntimeException("Oooops... Unsupported Individual type");
        }

    }

    /**
     * Selects individual for population
     * @param selection 0-roullette, 1-random, 1+tournament selection
     * @return individual
     */
    private I select(int selection) {
        // IF-ELSE class hierarchy struggling to be free XXX

        int indv = 0;

        if (selection > 1) { ///////////////////////////// tournament selection with repeat
            int bestID = 0, candID = 0;
            float bestFitness = 0, candFitness = 0;
            for (int i = 0; i < selection; i++) {
                candID = Rand.getRandomInt(Configuration.getConfiguration().getPopSize());
                candFitness = this.individuals.get(candID).getEvaluation().getFitness();
                if (i == 0 || (i != 0 & bestFitness < candFitness)) {
                    bestID = candID;
                    bestFitness = candFitness;
                }
            }
            indv = bestID;
        } else if (selection == 1) { ///////////////////////////// random selection
            indv = Rand.getRandomInt(Configuration.getConfiguration().getPopSize());
        } else if (selection == 0) {        ////////////////////// roullette wheel
            float rToken = Rand.GetRandomFloat() * fitnessSum;
            float partSum = 0.0f;
            int i = -1;
            do {
                i++;
                partSum = partSum + individuals.get(i).getEvaluation().getFitness();
            } while (partSum <= rToken && i < (Configuration.getConfiguration().getPopSize() - 1));
            indv = i;
        } else {
            throw new RuntimeException("Wrong selection: " + selection);
        }

        // if (Configuration.isEcho()) System.out.print(" s("+selection+")->"+indv+" \n");
        return this.individuals.get(indv);
    }

    public void addInividual(I inv) {
        this.individuals.add(inv);
    }

    public float getAvgFitness() {
        return this.fitnessAvg;
    }

    public float getWorstFitness() {
        return this.fitnessWorst;
    }

    public float getBestFitness() {
        return this.fitnessBest;
    }

    /**
     * Gets staticsits about population
     * @return [bestFitness; worstFitness;avgFitness;;;;]
     */
    public String report() {
        StringBuilder r = new StringBuilder("" + this.fitnessBest + ";" + this.fitnessAvg + ";" + this.fitnessWorst + ";;;;\n");
        return r.toString();
    }

    public I getBestIndividual() {
        float f = 0;
        int fi = 0;
        final int popSize = Configuration.getConfiguration().getPopSize();
        for (int i = 0; i < popSize; i++) {
            final float fitness = this.individuals.get(i).getEvaluation().getFitness();
            if (f < fitness) {
                f = fitness;
                fi = i;
            }
        }
        return (this.individuals.get(fi));
    }

    public String getBest() {
        StringBuilder s = new StringBuilder();
        float f = 0;
        int fi = 0;

        final int popSize = Configuration.getConfiguration().getPopSize();
        for (int i = 0; i < popSize; i++) {
            final float fitness = this.individuals.get(i).getEvaluation().getFitness();
            if (f < fitness) {
                f = fitness;
                fi = i;
            }
        }

        s.append("BEST " + this.individuals.get(fi).toString() + "\n");
        return s.toString();
    }

    @Override
    public String toString() {
        //Arrays.sort(this.Individuals.toArray(), new IndividualComparator());
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
            s.append(this.individuals.get(i).toString() + "\n");
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
        final int popSize = Configuration.getConfiguration().getPopSize();
        for (int i = 0; i < popSize; i++) {
            for (int j = i + 1; j < popSize; j++) {
                tmp = this.individuals.get(i).diversityMeasure(this.individuals.get(j));

                if (j != i) {
                    if (tmp == 0) {
                        d.clones++;
                    }
                    d.diff = d.diff + tmp;
                }
            }
        }
        return d;
    }
}

