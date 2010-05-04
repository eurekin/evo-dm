package pwr.evolutionaryAlgorithm;

import java.util.ArrayList;
import pwr.evolutionaryAlgorithm.data.DataSource;
import pwr.evolutionaryAlgorithm.data.Evaluator;
import pwr.evolutionaryAlgorithm.utils.Rand;
import pwr.evolutionaryAlgorithm.individual.Individual;
import pwr.evolutionaryAlgorithm.individual.Rule;
import pwr.evolutionaryAlgorithm.individual.RuleSet;

public class Population {

    private ArrayList<Individual> Individuals;
    private float fitnessBest = 0.0f;
    private float fitnessWorst = 0.0f;
    private float fitnessAvg = 0.0f;
    private float fitnessSum = 0.0f;

    /**
     * Proper OO implementation
     *
     * Krzyżowanie i mutacja
     * @param selection selection method 0-roullete, 1-random, 1+tournament (0..N)
     * @param Px probability of crossover (>=0)
     * @param Pm probability of mutation(>=0)
     * @return new population (new object)
     */
    public Population recombinate() {
        Population tmp = new Population();
        int i = 0; //individuals counter
        final Configuration configuration = Configuration.getConfiguration();
        final int selection = configuration.getSelection();
        final float crossoverValue = configuration.getCrossoverValue();
        final int popSize = configuration.getPopSize();
        Individual p1, p2;
        do {
            p1 = select(selection);
            p2 = select(selection);

            //crossover?
            if (Rand.getRandomBooleanFlip(crossoverValue)) {
                tmp.addInividual(p1.crossoverWith(p2).Mutation());
                i++;
                //System.out.print("\n X ("+p1.Fitness+" "+p2.Fitness+") => "+o.Fitness+" ");
            } else { //there is no crossover -> p1 and p2 into new population
                tmp.addInividual(p1.Mutation());
                i++;
                if (i < popSize) //there is place?
                {
                    tmp.addInividual(p2.Mutation());
                    i++;
                }
            }
        } while (i < popSize);
        return tmp;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Evalutation of population
     * @version Rule evaluation, scaling
     * @param DS dataSource needed to evaluate inidvluals
     */
    public void evaluate(DataSource DSc) {

        int popSize = Configuration.getConfiguration().getPopSize();
        Evaluator E = Evaluator.getEvaluator();

        for (int i = 0; i < popSize; i++) {
            E.evaluate(DSc, this.Individuals.get(i));
        }

        /////// end EVALUATION

        ////////// STATISTICS
        float fitness = 0.0f;
        fitnessSum = 0;
        for (int i = 0; i < popSize; i++) {
            fitness = this.Individuals.get(i).getEvaluation().getFitness();
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

    //-----------------------------------------------------------------------------------
    public void Initialise() {
        //System.out.println("X"+this.PopSize+"_"+this.Individuals.size()+"_");
        for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
            this.Individuals.get(i).Initialize();
        }
    }

    public Population() {
        this.Individuals = new ArrayList<Individual>(Configuration.getConfiguration().getPopSize());
    }

    //-----------------------------------------------------------------------------------
    public Population(Individual type) {
        this.Individuals = new ArrayList<Individual>(Configuration.getConfiguration().getPopSize());

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
                this.Individuals.add(rs);
            }
        } else if (type instanceof Rule) {
            for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
                Rule r = new Rule();
                this.Individuals.add(r);
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
    private Individual select(int selection) {
        // IF-ELSE class hierarchy struggling to be free XXX

        int indv = 0;
        ///////////////////////////// roullette wheel
        if (selection == 0) {
            float rToken = Rand.GetRandomFloat() * this.fitnessSum;
            float partSum = 0.0f;
            int i = -1;
            do {
                i++;
                partSum = partSum + this.Individuals.get(i).getEvaluation().getFitness();
            } while (partSum <= rToken && i < (Configuration.getConfiguration().getPopSize() - 1));
            indv = i;
        } else ///////////////////////////// random selection
        if (selection == 1) {
            indv = Rand.getRandomInt(Configuration.getConfiguration().getPopSize());
        } else ///////////////////////////// tournament selection with repeat
        if (selection > 1) {
            int bestID = 0, candID = 0;
            float bestFitness = 0, candFitness = 0;
            for (int i = 0; i < selection; i++) {
                candID = Rand.getRandomInt(Configuration.getConfiguration().getPopSize());
                candFitness = this.Individuals.get(candID).getEvaluation().getFitness();
                if (i == 0 || (i != 0 & bestFitness < candFitness)) {
                    bestID = candID;
                    bestFitness = candFitness;
                }
            }
            indv = bestID;
        }
        //////////////////////////////////

        // if (Configuration.isEcho()) System.out.print(" s("+selection+")->"+indv+" \n");
        return this.Individuals.get(indv);
    }

    public void addInividual(Individual inv) {
        this.Individuals.add(inv);
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

    public Individual getBestIndividual() {
        float f = 0;
        int fi = 0;
        final int popSize = Configuration.getConfiguration().getPopSize();
        for (int i = 0; i < popSize; i++) {
            if (f < this.Individuals.get(i).getEvaluation().getFitness()) {
                f = this.Individuals.get(i).getEvaluation().getFitness();
                fi = i;
            }
        }
        return (this.Individuals.get(fi));
    }

    public String getBest() {
        StringBuilder s = new StringBuilder("");
        float f = 0;
        int fi = 0;

        final int popSize = Configuration.getConfiguration().getPopSize();
        for (int i = 0; i < popSize; i++) {
            if (f < this.Individuals.get(i).getEvaluation().getFitness()) {
                f = this.Individuals.get(i).getEvaluation().getFitness();
                fi = i;
            }
        }

        s.append("BEST " + this.Individuals.get(fi).toString() + "\n");
        return s.toString();
    }

    @Override
    public String toString() {
        //Arrays.sort(this.Individuals.toArray(), new IndividualComparator());
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
            s.append(this.Individuals.get(i).toString() + "\n");
        }
        return s.toString();
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
        for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
            for (int j = i + 1; j < Configuration.getConfiguration().getPopSize(); j++) {
                tmp = this.Individuals.get(i).diversityMeasure(this.Individuals.get(j));

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

