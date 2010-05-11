package pwr.evolutionaryAlgorithm.individual;

import java.util.ArrayList;
import java.util.Iterator;
import pwr.evolutionaryAlgorithm.utils.Rand;
import pwr.evolutionaryAlgorithm.data.DataLoader;
import pwr.evolutionaryAlgorithm.data.Evaluation;
import pwr.evolutionaryAlgorithm.Configuration;
import pwr.evolutionaryAlgorithm.Configuration.CrossoverType;

/**
 *
 * @author pawelm
 */
public class RuleSet extends Individual implements Iterable<Rule> {

    /*
     * TODO: remove this const form here!
     */
    private static boolean fixedLength = false;
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

        if (!fixedLength) {
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
     * Mogę użyć do koewolucji bez zmian.
     *
     * do average value of all classes -> without unused (or not tested) class
     * @param classess number of classes
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
    public Individual mutate() {
        RuleSet rs = new RuleSet();
        for (int i = 0; i < this.rules.size(); i++) {
            rs.rules.add(i, (Rule) this.rules.get(i).mutate());
        }
        return rs;
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
    private static Individual crossoverBCX(Individual ind1, Individual ind2) {

        /**
         * @TODO reczna zmiana Fixed lenght
         */
        fixedLength = true;

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
            return crossoverBCX(this, other);
        } else {
            return crossoverSimpleCut(this, other);
        }

    }

    /**
     * Random cut gives one individual as offsping
     * Chromosome FIXED lenght updated 
     * 
     */
    private static Individual crossoverSimpleCut(Individual mother, Individual father) {

        RuleSet rs = new RuleSet();
        RuleSet p1 = (RuleSet) mother;
        RuleSet p2 = (RuleSet) father;

        int MAX = p1.rulesNo();
        int cut;
        boolean biggerP2 = false;


        if (fixedLength && p1.rulesNo() < p2.rulesNo()) {
            cut = Rand.getRandomInt(p1.rulesNo());
            biggerP2 = true;
        } else {
            cut = Rand.getRandomInt(p2.rulesNo());
        }

        Rule d;
        for (int i = 0; i < MAX; i++) {
            d = null;
            if (biggerP2) {
                if (i <= cut) {
                    d = new Rule(p1.rules.get(i));
                } else {
                    d = new Rule(p2.rules.get(i));
                }
            } else {
                if (i <= cut) {
                    d = new Rule(p2.rules.get(i));
                } else {
                    d = new Rule(p1.rules.get(i));
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
        SB.append("\n TOTAL ").append(totalEvaluation.toString());
        ///classes evaluations...
        if (Configuration.getConfiguration().isOneClassActive()) {
            SB.append(evaluations.get(0).toString());
        } else {
            SB.append("\n classes ");
            for (int cl = 0; cl < DataLoader.getClassNumber(); cl++) {
                SB.append("\n cl_").append(cl).append(" [").append(evaluations.get(cl).toString()).append("]");
            }
        }

        SB.append("\n\n RULES");
        for (int i = 0; i < this.rules.size(); i++) {
            SB.append("\n ").append(this.rules.get(i).toString());
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

    @Override
    public Iterator<Rule> iterator() {
        return rules.iterator();
    }
}
