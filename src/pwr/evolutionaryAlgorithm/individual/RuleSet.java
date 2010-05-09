package pwr.evolutionaryAlgorithm.individual;

import java.util.ArrayList;
import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.Configuration.CrossoverType;
import pwr.evolutionaryAlgorithm.utils.Rand;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.Evaluation;

/**
 *
 * @author pawelm
 */
public class RuleSet extends Individual {

    /*
     * TODO: remove this const form here!
     */
    private static boolean FIXED_LENGTH = false;
    ArrayList<Rule> rules;
    private Evaluation totalEvaluation;

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public RuleSet(final RuleSet RS) {
        rules = new ArrayList<Rule>();
        for (int i = 0; i < RS.rulesNo(); i++) {
            rules.add(new Rule(RS.rules.get(i)));
        }

        totalEvaluation = new Evaluation(RS.totalEvaluation);
        evaluations = new ArrayList<Evaluation>();
        if (Configuration.getConfiguration().isOneClassActive()) {
            evaluations.add(new Evaluation(RS.getEvaluation()));
        } else {
            for (int cl = 0; cl < Configuration.getClassesNo(); cl++) {
                this.evaluations.add(RS.getEvaluation(cl));
            }
        }
    }

    public RuleSet() {
        rules = new ArrayList<Rule>();

        totalEvaluation = new Evaluation();
        evaluations = new ArrayList<Evaluation>();
        if (Configuration.getConfiguration().isOneClassActive()) {
            evaluations.add(new Evaluation());
        } else {
            for (int cl = 0; cl < Configuration.getClassesNo(); cl++) {
                evaluations.add(new Evaluation());
            }
        }
    }

    @Override
    public void init() {
        this.rules.clear();

        int RULES = Rand.getRandomInt(Configuration.getConfiguration().getNumberOfRules()) + 1;

        if (!FIXED_LENGTH) {
            RULES = Configuration.getConfiguration().getNumberOfRules();
        }

        for (int i = 0; i < RULES; i++) {
            Rule r = new Rule();
            this.rules.add(r);
            this.rules.get(i).init();
        }

        this.clearEvaluations();
    }

    @Override
    public Evaluation getEvaluation() {
        return totalEvaluation;
    }

    public Evaluation getTotalEvaluation() {
        return totalEvaluation;
    }

    /**
     * do average value of all classes -> without unused (or not tested) class
     */
    public void doCountTotalEvaluation(int classess) {
        totalEvaluation.clear();
        int usedClasses = classess;
        //for each class
        for (int cl = 0; cl < classess; cl++) {
            if (!evaluations.get(cl).isDone()) {
                usedClasses--;
            } else {
                totalEvaluation.update(evaluations.get(cl));
            }
        }
        totalEvaluation.doAverage(usedClasses);
    }

    @Override
    protected int getGenesInIndividual() {
        return this.rulesNo();
    }

    @Override
    public Individual Mutation() {
        RuleSet RS = new RuleSet();
        for (int i = 0; i < this.rules.size(); i++) {
            RS.rules.add(i, (Rule) this.rules.get(i).Mutation());
        }
        return RS;
    }

    @Override
    public void clearEvaluations() {
        totalEvaluation.clear();
        super.clearEvaluations();
        for (int i = 0; i < this.rulesNo(); i++) {
            this.rules.get(i).clearEvaluations();
        }
    }
    /*
     *DCC: Data Covering crossoverWith
     */

    public Individual CrossoverDCC(Individual Indv1, Individual Indv2) {
        /**
         * TODO: insert code here
         */
        return null;
    }

    /*
     *BCX: Best Class crossoverWith
     */
    private static Individual CrossoverBCX(Individual ind1, Individual ind2) {

        /**
         * @TODO reczna zmiana Fixed lenght
         */
        FIXED_LENGTH = true;

        RuleSet p1 = (RuleSet) ind1, p2 = (RuleSet) ind2;
        RuleSet fittest = null, Offspring = new RuleSet();
        float f1, f2;

        for (int cl = 0; cl < DataLoader.getClassNumber(); cl++) {
            f1 = p1.evaluations.get(cl).getFitness();
            f2 = p2.evaluations.get(cl).getFitness();
            fittest = f1 > f2 ? p1 : p2;

            // find
            for (Rule rl : fittest.rules) {
                //if rule is activa and returns such class
                if (rl.isActive() && rl.getClassID() == cl) {
                    Offspring.rules.add(new Rule(rl));
                }
            }
        }
        return Offspring;
    }

    @Override
    public Individual crossoverWith(Individual other) {

        if (Configuration.getConfiguration().getCrossoverType() == CrossoverType.BCX) {
            return CrossoverBCX(this, other);
        } else {
            return CrossoverSimpleCut(this, other);
        }

    }

    /**
     * Random cut gives one individual as offsping
     * Chromosome FIXED lenght updated 
     * 
     */
    private static Individual CrossoverSimpleCut(Individual mother, Individual father) {

        RuleSet rs = new RuleSet();
        RuleSet P1 = (RuleSet) mother;
        RuleSet P2 = (RuleSet) father;

        int MAX = P1.rulesNo();
        int cut;
        boolean biggerP2 = false;


        if (FIXED_LENGTH && P1.rulesNo() < P2.rulesNo()) {
            cut = Rand.getRandomInt(P1.rulesNo());
            biggerP2 = true;
        } else {
            cut = Rand.getRandomInt(P2.rulesNo());
        }

        Rule d;
        for (int i = 0; i < MAX; i++) {
            d = null;
            if (biggerP2) {
                if (i <= cut) {
                    d = new Rule(P1.rules.get(i));
                } else {
                    d = new Rule(P2.rules.get(i));
                }
            } else {
                if (i <= cut) {
                    d = new Rule(P2.rules.get(i));
                } else {
                    d = new Rule(P1.rules.get(i));
                }
            }
            rs.rules.add(i, d);
        }
        return rs;
    }

    public Rule getRule(int ruleID) {
        return this.rules.get(ruleID);
    }

    public int rulesNo() {
        return this.rules.size();
    }

    @Override
    public String toString() {
        StringBuilder SB = new StringBuilder();

        SB.append("\n RULESET_EVAL");
        SB.append("\n TOTAL " + totalEvaluation.toString());
        ///classes evaluations...
        if (Configuration.getConfiguration().isOneClassActive()) {
            SB.append(evaluations.get(0).toString());
        } else {
            SB.append("\n classes ");
            for (int cl = 0; cl < DataLoader.getClassNumber(); cl++) {
                SB.append("\n cl_" + cl + " [" + evaluations.get(cl).toString() + "]");
            }
        }

        SB.append("\n\n RULES");
        for (int i = 0; i < this.rules.size(); i++) {
            SB.append("\n " + this.rules.get(i).toString());
        }

        return SB.toString();
    }

    /**
     * returns a positive number of contained rules comparision
     */
    @Override
    public int diversityMeasure(final Individual ind) {
        int diff = 0;
        if (ind instanceof RuleSet) {
            RuleSet RS = (RuleSet) ind;
            for (int i = 0; i < this.rulesNo(); i++) {
                diff = diff + this.rules.get(i).diversityMeasure(RS.rules.get(i));
            }
        } else {
            throw new RuntimeException("Illegal Object is given!");
        }
        return diff;
    }
}
