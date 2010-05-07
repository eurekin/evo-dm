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
    private ArrayList<Rule> Rules;
    private Evaluation TotalEvaluation;

    public RuleSet(final RuleSet RS) {
        this.Rules = new ArrayList<Rule>();
        for (int i = 0; i < RS.rulesNo(); i++) {
            this.Rules.add(new Rule(RS.Rules.get(i)));
        }

        TotalEvaluation = new Evaluation(RS.TotalEvaluation);
        this.Evaluations = new ArrayList<Evaluation>();
        if (Configuration.getConfiguration().isOneClassActive()) {
            Evaluations.add(new Evaluation(RS.getEvaluation()));
        } else {
            for (int cl = 0; cl < Configuration.getClassesNo(); cl++) {
                this.Evaluations.add(RS.getEvaluation(cl));
            }
        }
    }

    public RuleSet() {
        this.Rules = new ArrayList<Rule>();

        TotalEvaluation = new Evaluation();
        this.Evaluations = new ArrayList<Evaluation>();
        if (Configuration.getConfiguration().isOneClassActive()) {
            Evaluations.add(new Evaluation());
        } else {
            for (int cl = 0; cl < Configuration.getClassesNo(); cl++) {
                this.Evaluations.add(new Evaluation());
            }
        }
    }

    @Override
    public void init() {
        this.Rules.clear();

        int RULES = Rand.getRandomInt(Configuration.getConfiguration().getNumberOfRules()) + 1;

        if (FIXED_LENGTH == false) {
            RULES = Configuration.getConfiguration().getNumberOfRules();
        }

        for (int i = 0; i < RULES; i++) {
            Rule r = new Rule();
            this.Rules.add(r);
            this.Rules.get(i).init();
        }

        this.clearEvaluations();
    }

    @Override
    public Evaluation getEvaluation() {
        return this.TotalEvaluation;
    }

    public Evaluation getTotalEvaluation() {
        return this.TotalEvaluation;
    }

    /*
     * do average value of all classes -> without unused (or not tested) class
     */
    public void doCountTotalEvaluation(int classess) {
        this.TotalEvaluation.clear();
        int usedClasses = classess;
        //for each class
        for (int cl = 0; cl < classess; cl++) {
            if (!this.Evaluations.get(cl).isDone()) {
                usedClasses--;
            } else {
                this.TotalEvaluation.update(this.Evaluations.get(cl));
            }
        }
        this.TotalEvaluation.doAverage(usedClasses);
    }

    @Override
    protected int getGenesInIndividual() {
        return this.rulesNo();
    }

    @Override
    public Individual Mutation() {
        RuleSet RS = new RuleSet();
        for (int i = 0; i < this.Rules.size(); i++) {
            RS.Rules.add(i, (Rule) this.Rules.get(i).Mutation());
        }
        return RS;
    }

    @Override
    public void clearEvaluations() {
        TotalEvaluation.clear();
        super.clearEvaluations();
        for (int i = 0; i < this.rulesNo(); i++) {
            this.Rules.get(i).clearEvaluations();
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
    private static Individual CrossoverBCX(Individual Indv1, Individual Indv2) {

        /**
         * @TODO reczna zmiana Fixed lenght
         */
        FIXED_LENGTH = true;

        RuleSet Parent1 = (RuleSet) Indv1;
        RuleSet Parent2 = (RuleSet) Indv2;
        RuleSet BetterClassParent = null;
        RuleSet Offspring = new RuleSet();

        for (int cl = 0; cl < DataLoader.getClassNumber(); cl++) {

            if (Parent1.Evaluations.get(cl).getFitness() > Parent2.Evaluations.get(cl).getFitness()) {
                BetterClassParent = Parent1;
            } else {
                BetterClassParent = Parent2;
            }

            /// find
            for (int r = 0; r < BetterClassParent.rulesNo(); r++) {
                //if rule is activa and returns such class
                if (BetterClassParent.getRule(r).isActive() && BetterClassParent.getRule(r).getClassID() == cl) {
                    Rule NewRule = new Rule(BetterClassParent.Rules.get(r));
                    Offspring.Rules.add(NewRule);
                }
            }
        }
        return Offspring;
    }

    @Override
    public Individual crossoverWith(Individual Indv1) {

        if (Configuration.getConfiguration().getCrossoverType() == CrossoverType.BCX) {
            return CrossoverBCX(Indv1, this);
        } else {
            return CrossoverSimpleCut(Indv1, this);
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
                    d = new Rule(P1.Rules.get(i));
                } else {
                    d = new Rule(P2.Rules.get(i));
                }
            } else {
                if (i <= cut) {
                    d = new Rule(P2.Rules.get(i));
                } else {
                    d = new Rule(P1.Rules.get(i));
                }
            }
            rs.Rules.add(i, d);
        }
        return rs;
    }

    public Rule getRule(int ruleID) {
        if (ruleID < this.Rules.size()) {
            return this.Rules.get(ruleID);
        } else {
            return null;
        }
    }

    public int rulesNo() {
        return this.Rules.size();
    }

    @Override
    public String toString() {
        StringBuilder SB = new StringBuilder();

        SB.append("\n RULESET_EVAL");
        SB.append("\n TOTAL " + TotalEvaluation.toString());
        ///classes evaluations...
        if (Configuration.getConfiguration().isOneClassActive()) {
            SB.append(Evaluations.get(0).toString());
        } else {
            SB.append("\n classes ");
            for (int cl = 0; cl < DataLoader.getClassNumber(); cl++) {
                SB.append("\n cl_" + cl + " [" + Evaluations.get(cl).toString() + "]");
            }
        }

        SB.append("\n\n RULES");
        for (int i = 0; i < this.Rules.size(); i++) {
            SB.append("\n " + this.Rules.get(i).toString());
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
                diff = diff + this.Rules.get(i).diversityMeasure(RS.Rules.get(i));
            }
        } else {
            throw new RuntimeException("Illegal Object is given!");
        }
        return diff;
    }
}
