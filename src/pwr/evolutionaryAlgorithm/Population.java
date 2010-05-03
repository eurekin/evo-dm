package pwr.evolutionaryAlgorithm;

import pwr.evolutionaryAlgorithm.Configuration;
import java.util.ArrayList;
import java.util.Comparator;

import pwr.evolutionaryAlgorithm.utils.Rand;
import pwr.evolutionaryAlgorithm.individual.Individual;
import pwr.evolutionaryAlgorithm.data.*;

public class Population {

    private ArrayList<Individual> Individuals;

    //-----------------------------------------------------------------------------------
    class IndividualComparator implements Comparator {

        public final int compare(Object a, Object b) {
            if (((Individual) a).getEvaluation().getFitness() == ((Individual) b).getEvaluation().getFitness()) {
                return 0;
            } else if (((Individual) a).getEvaluation().getFitness() > ((Individual) b).getEvaluation().getFitness()) {
                return 1;
            } else {
                return -1;
            }
        } // end compare
    } // end class StringComparator
    private float fitnessBest = 0.0f;
    private float fitnessWorst = 0.0f;
    private float fitnessAvg = 0.0f;
    private float fitnessSum = 0.0f;

    //-----------------------------------------------------------------------------------
    /**
     * Krzyżowanie i mutacja
     * @param selection selection method 0-roullete, 1-random, 1+tournament (0..N)
     * @param Px probability of crossover (>=0)
     * @param Pm probability of mutation(>=0)
     * @return new population (new object)
     */
    public Population recombinate() {
        Population tmp = new Population();
        int i = 0; //individuals counter
        do {
            Individual p1 = this.Select(Configuration.getConfiguration().getSelection());
            Individual p2 = this.Select(Configuration.getConfiguration().getSelection());

            if (Rand.GetRandomBooleanFlip(Configuration.getConfiguration().getCrossoverValue())) //crossover?
            {
                Individual o;
                try {
                    o = p1.Crossover(p1, p2);
                    o = o.Mutation();
                    tmp.addInividual(o);
                    i++;
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                //System.out.print("\n X ("+p1.Fitness+" "+p2.Fitness+") => "+o.Fitness+" ");
            } else { //there is no crossover -> p1 and p2 into new population
                Individual o1 = p1.Mutation();
                tmp.addInividual(o1);
                i++;
                if (i < Configuration.getConfiguration().getPopSize()) //there is place?
                {
                    Individual o2 = p2.Mutation();
                    tmp.addInividual(o2);
                    i++;
                }
            }

        } while (i < Configuration.getConfiguration().getPopSize());
        return tmp;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Evalutation of population
     * @version Rule evaluation, scaling
     * @param DS dataSource needed to evaluate inidvluals
     */
    public void evaluate(DataSource DSc) {

        int pop_size = Configuration.getConfiguration().getPopSize();
        Evaluator E = Evaluator.getEvaluator();

        for (int i = 0; i < pop_size; i++) {
            E.evaluate(DSc, this.Individuals.get(i));
        }

        /////// end EVALUATION

        ////////// STATISTICS
        float fitness = 0.0f;
        fitnessSum = 0;
        for (int i = 0; i < pop_size; i++) {
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
            this.Individuals.get(i).Initialise();
        }
    }

    //-----------------------------------------------------------------------------------
    public Population() {
        this.Individuals = new ArrayList<Individual>(Configuration.getConfiguration().getPopSize());
    }

    //-----------------------------------------------------------------------------------
    public Population(Individual type) {
        this.Individuals = new ArrayList<Individual>(Configuration.getConfiguration().getPopSize());

        // changes for extension
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
        }


        /*
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
        }*/
    }

    //-----------------------------------------------------------------------------------
    /**
     * Selects individual for population
     * @param selection 0-roullette, 1-random, 1+tournament selection
     * @return individual
     */
    private Individual Select(int selection) {

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
            indv = Rand.GetRandomInt(Configuration.getConfiguration().getPopSize());
        } else ///////////////////////////// tournament selection with repeat
        if (selection > 1) {
            int bestID = 0, candID = 0;
            float bestFitness = 0, candFitness = 0;
            for (int i = 0; i < selection; i++) {
                candID = Rand.GetRandomInt(Configuration.getConfiguration().getPopSize());
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

    //-----------------------------------------------------------------------------------
    public void addInividual(Individual inv) {
        this.Individuals.add(inv);
    }

    //-----------------------------------------------------------------------------------
    public float getAvgFitness() {
        return this.fitnessAvg;
    }

    //-----------------------------------------------------------------------------------
    public float getWorstFitness() {
        return this.fitnessWorst;
    }

    //-----------------------------------------------------------------------------------
    public float getBestFitness() {
        return this.fitnessBest;
    }

    //-----------------------------------------------------------------------------------
    /**
     * Gets staticsits about population
     * @return [bestFitness; worstFitness;avgFitness;;;;]
     */
    public String report() {
        StringBuilder r = new StringBuilder("" + this.fitnessBest + ";" + this.fitnessAvg + ";" + this.fitnessWorst + ";;;;\n");
        return r.toString();
    }

    //-----------------------------------------------------------------------------------
    public Individual getBestIndividual() {
        float f = 0;
        int fi = 0;
        for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
            if (f < this.Individuals.get(i).getEvaluation().getFitness()) {
                f = this.Individuals.get(i).getEvaluation().getFitness();
                fi = i;
            }
        }
        return (this.Individuals.get(fi));
    }

    //-----------------------------------------------------------------------------------
    public String getBest() {
        StringBuilder s = new StringBuilder("");
        float f = 0;
        int fi = 0;
        for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
            if (f < this.Individuals.get(i).getEvaluation().getFitness()) {
                f = this.Individuals.get(i).getEvaluation().getFitness();
                fi = i;
            }
        }

        s.append("BEST " + this.Individuals.get(fi).toString() + "\n");
        return s.toString();

    }

    //-----------------------------------------------------------------------------------
    @Override
    public String toString() {
        //Arrays.sort(this.Individuals.toArray(), new IndividualComparator());
        StringBuilder s = new StringBuilder("");
        for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
            s.append(this.Individuals.get(i).toString() + "\n");
        }
        return s.toString();
    }

    //-----------------------------------------------------------------------------------
    public class diversity {

        public int diff;
        public int clones;

        diversity() {
            diff = 0;
            clones = 0;
        }
    }

    // -----------------------------------------------------------------------------------
    public diversity getDiversity() {
        diversity d = new diversity();
        int tmp = 0;
        for (int i = 0; i < Configuration.getConfiguration().getPopSize(); i++) {
            for (int j = i + 1; j < Configuration.getConfiguration().getPopSize(); j++) {
                try {
                    tmp = this.Individuals.get(i).diversityMeasure(this.Individuals.get(j));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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
    //-----------------------------------------------------------------------------------
}

